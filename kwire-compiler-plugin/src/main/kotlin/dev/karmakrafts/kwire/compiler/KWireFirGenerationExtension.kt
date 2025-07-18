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

package dev.karmakrafts.kwire.compiler

import dev.karmakrafts.kwire.compiler.util.ABIConstants
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.getCleanSpecialName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.builder.FirPropertyBuilder
import org.jetbrains.kotlin.fir.declarations.builder.FirRegularClassBuilder
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.expressions.FirEmptyArgumentList
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArrayLiteral
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildGetClassCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildVarargArgumentsExpression
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.scopes.kotlinScopeProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertyAccessorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.builder.buildTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.types.Variance

internal class KWireFirGenerationExtension( // @formatter:off
    session: FirSession,
    private val messageCollector: MessageCollector
) : FirDeclarationGenerationExtension(session) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        val declOrigin: FirDeclarationOrigin = FirDeclarationOrigin.Plugin(this)
    }

    private val moduleName: String = session.moduleData.name.getCleanSpecialName()

    private val monoFunctionClassName: Name = Name.identifier("__KWireMonoFunctions\$${moduleName}__")
    private val monoFunctionClassId: ClassId = ClassId.topLevel(FqName.topLevel(monoFunctionClassName))

    private val moduleDataClassName: Name = Name.identifier("__KWireModuleData\$${moduleName}__")
    private val moduleDataClassId: ClassId = ClassId.topLevel(FqName.topLevel(moduleDataClassName))
    private val moduleDataNameId: CallableId = CallableId(moduleDataClassId, ABIConstants.moduleDataNameName)
    private val moduleDataDependenciesId: CallableId =
        CallableId(moduleDataClassId, ABIConstants.moduleDataDependenciesName)
    private val moduleDataSymbolTableDataId: CallableId =
        CallableId(moduleDataClassId, ABIConstants.moduleDataSymbolTableData)

    private inline fun generateSimpleObject(
        id: ClassId, init: FirRegularClassBuilder.() -> Unit = {}
    ): FirClassSymbol<*> = buildRegularClass {
        name = id.shortClassName
        classKind = ClassKind.OBJECT
        status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
        moduleData = session.moduleData
        scopeProvider = session.kotlinScopeProvider
        origin = declOrigin
        symbol = FirRegularClassSymbol(id)
        init()
    }.symbol

    private inline fun generateSimpleProperty( // @formatter:off
        id: CallableId,
        type: FirTypeRef,
        init: FirPropertyBuilder.() -> Unit = {}
    ): FirPropertySymbol = buildProperty { // @formatter:on
        name = id.callableName
        status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
        moduleData = session.moduleData
        origin = declOrigin
        symbol = FirPropertySymbol(id)
        returnTypeRef = type
        isVar = false
        isLocal = false
        getter = buildPropertyAccessor {
            symbol = FirPropertyAccessorSymbol()
            moduleData = session.moduleData
            propertySymbol = this@buildProperty.symbol
            status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
            origin = declOrigin
            returnTypeRef = type
            isGetter = true
        }
        init()
    }.symbol

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

    @OptIn(SymbolInternals::class)
    private fun generateModuleDataProperties(owner: FirClassSymbol<*>): List<FirPropertySymbol> {
        val listSymbol = session.getRegularClassSymbolByClassId(KWireNames.Kotlin.List.id) ?: return emptyList()
        val arraySymbol = session.getRegularClassSymbolByClassId(KWireNames.Kotlin.Array.id) ?: return emptyList()
        val listOfSymbol = session.symbolProvider.getTopLevelFunctionSymbols(
            KWireNames.Kotlin.collectionsPackageName, KWireNames.Functions.listOf
        ).first { it.fir.valueParameters.first().isVararg }

        val stringListType =
            listSymbol.constructType(arrayOf(session.builtinTypes.stringType.coneType)).toFirResolvedTypeRef()

        val moduleDataSymbol = session.getRegularClassSymbolByClassId(KWireNames.ModuleData.id) ?: return emptyList()
        val moduleDataType = moduleDataSymbol.defaultType()
        val moduleDataListType = listSymbol.constructType(arrayOf(moduleDataType)).toFirResolvedTypeRef()
        val moduleDataArrayType = arraySymbol.constructType(arrayOf(moduleDataType)).toFirResolvedTypeRef()

        val byteArraySymbol =
            session.getRegularClassSymbolByClassId(KWireNames.Kotlin.ByteArray.id) ?: return emptyList()
        val byteArrayType = byteArraySymbol.defaultType().toFirResolvedTypeRef()

        return listOf( // @formatter:off
            generateSimpleProperty(moduleDataNameId, session.builtinTypes.stringType) {
                dispatchReceiverType = owner.defaultType()
                initializer = buildLiteralExpression(null, ConstantValueKind.String, moduleName, setType = true)
            },
            generateSimpleProperty(moduleDataDependenciesId, moduleDataListType) {
                dispatchReceiverType = owner.defaultType()
                initializer = buildFunctionCall {
                    coneTypeOrNull = stringListType.coneType
                    calleeReference = buildResolvedNamedReference {
                        name = listOfSymbol.name
                        resolvedSymbol = listOfSymbol
                    }
                    typeArguments += buildTypeProjectionWithVariance {
                        typeRef = moduleDataType.toFirResolvedTypeRef()
                        variance = Variance.INVARIANT
                    }
                    argumentList = buildResolvedArgumentList(buildArgumentList {
                        buildVarargArgumentsExpression {
                            coneTypeOrNull = moduleDataArrayType.coneType
                            coneElementTypeOrNull = moduleDataType
                            arguments += session.moduleData.dependencies.mapNotNull {
                                getModuleDataReference(it.name)
                            }
                        }
                    }, LinkedHashMap())
                }
            },
            generateSimpleProperty(moduleDataSymbolTableDataId, byteArrayType) {
                dispatchReceiverType = owner.defaultType()
                initializer = buildArrayLiteral {
                    // This is filled in during the IR lowering after monomorphization
                    coneTypeOrNull = byteArraySymbol.defaultType()
                    argumentList = FirEmptyArgumentList
                }
            }
        ) // @formatter:on
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        return when (classId) {
            monoFunctionClassId -> generateSimpleObject(monoFunctionClassId)
            moduleDataClassId -> generateSimpleObject(moduleDataClassId) {
                // The generated module data object needs to implement ModuleData to bind against the runtime API
                val moduleDataClass = session.getRegularClassSymbolByClassId(KWireNames.ModuleData.id) ?: return null
                superTypeRefs += moduleDataClass.defaultType().toFirResolvedTypeRef()
            }

            else -> null
        }
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val owner = context.owner
        return when (owner.classId) { // @formatter:off
            monoFunctionClassId,
            moduleDataClassId -> listOf(createDefaultPrivateConstructor(owner, KWireFirGenerationExtension).symbol)
            else -> emptyList()
        } // @formatter:on
    }

    override fun generateProperties(
        callableId: CallableId, context: MemberGenerationContext?
    ): List<FirPropertySymbol> {
        val owner = context?.owner ?: return emptyList()
        return when (owner.classId) {
            moduleDataClassId -> generateModuleDataProperties(owner)
            else -> emptyList()
        }
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return when (classSymbol.classId) { // @formatter:off
            monoFunctionClassId -> setOf(SpecialNames.INIT)
            moduleDataClassId -> setOf(
                SpecialNames.INIT,
                ABIConstants.moduleDataNameName,
                ABIConstants.moduleDataDependenciesName
            )
            else -> emptySet()
        } // @formatter:on
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        return setOf(monoFunctionClassId, moduleDataClassId)
    }
}