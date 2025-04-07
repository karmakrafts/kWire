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

package dev.karmakrafts.kwire

import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemoryLayout

/**
 * Converts the parameter types of this [FFIDescriptor] to an array of [MemoryLayout]s.
 *
 * This extension function maps each parameter type of this descriptor to its corresponding
 * memory layout and returns them as an array.
 *
 * @return An array of [MemoryLayout]s representing the memory layouts of the parameter types.
 */
fun FFIDescriptor.toArgumentMemoryLayouts(): Array<MemoryLayout> {
    return parameterTypes.map { it.getMemoryLayout() }.toTypedArray()
}

/**
 * Converts the return type of this [FFIDescriptor] to a [MemoryLayout].
 *
 * This extension function maps the return type of this descriptor to its corresponding
 * memory layout.
 *
 * @return A [MemoryLayout] representing the memory layout of the return type.
 */
fun FFIDescriptor.toResultMemoryLayout(): MemoryLayout {
    return returnType.getMemoryLayout()
}

/**
 * Converts this [FFIDescriptor] to a Java [FunctionDescriptor].
 *
 * This extension function creates a function descriptor that can be used with the Java
 * Foreign Function Interface. If the return type is VOID, it creates a void function descriptor;
 * otherwise, it creates a function descriptor with the specified return type and parameter types.
 *
 * @return A [FunctionDescriptor] that represents this FFI descriptor.
 */
fun FFIDescriptor.toFunctionDescriptor(): FunctionDescriptor {
    return if (returnType == FFIType.VOID) {
        FunctionDescriptor.ofVoid(*toArgumentMemoryLayouts())
    } else {
        FunctionDescriptor.of(toResultMemoryLayout(), *toArgumentMemoryLayouts())
    }
}
