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

import dev.karmakrafts.iridium.CompilerTestDsl
import dev.karmakrafts.iridium.CompilerTestScope
import dev.karmakrafts.iridium.pipeline.addJvmClasspathRootByType
import dev.karmakrafts.iridium.pipeline.defaultPipelineSpec
import dev.karmakrafts.kwire.abi.ABI
import dev.karmakrafts.kwire.compiler.generation.LoweringIrGenerationExtension
import dev.karmakrafts.kwire.ffi.Marshal
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.jvmTarget

internal val signedTypeNames: List<String> = listOf("Byte", "Short", "Int", "Long")
internal val unsignedTypeNames: List<String> = listOf("UByte", "UShort", "UInt", "ULong")
internal val nativeTypeNames: List<String> = listOf("NInt", "NUInt", "NFloat")
internal val primitiveTypeNames: List<String> = signedTypeNames + unsignedTypeNames + nativeTypeNames

@CompilerTestDsl
internal fun CompilerTestScope.kwirePipeline(moduleName: String = "test") {
    pipeline {
        defaultPipelineSpec(moduleName)
        config {
            jvmTarget = JvmTarget.JVM_21
            addJvmClasspathRootByType<Marshal<*>>()
            addJvmClasspathRootByType<ABI>()
        }
    }
}

@CompilerTestDsl
internal fun CompilerTestScope.kwireTransformerPipeline(moduleName: String = "test") {
    kwirePipeline(moduleName)
    pipeline {
        firExtensionRegistrar(::KWireFirExtensionRegistrar)
        irExtension(LoweringIrGenerationExtension())
    }
}