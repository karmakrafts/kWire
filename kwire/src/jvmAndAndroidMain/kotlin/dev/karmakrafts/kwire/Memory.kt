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

@file:JvmName("MemoryImpl")

package dev.karmakrafts.kwire

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private object PanamaMemory : Memory {
    override val defaultAlignment: NUInt = if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) 4U.toNUInt() else 16U.toNUInt()

    // @formatter:off
    private val malloc: MethodHandle =
        SharedLibrary.cRuntime["malloc", FFIType.PTR, FFIType.NUINT].toMethodHandle(true)
    private val realloc: MethodHandle =
        SharedLibrary.cRuntime["realloc", FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle(true)
    private val free: MethodHandle =
        SharedLibrary.cRuntime["free", FFIType.VOID, FFIType.PTR].toMethodHandle(true)
    private val memcpy: MethodHandle =
        SharedLibrary.cRuntime["memcpy", FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle(true)
    private val memmove: MethodHandle =
        SharedLibrary.cRuntime["memmove", FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle(true)
    private val memcmp: MethodHandle =
        SharedLibrary.cRuntime["memcmp", FFIType.INT, FFIType.PTR, FFIType.PTR, FFIType.NUINT].toMethodHandle(true)
    private val strlen: MethodHandle =
        SharedLibrary.cRuntime["strlen", FFIType.NUINT, FFIType.PTR].toMethodHandle(true)
    private val strcpy: MethodHandle =
        SharedLibrary.cRuntime["strcpy", FFIType.PTR, FFIType.PTR, FFIType.PTR].toMethodHandle(true)
    private val strcmp: MethodHandle =
        SharedLibrary.cRuntime["strcmp", FFIType.INT, FFIType.PTR, FFIType.PTR].toMethodHandle(true)
    // @formatter:on

    override fun allocate(size: NUInt, alignment: NUInt): Pointer {
        return (malloc.invokeExact(Memory.align(size, alignment).value) as MemorySegment).toPointer().align(alignment)
    }

    override fun reallocate(address: Pointer, size: NUInt, alignment: NUInt): Pointer {
        return (realloc.invokeExact(
            address.toMemorySegment(), Memory.align(size, alignment).value
        ) as MemorySegment).toPointer().align(alignment)
    }

    override fun free(address: Pointer) {
        free.invokeExact(address.toMemorySegment())
    }

    override fun set(address: Pointer, value: Byte, size: NUInt) {
        address.toMemorySegment(size).fill(value)
    }

    override fun copy(source: Pointer, dest: Pointer, size: NUInt) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        memcpy.invokeExact(dest.toMemorySegment(), source.toMemorySegment(), size.value) as MemorySegment
    }

    override fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        memmove.invokeExact(dest.toMemorySegment(), source.toMemorySegment(), size.value) as MemorySegment
    }

    override fun compare(first: Pointer, second: Pointer, size: NUInt): Int {
        return memcmp.invokeExact(first.toMemorySegment(), second.toMemorySegment(), size.value) as Int
    }

    override fun strlen(address: Pointer): NUInt {
        return if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            (strlen.invokeExact(address.toMemorySegment()) as Int).toNUInt()
        }
        else {
            (strlen.invokeExact(address.toMemorySegment()) as Long).toNUInt()
        }
    }

    override fun strcpy(source: Pointer, dest: Pointer) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        strcpy.invokeExact(dest.toMemorySegment(), source.toMemorySegment()) as MemorySegment
    }

    override fun strcmp(first: Pointer, second: Pointer): Int {
        return strcmp.invokeExact(first.toMemorySegment(), second.toMemorySegment()) as Int
    }

    override fun readByte(address: Pointer): Byte {
        return address.toMemorySegment(Byte.SIZE_BYTES).get(ValueLayout.JAVA_BYTE, 0L)
    }

    override fun readShort(address: Pointer): Short {
        return address.toMemorySegment(Short.SIZE_BYTES).get(ValueLayout.JAVA_SHORT_UNALIGNED, 0L)
    }

    override fun readInt(address: Pointer): Int {
        return address.toMemorySegment(Int.SIZE_BYTES).get(ValueLayout.JAVA_INT_UNALIGNED, 0L)
    }

    override fun readLong(address: Pointer): Long {
        return address.toMemorySegment(Long.SIZE_BYTES).get(ValueLayout.JAVA_LONG_UNALIGNED, 0L)
    }

    override fun readNInt(address: Pointer): NInt {
        val segment = address.toMemorySegment(Pointer.SIZE_BYTES)
        return if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.get(ValueLayout.JAVA_INT_UNALIGNED, 0L).toNInt()
        }
        else {
            segment.get(ValueLayout.JAVA_LONG_UNALIGNED, 0L).toNInt()
        }
    }

    override fun readPointer(address: Pointer): Pointer {
        return address.toMemorySegment(Pointer.SIZE_BYTES).get(AddressLayout.ADDRESS_UNALIGNED, 0L).toPointer()
    }

    override fun readFloat(address: Pointer): Float {
        return address.toMemorySegment(Float.SIZE_BYTES).get(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L)
    }

    override fun readDouble(address: Pointer): Double {
        return address.toMemorySegment(Double.SIZE_BYTES).get(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L)
    }

    override fun readBytes(address: Pointer, data: ByteArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Byte.SIZE_BYTES), ValueLayout.JAVA_BYTE, 0L, data, dataStart, size
        )
    }

    override fun readShorts(address: Pointer, data: ShortArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Short.SIZE_BYTES),
            ValueLayout.JAVA_SHORT_UNALIGNED,
            0L,
            data,
            dataStart,
            size
        )
    }

    override fun readInts(address: Pointer, data: IntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Int.SIZE_BYTES), ValueLayout.JAVA_INT_UNALIGNED, 0L, data, dataStart, size
        )
    }

    override fun readLongs(address: Pointer, data: LongArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Long.SIZE_BYTES), ValueLayout.JAVA_LONG_UNALIGNED, 0L, data, dataStart, size
        )
    }

    override fun readNInts(address: Pointer, data: NIntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                address.toMemorySegment(size * Int.SIZE_BYTES),
                ValueLayout.JAVA_INT_UNALIGNED,
                0L,
                data,
                dataStart,
                size
            )
        }
        else {
            MemorySegment.copy(
                address.toMemorySegment(size * Long.SIZE_BYTES),
                ValueLayout.JAVA_LONG_UNALIGNED,
                0L,
                data,
                dataStart,
                size
            )
        }
    }

    override fun readFloats(address: Pointer, data: FloatArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Float.SIZE_BYTES),
            ValueLayout.JAVA_FLOAT_UNALIGNED,
            0L,
            data,
            dataStart,
            size
        )
    }

    override fun readDoubles(address: Pointer, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Double.SIZE_BYTES),
            ValueLayout.JAVA_DOUBLE_UNALIGNED,
            0L,
            data,
            dataStart,
            size
        )
    }

    override fun readPointers(address: Pointer, data: PointerArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                address.toMemorySegment(size * Int.SIZE_BYTES),
                ValueLayout.JAVA_INT_UNALIGNED,
                0L,
                data.value.value,
                dataStart,
                size
            )
        }
        else {
            MemorySegment.copy(
                address.toMemorySegment(size * Long.SIZE_BYTES),
                ValueLayout.JAVA_LONG_UNALIGNED,
                0L,
                data.value.value,
                dataStart,
                size
            )
        }
    }

    override fun writeByte(address: Pointer, value: Byte) {
        address.toMemorySegment(Byte.SIZE_BYTES).set(ValueLayout.JAVA_BYTE, 0L, value)
    }

    override fun writeShort(address: Pointer, value: Short) {
        address.toMemorySegment(Short.SIZE_BYTES).set(ValueLayout.JAVA_SHORT_UNALIGNED, 0L, value)
    }

    override fun writeInt(address: Pointer, value: Int) {
        address.toMemorySegment(Int.SIZE_BYTES).set(ValueLayout.JAVA_INT_UNALIGNED, 0L, value)
    }

    override fun writeLong(address: Pointer, value: Long) {
        address.toMemorySegment(Long.SIZE_BYTES).set(ValueLayout.JAVA_LONG_UNALIGNED, 0L, value)
    }

    override fun writeNInt(address: Pointer, value: NInt) {
        val segment = address.toMemorySegment(Pointer.SIZE_BYTES)
        return if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.set(ValueLayout.JAVA_INT_UNALIGNED, 0L, value.intValue)
        }
        else {
            segment.set(ValueLayout.JAVA_LONG_UNALIGNED, 0L, value.longValue)
        }
    }

    override fun writePointer(address: Pointer, value: Pointer) {
        address.toMemorySegment(Pointer.SIZE_BYTES).set(ValueLayout.ADDRESS_UNALIGNED, 0L, value.toMemorySegment())
    }

    override fun writeFloat(address: Pointer, value: Float) {
        address.toMemorySegment(Float.SIZE_BYTES).set(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L, value)
    }

    override fun writeDouble(address: Pointer, value: Double) {
        address.toMemorySegment(Double.SIZE_BYTES).set(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L, value)
    }

    override fun writeBytes(address: Pointer, data: ByteArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Byte.SIZE_BYTES), ValueLayout.JAVA_BYTE, 0L, size
        )
    }

    override fun writeShorts(address: Pointer, data: ShortArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data,
            dataStart,
            address.toMemorySegment(size * Short.SIZE_BYTES),
            ValueLayout.JAVA_SHORT_UNALIGNED,
            0L,
            size
        )
    }

    override fun writeInts(address: Pointer, data: IntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Int.SIZE_BYTES), ValueLayout.JAVA_INT_UNALIGNED, 0L, size
        )
    }

    override fun writeLongs(address: Pointer, data: LongArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Long.SIZE_BYTES), ValueLayout.JAVA_LONG_UNALIGNED, 0L, size
        )
    }

    override fun writeNInts(address: Pointer, data: NIntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                data,
                dataStart,
                address.toMemorySegment(size * Int.SIZE_BYTES),
                ValueLayout.JAVA_INT_UNALIGNED,
                0L,
                size
            )
        }
        else {
            MemorySegment.copy(
                data,
                dataStart,
                address.toMemorySegment(size * Long.SIZE_BYTES),
                ValueLayout.JAVA_LONG_UNALIGNED,
                0L,
                size
            )
        }
    }

    override fun writeFloats(address: Pointer, data: FloatArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data,
            dataStart,
            address.toMemorySegment(size * Float.SIZE_BYTES),
            ValueLayout.JAVA_FLOAT_UNALIGNED,
            0L,
            size
        )
    }

    override fun writeDoubles(address: Pointer, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data,
            dataStart,
            address.toMemorySegment(size * Double.SIZE_BYTES),
            ValueLayout.JAVA_DOUBLE_UNALIGNED,
            0L,
            size
        )
    }

    override fun writePointers(address: Pointer, data: PointerArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                data.value.value,
                dataStart,
                address.toMemorySegment(size * Int.SIZE_BYTES),
                ValueLayout.JAVA_INT_UNALIGNED,
                0L,
                size
            )
        }
        else {
            MemorySegment.copy(
                data.value.value,
                dataStart,
                address.toMemorySegment(size * Long.SIZE_BYTES),
                ValueLayout.JAVA_LONG_UNALIGNED,
                0L,
                size
            )
        }
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = PanamaMemory