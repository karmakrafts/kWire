/*
 * Copyright 2025 Karma Krafts & associates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.karmakrafts.kwire.compiler.generation

import dev.karmakrafts.kwire.compiler.util.ABIConstants
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.buildSimpleObject
import dev.karmakrafts.kwire.compiler.util.buildSimpleProperty
import dev.karmakrafts.kwire.compiler.util.getABIFriendlyName
import dev.karmakrafts.kwire.compiler.util.getCleanSpecialName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.expressions.FirEmptyArgumentList
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArrayLiteral
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildGetClassCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildVarargArgumentsExpression
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.types.Variance

internal class ModuleDataFirGenerationExtension(
    session: FirSession, messageCollector: MessageCollector
) : FirDeclarationGenerationExtension(session) {
    companion object : GeneratedDeclarationKey()

    private val moduleName: String = session.moduleData.name.getABIFriendlyName()
    private val moduleDataClassName: Name = Name.identifier("__KWireModuleData\$${moduleName}__")
    private val moduleDataClassId: ClassId = ClassId.topLevel(FqName.topLevel(moduleDataClassName))
    private val moduleDataNameId: CallableId = CallableId(moduleDataClassId, ABIConstants.moduleDataNameName)
    private val moduleDataDependenciesId: CallableId =
        CallableId(moduleDataClassId, ABIConstants.moduleDataDependenciesName)
    private val moduleDataSymbolTableDataId: CallableId =
        CallableId(moduleDataClassId, ABIConstants.moduleDataSymbolTableData)

    @OptIn(SymbolInternals::class)
    inner class SymbolsAndTypes {
        val listSymbol = session.getRegularClassSymbolByClassId(KWireNames.Kotlin.List.id)!!
        val arraySymbol = session.getRegularClassSymbolByClassId(KWireNames.Kotlin.Array.id)!!
        val listOfSymbol = session.symbolProvider.getTopLevelFunctionSymbols(
            KWireNames.Kotlin.collectionsPackageName, KWireNames.Functions.listOf
        ).first { it.fir.valueParameters.first().isVararg }

        val stringListType =
            listSymbol.constructType(arrayOf(session.builtinTypes.stringType.coneType)).toFirResolvedTypeRef()

        val moduleDataSymbol = session.getRegularClassSymbolByClassId(KWireNames.ModuleData.id)!!
        val moduleDataType = moduleDataSymbol.defaultType()
        val moduleDataListType = listSymbol.constructType(arrayOf(moduleDataType)).toFirResolvedTypeRef()
        val moduleDataArrayType = arraySymbol.constructType(arrayOf(moduleDataType)).toFirResolvedTypeRef()

        val byteArraySymbol = session.getRegularClassSymbolByClassId(KWireNames.Kotlin.ByteArray.id)!!
        val byteArrayType = byteArraySymbol.defaultType().toFirResolvedTypeRef()
    }

    private fun getModuleDataReference(name: Name): FirExpression? {
        // Try to find the module data implementation class for the given module name
        val rawModuleName = name.getCleanSpecialName().replace('.', '_')
        val moduleDataImplClassName = Name.identifier("__KWireModuleData\$${rawModuleName}__")
        val moduleDataImplClassId = ClassId.topLevel(FqName.topLevel(moduleDataImplClassName))
        val moduleDataImplClass = session.getRegularClassSymbolByClassId(moduleDataImplClassId) ?: return null
        // Construct a singleton reference to the implementation
        return buildGetClassCall {
            coneTypeOrNull = moduleDataImplClass.defaultType()
            argumentList = FirEmptyArgumentList
        }
    }

    private fun generateNameProperty(
        dispatchReceiver: FirExpression
    ): FirPropertySymbol = session.buildSimpleProperty( // @formatter:off
        id = moduleDataNameId,
        dispatchReceiver = dispatchReceiver,
        type = session.builtinTypes.stringType
    ) { // @formatter:on
        origin = FirDeclarationOrigin.Plugin(ModuleDataFirGenerationExtension)
        initializer = buildLiteralExpression( // @formatter:off
            source = null,
            kind = ConstantValueKind.String,
            value = moduleName,
            setType = true
        ) // @formatter:on
    }

    private fun generateDependenciesProperty(
        symbolsAndTypes: SymbolsAndTypes, dispatchReceiver: FirExpression
    ): FirPropertySymbol = session.buildSimpleProperty( // @formatter:off
        id = moduleDataDependenciesId,
        dispatchReceiver = dispatchReceiver,
        type = symbolsAndTypes.moduleDataListType
    ) { // @formatter:on
        origin = FirDeclarationOrigin.Plugin(ModuleDataFirGenerationExtension)
        initializer = buildFunctionCall {
            coneTypeOrNull = symbolsAndTypes.stringListType.coneType
            calleeReference = buildResolvedNamedReference {
                name = symbolsAndTypes.listOfSymbol.name
                resolvedSymbol = symbolsAndTypes.listOfSymbol
            }
            typeArguments += buildTypeProjectionWithVariance {
                typeRef = symbolsAndTypes.moduleDataType.toFirResolvedTypeRef()
                variance = Variance.INVARIANT
            }
            argumentList = buildResolvedArgumentList(buildArgumentList {
                buildVarargArgumentsExpression {
                    coneTypeOrNull = symbolsAndTypes.moduleDataArrayType.coneType
                    coneElementTypeOrNull = symbolsAndTypes.moduleDataType
                    arguments += session.moduleData.dependencies.mapNotNull {
                        getModuleDataReference(it.name)
                    }
                }
            }, LinkedHashMap())
        }
    }

    private fun generateSymbolTableDataProperty(
        symbolsAndTypes: SymbolsAndTypes, dispatchReceiver: FirExpression
    ): FirPropertySymbol = session.buildSimpleProperty( // @formatter:off
        id = moduleDataSymbolTableDataId,
        dispatchReceiver = dispatchReceiver,
        type = symbolsAndTypes.byteArrayType
    ) { // @formatter:on
        origin = FirDeclarationOrigin.Plugin(ModuleDataFirGenerationExtension)
        initializer = buildArrayLiteral {
            // This is filled in during the IR lowering after monomorphization
            coneTypeOrNull = symbolsAndTypes.byteArraySymbol.defaultType()
            argumentList = FirEmptyArgumentList
        }
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        return when (classId) {
            moduleDataClassId -> session.buildSimpleObject(moduleDataClassId) {
                // The generated module data object needs to implement ModuleData to bind against the runtime API
                val moduleDataClass = session.getRegularClassSymbolByClassId(KWireNames.ModuleData.id) ?: return null
                superTypeRefs += moduleDataClass.defaultType().toFirResolvedTypeRef()
                origin = FirDeclarationOrigin.Plugin(ModuleDataFirGenerationExtension)
            }

            else -> null
        }
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val owner = context.owner
        return when (owner.classId) { // @formatter:off
            moduleDataClassId -> listOf(createDefaultPrivateConstructor(owner, MonoFunctionClassFirGenerationExtension).symbol)
            else -> emptyList()
        } // @formatter:on
    }

    override fun generateProperties(
        callableId: CallableId, context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        val owner = context?.owner ?: return emptyList()
        fun createDispatchReceiver(): FirExpression = buildThisReceiverExpression {
            coneTypeOrNull = owner.defaultType()
            calleeReference = buildImplicitThisReference {
                boundSymbol = owner
            }
        }

        val symbolsAndTypes = SymbolsAndTypes()
        return when (callableId) {
            moduleDataNameId -> listOf(generateNameProperty(createDispatchReceiver()))
            moduleDataDependenciesId -> listOf(generateDependenciesProperty(symbolsAndTypes, createDispatchReceiver()))
            moduleDataSymbolTableDataId -> listOf(
                generateSymbolTableDataProperty(
                    symbolsAndTypes, createDispatchReceiver()
                )
            )

            else -> emptyList()
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return when (classSymbol.classId) { // @formatter:off
            moduleDataClassId -> setOf(
                SpecialNames.INIT,
                ABIConstants.moduleDataNameName,
                ABIConstants.moduleDataDependenciesName,
                ABIConstants.moduleDataSymbolTableData
            )
            else -> emptySet()
        } // @formatter:on
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        val moduleData = session.moduleData
        return if (moduleData.isCommon) emptySet()
        else setOf(moduleDataClassId)
    }
}