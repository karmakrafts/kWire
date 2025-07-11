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
// @formatter:on

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NFloatArray
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NIntArray
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.NUIntArray
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.nFloatArray
import dev.karmakrafts.kwire.ctype.nIntArray
import dev.karmakrafts.kwire.ctype.nUIntArray
import dev.karmakrafts.kwire.ctype.size
import dev.karmakrafts.kwire.ctype.toNInt
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.ctype.toUnsigned
import dev.karmakrafts.kwire.memory.Memory.Companion.defaultAlignment

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
@KWireCompilerApi
interface Memory : Allocator {
    /**
     * Companion object that delegates to the platform-specific Memory implementation.
     *
     * This allows for static access to Memory functionality through the Memory class,
     * e.g., `Memory.allocate(...)` instead of requiring an instance.
     */
    @KWireCompilerApi
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
     * Sets a block of memory to zero.
     *
     * @param address The pointer to the memory block to set
     * @param size The number of bytes to set to zero
     */
    fun zero(address: Address, size: NUInt) = set(address, 0, size)

    /**
     * Sets a block of memory to a specific byte value, similar to C's memset.
     *
     * @param address The pointer to the memory block to set
     * @param value The byte value to set
     * @param size The number of bytes to set
     */
    fun set(address: Address, value: Byte, size: NUInt)

    /**
     * Copies a block of memory from one location to another, similar to C's memcpy.
     * The source and destination memory blocks must not overlap.
     *
     * @param source The pointer to the source memory block
     * @param dest The pointer to the destination memory block
     * @param size The number of bytes to copy
     */
    fun copy(source: @Const Address, dest: Address, size: NUInt)

    /**
     * Copies a block of memory from one location to another, similar to C's memmove.
     * This function correctly handles overlapping memory regions.
     *
     * @param source The pointer to the source memory block
     * @param dest The pointer to the destination memory block
     * @param size The number of bytes to copy
     */
    fun copyOverlapping(source: @Const Address, dest: Address, size: NUInt)

    /**
     * Compares two blocks of memory, similar to C's memcmp.
     *
     * @param first The pointer to the first memory block
     * @param second The pointer to the second memory block
     * @param size The number of bytes to compare
     * @return A negative value if first < second, zero if first == second, or a positive value if first > second
     */
    fun compare(first: @Const Address, second: @Const Address, size: NUInt): Int

    /**
     * Gets the length of a null-terminated UTF-8 string.
     *
     * @param address The pointer to the null-terminated string
     * @return The length of the string in bytes, not including the null terminator
     */
    fun strlen(address: @Const Address): NUInt

    /**
     * Copies a null-terminated string from source to destination, similar to C's strcpy.
     *
     * @param source The pointer to the source null-terminated string
     * @param dest The pointer to the destination buffer where the string will be copied
     */
    fun strcpy(source: @Const Address, dest: Address)

    /**
     * Compares two null-terminated strings lexicographically, similar to C's strcmp.
     *
     * @param first The pointer to the first null-terminated string
     * @param second The pointer to the second null-terminated string
     * @return A negative value if first < second, zero if first == second, or a positive value if first > second
     */
    fun strcmp(first: @Const Address, second: @Const Address): Int

    /**
     * Reads a byte value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The byte value at the specified address
     */
    @KWireCompilerApi
    fun readByte(address: @Const Address): Byte

    /**
     * Reads a short value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The short value at the specified address
     */
    @KWireCompilerApi
    fun readShort(address: @Const Address): Short

    /**
     * Reads an int value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The int value at the specified address
     */
    @KWireCompilerApi
    fun readInt(address: @Const Address): Int

    /**
     * Reads a long value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The long value at the specified address
     */
    @KWireCompilerApi
    fun readLong(address: @Const Address): Long

    /**
     * Reads a native integer value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The native integer value at the specified address
     */
    @KWireCompilerApi
    fun readNInt(address: @Const Address): NInt

    /**
     * Reads a pointer value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The pointer value at the specified address
     */
    @KWireCompilerApi
    fun readPointer(address: @Const Address): VoidPtr

