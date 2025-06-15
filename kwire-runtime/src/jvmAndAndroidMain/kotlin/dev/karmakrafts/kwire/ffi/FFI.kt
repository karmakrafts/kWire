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

// @formatter:off
@file:JvmName("FFIImpl")
// @formatter:on

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asVoidPtr
import dev.karmakrafts.kwire.ctype.longValue
import dev.karmakrafts.kwire.ctype.plus
import dev.karmakrafts.kwire.ctype.toMemorySegment
import dev.karmakrafts.kwire.ctype.toNInt
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.memory.Memory
import java.lang.invoke.MethodHandle
import java.lang.foreign.Linker as JvmLinker

internal object PanamaFFI : FFI {
    fun FFIArgBuffer.toArray(): Array<Any> {
        var offset = 0.toNUInt()
        return Array(types.size) { index ->
            val type = types[index]
            val value: Any = when (type) {
                FFIType.BYTE, FFIType.UBYTE -> Memory.readByte(address + offset)
                FFIType.SHORT, FFIType.USHORT -> Memory.readShort(address + offset)
                FFIType.INT, FFIType.UINT -> Memory.readInt(address + offset)
                FFIType.LONG, FFIType.ULONG -> Memory.readLong(address + offset)
                FFIType.NINT, FFIType.NUINT -> Memory.readNInt(address + offset).longValue
                FFIType.FLOAT -> Memory.readFloat(address + offset)
                FFIType.DOUBLE -> Memory.readDouble(address + offset)
                FFIType.PTR -> Memory.readPointer(address + offset).rawAddress.value.longValue
                else -> throw IllegalStateException("Cannot map FFI parameter type $type")
            }
            offset += type.size.toNUInt()
            value
        }
    }

    internal fun getHandle(address: Address, descriptor: FFIDescriptor, useSegments: Boolean = false): MethodHandle {
        return JvmLinker.nativeLinker()
            .downcallHandle(address.toMemorySegment(), descriptor.toFunctionDescriptor(useSegments))
    }

    override fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec) {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        getHandle(address, descriptor).invokeWithArguments(*buffer.toArray())
    }

    override fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Byte {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Byte
    }

    override fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Short {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Short
    }

    override fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Int {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Int
    }

    override fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Long {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Long
    }

    override fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): NInt {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return if (Address.SIZE_BYTES == Int.SIZE_BYTES) {
            (getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Int).toNInt()
        }
        else {
            (getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Long).toNInt()
        }
    }

    override fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Float {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Float
    }

    override fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Double {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Double
    }

    override fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): VoidPtr {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return (getHandle(address, descriptor).invokeWithArguments(*buffer.toArray()) as Long).asVoidPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = PanamaFFI