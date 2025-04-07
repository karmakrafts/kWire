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

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private object PanamaMemory : Memory {
    override val defaultAlignment: NUInt = if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) 4U.toNUInt() else 16U.toNUInt()

    // @formatter:off
    private val malloc: MethodHandle =
        SharedLibrary.cRuntime["malloc", FFIType.PTR, FFIType.NUINT].toMethodHandle()
    private val realloc: MethodHandle =
        SharedLibrary.cRuntime["realloc", FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle()
    private val free: MethodHandle =
        SharedLibrary.cRuntime["free", FFIType.VOID, FFIType.PTR].toMethodHandle()
    private val memmove: MethodHandle =
        SharedLibrary.cRuntime["memmove", FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle()
    private val memcmp: MethodHandle =
        SharedLibrary.cRuntime["memcmp", FFIType.INT, FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle()
    // @formatter:on

    override fun allocate(size: NUInt, alignment: NUInt): Pointer {
        return (malloc.invokeExact(Memory.align(size, alignment).value) as MemorySegment).toPointer().align(alignment)
    }

    override fun reallocate(address: Pointer, size: NUInt, alignment: NUInt): Pointer {
        return (realloc.invokeExact(
            address.toMemorySegment(), Memory.align(size, alignment)
        ) as MemorySegment).toPointer().align(alignment)
    }

    override fun free(address: Pointer) {
        free.invokeExact(address.toMemorySegment())
    }

    override fun set(address: Pointer, value: Byte, size: NUInt) {
        address.toMemorySegment(size).fill(value)
    }

    override fun copy(source: Pointer, dest: Pointer, size: NUInt) {
        dest.toMemorySegment(size).copyFrom(source.toMemorySegment(size))
    }

    override fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        memmove.invokeExact(dest.toMemorySegment(), source.toMemorySegment(), size.value) as MemorySegment
    }

    override fun compare(first: Pointer, second: Pointer, size: NUInt): Int {
        return memcmp.invokeExact(first.toMemorySegment(), second.toMemorySegment(), size.value) as Int
    }

    override fun readByte(address: Pointer): Byte {
        return address.toMemorySegment(Byte.SIZE_BYTES).get(ValueLayout.JAVA_BYTE, 0L)
    }

    override fun readShort(address: Pointer): Short {
        return address.toMemorySegment(Short.SIZE_BYTES).get(ValueLayout.JAVA_SHORT, 0L)
    }

    override fun readInt(address: Pointer): Int {
        return address.toMemorySegment(Int.SIZE_BYTES).get(ValueLayout.JAVA_INT, 0L)
    }

    override fun readLong(address: Pointer): Long {
        return address.toMemorySegment(Long.SIZE_BYTES).get(ValueLayout.JAVA_LONG, 0L)
    }

    override fun readNInt(address: Pointer): NInt {
        return address.toMemorySegment(Pointer.SIZE_BYTES).get(ValueLayout.ADDRESS, 0L).address().toNInt()
    }

    override fun readFloat(address: Pointer): Float {
        return address.toMemorySegment(Float.SIZE_BYTES).get(ValueLayout.JAVA_FLOAT, 0L)
    }

    override fun readDouble(address: Pointer): Double {
        return address.toMemorySegment(Double.SIZE_BYTES).get(ValueLayout.JAVA_DOUBLE, 0L)
    }

    override fun readBytes(address: Pointer, size: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun readShorts(address: Pointer, size: Int): ShortArray {
        TODO("Not yet implemented")
    }

    override fun readInts(address: Pointer, size: Int): IntArray {
        TODO("Not yet implemented")
    }

    override fun readLongs(address: Pointer, size: Int): LongArray {
        TODO("Not yet implemented")
    }

    override fun readNInts(address: Pointer, size: Int): NIntArray {
        TODO("Not yet implemented")
    }

    override fun readFloats(address: Pointer, size: Int): FloatArray {
        TODO("Not yet implemented")
    }

    override fun readDoubles(address: Pointer, size: Int): DoubleArray {
        TODO("Not yet implemented")
    }

    override fun writeByte(address: Pointer, value: Byte) {
        address.toMemorySegment(Byte.SIZE_BYTES).set(ValueLayout.JAVA_BYTE, 0L, value)
    }

    override fun writeShort(address: Pointer, value: Short) {
        address.toMemorySegment(Short.SIZE_BYTES).set(ValueLayout.JAVA_SHORT, 0L, value)
    }

    override fun writeInt(address: Pointer, value: Int) {
        address.toMemorySegment(Int.SIZE_BYTES).set(ValueLayout.JAVA_INT, 0L, value)
    }

    override fun writeLong(address: Pointer, value: Long) {
        address.toMemorySegment(Long.SIZE_BYTES).set(ValueLayout.JAVA_LONG, 0L, value)
    }

    override fun writeNInt(address: Pointer, value: NInt) {
        address.toMemorySegment(Pointer.SIZE_BYTES).set(ValueLayout.ADDRESS, 0L, MemorySegment.ofAddress(value))
    }

    override fun writeFloat(address: Pointer, value: Float) {
        address.toMemorySegment(Float.SIZE_BYTES).set(ValueLayout.JAVA_FLOAT, 0L, value)
    }

    override fun writeDouble(address: Pointer, value: Double) {
        address.toMemorySegment(Double.SIZE_BYTES).set(ValueLayout.JAVA_DOUBLE, 0L, value)
    }

    override fun writeBytes(address: Pointer, data: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeShorts(address: Pointer, data: ShortArray) {
        TODO("Not yet implemented")
    }

    override fun writeInts(address: Pointer, data: IntArray) {
        TODO("Not yet implemented")
    }

    override fun writeLongs(address: Pointer, data: LongArray) {
        TODO("Not yet implemented")
    }

    override fun writeNInts(address: Pointer, data: NIntArray) {
        TODO("Not yet implemented")
    }

    override fun writeFloats(address: Pointer, data: FloatArray) {
        TODO("Not yet implemented")
    }

    override fun writeDoubles(address: Pointer, data: DoubleArray) {
        TODO("Not yet implemented")
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = PanamaMemory