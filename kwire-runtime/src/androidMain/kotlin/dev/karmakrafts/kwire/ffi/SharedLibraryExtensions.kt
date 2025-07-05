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

import com.v7878.foreign.MemorySegment
import dev.karmakrafts.kwire.ctype.toMemorySegment
import java.lang.invoke.MethodHandle

fun SharedLibrary.findFunction(name: String, descriptor: FFIDescriptor): MethodHandle? {
    val address = findFunctionAddress(name) ?: return null
    return PanamaFFI.getDowncallHandle(address, descriptor)
}

fun SharedLibrary.findFunction(name: String, returnType: FFIType, vararg paramTypes: FFIType): MethodHandle? {
    return findFunction(name, FFIDescriptor.of(returnType, *paramTypes))
}

fun SharedLibrary.getFunction(name: String, descriptor: FFIDescriptor): MethodHandle = findFunction(name, descriptor)!!

fun SharedLibrary.getFunction(name: String, returnType: FFIType, vararg paramTypes: FFIType): MethodHandle =
    getFunction(name, FFIDescriptor.of(returnType, *paramTypes))

fun SharedLibrary.findVariable(name: String): MemorySegment? = findFunctionAddress(name)?.toMemorySegment()
fun SharedLibrary.getVariable(name: String): MemorySegment = findVariable(name)!!