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
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.NUIntArray
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.Pointed
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.VoidPtr
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

    /**
     * Allocates a block of memory of the specified size and fills it with the specified byte value.
     *
     * @param value The byte value to fill the memory with
     * @param size The size of the memory block to allocate in bytes
     * @param alignment The alignment of the memory block (defaults to the platform's default alignment)
     * @return A pointer to the allocated and initialized memory block
     */
    fun splat(value: Byte, size: NUInt, alignment: NUInt = defaultAlignment): VoidPtr =
        Memory.allocate(size, alignment).apply {
            Memory.set(this, value, size)
        }
}

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC)
fun <N : Comparable<N>> Allocator.allocateNum(): NumPtr<N> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC_ARRAY)
fun <N : Comparable<N>> Allocator.allocateNumArray(count: NUInt): NumPtr<N> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC)
fun <T : Pointed> Allocator.allocate(): Ptr<T> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.ALLOCATOR_ALLOC_ARRAY)
fun <T : Pointed> Allocator.allocateArray(count: NUInt): Ptr<T> = throw KWirePluginNotAppliedException()

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

inline fun Allocator.nInt(value: NInt): NumPtr<NInt> {
    val address = allocate(Address.SIZE_BYTES.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writeNInt(address, value)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uByte(value: UByte): NumPtr<UByte> {
    val address = allocate(UByte.SIZE_BYTES.toNUInt(), UByte.SIZE_BYTES.toNUInt())
    Memory.writeUByte(address, value)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uShort(value: UShort): NumPtr<UShort> {
    val address = allocate(UShort.SIZE_BYTES.toNUInt(), UShort.SIZE_BYTES.toNUInt())
    Memory.writeUShort(address, value)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uInt(value: UInt): NumPtr<UInt> {
    val address = allocate(UInt.SIZE_BYTES.toNUInt(), UInt.SIZE_BYTES.toNUInt())
    Memory.writeUInt(address, value)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uLong(value: ULong): NumPtr<ULong> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeULong(address, value)
    return address.reinterpretNum()
}

inline fun Allocator.nUInt(value: NUInt): NumPtr<NUInt> {
    val address = allocate(Address.SIZE_BYTES.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writeNUInt(address, value)
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

inline fun Allocator.nFloat(value: NFloat): NumPtr<NFloat> {
    val address = allocate(Address.SIZE_BYTES.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writeNFloat(address, value)
    return address.reinterpretNum()
}

inline fun <A : Address> Allocator.pointer(value: A): Ptr<A> {
    val address = allocate(Address.SIZE_BYTES.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writePointer(address, value)
    return address.reinterpret()
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

inline fun Allocator.nInts(vararg values: NInt): NumPtr<NInt> {
    val address = allocate(Address.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writeNInts(address, (values as Array<NInt>).toNIntArray())
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uBytes(vararg values: UByte): NumPtr<UByte> {
    val address = allocate(UByte.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UByte.SIZE_BYTES.toNUInt())
    Memory.writeUBytes(address, values)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uShorts(vararg values: UShort): NumPtr<UShort> {
    val address = allocate(UShort.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UShort.SIZE_BYTES.toNUInt())
    Memory.writeUShorts(address, values)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uInts(vararg values: UInt): NumPtr<UInt> {
    val address = allocate(UInt.SIZE_BYTES.toNUInt() * values.size.toNUInt(), UInt.SIZE_BYTES.toNUInt())
    Memory.writeUInts(address, values)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.uLongs(vararg values: ULong): NumPtr<ULong> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt() * values.size.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeULongs(address, values)
    return address.reinterpretNum()
}

@ExperimentalUnsignedTypes
inline fun Allocator.nUInts(values: NUIntArray): NumPtr<NUInt> {
    val address = allocate(ULong.SIZE_BYTES.toNUInt() * values.size.toNUInt(), ULong.SIZE_BYTES.toNUInt())
    Memory.writeNUInts(address, values)
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

inline fun Allocator.nFloats(vararg values: NFloat): NumPtr<NFloat> {
    val address = allocate(Address.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writeNFloats(address, (values as Array<NFloat>).toNFloatArray())
    return address.reinterpretNum()
}

inline fun <A : Address> Allocator.pointers(vararg values: A): Ptr<A> {
    val address = allocate(Address.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writePointers(address, ptrArrayOf(*values))
    return address.reinterpret()
}

inline fun <A : Address> Allocator.pointers(values: PtrArray<A>): Ptr<A> {
    val address = allocate(Address.SIZE_BYTES.toNUInt() * values.size.toNUInt(), Address.SIZE_BYTES.toNUInt())
    Memory.writePointers(address, values)
    return address.reinterpret()
}