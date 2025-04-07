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
@file:OptIn(ExperimentalUnsignedTypes::class) 
@file:Suppress("NOTHING_TO_INLINE") 
@file:JvmName("Memory$")
// @formatter:on

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

/**
 * Internal function to get the platform-specific implementation of the Memory interface.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate Memory implementation for that platform.
 *
 * @return The platform-specific Memory implementation
 */
@PublishedApi
internal expect fun getPlatformMemory(): Memory

/**
 * Interface for low-level memory operations.
 *
 * This interface provides methods for allocating, deallocating, and manipulating memory,
 * as well as reading and writing various data types to and from memory addresses.
 * It serves as a type-safe wrapper around platform-specific memory operations.
 */
interface Memory {
    /**
     * Companion object that delegates to the platform-specific Memory implementation.
     *
     * This allows for static access to Memory functionality through the Memory class,
     * e.g., `Memory.allocate(...)` instead of requiring an instance.
     */
    companion object : Memory by getPlatformMemory() {
        /**
         * Aligns a memory address to the specified alignment boundary.
         *
         * @param value The memory address or size to align
         * @param alignment The alignment boundary, defaults to [defaultAlignment]
         * @return The aligned memory address or size
         */
        inline fun align(value: NUInt, alignment: NUInt = defaultAlignment): NUInt {
            val mask = alignment - 1U.toNUInt()
            return (value + mask) and mask.inv()
        }
    }

    /**
     * The default memory alignment used for memory operations.
     *
     * This value is platform-specific and represents the natural alignment
     * for memory operations on the current platform.
     */
    val defaultAlignment: NUInt

    /**
     * Allocates a block of memory of the specified size with the given alignment.
     *
     * @param size The size of the memory block to allocate in bytes
     * @param alignment The alignment boundary for the allocated memory, defaults to [defaultAlignment]
     * @return A pointer to the allocated memory block
     */
    fun allocate(size: NUInt, alignment: NUInt = defaultAlignment): Pointer

    /**
     * Reallocates a previously allocated memory block to a new size.
     *
     * @param address The pointer to the memory block to reallocate
     * @param size The new size for the memory block in bytes
     * @param alignment The alignment boundary for the reallocated memory, defaults to [defaultAlignment]
     * @return A pointer to the reallocated memory block, which may be different from the original address
     */
    fun reallocate(address: Pointer, size: NUInt, alignment: NUInt = defaultAlignment): Pointer

    /**
     * Frees a previously allocated memory block.
     *
     * @param address The pointer to the memory block to free
     */
    fun free(address: Pointer)

    /**
     * Sets a block of memory to a specific byte value, similar to C's memset.
     *
     * @param address The pointer to the memory block to set
     * @param value The byte value to set
     * @param size The number of bytes to set
     */
    fun set(address: Pointer, value: Byte, size: NUInt)

    /**
     * Copies a block of memory from one location to another, similar to C's memcpy.
     * The source and destination memory blocks must not overlap.
     *
     * @param source The pointer to the source memory block
     * @param dest The pointer to the destination memory block
     * @param size The number of bytes to copy
     */
    fun copy(source: Pointer, dest: Pointer, size: NUInt)

