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
@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalUnsignedTypes::class)
// @formatter:on

package dev.karmakrafts.kwire

import kotlin.native.concurrent.ThreadLocal

/**
 * Thread-local singleton instance of [FFIArgBufferImpl] used for passing arguments to native functions.
 * This buffer is reused across FFI calls to avoid allocating a new buffer for each call.
 */
@PublishedApi
@ThreadLocal
internal val argBuffer: FFIArgBufferImpl = FFIArgBufferImpl()

/**
 * Interface for a buffer that stores arguments to be passed to native functions.
 *
 * The buffer manages memory for storing arguments of various types and tracks the
 * types of arguments stored. This is used by the FFI system to pass arguments to
 * native functions.
 */
interface FFIArgBuffer {
    companion object {
        /**
         * Default size of the argument buffer in bytes.
         */
        const val DEFAULT_SIZE: Int = 4096

        /**
         * Gets the thread-local argument buffer instance, clearing it first.
         *
         * @return The cleared argument buffer
         */
        inline fun get(): FFIArgBuffer = argBuffer.clear()
    }

    /**
     * The memory address of the buffer.
     */
    val address: Pointer

    /**
     * List of types of arguments stored in the buffer.
     */
    val types: List<FFIType>

    /**
     * Get the address of an argument at the given index.
     *
     * @param index The index of the argument to retrieve the address of
     * @return The memory address where the argument is stored
     */
    fun getAddress(index: Int): Pointer

    /**
     * Puts a byte value into the buffer.
     *
     * @param value The byte value to store
     */
    fun putByte(value: Byte)

    /**
     * Puts a short value into the buffer.
     *
     * @param value The short value to store
     */
    fun putShort(value: Short)

    /**
     * Puts an int value into the buffer.
     *
     * @param value The int value to store
     */
    fun putInt(value: Int)

    /**
     * Puts a long value into the buffer.
     *
     * @param value The long value to store
     */
    fun putLong(value: Long)

    /**
     * Puts a native integer value into the buffer.
     *
     * @param value The native integer value to store
     */
    fun putNInt(value: NInt)

    /**
     * Puts a pointer value into the buffer.
     *
     * @param value The pointer value to store
     */
    fun putPointer(value: Pointer)

    /**
     * Puts a float value into the buffer.
     *
     * @param value The float value to store
     */
    fun putFloat(value: Float)

    /**
     * Puts a double value into the buffer.
     *
     * @param value The double value to store
     */
    fun putDouble(value: Double)

    /**
     * Puts a byte array into the buffer.
     *
     * @param value The byte array to store
     */
    fun putBytes(value: ByteArray)

    /**
     * Puts a short array into the buffer.
     *
     * @param value The short array to store
     */
    fun putShorts(value: ShortArray)

    /**
     * Puts an int array into the buffer.
     *
     * @param value The int array to store
     */
    fun putInts(value: IntArray)

    /**
     * Puts a long array into the buffer.
     *
     * @param value The long array to store
     */
    fun putLongs(value: LongArray)

    /**
     * Puts a native integer array into the buffer.
     *
     * @param value The native integer array to store
     */
    fun putNInts(value: NIntArray)

    /**
     * Puts an unsigned byte value into the buffer.
     *
     * @param value The unsigned byte value to store
     */
    fun putUByte(value: UByte)

    /**
     * Puts an unsigned short value into the buffer.
     *
     * @param value The unsigned short value to store
     */
    fun putUShort(value: UShort)

    /**
     * Puts an unsigned int value into the buffer.
     *
     * @param value The unsigned int value to store
     */
    fun putUInt(value: UInt)

    /**
     * Puts an unsigned long value into the buffer.
     *
     * @param value The unsigned long value to store
     */
    fun putULong(value: ULong)

    /**
     * Puts a native unsigned integer value into the buffer.
     *
     * @param value The native unsigned integer value to store
     */
    fun putNUInt(value: NUInt)

    /**
     * Puts an unsigned byte array into the buffer.
     *
     * @param value The unsigned byte array to store
     */
    fun putUBytes(value: UByteArray)

    /**
     * Puts an unsigned short array into the buffer.
     *
     * @param value The unsigned short array to store
     */
    fun putUShorts(value: UShortArray)

    /**
     * Puts an unsigned int array into the buffer.
     *
     * @param value The unsigned int array to store
     */
    fun putUInts(value: UIntArray)

    /**
     * Puts an unsigned long array into the buffer.
     *
     * @param value The unsigned long array to store
     */
    fun putULongs(value: ULongArray)