    /**
     * Reads a float value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The float value at the specified address
     */
    @KWireCompilerApi
    fun readFloat(address: @Const Address): Float

    /**
     * Reads a double value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The double value at the specified address
     */
    @KWireCompilerApi
    fun readDouble(address: @Const Address): Double

    /**
     * Reads a native floating-point value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The native floating-point value at the specified address
     */
    @KWireCompilerApi
    fun readNFloat(address: @Const Address): NFloat

    /**
     * Reads an unsigned byte value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The unsigned byte value at the specified address
     */
    @KWireCompilerApi
    fun readUByte(address: @Const Address): UByte = readByte(address).toUByte()

    /**
     * Reads an unsigned short value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The unsigned short value at the specified address
     */
    @KWireCompilerApi
    fun readUShort(address: @Const Address): UShort = readShort(address).toUShort()

    /**
     * Reads an unsigned int value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The unsigned int value at the specified address
     */
    @KWireCompilerApi
    fun readUInt(address: @Const Address): UInt = readInt(address).toUInt()

    /**
     * Reads an unsigned long value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The unsigned long value at the specified address
     */
    @KWireCompilerApi
    fun readULong(address: @Const Address): ULong = readLong(address).toULong()

    /**
     * Reads a native unsigned integer value from the specified memory address.
     *
     * @param address The memory address to read from
     * @return The native unsigned integer value at the specified address
     */
    @KWireCompilerApi
    fun readNUInt(address: @Const Address): NUInt = readNInt(address).toUnsigned()

    /**
     * Reads an array of bytes from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read bytes
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readBytes(address: @Const Address, data: ByteArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of shorts from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read shorts
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readShorts(address: @Const Address, data: ShortArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of ints from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read ints
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readInts(address: @Const Address, data: IntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of longs from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read longs
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readLongs(address: @Const Address, data: LongArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of native integers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read native integers
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readNInts(address: @Const Address, data: NIntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of floats from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read floats
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readFloats(address: @Const Address, data: FloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of doubles from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read doubles
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readDoubles(address: @Const Address, data: DoubleArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of native floating-point values from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read native floating-point values
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readNFloats(address: @Const Address, data: NFloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of pointers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read pointers
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readPointers(address: @Const Address, data: PtrArray<*>, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes a byte value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The byte value to write
     */
    @KWireCompilerApi
    fun writeByte(address: Address, value: Byte)

    /**
     * Writes a short value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The short value to write
     */
    @KWireCompilerApi
    fun writeShort(address: Address, value: Short)

    /**
     * Writes an int value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The int value to write
     */
    @KWireCompilerApi
    fun writeInt(address: Address, value: Int)

    /**
     * Writes a long value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The long value to write
     */
    @KWireCompilerApi
    fun writeLong(address: Address, value: Long)

    /**
     * Writes a native integer value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The native integer value to write
     */
    @KWireCompilerApi
    fun writeNInt(address: Address, value: NInt)

    /**
     * Writes a pointer value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The pointer value to write
     */
    @KWireCompilerApi
    fun writePointer(address: Address, value: Address)

    /**
     * Writes a float value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The float value to write
     */
    @KWireCompilerApi
    fun writeFloat(address: Address, value: Float)

    /**
     * Writes a double value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The double value to write
     */
    @KWireCompilerApi
    fun writeDouble(address: Address, value: Double)

    /**
     * Writes a native floating-point value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The native floating-point value to write
     */
    @KWireCompilerApi
    fun writeNFloat(address: Address, value: NFloat)

    /**
     * Writes an unsigned byte value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The unsigned byte value to write
     */
    @KWireCompilerApi
    fun writeUByte(address: Address, value: UByte) = writeByte(address, value.toByte())

    /**
     * Writes an unsigned short value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The unsigned short value to write
     */
    @KWireCompilerApi
    fun writeUShort(address: Address, value: UShort) = writeShort(address, value.toShort())

    /**
     * Writes an unsigned int value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The unsigned int value to write
     */
    @KWireCompilerApi
    fun writeUInt(address: Address, value: UInt) = writeInt(address, value.toInt())

