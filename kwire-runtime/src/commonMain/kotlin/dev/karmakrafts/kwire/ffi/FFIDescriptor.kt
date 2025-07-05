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

package dev.karmakrafts.kwire.ffi

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.karmakrafts.kwire.KWireCompilerApi

/**
 * Descriptor for a foreign function interface (FFI) function signature.
 *
 * @property returnType The return type of the function, defaults to [FFIType.VOID]
 * @property parameterTypes List of parameter types for the function, defaults to an empty list
 */
@ConsistentCopyVisibility
@KWireCompilerApi
data class FFIDescriptor private constructor( // @formatter:off
    val returnType: FFIType,
    val parameterTypes: List<FFIType>
) { // @formatter:on
    companion object {
        private val cache: ConcurrentMutableMap<Int, FFIDescriptor> = ConcurrentMutableMap()

        private fun getCacheKey(returnType: FFIType, parameterTypes: List<FFIType>): Int {
            var hash = returnType.hashCode()
            hash = 31 * hash + parameterTypes.hashCode()
            return hash
        }

        fun of(returnType: FFIType, parameterTypes: List<FFIType>): FFIDescriptor {
            return cache.getOrPut(getCacheKey(returnType, parameterTypes)) {
                FFIDescriptor(returnType, parameterTypes)
            }
        }

        @KWireCompilerApi
        fun of(returnType: FFIType, vararg parameterTypes: FFIType): FFIDescriptor {
            return of(returnType, parameterTypes.asList())
        }
    }
}
