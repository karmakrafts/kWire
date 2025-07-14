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

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.scopes.kotlinScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

internal class KWireFirGenerationExtension( // @formatter:off
    session: FirSession,
    private val messageCollector: MessageCollector
) : FirDeclarationGenerationExtension(session) { // @formatter:on
    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        return setOf(KWireModuleContext.classId)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        if (classSymbol.classId == KWireModuleContext.classId) {
            return setOf(SpecialNames.INIT)
        }
        return emptySet()
    }

    private fun createModuleClass(): FirClassLikeSymbol<*> = buildRegularClass {
        name = KWireModuleContext.className
        origin = KWireModuleContext.firDeclOrigin
        moduleData = session.moduleData
        classKind = ClassKind.OBJECT
        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Internal, Modality.FINAL, EffectiveVisibility.Internal
        )
        symbol = FirRegularClassSymbol(KWireModuleContext.classId)
        scopeProvider = session.kotlinScopeProvider
    }.symbol

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        return when (classId) {
            KWireModuleContext.classId -> createModuleClass()
            else -> null
        }
    }

    private fun createModuleClassConstructor(owner: FirClassSymbol<*>): List<FirConstructorSymbol> {
        return listOf(createDefaultPrivateConstructor(owner, KWireModuleContext).symbol)
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val owner = context.owner
        return when (owner.classId) {
            KWireModuleContext.classId -> createModuleClassConstructor(owner)
            else -> emptyList()
        }
    }
}