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

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.times
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.memory.Memory.Companion.defaultAlignment

interface Allocator {
    /**
     * Allocates a block of memory of the specified size with the given alignment.
     *
     * @param size The size of the memory block to allocate in bytes
     * @param alignment The alignment boundary for the allocated memory, defaults to [defaultAlignment]
     * @return A pointer to the allocated memory block
     */
    fun allocate(size: NUInt, alignment: NUInt = Memory.defaultAlignment): VoidPtr

    /**
     * Reallocates a previously allocated memory block to a new size.
     *
     * @param address The pointer to the memory block to reallocate
     * @param size The new size for the memory block in bytes
     * @param alignment The alignment boundary for the reallocated memory, defaults to [defaultAlignment]
     * @return A pointer to the reallocated memory block, which may be different from the original address
     */
    fun reallocate(address: Address, size: NUInt, alignment: NUInt = Memory.defaultAlignment): VoidPtr

    /**
     * Frees a previously allocated memory block.
     *
     * @param address The pointer to the memory block to free
     */
    fun free(address: Address)
}

// Single values (mostly used by generated code)

inline fun Allocator.byte(value: Byte): NumPtr<Byte> {
    val address = allocate(Byte.SIZE_BYTES.toNUInt(), Byte.SIZE_BYTES.toNUInt())
    Memory.writeByte(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.short(value: Short): NumPtr<Short> {
    val address = allocate(Short.SIZE_BYTES.toNUInt(), Short.SIZE_BYTES.toNUInt())
    Memory.writeShort(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.int(value: Int): NumPtr<Int> {
    val address = allocate(Int.SIZE_BYTES.toNUInt(), Int.SIZE_BYTES.toNUInt())
    Memory.writeInt(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.long(value: Long): NumPtr<Long> {
    val address = allocate(Long.SIZE_BYTES.toNUInt(), Long.SIZE_BYTES.toNUInt())
    Memory.writeLong(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.float(value: Float): NumPtr<Float> {
    val address = allocate(Float.SIZE_BYTES.toNUInt(), Float.SIZE_BYTES.toNUInt())
    Memory.writeFloat(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.double(value: Double): NumPtr<Double> {
    val address = allocate(Double.SIZE_BYTES.toNUInt(), Double.SIZE_BYTES.toNUInt())
    Memory.writeDouble(address, value)
    return address.reinterpretNum()
}

// Multiple values

inline fun Allocator.bytes(vararg values: Byte): NumPtr<Byte> {
    val address = allocate(Byte.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Byte.SIZE_BYTES.toNUInt())
    Memory.writeBytes(address, values)
    return address.reinterpretNum()
}

inline fun Allocator.shorts(vararg values: Short): NumPtr<Short> {
    val address = allocate(Short.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Short.SIZE_BYTES.toNUInt())
    Memory.writeShorts(address, values)
    return address.reinterpretNum()
}

inline fun Allocator.ints(vararg values: Int): NumPtr<Int> {
    val address = allocate(Int.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Int.SIZE_BYTES.toNUInt())
    Memory.writeInts(address, values)
    return address.reinterpretNum()
}

inline fun Allocator.longs(vararg values: Long): NumPtr<Long> {
    val address = allocate(Long.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Long.SIZE_BYTES.toNUInt())
    Memory.writeLongs(address, values)
    return address.reinterpretNum()
}

inline fun Allocator.floats(vararg values: Float): NumPtr<Float> {
    val address = allocate(Float.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Float.SIZE_BYTES.toNUInt())
    Memory.writeFloats(address, values)
    return address.reinterpretNum()
}

inline fun Allocator.doubles(vararg values: Double): NumPtr<Double> {
    val address = allocate(Double.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Double.SIZE_BYTES.toNUInt())
    Memory.writeDoubles(address, values)
    return address.reinterpretNum()
}