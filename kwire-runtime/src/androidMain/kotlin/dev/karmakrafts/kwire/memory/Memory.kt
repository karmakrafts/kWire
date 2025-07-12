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

package dev.karmakrafts.kwire.memory

import com.v7878.foreign.AddressLayout
import com.v7878.foreign.MemorySegment
import com.v7878.foreign.ValueLayout
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NFloatArray
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NIntArray
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.doubleArrayValue
import dev.karmakrafts.kwire.ctype.floatArrayValue
import dev.karmakrafts.kwire.ctype.toMemorySegment
import dev.karmakrafts.kwire.ctype.toNFloat
import dev.karmakrafts.kwire.ctype.toNInt
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.ctype.toPtr
import dev.karmakrafts.kwire.ffi.FFIType
import dev.karmakrafts.kwire.ffi.SharedLibrary
import dev.karmakrafts.kwire.ffi.getFunction
import java.lang.invoke.MethodHandle

private object PanamaMemory : Memory {
    override val defaultAlignment: NUInt = if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) 4U.toNUInt() else 16U.toNUInt()

    // @formatter:off
    private val malloc: MethodHandle =
        SharedLibrary.cRuntime.getFunction("malloc", FFIType.PTR, FFIType.NUINT)
    private val realloc: MethodHandle =
        SharedLibrary.cRuntime.getFunction("realloc", FFIType.PTR, FFIType.PTR, FFIType.NUINT)
    private val free: MethodHandle =
        SharedLibrary.cRuntime.getFunction("free", FFIType.VOID, FFIType.PTR)
    private val memcpy: MethodHandle =
        SharedLibrary.cRuntime.getFunction("memcpy", FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.NUINT)
    private val memmove: MethodHandle =
        SharedLibrary.cRuntime.getFunction("memmove", FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.NUINT)
    private val memcmp: MethodHandle =
        SharedLibrary.cRuntime.getFunction("memcmp", FFIType.INT, FFIType.PTR, FFIType.PTR, FFIType.NUINT)

    private val strlen: MethodHandle =
        SharedLibrary.cRuntime.getFunction("strlen", FFIType.NUINT, FFIType.PTR)
    private val strcpy: MethodHandle =
        SharedLibrary.cRuntime.getFunction("strcpy", FFIType.PTR, FFIType.PTR, FFIType.PTR)
    private val strcmp: MethodHandle =
        SharedLibrary.cRuntime.getFunction("strcmp", FFIType.INT, FFIType.PTR, FFIType.PTR)
    // @formatter:on

    override fun allocate(size: NUInt, alignment: NUInt): Ptr<CVoid> {
        return (malloc.invokeExact(Memory.align(size, alignment).value) as MemorySegment).toPtr().align(alignment)
    }

    override fun reallocate(address: Ptr<*>, size: NUInt, alignment: NUInt): Ptr<CVoid> {
        return (realloc.invokeExact(
            address.toMemorySegment(), Memory.align(size, alignment).value
        ) as MemorySegment).toPtr().align(alignment)
    }

    override fun free(address: Ptr<*>) {
        free.invokeExact(address.toMemorySegment())
    }

    override fun set(address: Ptr<*>, value: Byte, size: NUInt) {
        address.toMemorySegment(size).fill(value)
    }

    override fun copy(source: Ptr<*>, dest: Ptr<*>, size: NUInt) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        memcpy.invokeExact(dest.toMemorySegment(), source.toMemorySegment(), size.value) as MemorySegment
    }

    override fun copyOverlapping(source: Ptr<*>, dest: Ptr<*>, size: NUInt) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        memmove.invokeExact(dest.toMemorySegment(), source.toMemorySegment(), size.value) as MemorySegment
    }

    override fun compare(first: Ptr<*>, second: Ptr<*>, size: NUInt): Int {
        return memcmp.invokeExact(first.toMemorySegment(), second.toMemorySegment(), size.value) as Int
    }

    override fun strlen(address: Ptr<*>): NUInt {
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            (strlen.invokeExact(address.toMemorySegment()) as Int).toNUInt()
        }
        else {
            (strlen.invokeExact(address.toMemorySegment()) as Long).toNUInt()
        }
    }

    override fun strcpy(source: Ptr<*>, dest: Ptr<*>) {
        // Discarding cast is required because of @PolymorphicSignature on invokeExact
        strcpy.invokeExact(dest.toMemorySegment(), source.toMemorySegment()) as MemorySegment
    }

    override fun strcmp(first: Ptr<*>, second: Ptr<*>): Int {
        return strcmp.invokeExact(first.toMemorySegment(), second.toMemorySegment()) as Int
    }

    override fun readByte(address: Ptr<*>): Byte {
        return address.toMemorySegment(Byte.SIZE_BYTES).get(ValueLayout.JAVA_BYTE, 0L)
    }

    override fun readShort(address: Ptr<*>): Short {
        return address.toMemorySegment(Short.SIZE_BYTES).get(ValueLayout.JAVA_SHORT_UNALIGNED, 0L)
    }

    override fun readInt(address: Ptr<*>): Int {
        return address.toMemorySegment(Int.SIZE_BYTES).get(ValueLayout.JAVA_INT_UNALIGNED, 0L)
    }

    override fun readLong(address: Ptr<*>): Long {
        return address.toMemorySegment(Long.SIZE_BYTES).get(ValueLayout.JAVA_LONG_UNALIGNED, 0L)
    }

    override fun readNInt(address: Ptr<*>): NInt {
        val segment = address.toMemorySegment(Ptr.SIZE_BYTES)
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.get(ValueLayout.JAVA_INT_UNALIGNED, 0L).toNInt()
        }
        else {
            segment.get(ValueLayout.JAVA_LONG_UNALIGNED, 0L).toNInt()
        }
    }

    override fun readPointer(address: Ptr<*>): Ptr<CVoid> {
        return address.toMemorySegment(Ptr.SIZE_BYTES).get(AddressLayout.ADDRESS_UNALIGNED, 0L).toPtr()
    }

    override fun readFloat(address: Ptr<*>): Float {
        return address.toMemorySegment(Float.SIZE_BYTES).get(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L)
    }

    override fun readDouble(address: Ptr<*>): Double {
        return address.toMemorySegment(Double.SIZE_BYTES).get(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L)
    }

    override fun readNFloat(address: Ptr<*>): NFloat {
        val segment = address.toMemorySegment(Ptr.SIZE_BYTES)
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.get(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L).toNFloat()
        }
        else {
            segment.get(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L).toNFloat()
        }
    }

    override fun readBytes(address: Ptr<*>, data: ByteArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Byte.SIZE_BYTES), ValueLayout.JAVA_BYTE, 0L, data, dataStart, size
        )
    }

    override fun readShorts(address: Ptr<*>, data: ShortArray, dataStart: Int, dataEnd: Int) {
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

    override fun readInts(address: Ptr<*>, data: IntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Int.SIZE_BYTES), ValueLayout.JAVA_INT_UNALIGNED, 0L, data, dataStart, size
        )
    }

    override fun readLongs(address: Ptr<*>, data: LongArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            address.toMemorySegment(size * Long.SIZE_BYTES), ValueLayout.JAVA_LONG_UNALIGNED, 0L, data, dataStart, size
        )
    }

    override fun readNInts(address: Ptr<*>, data: NIntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
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

    override fun readFloats(address: Ptr<*>, data: FloatArray, dataStart: Int, dataEnd: Int) {
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

    override fun readDoubles(address: Ptr<*>, data: DoubleArray, dataStart: Int, dataEnd: Int) {
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

    override fun readNFloats(address: Ptr<*>, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                address.toMemorySegment(size * Float.SIZE_BYTES),
                ValueLayout.JAVA_FLOAT_UNALIGNED,
                0L,
                data.floatArrayValue,
                dataStart,
                size
            )
        }
        else {
            MemorySegment.copy(
                address.toMemorySegment(size * Double.SIZE_BYTES),
                ValueLayout.JAVA_DOUBLE_UNALIGNED,
                0L,
                data.doubleArrayValue,
                dataStart,
                size
            )
        }
    }

    override fun readPointers(address: Ptr<*>, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
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

    override fun writeByte(address: Ptr<*>, value: Byte) {
        address.toMemorySegment(Byte.SIZE_BYTES).set(ValueLayout.JAVA_BYTE, 0L, value)
    }

    override fun writeShort(address: Ptr<*>, value: Short) {
        address.toMemorySegment(Short.SIZE_BYTES).set(ValueLayout.JAVA_SHORT_UNALIGNED, 0L, value)
    }

    override fun writeInt(address: Ptr<*>, value: Int) {
        address.toMemorySegment(Int.SIZE_BYTES).set(ValueLayout.JAVA_INT_UNALIGNED, 0L, value)
    }

    override fun writeLong(address: Ptr<*>, value: Long) {
        address.toMemorySegment(Long.SIZE_BYTES).set(ValueLayout.JAVA_LONG_UNALIGNED, 0L, value)
    }

    override fun writeNInt(address: Ptr<*>, value: NInt) {
        val segment = address.toMemorySegment(Ptr.SIZE_BYTES)
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.set(ValueLayout.JAVA_INT_UNALIGNED, 0L, value.toInt())
        }
        else {
            segment.set(ValueLayout.JAVA_LONG_UNALIGNED, 0L, value)
        }
    }

    override fun writePointer(address: Ptr<*>, value: Ptr<*>) {
        address.toMemorySegment(Ptr.SIZE_BYTES).set(ValueLayout.ADDRESS_UNALIGNED, 0L, value.toMemorySegment())
    }

    override fun writeFloat(address: Ptr<*>, value: Float) {
        address.toMemorySegment(Float.SIZE_BYTES).set(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L, value)
    }

    override fun writeDouble(address: Ptr<*>, value: Double) {
        address.toMemorySegment(Double.SIZE_BYTES).set(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L, value)
    }

    override fun writeNFloat(address: Ptr<*>, value: NFloat) {
        val segment = address.toMemorySegment(Ptr.SIZE_BYTES)
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            segment.set(ValueLayout.JAVA_FLOAT_UNALIGNED, 0L, value.toFloat())
        }
        else {
            segment.set(ValueLayout.JAVA_DOUBLE_UNALIGNED, 0L, value)
        }
    }

    override fun writeBytes(address: Ptr<*>, data: ByteArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Byte.SIZE_BYTES), ValueLayout.JAVA_BYTE, 0L, size
        )
    }

    override fun writeShorts(address: Ptr<*>, data: ShortArray, dataStart: Int, dataEnd: Int) {
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

    override fun writeInts(address: Ptr<*>, data: IntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Int.SIZE_BYTES), ValueLayout.JAVA_INT_UNALIGNED, 0L, size
        )
    }

    override fun writeLongs(address: Ptr<*>, data: LongArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        MemorySegment.copy(
            data, dataStart, address.toMemorySegment(size * Long.SIZE_BYTES), ValueLayout.JAVA_LONG_UNALIGNED, 0L, size
        )
    }

    override fun writeNInts(address: Ptr<*>, data: NIntArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
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

    override fun writeFloats(address: Ptr<*>, data: FloatArray, dataStart: Int, dataEnd: Int) {
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

    override fun writeDoubles(address: Ptr<*>, data: DoubleArray, dataStart: Int, dataEnd: Int) {
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

    override fun writeNFloats(address: Ptr<*>, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            MemorySegment.copy(
                data.floatArrayValue,
                dataStart,
                address.toMemorySegment(size * Float.SIZE_BYTES),
                ValueLayout.JAVA_FLOAT_UNALIGNED,
                0L,
                size
            )
        }
        else {
            MemorySegment.copy(
                data.doubleArrayValue,
                dataStart,
                address.toMemorySegment(size * Double.SIZE_BYTES),
                ValueLayout.JAVA_DOUBLE_UNALIGNED,
                0L,
                size
            )
        }
    }

    override fun writePointers(address: Ptr<*>, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        val size = dataEnd - dataStart
        if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
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