    /**
     * Copies a block of memory from one location to another, similar to C's memmove.
     * This function correctly handles overlapping memory regions.
     *
     * @param source The pointer to the source memory block
     * @param dest The pointer to the destination memory block
     * @param size The number of bytes to copy
     */
    fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt)

    /**
     * Compares two blocks of memory, similar to C's memcmp.
     *
     * @param first The pointer to the first memory block
     * @param second The pointer to the second memory block
     * @param size The number of bytes to compare
     * @return A negative value if first < second, zero if first == second, or a positive value if first > second
     */
    fun compare(first: Pointer, second: Pointer, size: NUInt): Int

    /**
     * Reads a byte value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The byte value at the specified address
     */
    fun readByte(address: Pointer): Byte

    /**
     * Reads a short value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The short value at the specified address
     */
    fun readShort(address: Pointer): Short

    /**
     * Reads an int value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The int value at the specified address
     */
    fun readInt(address: Pointer): Int

    /**
     * Reads a long value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The long value at the specified address
     */
    fun readLong(address: Pointer): Long

    /**
     * Reads a native integer value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The native integer value at the specified address
     */
    fun readNInt(address: Pointer): NInt

    /**
     * Reads a float value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The float value at the specified address
     */
    fun readFloat(address: Pointer): Float

    /**
     * Reads a double value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The double value at the specified address
     */
    fun readDouble(address: Pointer): Double

    /**
     * Reads an array of bytes from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of bytes to read
     * @return The array of bytes read from the specified address
     */
    fun readBytes(address: Pointer, size: Int): ByteArray

    /**
     * Reads an array of shorts from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of shorts to read
     * @return The array of shorts read from the specified address
     */
    fun readShorts(address: Pointer, size: Int): ShortArray

    /**
     * Reads an array of ints from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of ints to read
     * @return The array of ints read from the specified address
     */
    fun readInts(address: Pointer, size: Int): IntArray

    /**
     * Reads an array of longs from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of longs to read
     * @return The array of longs read from the specified address
     */
    fun readLongs(address: Pointer, size: Int): LongArray

    /**
     * Reads an array of native integers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of native integers to read
     * @return The array of native integers read from the specified address
     */
    fun readNInts(address: Pointer, size: Int): NIntArray

    /**
     * Reads an array of floats from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of floats to read
     * @return The array of floats read from the specified address
     */
    fun readFloats(address: Pointer, size: Int): FloatArray

    /**
     * Reads an array of doubles from the specified memory address.
     *
     * @param address The memory address to read from
     * @param size The number of doubles to read
     * @return The array of doubles read from the specified address
     */
    fun readDoubles(address: Pointer, size: Int): DoubleArray

    /**
     * Writes a byte value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The byte value to write
     */
    fun writeByte(address: Pointer, value: Byte)

    /**
     * Writes a short value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The short value to write
     */
    fun writeShort(address: Pointer, value: Short)

    /**
     * Writes an int value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The int value to write
     */
    fun writeInt(address: Pointer, value: Int)

    /**
     * Writes a long value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The long value to write
     */
    fun writeLong(address: Pointer, value: Long)

    /**
     * Writes a native integer value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The native integer value to write
     */
    fun writeNInt(address: Pointer, value: NInt)

    /**
     * Writes a float value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The float value to write
     */
    fun writeFloat(address: Pointer, value: Float)

    /**
     * Writes a double value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The double value to write
     */
    fun writeDouble(address: Pointer, value: Double)

    /**
     * Writes an array of bytes to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of bytes to write
     */
    fun writeBytes(address: Pointer, data: ByteArray)

    /**
     * Writes an array of shorts to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of shorts to write
     */
    fun writeShorts(address: Pointer, data: ShortArray)

    /**
     * Writes an array of ints to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of ints to write
     */
    fun writeInts(address: Pointer, data: IntArray)

    /**
     * Writes an array of longs to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of longs to write
     */
    fun writeLongs(address: Pointer, data: LongArray)

    /**
     * Writes an array of native integers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of native integers to write
     */
    fun writeNInts(address: Pointer, data: NIntArray)

    /**
     * Writes an array of floats to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of floats to write
     */
    fun writeFloats(address: Pointer, data: FloatArray)

    /**
     * Writes an array of doubles to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of doubles to write
     */
    fun writeDoubles(address: Pointer, data: DoubleArray)
}

/**
 * Sets a block of memory to zero.
 *
 * @param address The pointer to the memory block to set
 * @param size The number of bytes to set to zero
 */
inline fun Memory.zero(address: Pointer, size: NUInt) = set(address, 0, size)

/**
 * Reads an unsigned byte value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The unsigned byte value at the specified address
 */
inline fun Memory.readUByte(address: Pointer): UByte = readByte(address).toUByte()

/**
 * Reads an unsigned short value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The unsigned short value at the specified address
 */
inline fun Memory.readUShort(address: Pointer): UShort = readShort(address).toUShort()

/**
 * Reads an unsigned int value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The unsigned int value at the specified address
 */
inline fun Memory.readUInt(address: Pointer): UInt = readInt(address).toUInt()