    /**
     * Puts a native unsigned integer array into the buffer.
     *
     * @param value The native unsigned integer array to store
     */
    fun putNUInts(value: NUIntArray)

    /**
     * Puts a float array into the buffer.
     *
     * @param value The float array to store
     */
    fun putFloats(value: FloatArray)

    /**
     * Puts a double array into the buffer.
     *
     * @param value The double array to store
     */
    fun putDoubles(value: DoubleArray)

    /**
     * Puts an array of pointers into the buffer.
     *
     * @param value The pointer array to store
     */
    fun putPointers(value: PointerArray)
}

/**
 * Internal implementation of [FFIArgBuffer] that manages a memory buffer for storing arguments.
 *
 * This class allocates a memory buffer of [FFIArgBuffer.DEFAULT_SIZE] bytes and provides methods
 * for writing values of various types to the buffer. It tracks the current offset in the buffer
 * and the types of arguments stored.
 */
@PublishedApi
internal class FFIArgBufferImpl internal constructor() : FFIArgBuffer, AutoCloseable {
    /**
     * The memory address of the buffer.
     */
    override val address: Pointer = Memory.allocate(FFIArgBuffer.DEFAULT_SIZE.toNUInt())

    /**
     * The current offset in the buffer.
     */
    private var offset: NUInt = 0U.toNUInt()

    /**
     * List of types of arguments stored in the buffer.
     */
    override val types: ArrayList<FFIType> = ArrayList()

    init {
        // Register for cleanup on shutdown
        ShutdownHandler.register(this)
    }

    /**
     * Calculates the next offset in the buffer for storing a value of the given type.
     *
     * @param type The type of value to store
     * @return The offset in the buffer where the value should be stored
     * @throws IllegalStateException if the buffer would overflow
     */
    private fun nextOffset(type: FFIType, count: Int = 1): NUInt {
        val newOffset = offset + (type.size * count).toNUInt()
        check(newOffset < FFIArgBuffer.DEFAULT_SIZE.toNUInt()) { "Exceeded argument buffer limit" }
        val oldOffset = offset
        offset = newOffset
        types += type
        return oldOffset
    }

    /**
     * Calculates the next address in the buffer for storing a value of the given type.
     *
     * @param type The type of value to store
     * @param count The number of values to store (for arrays)
     * @return The address in the buffer where the value should be stored
     */
    private inline fun nextAddress(type: FFIType, count: Int = 1): Pointer = address + nextOffset(type, count)

    /**
     * Gets the memory address for the argument at the specified index in the buffer.
     *
     * @param index The index of the argument to get the address for
     * @return The memory address where the argument is stored
     */
    override fun getAddress(index: Int): Pointer {
        return address + types.take(index).sumOf { it.size }
    }

    /**
     * Puts a byte value into the buffer.
     *
     * @param value The byte value to store
     */
    override fun putByte(value: Byte) = Memory.writeByte(nextAddress(FFIType.BYTE), value)

    /**
     * Puts a short value into the buffer.
     *
     * @param value The short value to store
     */
    override fun putShort(value: Short) = Memory.writeShort(nextAddress(FFIType.SHORT), value)

    /**
     * Puts an int value into the buffer.
     *
     * @param value The int value to store
     */
    override fun putInt(value: Int) = Memory.writeInt(nextAddress(FFIType.INT), value)

    /**
     * Puts a long value into the buffer.
     *
     * @param value The long value to store
     */
    override fun putLong(value: Long) = Memory.writeLong(nextAddress(FFIType.LONG), value)

    /**
     * Puts a native integer value into the buffer.
     *
     * @param value The native integer value to store
     */
    override fun putNInt(value: NInt) = Memory.writeNInt(nextAddress(FFIType.NINT), value)

    /**
     * Puts a pointer value into the buffer.
     *
     * @param value The pointer value to store
     */
    override fun putPointer(value: Pointer) = Memory.writePointer(nextAddress(FFIType.PTR), value)

    /**
     * Puts an unsigned byte value into the buffer.
     *
     * @param value The unsigned byte value to store
     */
    override fun putUByte(value: UByte) = Memory.writeUByte(nextAddress(FFIType.UBYTE), value)

    /**
     * Puts an unsigned short value into the buffer.
     *
     * @param value The unsigned short value to store
     */
    override fun putUShort(value: UShort) = Memory.writeUShort(nextAddress(FFIType.USHORT), value)

    /**
     * Puts an unsigned int value into the buffer.
     *
     * @param value The unsigned int value to store
     */
    override fun putUInt(value: UInt) = Memory.writeUInt(nextAddress(FFIType.UINT), value)

