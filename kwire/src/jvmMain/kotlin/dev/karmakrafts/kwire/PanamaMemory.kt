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

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.foreign.Linker as JvmLinker

private object PanamaMemory : Memory {
    override val defaultAlignment: NUInt = 8U.toNUInt() // TODO: read from system

    private val runtimeLookup: SymbolLookup = SymbolLookup.libraryLookup("", Arena.global())

    private fun getRuntimeFunction(name: String, returnType: FFIType, vararg paramTypes: FFIType): MethodHandle {
        return runtimeLookup
            .find(name)
            .map { segment ->
                val descriptor = FFIDescriptor(returnType, *paramTypes).toFunctionDescriptor()
                JvmLinker.nativeLinker().downcallHandle(segment, descriptor)
            }
            .orElseThrow { IllegalStateException("Could not load '$name' runtime function") }
    }

    private val malloc: MethodHandle = getRuntimeFunction("malloc", FFIType.NINT, FFIType.NINT)

    //private val realloc: MethodHandle = getRuntimeFunction("realloc", FFIType.NINT, FFIType.NINT, FFIType.NINT)
    private val free: MethodHandle = getRuntimeFunction("free", FFIType.VOID, FFIType.NINT)
    //private val memset: MethodHandle =
    //    getRuntimeFunction("memset", FFIType.NINT, FFIType.NINT, FFIType.INT, FFIType.NINT)
    //private val memcpy: MethodHandle =
    //    getRuntimeFunction("memcpy", FFIType.NINT, FFIType.NINT, FFIType.NINT, FFIType.NINT)
    //private val memmove: MethodHandle =
    //    getRuntimeFunction("memmove", FFIType.NINT, FFIType.NINT, FFIType.NINT, FFIType.NINT)
    //private val memcmp: MethodHandle =
    //    getRuntimeFunction("memcmp", FFIType.INT, FFIType.NINT, FFIType.NINT, FFIType.NINT)

    override fun allocate(size: NUInt, alignment: NUInt): Pointer {
        // TODO: implement alignment
        return Pointer((malloc.invokeExact(size.value) as Long).toNUInt())
    }

    override fun reallocate(address: Pointer, size: NUInt, alignment: NUInt): Pointer {
        // TODO: implement alignment
        //return Pointer((realloc.invoke(address.value.value, size.value) as Long).toNUInt())
        return address
    }

    override fun free(address: Pointer) {
        free.invokeExact(address.value.value)
    }

    override fun set(address: Pointer, value: Byte, size: NUInt) {
        //memset.invoke(address.value.value, value.toInt(), size.value)
    }

    override fun copy(source: Pointer, dest: Pointer, size: NUInt) {
        //memcpy.invoke(dest.value.value, source.value.value, size.value)
    }

    override fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt) {
        //memmove.invoke(dest.value.value, source.value.value, size.value)
    }

    override fun compare(first: Pointer, second: Pointer, size: NUInt): Int {
        //return memcmp.invoke(first.value.value, second.value.value, size.value) as Int
        return 0
    }

    override fun readByte(address: Pointer): Byte {
        return address.toMemorySegment().get(ValueLayout.JAVA_BYTE, 0L)
    }

    override fun readShort(address: Pointer): Short {
        return address.toMemorySegment().get(ValueLayout.JAVA_SHORT_UNALIGNED, 0L)
    }

    override fun readInt(address: Pointer): Int {
        return address.toMemorySegment().get(ValueLayout.JAVA_INT_UNALIGNED, 0L)
    }

    override fun readLong(address: Pointer): Long {
        return address.toMemorySegment().get(ValueLayout.JAVA_LONG_UNALIGNED, 0L)
    }

    override fun readNInt(address: Pointer): NInt {
        return address.toMemorySegment().get(ValueLayout.ADDRESS_UNALIGNED, 0L).address().toNInt()
    }

    override fun readFloat(address: Pointer): Float {
        return address.toMemorySegment().get(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L)
    }

    override fun readDouble(address: Pointer): Double {
        return address.toMemorySegment().get(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L)
    }

    override fun readBytes(address: Pointer, size: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun writeByte(address: Pointer, value: Byte) {
        address.toMemorySegment().set(ValueLayout.JAVA_BYTE, 0L, value)
    }

    override fun writeShort(address: Pointer, value: Short) {
        address.toMemorySegment().set(ValueLayout.JAVA_SHORT_UNALIGNED, 0L, value)
    }

    override fun writeInt(address: Pointer, value: Int) {
        address.toMemorySegment().set(ValueLayout.JAVA_INT_UNALIGNED, 0L, value)
    }

    override fun writeLong(address: Pointer, value: Long) {
        address.toMemorySegment().set(ValueLayout.JAVA_LONG_UNALIGNED, 0L, value)
    }

    override fun writeNInt(address: Pointer, value: NInt) {
        address.toMemorySegment().set(ValueLayout.ADDRESS_UNALIGNED, 0L, MemorySegment.ofAddress(value))
    }

    override fun writeFloat(address: Pointer, value: Float) {
        address.toMemorySegment().set(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L, value)
    }

    override fun writeDouble(address: Pointer, value: Double) {
        address.toMemorySegment().set(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L, value)
    }

    override fun writeBytes(address: Pointer, data: ByteArray) {
        TODO("Not yet implemented")
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = PanamaMemory