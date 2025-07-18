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

package dev.karmakrafts.kwire.compiler.util

import dev.karmakrafts.kwire.compiler.generation.MonoFunctionClassFirGenerationExtension.Companion.declOrigin
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.builder.FirPropertyBuilder
import org.jetbrains.kotlin.fir.declarations.builder.FirRegularClassBuilder
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.moduleData
import org.jetbrains.kotlin.fir.scopes.kotlinScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertyAccessorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId

internal inline fun FirSession.buildSimpleProperty( // @formatter:off
    id: CallableId,
    type: FirTypeRef,
    init: FirPropertyBuilder.() -> Unit = {}
): FirPropertySymbol = buildProperty { // @formatter:on
    name = id.callableName
    status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
    moduleData = this@buildSimpleProperty.moduleData
    symbol = FirPropertySymbol(id)
    returnTypeRef = type
    isVar = false
    isLocal = false
    init()
    getter = buildPropertyAccessor {
        origin = this@buildProperty.origin
        symbol = FirPropertyAccessorSymbol()
        moduleData = this@buildSimpleProperty.moduleData
        propertySymbol = this@buildProperty.symbol
        status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
        returnTypeRef = type
        isGetter = true
    }
}.symbol

internal inline fun FirSession.buildSimpleObject(
    id: ClassId,
    init: FirRegularClassBuilder.() -> Unit = {}
): FirClassSymbol<*> = buildRegularClass {
    name = id.shortClassName
    classKind = ClassKind.OBJECT
    status = FirResolvedDeclarationStatusImpl(Visibilities.Public, Modality.FINAL, EffectiveVisibility.Public)
    moduleData = this@buildSimpleObject.moduleData
    scopeProvider = kotlinScopeProvider
    symbol = FirRegularClassSymbol(id)
    init()
}.symbol