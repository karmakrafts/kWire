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
     * Gets the length of a null-terminated string, similar to C's strlen.
     *
     * @param address The pointer to the null-terminated string
     * @return The length of the string in bytes, not including the null terminator
     */
    fun strlen(address: Pointer): NUInt

    /**
     * Copies a null-terminated string from source to destination, similar to C's strcpy.
     *
     * @param source The pointer to the source null-terminated string
     * @param dest The pointer to the destination buffer where the string will be copied
     */
    fun strcpy(source: Pointer, dest: Pointer)

    /**
     * Compares two null-terminated strings lexicographically, similar to C's strcmp.
     *
     * @param first The pointer to the first null-terminated string
     * @param second The pointer to the second null-terminated string
     * @return A negative value if first < second, zero if first == second, or a positive value if first > second
     */
    fun strcmp(first: Pointer, second: Pointer): Int

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
     * Reads a pointer value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The pointer value at the specified address
     */
    fun readPointer(address: Pointer): Pointer

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
     * @param data The array to store the read bytes
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readBytes(address: Pointer, data: ByteArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of shorts from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read shorts
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readShorts(address: Pointer, data: ShortArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of ints from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read ints
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readInts(address: Pointer, data: IntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of longs from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read longs
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readLongs(address: Pointer, data: LongArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of native integers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read native integers
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readNInts(address: Pointer, data: NIntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of floats from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read floats
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readFloats(address: Pointer, data: FloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of doubles from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read doubles
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readDoubles(address: Pointer, data: DoubleArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of pointers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read pointers
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readPointers(address: Pointer, data: PointerArray, dataStart: Int = 0, dataEnd: Int = data.size)

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
     * Writes a pointer value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The pointer value to write
     */
    fun writePointer(address: Pointer, value: Pointer)

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
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeBytes(address: Pointer, data: ByteArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of shorts to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of shorts to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeShorts(address: Pointer, data: ShortArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of ints to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of ints to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeInts(address: Pointer, data: IntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of longs to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of longs to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeLongs(address: Pointer, data: LongArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of native integers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of native integers to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeNInts(address: Pointer, data: NIntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of floats to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of floats to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeFloats(address: Pointer, data: FloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of doubles to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of doubles to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeDoubles(address: Pointer, data: DoubleArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of pointers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of pointers to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writePointers(address: Pointer, data: PointerArray, dataStart: Int = 0, dataEnd: Int = data.size)
}

/**
 * Sets a block of memory to zero.
 *
 * @param address The pointer to the memory block to set
 * @param size The number of bytes to set to zero
 */
inline fun Memory.zero(address: Pointer, size: NUInt) = set(address, 0, size)

/**
 * Allocates a block of memory of the specified size and fills it with the specified byte value.
 *
 * @param value The byte value to fill the memory with
 * @param size The size of the memory block to allocate in bytes
 * @param alignment The alignment of the memory block (defaults to the platform's default alignment)
 * @return A pointer to the allocated and initialized memory block
 */
inline fun Memory.splat(value: Byte, size: NUInt, alignment: NUInt = defaultAlignment): Pointer =
    Memory.allocate(size, alignment).apply {
        set(this, value, size)
    }

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
 * Reads an array of unsigned bytes from the specified memory address.
 *
 * @param address The memory address to read from
 * @param data The array to store the read unsigned bytes
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.readUBytes(address: Pointer, data: UByteArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    readBytes(address, data.asByteArray(), dataStart, dataEnd)

/**
 * Reads an array of unsigned shorts from the specified memory address.
 *
 * @param address The memory address to read from
 * @param data The array to store the read unsigned shorts
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.readUShorts(address: Pointer, data: UShortArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    readShorts(address, data.asShortArray(), dataStart, dataEnd)

/**
 * Reads an array of unsigned ints from the specified memory address.
 *
 * @param address The memory address to read from
 * @param data The array to store the read unsigned ints
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.readUInts(address: Pointer, data: UIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    readInts(address, data.asIntArray(), dataStart, dataEnd)

/**
 * Reads an array of unsigned longs from the specified memory address.
 *
 * @param address The memory address to read from
 * @param data The array to store the read unsigned longs
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.readULongs(address: Pointer, data: ULongArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    readLongs(address, data.asLongArray(), dataStart, dataEnd)

/**
 * Reads an array of native unsigned integers from the specified memory address.
 *
 * @param address The memory address to read from
 * @param data The array to store the read native unsigned integers
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.readNUInts(address: Pointer, data: NUIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    readNInts(address, data.asNIntArray(), dataStart, dataEnd)

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
 * Writes an array of unsigned bytes to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned bytes to write
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.writeUBytes(address: Pointer, data: UByteArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    writeBytes(address, data.asByteArray(), dataStart, dataEnd)

/**
 * Writes an array of unsigned shorts to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned shorts to write
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.writeUShorts(address: Pointer, data: UShortArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    writeShorts(address, data.asShortArray(), dataStart, dataEnd)

/**
 * Writes an array of unsigned ints to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned ints to write
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.writeUInts(address: Pointer, data: UIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    writeInts(address, data.asIntArray(), dataStart, dataEnd)

/**
 * Writes an array of unsigned longs to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of unsigned longs to write
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.writeULongs(address: Pointer, data: ULongArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    writeLongs(address, data.asLongArray(), dataStart, dataEnd)

/**
 * Writes an array of native unsigned integers to the specified memory address.
 *
 * @param address The memory address to write to
 * @param data The array of native unsigned integers to write
 * @param dataStart The starting index in the array (inclusive)
 * @param dataEnd The ending index in the array (exclusive)
 */
inline fun Memory.writeNUInts(address: Pointer, data: NUIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
    writeNInts(address, data.asNIntArray(), dataStart, dataEnd)

// Allocating reads

/**
 * Reads an array of bytes from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of bytes to read
 * @return A newly allocated array containing the bytes read from the specified address
 */
inline fun Memory.readBytes(address: Pointer, size: Int): ByteArray = ByteArray(size).apply {
    readBytes(address, this)
}

/**
 * Reads an array of shorts from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of shorts to read
 * @return A newly allocated array containing the shorts read from the specified address
 */
inline fun Memory.readShorts(address: Pointer, size: Int): ShortArray = ShortArray(size).apply {
    readShorts(address, this)
}

/**
 * Reads an array of ints from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of ints to read
 * @return A newly allocated array containing the ints read from the specified address
 */
inline fun Memory.readInts(address: Pointer, size: Int): IntArray = IntArray(size).apply {
    readInts(address, this)
}

/**
 * Reads an array of longs from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of longs to read
 * @return A newly allocated array containing the longs read from the specified address
 */
inline fun Memory.readLongs(address: Pointer, size: Int): LongArray = LongArray(size).apply {
    readLongs(address, this)
}

/**
 * Reads an array of native integers from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of native integers to read
 * @return A newly allocated array containing the native integers read from the specified address
 */
inline fun Memory.readNInts(address: Pointer, size: Int): NIntArray = nIntArray(size).apply {
    readNInts(address, this)
}

/**
 * Reads an array of unsigned bytes from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned bytes to read
 * @return A newly allocated array containing the unsigned bytes read from the specified address
 */
inline fun Memory.readUBytes(address: Pointer, size: Int): UByteArray = UByteArray(size).apply {
    readUBytes(address, this)
}

/**
 * Reads an array of unsigned shorts from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned shorts to read
 * @return A newly allocated array containing the unsigned shorts read from the specified address
 */
inline fun Memory.readUShorts(address: Pointer, size: Int): UShortArray = UShortArray(size).apply {
    readUShorts(address, this)
}

/**
 * Reads an array of unsigned ints from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned ints to read
 * @return A newly allocated array containing the unsigned ints read from the specified address
 */
inline fun Memory.readUInts(address: Pointer, size: Int): UIntArray = UIntArray(size).apply {
    readUInts(address, this)
}

/**
 * Reads an array of unsigned longs from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of unsigned longs to read
 * @return A newly allocated array containing the unsigned longs read from the specified address
 */
inline fun Memory.readULongs(address: Pointer, size: Int): ULongArray = ULongArray(size).apply {
    readULongs(address, this)
}

/**
 * Reads an array of native unsigned integers from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of native unsigned integers to read
 * @return A newly allocated array containing the native unsigned integers read from the specified address
 */
inline fun Memory.readNUInts(address: Pointer, size: Int): NUIntArray = nUIntArray(size).apply {
    readNUInts(address, this)
}

/**
 * Reads an array of floats from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of floats to read
 * @return A newly allocated array containing the floats read from the specified address
 */
inline fun Memory.readFloats(address: Pointer, size: Int): FloatArray = FloatArray(size).apply {
    readFloats(address, this)
}

/**
 * Reads an array of doubles from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of doubles to read
 * @return A newly allocated array containing the doubles read from the specified address
 */
inline fun Memory.readDoubles(address: Pointer, size: Int): DoubleArray = DoubleArray(size).apply {
    readDoubles(address, this)
}

/**
 * Reads an array of pointers from the specified memory address, allocating a new array of the specified size.
 *
 * @param address The memory address to read from
 * @param size The number of pointers to read
 * @return A newly allocated array containing the pointers read from the specified address
 */
inline fun Memory.readPointers(address: Pointer, size: Int): PointerArray = pointerArray(size).apply { 
    readPointers(address, this)
}
