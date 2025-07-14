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

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * A per-translation-unit module context which holds synthetic classes
 * and members which may be re-used across multiple files.
 */
internal class KWireModuleContext( // @formatter:off
    private val context: IrPluginContext,
    private val kwireSymbols: KWireSymbols,
    private val module: IrModuleFragment
) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        val firDeclOrigin: FirDeclarationOrigin = FirDeclarationOrigin.Plugin(this)
        val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
        val className: Name = Name.identifier("__KWireModuleClass__")
        val classId: ClassId = ClassId(FqName.ROOT, className)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val moduleClass: IrClass = context.referenceClass(classId)!!.owner
}