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

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.NUIntArray
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.ValueType
import dev.karmakrafts.kwire.ctype.ptrArrayOf
import dev.karmakrafts.kwire.ctype.toNFloatArray
import dev.karmakrafts.kwire.ctype.toNIntArray
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
    fun allocate(size: NUInt, alignment: NUInt = Memory.defaultAlignment): Ptr<CVoid>

    /**
     * Reallocates a previously allocated memory block to a new size.
     *
     * @param address The pointer to the memory block to reallocate
     * @param size The new size for the memory block in bytes
     * @param alignment The alignment boundary for the reallocated memory, defaults to [defaultAlignment]
     * @return A pointer to the reallocated memory block, which may be different from the original address
     */
    fun reallocate(address: Ptr<*>, size: NUInt, alignment: NUInt = Memory.defaultAlignment): Ptr<CVoid>

    /**
     * Frees a previously allocated memory block.
     *
     * @param address The pointer to the memory block to free
     */
    fun free(address: Ptr<*>)

    /**
     * Allocates a block of memory of the specified size and fills it with the specified byte value.
     *
     * @param value The byte value to fill the memory with
     * @param size The size of the memory block to allocate in bytes
     * @param alignment The alignment of the memory block (defaults to the platform's default alignment)
     * @return A pointer to the allocated and initialized memory block
     */
    fun splat(value: Byte, size: NUInt, alignment: NUInt = defaultAlignment): Ptr<CVoid> =
        Memory.allocate(size, alignment).apply {
            Memory.set(this, value, size)
        }
}

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC)
fun <@ValueType T> Allocator.allocate(): Ptr<T> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC_ARRAY)
fun <@ValueType T> Allocator.allocateArray(count: NUInt): Ptr<T> = throw KWirePluginNotAppliedException()

// Single values (mostly used by generated code)

inline fun Allocator.byte(value: Byte): Ptr<Byte> {
    val address = allocate(Byte.SIZE_BYTES.toNUInt(), Byte.SIZE_BYTES.toNUInt())
    Memory.writeByte(address, value)
    return address.reinterpret()
}

inline fun Allocator.short(value: Short): Ptr<Short> {
    val address = allocate(Short.SIZE_BYTES.toNUInt(), Short.SIZE_BYTES.toNUInt())
    Memory.writeShort(address, value)
    return address.reinterpret()
}

inline fun Allocator.int(value: Int): Ptr<Int> {
    val address = allocate(Int.SIZE_BYTES.toNUInt(), Int.SIZE_BYTES.toNUInt())
    Memory.writeInt(address, value)
    return address.reinterpret()
}

inline fun Allocator.long(value: Long): Ptr<Long> {
    val address = allocate(Long.SIZE_BYTES.toNUInt(), Long.SIZE_BYTES.toNUInt())
    Memory.writeLong(address, value)
    return address.reinterpret()
}

inline fun Allocator.nInt(value: NInt): Ptr<NInt> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writeNInt(address, value)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uByte(value: UByte): Ptr<UByte> {
    val address = allocate(UByte.SIZE_BYTES.toNUInt(), UByte.SIZE_BYTES.toNUInt())
    Memory.writeUByte(address, value)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uShort(value: UShort): Ptr<UShort> {
    val address = allocate(UShort.SIZE_BYTES.toNUInt(), UShort.SIZE_BYTES.toNUInt())
    Memory.writeUShort(address, value)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uInt(value: UInt): Ptr<UInt> {
    val address = allocate(UInt.SIZE_BYTES.toNUInt(), UInt.SIZE_BYTES.toNUInt())
    Memory.writeUInt(address, value)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uLong(value: ULong): Ptr<ULong> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeULong(address, value)
    return address.reinterpret()
}

inline fun Allocator.nUInt(value: NUInt): Ptr<NUInt> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writeNUInt(address, value)
    return address.reinterpret()
}

inline fun Allocator.float(value: Float): Ptr<Float> {
    val address = allocate(Float.SIZE_BYTES.toNUInt(), Float.SIZE_BYTES.toNUInt())
    Memory.writeFloat(address, value)
    return address.reinterpret()
}

inline fun Allocator.double(value: Double): Ptr<Double> {
    val address = allocate(Double.SIZE_BYTES.toNUInt(), Double.SIZE_BYTES.toNUInt())
    Memory.writeDouble(address, value)
    return address.reinterpret()
}

inline fun Allocator.nFloat(value: NFloat): Ptr<NFloat> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writeNFloat(address, value)
    return address.reinterpret()
}