    /**
     * Writes an unsigned long value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The unsigned long value to write
     */
    @KWireCompilerApi
    fun writeULong(address: Address, value: ULong) = writeLong(address, value.toLong())

    /**
     * Writes a native unsigned integer value to the specified memory address.
     *
     * @param address The memory address to write to
     * @param value The native unsigned integer value to write
     */
    @KWireCompilerApi
    fun writeNUInt(address: Address, value: NUInt) = writeNInt(address, value.toNInt())

    /**
     * Writes an array of bytes to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of bytes to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeBytes(address: Address, data: ByteArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of shorts to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of shorts to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeShorts(address: Address, data: ShortArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of ints to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of ints to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeInts(address: Address, data: IntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of longs to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of longs to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeLongs(address: Address, data: LongArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of native integers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of native integers to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeNInts(address: Address, data: NIntArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of floats to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of floats to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeFloats(address: Address, data: FloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of doubles to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of doubles to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeDoubles(address: Address, data: DoubleArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of native floating-point values to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of native floating-point values to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writeNFloats(address: Address, data: NFloatArray, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Writes an array of pointers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of pointers to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    @KWireCompilerApi
    fun writePointers(address: Address, data: PtrArray<*>, dataStart: Int = 0, dataEnd: Int = data.size)

    /**
     * Reads an array of unsigned bytes from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read unsigned bytes
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readUBytes(address: @Const Address, data: UByteArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        readBytes(address, data.asByteArray(), dataStart, dataEnd)

    /**
     * Reads an array of unsigned shorts from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read unsigned shorts
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readUShorts(address: @Const Address, data: UShortArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        readShorts(address, data.asShortArray(), dataStart, dataEnd)

    /**
     * Reads an array of unsigned ints from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read unsigned ints
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readUInts(address: @Const Address, data: UIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        readInts(address, data.asIntArray(), dataStart, dataEnd)

    /**
     * Reads an array of unsigned longs from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read unsigned longs
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readULongs(address: @Const Address, data: ULongArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        readLongs(address, data.asLongArray(), dataStart, dataEnd)

    /**
     * Reads an array of native unsigned integers from the specified memory address.
     *
     * @param address The memory address to read from
     * @param data The array to store the read native unsigned integers
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun readNUInts(address: @Const Address, data: NUIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        readNInts(address, data.asNIntArray(), dataStart, dataEnd)

    /**
     * Writes an array of unsigned bytes to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of unsigned bytes to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeUBytes(address: Address, data: UByteArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        writeBytes(address, data.asByteArray(), dataStart, dataEnd)

    /**
     * Writes an array of unsigned shorts to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of unsigned shorts to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeUShorts(address: Address, data: UShortArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        writeShorts(address, data.asShortArray(), dataStart, dataEnd)

    /**
     * Writes an array of unsigned ints to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of unsigned ints to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeUInts(address: Address, data: UIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        writeInts(address, data.asIntArray(), dataStart, dataEnd)

    /**
     * Writes an array of unsigned longs to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of unsigned longs to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeULongs(address: Address, data: ULongArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        writeLongs(address, data.asLongArray(), dataStart, dataEnd)

    /**
     * Writes an array of native unsigned integers to the specified memory address.
     *
     * @param address The memory address to write to
     * @param data The array of native unsigned integers to write
     * @param dataStart The starting index in the array (inclusive)
     * @param dataEnd The ending index in the array (exclusive)
     */
    fun writeNUInts(address: Address, data: NUIntArray, dataStart: Int = 0, dataEnd: Int = data.size) =
        writeNInts(address, data.asNIntArray(), dataStart, dataEnd)

    // Allocating reads

    /**
     * Reads an array of bytes from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of bytes to read
     * @return A newly allocated array containing the bytes read from the specified address
     */
    @KWireCompilerApi
    fun readBytes(address: @Const Address, size: Int): ByteArray = ByteArray(size).apply {
        readBytes(address, this)
    }