    /**
     * Puts an unsigned long value into the buffer.
     *
     * @param value The unsigned long value to store
     */
    override fun putULong(value: ULong) = Memory.writeULong(nextAddress(FFIType.ULONG), value)

    /**
     * Puts a native unsigned integer value into the buffer.
     *
     * @param value The native unsigned integer value to store
     */
    override fun putNUInt(value: NUInt) = Memory.writeNUInt(nextAddress(FFIType.NUINT), value)

    /**
     * Puts a float value into the buffer.
     *
     * @param value The float value to store
     */
    override fun putFloat(value: Float) = Memory.writeFloat(nextAddress(FFIType.FLOAT), value)

    /**
     * Puts a double value into the buffer.
     *
     * @param value The double value to store
     */
    override fun putDouble(value: Double) = Memory.writeDouble(nextAddress(FFIType.DOUBLE), value)

    /**
     * Puts a byte array into the buffer.
     *
     * @param value The byte array to store
     */
    override fun putBytes(value: ByteArray) = Memory.writeBytes(nextAddress(FFIType.BYTE, value.size), value)

    /**
     * Puts a short array into the buffer.
     *
     * @param value The short array to store
     */
    override fun putShorts(value: ShortArray) = Memory.writeShorts(nextAddress(FFIType.SHORT, value.size), value)

    /**
     * Puts an int array into the buffer.
     *
     * @param value The int array to store
     */
    override fun putInts(value: IntArray) = Memory.writeInts(nextAddress(FFIType.INT, value.size), value)

    /**
     * Puts a long array into the buffer.
     *
     * @param value The long array to store
     */
    override fun putLongs(value: LongArray) = Memory.writeLongs(nextAddress(FFIType.LONG, value.size), value)

    /**
     * Puts a native integer array into the buffer.
     *
     * @param value The native integer array to store
     */
    override fun putNInts(value: NIntArray) = Memory.writeNInts(nextAddress(FFIType.NINT, value.size), value)

    /**
     * Puts an unsigned byte array into the buffer.
     *
     * @param value The unsigned byte array to store
     */
    override fun putUBytes(value: UByteArray) = Memory.writeUBytes(nextAddress(FFIType.UBYTE, value.size), value)

    /**
     * Puts an unsigned short array into the buffer.
     *
     * @param value The unsigned short array to store
     */
    override fun putUShorts(value: UShortArray) = Memory.writeUShorts(nextAddress(FFIType.USHORT, value.size), value)

    /**
     * Puts an unsigned int array into the buffer.
     *
     * @param value The unsigned int array to store
     */
    override fun putUInts(value: UIntArray) = Memory.writeUInts(nextAddress(FFIType.UINT, value.size), value)

    /**
     * Puts an unsigned long array into the buffer.
     *
     * @param value The unsigned long array to store
     */
    override fun putULongs(value: ULongArray) = Memory.writeULongs(nextAddress(FFIType.ULONG, value.size), value)

    /**
     * Puts a native unsigned integer array into the buffer.
     *
     * @param value The native unsigned integer array to store
     */
    override fun putNUInts(value: NUIntArray) = Memory.writeNUInts(nextAddress(FFIType.NUINT, value.size), value)

    /**
     * Puts a float array into the buffer.
     *
     * @param value The float array to store
     */
    override fun putFloats(value: FloatArray) = Memory.writeFloats(nextAddress(FFIType.FLOAT, value.size), value)

    /**
     * Puts a double array into the buffer.
     *
     * @param value The double array to store
     */
    override fun putDoubles(value: DoubleArray) = Memory.writeDoubles(nextAddress(FFIType.DOUBLE, value.size), value)

    /**
     * Puts an array of pointers into the buffer.
     *
     * @param value The pointer array to store
     */
    override fun putPointers(value: PointerArray) = Memory.writePointers(nextAddress(FFIType.PTR, value.size), value)

    /**
     * Clears the buffer by resetting the offset and types list.
     *
     * @return This buffer instance
     */
    @PublishedApi
    internal fun clear(): FFIArgBufferImpl {
        if (offset == 0U.toNUInt()) return this
        offset = 0U.toNUInt()
        types.clear()
        return this
    }

    /**
     * Frees the memory allocated for the buffer.
     */
    override fun close() {
        Memory.free(address)
        types.clear()
    }
}

/**
 * Type alias for a function with [FFIArgBuffer] as its receiver.
 *
 * This is used to specify arguments to native function calls in a type-safe and convenient way.
 * For example:
 * ```
 * FFI.call(address, descriptor) {
 *     putInt(42)
 *     putFloat(3.14f)
 * }
 * ```
 */
typealias FFIArgSpec = FFIArgBuffer.() -> Unit