inline fun <A : Ptr<*>> Allocator.pointer(value: A): Ptr<A> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writePointer(address, value)
    return address.reinterpret()
}

// Multiple values

inline fun Allocator.bytes(vararg values: Byte): Ptr<Byte> {
    val address = allocate(Byte.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Byte.SIZE_BYTES.toNUInt())
    Memory.writeBytes(address, values)
    return address.reinterpret()
}

inline fun Allocator.shorts(vararg values: Short): Ptr<Short> {
    val address = allocate(Short.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Short.SIZE_BYTES.toNUInt())
    Memory.writeShorts(address, values)
    return address.reinterpret()
}

inline fun Allocator.ints(vararg values: Int): Ptr<Int> {
    val address = allocate(Int.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Int.SIZE_BYTES.toNUInt())
    Memory.writeInts(address, values)
    return address.reinterpret()
}

inline fun Allocator.longs(vararg values: Long): Ptr<Long> {
    val address = allocate(Long.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Long.SIZE_BYTES.toNUInt())
    Memory.writeLongs(address, values)
    return address.reinterpret()
}

inline fun Allocator.nInts(vararg values: NInt): Ptr<NInt> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writeNInts(address, (values as Array<NInt>).toNIntArray())
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uBytes(vararg values: UByte): Ptr<UByte> {
    val address = allocate(UByte.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UByte.SIZE_BYTES.toNUInt())
    Memory.writeUBytes(address, values)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uShorts(vararg values: UShort): Ptr<UShort> {
    val address = allocate(UShort.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UShort.SIZE_BYTES.toNUInt())
    Memory.writeUShorts(address, values)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uInts(vararg values: UInt): Ptr<UInt> {
    val address = allocate(UInt.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UInt.SIZE_BYTES.toNUInt())
    Memory.writeUInts(address, values)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uLongs(vararg values: ULong): Ptr<ULong> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt() * values.size.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeULongs(address, values)
    return address.reinterpret()
}

@ExperimentalUnsignedTypes
inline fun Allocator.nUInts(values: NUIntArray): Ptr<NUInt> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt() * values.size.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeNUInts(address, values)
    return address.reinterpret()
}

inline fun Allocator.floats(vararg values: Float): Ptr<Float> {
    val address = allocate(Float.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Float.SIZE_BYTES.toNUInt())
    Memory.writeFloats(address, values)
    return address.reinterpret()
}

inline fun Allocator.doubles(vararg values: Double): Ptr<Double> {
    val address = allocate(Double.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Double.SIZE_BYTES.toNUInt())
    Memory.writeDoubles(address, values)
    return address.reinterpret()
}

inline fun Allocator.nFloats(vararg values: NFloat): Ptr<NFloat> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writeNFloats(address, (values as Array<NFloat>).toNFloatArray())
    return address.reinterpret()
}

inline fun <A : Ptr<*>> Allocator.pointers(vararg values: A): Ptr<A> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writePointers(address, ptrArrayOf(*values))
    return address.reinterpret()
}

inline fun <A : Ptr<*>> Allocator.pointers(values: PtrArray<A>): Ptr<A> {
    val address = allocate(Ptr.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Ptr.SIZE_BYTES.toNUInt())
    Memory.writePointers(address, values)
    return address.reinterpret()
}