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

import java.lang.invoke.MethodHandle
import java.util.concurrent.ConcurrentHashMap
import java.lang.foreign.Linker as JvmLinker

internal object PanamaFFI : FFI {
    private val handleCache: ConcurrentHashMap<Pointer, MethodHandle> = ConcurrentHashMap()

    internal fun getHandle(address: Pointer, descriptor: FFIDescriptor): MethodHandle {
        var handle = handleCache[address]
        if (handle == null) {
            handle = JvmLinker.nativeLinker()
                .downcallHandle(address.toMemorySegment(), descriptor.toFunctionDescriptor())
            handleCache[address] = handle
        }
        // TODO: Improve error message, possibly including function name
        return requireNotNull(handle) { "Could not obtain method handle for native function at $address" }
    }

    override fun call(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec) {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        getHandle(address, descriptor).invokeWithArguments(*buffer.toArray())
    }

    override fun callByte(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Byte {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Byte
    }

    override fun callShort(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Short {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Short
    }

    override fun callInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Int {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Int
    }

    override fun callLong(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Long {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Long
    }

    override fun callNInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): NInt {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return (getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Long).toNInt()
    }

    override fun callFloat(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Float {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Float
    }

    override fun callDouble(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec): Double {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Double
    }
}

internal actual fun getPlatformFFI(): FFI = PanamaFFI