/**
 * Reads an unsigned long value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The unsigned long value at the specified address
 */
inline fun Memory.readULong(address: Pointer): ULong = readLong(address).toULong()

/**
 * Reads a native unsigned integer value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The native unsigned integer value at the specified address
 */
inline fun Memory.readNUInt(address: Pointer): NUInt = readNInt(address).toUnsigned()

/**
 * Reads a pointer value from the specified memory address.
 *
 * @param address The memory address to read from
 * @return The pointer value at the specified address
 */
inline fun Memory.readPointer(address: Pointer): Pointer = Pointer(readNUInt(address))

/**
 * Reads an array of unsigned bytes from the specified memory address.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned bytes to read
 * @return The array of unsigned bytes read from the specified address
 */
inline fun Memory.readUBytes(address: Pointer, size: Int): UByteArray = readBytes(address, size).asUByteArray()

/**
 * Reads an array of unsigned shorts from the specified memory address.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned shorts to read
 * @return The array of unsigned shorts read from the specified address
 */
inline fun Memory.readUShorts(address: Pointer, size: Int): UShortArray = readShorts(address, size).asUShortArray()

/**
 * Reads an array of unsigned ints from the specified memory address.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned ints to read
 * @return The array of unsigned ints read from the specified address
 */
inline fun Memory.readUInts(address: Pointer, size: Int): UIntArray = readInts(address, size).asUIntArray()

/**
 * Reads an array of unsigned longs from the specified memory address.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned longs to read
 * @return The array of unsigned longs read from the specified address
 */
inline fun Memory.readULongs(address: Pointer, size: Int): ULongArray = readLongs(address, size).asULongArray()

/**
 * Reads an array of native unsigned integers from the specified memory address.
 *
 * @param address The memory address to read from
 * @param size The number of native unsigned integers to read
 * @return The array of native unsigned integers read from the specified address
 */
inline fun Memory.readNUInts(address: Pointer, size: Int): NUIntArray = readNInts(address, size).asNUIntArray()

/**
 * Writes an unsigned byte value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The unsigned byte value to write
 */
inline fun Memory.writeUByte(address: Pointer, value: UByte) = writeByte(address, value.toByte())

/**
 * Writes an unsigned short value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The unsigned short value to write
 */
inline fun Memory.writeUShort(address: Pointer, value: UShort) = writeShort(address, value.toShort())

/**
 * Writes an unsigned int value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The unsigned int value to write
 */
inline fun Memory.writeUInt(address: Pointer, value: UInt) = writeInt(address, value.toInt())

/**
 * Writes an unsigned long value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The unsigned long value to write
 */
inline fun Memory.writeULong(address: Pointer, value: ULong) = writeLong(address, value.toLong())

/**
 * Writes a native unsigned integer value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The native unsigned integer value to write
 */
inline fun Memory.writeNUInt(address: Pointer, value: NUInt) = writeNInt(address, value.toSigned())

/**
 * Writes a pointer value to the specified memory address.
 *
 * @param address The memory address to write to
 * @param value The pointer value to write
 */
inline fun Memory.writePointer(address: Pointer, value: Pointer) = writeNUInt(address, value.value)

/**
 * Writes an array of unsigned bytes to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned bytes to write
 */
inline fun Memory.writeUBytes(address: Pointer, data: UByteArray) = writeBytes(address, data.asByteArray())

/**
 * Writes an array of unsigned shorts to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned shorts to write
 */
inline fun Memory.writeUShorts(address: Pointer, data: UShortArray) = writeShorts(address, data.asShortArray())

/**
 * Writes an array of unsigned ints to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned ints to write
 */
inline fun Memory.writeUInts(address: Pointer, data: UIntArray) = writeInts(address, data.asIntArray())

/**
 * Writes an array of unsigned longs to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned longs to write
 */
inline fun Memory.writeULongs(address: Pointer, data: ULongArray) = writeLongs(address, data.asLongArray())

/**
 * Writes an array of native unsigned integers to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of native unsigned integers to write
 */
inline fun Memory.writeNUInts(address: Pointer, data: NUIntArray) = writeNInts(address, data.asNIntArray())
