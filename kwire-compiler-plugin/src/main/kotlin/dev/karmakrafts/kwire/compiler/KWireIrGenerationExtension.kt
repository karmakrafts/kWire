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

import dev.karmakrafts.kwire.compiler.transformer.KWireIntrinsicContext
import dev.karmakrafts.kwire.compiler.transformer.MemoryIntrinsicsTransformer
import dev.karmakrafts.kwire.compiler.transformer.MemoryLayoutTransformer
import dev.karmakrafts.kwire.compiler.transformer.PtrIntrinsicsTransformer
import dev.karmakrafts.kwire.compiler.transformer.SharedImportTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid

internal class KWireIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val kwireContext = KWirePluginContext(pluginContext)
        // Generate all FFI/native related code before lowering intrinsics etc.
        moduleFragment.acceptVoid(SharedImportTransformer(kwireContext))
        // Pre-compute all struct layouts and export them using @StructLayout
        moduleFragment.acceptVoid(MemoryLayoutTransformer(kwireContext))
        // Transform all intrinsics
        val intrinsicContext = KWireIntrinsicContext()
        moduleFragment.transform(MemoryIntrinsicsTransformer(kwireContext), intrinsicContext)
        moduleFragment.transform(PtrIntrinsicsTransformer(kwireContext), intrinsicContext)
    }
}