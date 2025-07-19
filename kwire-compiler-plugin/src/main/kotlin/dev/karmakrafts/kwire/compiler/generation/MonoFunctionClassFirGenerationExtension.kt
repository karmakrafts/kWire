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

import dev.karmakrafts.kwire.compiler.util.buildSimpleObject
import dev.karmakrafts.kwire.compiler.util.getABIFriendlyName
import dev.karmakrafts.kwire.compiler.util.getCleanSpecialName
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

internal class MonoFunctionClassFirGenerationExtension( // @formatter:off
    session: FirSession,
    private val messageCollector: MessageCollector
) : FirDeclarationGenerationExtension(session) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        val declOrigin: FirDeclarationOrigin = FirDeclarationOrigin.Plugin(this)
    }

    private val moduleName: String = session.moduleData.name.getABIFriendlyName()

    private val monoFunctionClassName: Name = Name.identifier("__KWireMonoFunctions\$${moduleName}__")
    private val monoFunctionClassId: ClassId = ClassId.topLevel(FqName.topLevel(monoFunctionClassName))

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        return when (classId) {
            monoFunctionClassId -> session.buildSimpleObject(monoFunctionClassId) {
                origin = declOrigin
            }

            else -> null
        }
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val owner = context.owner
        return when (owner.classId) { // @formatter:off
            monoFunctionClassId -> listOf(createDefaultPrivateConstructor(owner, MonoFunctionClassFirGenerationExtension).symbol)
            else -> emptyList()
        } // @formatter:on
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return when (classSymbol.classId) { // @formatter:off
            monoFunctionClassId -> setOf(SpecialNames.INIT)
            else -> emptySet()
        } // @formatter:on
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        val moduleData = session.moduleData
        return if (moduleData.isCommon) emptySet()
        else setOf(monoFunctionClassId)
    }
}