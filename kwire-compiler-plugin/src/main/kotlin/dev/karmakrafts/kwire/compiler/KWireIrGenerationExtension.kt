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

import dev.karmakrafts.kwire.compiler.checker.ConstChecker
import dev.karmakrafts.kwire.compiler.checker.PtrCFnChecker
import dev.karmakrafts.kwire.compiler.checker.PtrCVoidChecker
import dev.karmakrafts.kwire.compiler.checker.StructChecker
import dev.karmakrafts.kwire.compiler.checker.ValueTypeChecker
import dev.karmakrafts.kwire.compiler.optimizer.JvmDowncallOptimizer
import dev.karmakrafts.kwire.compiler.optimizer.NativeDowncallOptimizer
import dev.karmakrafts.kwire.compiler.optimizer.PtrOptimizer
import dev.karmakrafts.kwire.compiler.transformer.AllocatorIntrinsicsTransformer
import dev.karmakrafts.kwire.compiler.transformer.IntrinsicContext
import dev.karmakrafts.kwire.compiler.transformer.MemoryIntrinsicsTransformer
import dev.karmakrafts.kwire.compiler.transformer.MemoryLayoutTransformer
import dev.karmakrafts.kwire.compiler.transformer.PtrIntrinsicsTransformer
import dev.karmakrafts.kwire.compiler.transformer.SharedImportTransformer
import dev.karmakrafts.kwire.compiler.transformer.TemplateTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.platform.konan.NativePlatforms

internal class KWireIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val kwireSymbols = KWireSymbols(pluginContext)
        val kwireModuleData = KWireModuleData(pluginContext, kwireSymbols, moduleFragment)

        // Checker pass over all files
        var checkerFailed = false
        for (file in moduleFragment.files) {
            val kwireContext = KWirePluginContext(pluginContext, moduleFragment, file, kwireSymbols, kwireModuleData)
            // Validation
            file.acceptVoid(StructChecker(kwireContext))
            file.accept(ConstChecker(kwireContext), null)
            file.accept(PtrCVoidChecker(kwireContext), null)
            file.accept(PtrCFnChecker(kwireContext), null)
            file.accept(ValueTypeChecker(kwireContext), null)
            checkerFailed = checkerFailed or kwireContext.checkerFailed
        }
        if (checkerFailed) return

        // Expand templates
        for (file in moduleFragment.files) {
            val kwireContext = KWirePluginContext(pluginContext, moduleFragment, file, kwireSymbols, kwireModuleData)
            file.transform(TemplateTransformer(kwireContext), kwireContext)
        }

        // Intrinsic lowering and optimization
        for (file in moduleFragment.files) {
            val kwireContext = KWirePluginContext(pluginContext, moduleFragment, file, kwireSymbols, kwireModuleData)
            // Generation
            file.acceptVoid(SharedImportTransformer(kwireContext))
            file.acceptVoid(MemoryLayoutTransformer(kwireContext))

            // Optimization pre-processing passes
            file.transform(PtrOptimizer(kwireContext), kwireContext)

            // Intrinsics lowering
            val intrinsicContext = IntrinsicContext(kwireContext)
            file.transform(MemoryIntrinsicsTransformer(kwireContext), intrinsicContext)
            file.transform(PtrIntrinsicsTransformer(kwireContext), intrinsicContext)
            file.transform(AllocatorIntrinsicsTransformer(kwireContext), intrinsicContext)

            // Optimization post-processing passes
            when (pluginContext.platform) {
                in JvmPlatforms.allJvmPlatforms -> {
                    file.transform(JvmDowncallOptimizer(), kwireContext)
                }

                in NativePlatforms.allNativePlatforms -> {
                    file.transform(NativeDowncallOptimizer(), kwireContext)
                }
            }
        }
    }
}