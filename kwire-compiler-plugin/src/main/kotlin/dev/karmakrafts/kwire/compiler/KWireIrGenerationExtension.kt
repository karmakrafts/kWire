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

import dev.karmakrafts.kwire.compiler.checker.ConstValidationVisitor
import dev.karmakrafts.kwire.compiler.checker.StructValidationVisitor
import dev.karmakrafts.kwire.compiler.transformer.IntrinsicContext
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
        for (file in moduleFragment.files) {
            val kwireContext = KWirePluginContext(pluginContext, moduleFragment, file)
            // Validation
            file.acceptVoid(StructValidationVisitor(kwireContext))
            file.acceptVoid(ConstValidationVisitor(kwireContext))
            // Generation
            file.acceptVoid(SharedImportTransformer(kwireContext))
            file.acceptVoid(MemoryLayoutTransformer(kwireContext))
            // Intrinsics lowering
            val intrinsicContext = IntrinsicContext(kwireContext)
            file.transform(MemoryIntrinsicsTransformer(kwireContext), intrinsicContext)
            file.transform(PtrIntrinsicsTransformer(kwireContext), intrinsicContext)
        }
    }
}