    /**
     * Reads an array of shorts from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of shorts to read
     * @return A newly allocated array containing the shorts read from the specified address
     */
    @KWireCompilerApi
    fun readShorts(address: @Const Address, size: Int): ShortArray = ShortArray(size).apply {
        readShorts(address, this)
    }

    /**
     * Reads an array of ints from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of ints to read
     * @return A newly allocated array containing the ints read from the specified address
     */
    @KWireCompilerApi
    fun readInts(address: @Const Address, size: Int): IntArray = IntArray(size).apply {
        readInts(address, this)
    }

    /**
     * Reads an array of longs from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of longs to read
     * @return A newly allocated array containing the longs read from the specified address
     */
    @KWireCompilerApi
    fun readLongs(address: @Const Address, size: Int): LongArray = LongArray(size).apply {
        readLongs(address, this)
    }

    /**
     * Reads an array of native integers from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of native integers to read
     * @return A newly allocated array containing the native integers read from the specified address
     */
    @KWireCompilerApi
    fun readNInts(address: @Const Address, size: Int): NIntArray = nIntArray(size).apply {
        readNInts(address, this)
    }

    /**
     * Reads an array of unsigned bytes from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of unsigned bytes to read
     * @return A newly allocated array containing the unsigned bytes read from the specified address
     */
    @KWireCompilerApi
    fun readUBytes(address: @Const Address, size: Int): UByteArray = UByteArray(size).apply {
        readUBytes(address, this)
    }

    /**
     * Reads an array of unsigned shorts from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of unsigned shorts to read
     * @return A newly allocated array containing the unsigned shorts read from the specified address
     */
    @KWireCompilerApi
    fun readUShorts(address: @Const Address, size: Int): UShortArray = UShortArray(size).apply {
        readUShorts(address, this)
    }

    /**
     * Reads an array of unsigned ints from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of unsigned ints to read
     * @return A newly allocated array containing the unsigned ints read from the specified address
     */
    @KWireCompilerApi
    fun readUInts(address: @Const Address, size: Int): UIntArray = UIntArray(size).apply {
        readUInts(address, this)
    }

    /**
     * Reads an array of unsigned longs from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of unsigned longs to read
     * @return A newly allocated array containing the unsigned longs read from the specified address
     */
    @KWireCompilerApi
    fun readULongs(address: @Const Address, size: Int): ULongArray = ULongArray(size).apply {
        readULongs(address, this)
    }

    /**
     * Reads an array of native unsigned integers from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of native unsigned integers to read
     * @return A newly allocated array containing the native unsigned integers read from the specified address
     */
    @KWireCompilerApi
    fun readNUInts(address: @Const Address, size: Int): NUIntArray = nUIntArray(size).apply {
        readNUInts(address, this)
    }

    /**
     * Reads an array of floats from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of floats to read
     * @return A newly allocated array containing the floats read from the specified address
     */
    @KWireCompilerApi
    fun readFloats(address: @Const Address, size: Int): FloatArray = FloatArray(size).apply {
        readFloats(address, this)
    }

    /**
     * Reads an array of doubles from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of doubles to read
     * @return A newly allocated array containing the doubles read from the specified address
     */
    @KWireCompilerApi
    fun readDoubles(address: @Const Address, size: Int): DoubleArray = DoubleArray(size).apply {
        readDoubles(address, this)
    }

    /**
     * Reads an array of native floating-point values from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of native floating-point values to read
     * @return A newly allocated array containing the native floating-point values read from the specified address
     */
    @KWireCompilerApi
    fun readNFloats(address: @Const Address, size: Int): NFloatArray = nFloatArray(size).apply {
        readNFloats(address, this)
    }

    /**
     * Reads an array of pointers from the specified memory address, allocating a new array of the specified size.
     *
     * @param address The memory address to read from
     * @param size The number of pointers to read
     * @return A newly allocated array containing the pointers read from the specified address
     */
    @KWireCompilerApi
    fun readPointers(address: @Const Address, size: Int): PtrArray<VoidPtr> = PtrArray<VoidPtr>(size).apply {
        readPointers(address, this)
    }
}