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

@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire.ffi

import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemoryLayout

/**
 * Converts the parameter types of this [dev.karmakrafts.kwire.ffi.FFIDescriptor] to an array of [MemoryLayout]s.
 *
 * This extension function maps each parameter type of this descriptor to its corresponding
 * memory layout and returns them as an array.
 *
 * @param useSegments When true, pointer types will use ValueLayout.ADDRESS which is appropriate for memory segments.
 *                    When false, they will use JAVA_INT or JAVA_LONG based on the platform's pointer size.
 * @return An array of [MemoryLayout]s representing the memory layouts of the parameter types.
 */
inline fun FFIDescriptor.toArgumentMemoryLayouts(useSegments: Boolean = false): Array<MemoryLayout> {
    return parameterTypes.map { it.getMemoryLayout(useSegments) }.toTypedArray()
}

/**
 * Converts the return type of this [FFIDescriptor] to a [MemoryLayout].
 *
 * This extension function maps the return type of this descriptor to its corresponding
 * memory layout.
 *
 * @param useSegments When true, pointer types will use ValueLayout.ADDRESS which is appropriate for memory segments.
 *                    When false, they will use JAVA_INT or JAVA_LONG based on the platform's pointer size.
 * @return A [MemoryLayout] representing the memory layout of the return type.
 */
inline fun FFIDescriptor.toResultMemoryLayout(useSegments: Boolean = false): MemoryLayout {
    return returnType.getMemoryLayout(useSegments)
}

/**
 * Converts this [FFIDescriptor] to a Java [FunctionDescriptor].
 *
 * This extension function creates a function descriptor that can be used with the Java
 * Foreign Function Interface. If the return type is VOID, it creates a void function descriptor;
 * otherwise, it creates a function descriptor with the specified return type and parameter types.
 *
 * @param useSegments When true, pointer types will use ValueLayout.ADDRESS which is appropriate for memory segments.
 *                    When false, they will use JAVA_INT or JAVA_LONG based on the platform's pointer size.
 * @return A [FunctionDescriptor] that represents this FFI descriptor.
 */
inline fun FFIDescriptor.toFunctionDescriptor(useSegments: Boolean = false): FunctionDescriptor {
    return if (returnType == FFIType.VOID) {
        FunctionDescriptor.ofVoid(*toArgumentMemoryLayouts(useSegments))
    }
    else {
        FunctionDescriptor.of(toResultMemoryLayout(useSegments), *toArgumentMemoryLayouts(useSegments))
    }
}
