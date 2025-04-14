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

package dev.karmakrafts.kwire

import kotlin.jvm.JvmInline

/**
 * Provides strongly-typed pointer implementations for various primitive data types.
 *
 * This file contains value classes that wrap a [Pointer] and provide type-specific
 * operations for different primitive data types. These typed pointers enable type-safe
 * memory access and pointer arithmetic, making it easier to work with different data
 * types in memory.
 *
 * The typed pointers are divided into three categories:
 * - Signed integer types: [BytePtr], [ShortPtr], [IntPtr], [LongPtr], [NIntPtr]
 * - Unsigned integer types: [UBytePtr], [UShortPtr], [UIntPtr], [ULongPtr], [NUIntPtr]
 * - IEEE-754 floating-point types: [FloatPtr], [DoublePtr]
 *
 * Each typed pointer provides:
 * - Pointer arithmetic operations (plus, minus) for different numeric types
 * - Indexing operations (get, set) for accessing array elements
 * - Type reinterpretation through the [Reinterpretable.reinterpret] method
 * - Resource management through the [AutoCloseable] interface
 */

// Signed

/**
 * A strongly-typed pointer for byte (8-bit signed integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for byte values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class BytePtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a byte.
     *
     * @param other The native unsigned integer value to add
     * @return A new byte pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): BytePtr = BytePtr(value + other * Byte.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a byte.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new byte pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): BytePtr = BytePtr(value - other * Byte.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a byte.
     *
     * @param other The unsigned long value to add
     * @return A new byte pointer with the resulting address
     */
    inline operator fun plus(other: ULong): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a byte.
     *
     * @param other The unsigned long value to subtract
     * @return A new byte pointer with the resulting address
     */
    inline operator fun minus(other: ULong): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a byte.
     *
     * @param other The unsigned integer value to add
     * @return A new byte pointer with the resulting address
     */
    inline operator fun plus(other: UInt): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a byte.
     *
     * @param other The unsigned integer value to subtract
     * @return A new byte pointer with the resulting address
     */
    inline operator fun minus(other: UInt): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a byte.
     *
     * @param other The long value to add
     * @return A new byte pointer with the resulting address
     */
    inline operator fun plus(other: Long): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a byte.
     *
     * @param other The long value to subtract
     * @return A new byte pointer with the resulting address
     */
    inline operator fun minus(other: Long): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a byte.
     *
     * @param other The integer value to add
     * @return A new byte pointer with the resulting address
     */
    inline operator fun plus(other: Int): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a byte.
     *
     * @param other The integer value to subtract
     * @return A new byte pointer with the resulting address
     */
    inline operator fun minus(other: Int): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one byte.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one byte. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): BytePtr = BytePtr(value + Byte.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one byte.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one byte. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): BytePtr = BytePtr(value - Byte.SIZE_BYTES.toNUInt())

    /**
     * Accesses the byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a byte array and indexing into it.
     *
     * @param index The integer index of the byte to access
     * @return The byte value at the specified index
     */
    inline operator fun get(index: Int): Byte = Memory.readByte(value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt())

    /**
     * Sets the byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a byte array and indexing into it.
     *
     * @param index The integer index of the byte to modify
     * @param value The new byte value to set
     */
    inline operator fun set(index: Int, value: Byte) =
        Memory.writeByte(this.value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a byte array and indexing into it with a long index.
     *
     * @param index The long index of the byte to access
     * @return The byte value at the specified index
     */
    inline operator fun get(index: Long): Byte = Memory.readByte(value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt())

    /**
     * Sets the byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a byte array and indexing into it with a long index.
     *
     * @param index The long index of the byte to modify
     * @param value The new byte value to set
     */
    inline operator fun set(index: Long, value: Byte) =
        Memory.writeByte(this.value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a byte array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the byte to access
     * @return The byte value at the specified index
     */
    inline operator fun get(index: NUInt): Byte = Memory.readByte(value + index * Byte.SIZE_BYTES.toNUInt())

    /**
     * Sets the byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a byte array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the byte to modify
     * @param value The new byte value to set
     */
    inline operator fun set(index: NUInt, value: Byte) =
        Memory.writeByte(this.value + index * Byte.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> this
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a byte pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [BytePtr] for type-safe memory access to byte values.
 *
 * @return A [BytePtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asBytePtr(): BytePtr = BytePtr(this)

/**
 * A strongly-typed pointer for short (16-bit signed integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for short values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class ShortPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a short.
     *
     * @param other The native unsigned integer value to add
     * @return A new short pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): ShortPtr = ShortPtr(value + other * Short.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a short.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new short pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): ShortPtr = ShortPtr(value - other * Short.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a short.
     *
     * @param other The unsigned long value to add
     * @return A new short pointer with the resulting address
     */
    inline operator fun plus(other: ULong): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a short.
     *
     * @param other The unsigned long value to subtract
     * @return A new short pointer with the resulting address
     */
    inline operator fun minus(other: ULong): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a short.
     *
     * @param other The unsigned integer value to add
     * @return A new short pointer with the resulting address
     */
    inline operator fun plus(other: UInt): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a short.
     *
     * @param other The unsigned integer value to subtract
     * @return A new short pointer with the resulting address
     */
    inline operator fun minus(other: UInt): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a short.
     *
     * @param other The long value to add
     * @return A new short pointer with the resulting address
     */
    inline operator fun plus(other: Long): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a short.
     *
     * @param other The long value to subtract
     * @return A new short pointer with the resulting address
     */
    inline operator fun minus(other: Long): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a short.
     *
     * @param other The integer value to add
     * @return A new short pointer with the resulting address
     */
    inline operator fun plus(other: Int): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a short.
     *
     * @param other The integer value to subtract
     * @return A new short pointer with the resulting address
     */
    inline operator fun minus(other: Int): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one short.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one short. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): ShortPtr = ShortPtr(value + Short.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one short.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one short. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): ShortPtr = ShortPtr(value - Short.SIZE_BYTES.toNUInt())

    /**
     * Accesses the short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a short array and indexing into it.
     *
     * @param index The integer index of the short to access
     * @return The short value at the specified index
     */
    inline operator fun get(index: Int): Short = Memory.readShort(value + index.toNUInt() * Short.SIZE_BYTES.toNUInt())

    /**
     * Sets the short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a short array and indexing into it.
     *
     * @param index The integer index of the short to modify
     * @param value The new short value to set
     */
    inline operator fun set(index: Int, value: Short) =
        Memory.writeShort(this.value + index.toNUInt() * Short.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a short array and indexing into it with a long index.
     *
     * @param index The long index of the short to access
     * @return The short value at the specified index
     */
    inline operator fun get(index: Long): Short = Memory.readShort(value + index.toNUInt() * Short.SIZE_BYTES.toNUInt())

    /**
     * Sets the short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a short array and indexing into it with a long index.
     *
     * @param index The long index of the short to modify
     * @param value The new short value to set
     */
    inline operator fun set(index: Long, value: Short) =
        Memory.writeShort(this.value + index.toNUInt() * Short.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a short array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the short to access
     * @return The short value at the specified index
     */
    inline operator fun get(index: NUInt): Short = Memory.readShort(value + index * Short.SIZE_BYTES.toNUInt())

    /**
     * Sets the short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a short array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the short to modify
     * @param value The new short value to set
     */
    inline operator fun set(index: NUInt, value: Short) =
        Memory.writeShort(this.value + index * Short.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> this
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a short pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [ShortPtr] for type-safe memory access to short values.
 *
 * @return A [ShortPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asShortPtr(): ShortPtr = ShortPtr(this)

/**
 * A strongly-typed pointer for int (32-bit signed integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for int values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class IntPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of an int.
     *
     * @param other The native unsigned integer value to add
     * @return A new int pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): IntPtr = IntPtr(value + other * Int.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of an int.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new int pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): IntPtr = IntPtr(value - other * Int.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of an int.
     *
     * @param other The unsigned long value to add
     * @return A new int pointer with the resulting address
     */
    inline operator fun plus(other: ULong): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of an int.
     *
     * @param other The unsigned long value to subtract
     * @return A new int pointer with the resulting address
     */
    inline operator fun minus(other: ULong): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of an int.
     *
     * @param other The unsigned integer value to add
     * @return A new int pointer with the resulting address
     */
    inline operator fun plus(other: UInt): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of an int.
     *
     * @param other The unsigned integer value to subtract
     * @return A new int pointer with the resulting address
     */
    inline operator fun minus(other: UInt): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of an int.
     *
     * @param other The long value to add
     * @return A new int pointer with the resulting address
     */
    inline operator fun plus(other: Long): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of an int.
     *
     * @param other The long value to subtract
     * @return A new int pointer with the resulting address
     */
    inline operator fun minus(other: Long): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of an int.
     *
     * @param other The integer value to add
     * @return A new int pointer with the resulting address
     */
    inline operator fun plus(other: Int): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of an int.
     *
     * @param other The integer value to subtract
     * @return A new int pointer with the resulting address
     */
    inline operator fun minus(other: Int): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one int.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one int. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): IntPtr = IntPtr(value + Int.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one int.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one int. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): IntPtr = IntPtr(value - Int.SIZE_BYTES.toNUInt())

    /**
     * Accesses the int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an int array and indexing into it.
     *
     * @param index The integer index of the int to access
     * @return The int value at the specified index
     */
    inline operator fun get(index: Int): Int = Memory.readInt(value + index.toNUInt() * Int.SIZE_BYTES.toNUInt())

    /**
     * Sets the int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an int array and indexing into it.
     *
     * @param index The integer index of the int to modify
     * @param value The new int value to set
     */
    inline operator fun set(index: Int, value: Int) =
        Memory.writeInt(this.value + index.toNUInt() * Int.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an int array and indexing into it with a long index.
     *
     * @param index The long index of the int to access
     * @return The int value at the specified index
     */
    inline operator fun get(index: Long): Int = Memory.readInt(value + index.toNUInt() * Int.SIZE_BYTES.toNUInt())

    /**
     * Sets the int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an int array and indexing into it with a long index.
     *
     * @param index The long index of the int to modify
     * @param value The new int value to set
     */
    inline operator fun set(index: Long, value: Int) =
        Memory.writeInt(this.value + index.toNUInt() * Int.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an int array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the int to access
     * @return The int value at the specified index
     */
    inline operator fun get(index: NUInt): Int = Memory.readInt(value + index * Int.SIZE_BYTES.toNUInt())

    /**
     * Sets the int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an int array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the int to modify
     * @param value The new int value to set
     */
    inline operator fun set(index: NUInt, value: Int) =
        Memory.writeInt(this.value + index * Int.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> this
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as an int pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [IntPtr] for type-safe memory access to int values.
 *
 * @return An [IntPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asIntPtr(): IntPtr = IntPtr(this)

/**
 * A strongly-typed pointer for long (64-bit signed integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for long values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class LongPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a long.
     *
     * @param other The native unsigned integer value to add
     * @return A new long pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): LongPtr = LongPtr(value + other * Long.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a long.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new long pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): LongPtr = LongPtr(value - other * Long.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a long.
     *
     * @param other The unsigned long value to add
     * @return A new long pointer with the resulting address
     */
    inline operator fun plus(other: ULong): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a long.
     *
     * @param other The unsigned long value to subtract
     * @return A new long pointer with the resulting address
     */
    inline operator fun minus(other: ULong): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a long.
     *
     * @param other The unsigned integer value to add
     * @return A new long pointer with the resulting address
     */
    inline operator fun plus(other: UInt): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a long.
     *
     * @param other The unsigned integer value to subtract
     * @return A new long pointer with the resulting address
     */
    inline operator fun minus(other: UInt): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a long.
     *
     * @param other The long value to add
     * @return A new long pointer with the resulting address
     */
    inline operator fun plus(other: Long): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a long.
     *
     * @param other The long value to subtract
     * @return A new long pointer with the resulting address
     */
    inline operator fun minus(other: Long): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a long.
     *
     * @param other The integer value to add
     * @return A new long pointer with the resulting address
     */
    inline operator fun plus(other: Int): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a long.
     *
     * @param other The integer value to subtract
     * @return A new long pointer with the resulting address
     */
    inline operator fun minus(other: Int): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one long.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one long. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): LongPtr = LongPtr(value + Long.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one long.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one long. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): LongPtr = LongPtr(value - Long.SIZE_BYTES.toNUInt())

    /**
     * Accesses the long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a long array and indexing into it.
     *
     * @param index The integer index of the long to access
     * @return The long value at the specified index
     */
    inline operator fun get(index: Int): Long = Memory.readLong(value + index.toNUInt() * Long.SIZE_BYTES.toNUInt())

    /**
     * Sets the long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a long array and indexing into it.
     *
     * @param index The integer index of the long to modify
     * @param value The new long value to set
     */
    inline operator fun set(index: Int, value: Long) =
        Memory.writeLong(this.value + index.toNUInt() * Long.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a long array and indexing into it with a long index.
     *
     * @param index The long index of the long to access
     * @return The long value at the specified index
     */
    inline operator fun get(index: Long): Long = Memory.readLong(value + index.toNUInt() * Long.SIZE_BYTES.toNUInt())

    /**
     * Sets the long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a long array and indexing into it with a long index.
     *
     * @param index The long index of the long to modify
     * @param value The new long value to set
     */
    inline operator fun set(index: Long, value: Long) =
        Memory.writeLong(this.value + index.toNUInt() * Long.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a long array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the long to access
     * @return The long value at the specified index
     */
    inline operator fun get(index: NUInt): Long = Memory.readLong(value + index * Long.SIZE_BYTES.toNUInt())

    /**
     * Sets the long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a long array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the long to modify
     * @param value The new long value to set
     */
    inline operator fun set(index: NUInt, value: Long) =
        Memory.writeLong(this.value + index * Long.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> this
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a long pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [LongPtr] for type-safe memory access to long values.
 *
 * @return A [LongPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asLongPtr(): LongPtr = LongPtr(this)

/**
 * A strongly-typed pointer for native integer (platform-dependent signed integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for native integer values,
 * including pointer arithmetic, array indexing, and type reinterpretation. The size of a native
 * integer depends on the platform (typically 32 bits on 32-bit platforms and 64 bits on 64-bit platforms).
 * It implements [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class NIntPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a native integer.
     *
     * @param other The native unsigned integer value to add
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): NIntPtr = NIntPtr(value + other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a native integer.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): NIntPtr = NIntPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a native integer.
     *
     * @param other The unsigned long value to add
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun plus(other: ULong): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a native integer.
     *
     * @param other The unsigned long value to subtract
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun minus(other: ULong): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a native integer.
     *
     * @param other The unsigned integer value to add
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun plus(other: UInt): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a native integer.
     *
     * @param other The unsigned integer value to subtract
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun minus(other: UInt): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a native integer.
     *
     * @param other The long value to add
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun plus(other: Long): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a native integer.
     *
     * @param other The long value to subtract
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun minus(other: Long): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a native integer.
     *
     * @param other The integer value to add
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun plus(other: Int): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a native integer.
     *
     * @param other The integer value to subtract
     * @return A new native integer pointer with the resulting address
     */
    inline operator fun minus(other: Int): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one native integer.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one native integer. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): NIntPtr = NIntPtr(value + Pointer.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one native integer.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one native integer. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): NIntPtr = NIntPtr(value - Pointer.SIZE_BYTES.toNUInt())

    /**
     * Accesses the native integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native integer array and indexing into it.
     *
     * @param index The integer index of the native integer to access
     * @return The native integer value at the specified index
     */
    inline operator fun get(index: Int): NInt = Memory.readNInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native integer array and indexing into it.
     *
     * @param index The integer index of the native integer to modify
     * @param value The new native integer value to set
     */
    inline operator fun set(index: Int, value: NInt) =
        Memory.writeNInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the native integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native integer array and indexing into it with a long index.
     *
     * @param index The long index of the native integer to access
     * @return The native integer value at the specified index
     */
    inline operator fun get(index: Long): NInt = Memory.readNInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native integer array and indexing into it with a long index.
     *
     * @param index The long index of the native integer to modify
     * @param value The new native integer value to set
     */
    inline operator fun set(index: Long, value: NInt) =
        Memory.writeNInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the native integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native integer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native integer to access
     * @return The native integer value at the specified index
     */
    inline operator fun get(index: NUInt): NInt = Memory.readNInt(value + index * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native integer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native integer to modify
     * @param value The new native integer value to set
     */
    inline operator fun set(index: NUInt, value: NInt) =
        Memory.writeNInt(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> this
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a native integer pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [NIntPtr] for type-safe memory access to native integer values.
 *
 * @return An [NIntPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asNIntPtr(): NIntPtr = NIntPtr(this)

// Unsigned

/**
 * A strongly-typed pointer for unsigned byte (8-bit unsigned integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for unsigned byte values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class UBytePtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The native unsigned integer value to add
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): UBytePtr = UBytePtr(value + other * UByte.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): UBytePtr = UBytePtr(value - other * UByte.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The unsigned long value to add
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun plus(other: ULong): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The unsigned long value to subtract
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun minus(other: ULong): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The unsigned integer value to add
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun plus(other: UInt): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The unsigned integer value to subtract
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun minus(other: UInt): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The long value to add
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun plus(other: Long): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The long value to subtract
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun minus(other: Long): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The integer value to add
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun plus(other: Int): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of an unsigned byte.
     *
     * @param other The integer value to subtract
     * @return A new unsigned byte pointer with the resulting address
     */
    inline operator fun minus(other: Int): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one unsigned byte.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one unsigned byte. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): UBytePtr = UBytePtr(value + UByte.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one unsigned byte.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one unsigned byte. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): UBytePtr = UBytePtr(value - UByte.SIZE_BYTES.toNUInt())

    /**
     * Accesses the unsigned byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it.
     *
     * @param index The integer index of the unsigned byte to access
     * @return The unsigned byte value at the specified index
     */
    inline operator fun get(index: Int): UByte = Memory.readUByte(value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it.
     *
     * @param index The integer index of the unsigned byte to modify
     * @param value The new unsigned byte value to set
     */
    inline operator fun set(index: Int, value: UByte) =
        Memory.writeUByte(this.value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned byte to access
     * @return The unsigned byte value at the specified index
     */
    inline operator fun get(index: Long): UByte = Memory.readUByte(value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned byte to modify
     * @param value The new unsigned byte value to set
     */
    inline operator fun set(index: Long, value: UByte) =
        Memory.writeUByte(this.value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned byte value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned byte to access
     * @return The unsigned byte value at the specified index
     */
    inline operator fun get(index: NUInt): UByte = Memory.readUByte(value + index * UByte.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned byte value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned byte array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned byte to modify
     * @param value The new unsigned byte value to set
     */
    inline operator fun set(index: NUInt, value: UByte) =
        Memory.writeUByte(this.value + index * UByte.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> this
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as an unsigned byte pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [UBytePtr] for type-safe memory access to unsigned byte values.
 *
 * @return A [UBytePtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asUBytePtr(): UBytePtr = UBytePtr(this)

/**
 * A strongly-typed pointer for unsigned short (16-bit unsigned integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for unsigned short values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class UShortPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The native unsigned integer value to add
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): UShortPtr = UShortPtr(value + other * UShort.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): UShortPtr = UShortPtr(value - other * UShort.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The unsigned long value to add
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun plus(other: ULong): UShortPtr =
        UShortPtr(value + (other * UShort.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The unsigned long value to subtract
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun minus(other: ULong): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The unsigned integer value to add
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun plus(other: UInt): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The unsigned integer value to subtract
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun minus(other: UInt): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The long value to add
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun plus(other: Long): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The long value to subtract
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun minus(other: Long): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The integer value to add
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun plus(other: Int): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of an unsigned short.
     *
     * @param other The integer value to subtract
     * @return A new unsigned short pointer with the resulting address
     */
    inline operator fun minus(other: Int): UShortPtr = UShortPtr(value - (other * UShort.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one unsigned short.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one unsigned short. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): UShortPtr = UShortPtr(value + UShort.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one unsigned short.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one unsigned short. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): UShortPtr = UShortPtr(value - UShort.SIZE_BYTES.toNUInt())

    /**
     * Accesses the unsigned short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned short array and indexing into it.
     *
     * @param index The integer index of the unsigned short to access
     * @return The unsigned short value at the specified index
     */
    inline operator fun get(index: Int): UShort =
        Memory.readUShort(value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned short array and indexing into it.
     *
     * @param index The integer index of the unsigned short to modify
     * @param value The new unsigned short value to set
     */
    inline operator fun set(index: Int, value: UShort) =
        Memory.writeUShort(this.value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned short array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned short to access
     * @return The unsigned short value at the specified index
     */
    inline operator fun get(index: Long): UShort =
        Memory.readUShort(value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned short array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned short to modify
     * @param value The new unsigned short value to set
     */
    inline operator fun set(index: Long, value: UShort) =
        Memory.writeUShort(this.value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned short value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned short array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned short to access
     * @return The unsigned short value at the specified index
     */
    inline operator fun get(index: NUInt): UShort = Memory.readUShort(value + index * UShort.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned short value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned short array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned short to modify
     * @param value The new unsigned short value to set
     */
    inline operator fun set(index: NUInt, value: UShort) =
        Memory.writeUShort(this.value + index * UShort.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> this
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as an unsigned short pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [UShortPtr] for type-safe memory access to unsigned short values.
 *
 * @return A [UShortPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asUShortPtr(): UShortPtr = UShortPtr(this)

/**
 * A strongly-typed pointer for unsigned int (32-bit unsigned integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for unsigned int values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class UIntPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The native unsigned integer value to add
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): UIntPtr = UIntPtr(value + other * UInt.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): UIntPtr = UIntPtr(value - other * UInt.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The unsigned long value to add
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun plus(other: ULong): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The unsigned long value to subtract
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun minus(other: ULong): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The unsigned integer value to add
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun plus(other: UInt): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The unsigned integer value to subtract
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun minus(other: UInt): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The long value to add
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun plus(other: Long): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The long value to subtract
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun minus(other: Long): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The integer value to add
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun plus(other: Int): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of an unsigned int.
     *
     * @param other The integer value to subtract
     * @return A new unsigned int pointer with the resulting address
     */
    inline operator fun minus(other: Int): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one unsigned int.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one unsigned int. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): UIntPtr = UIntPtr(value + UInt.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one unsigned int.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one unsigned int. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): UIntPtr = UIntPtr(value - UInt.SIZE_BYTES.toNUInt())

    /**
     * Accesses the unsigned int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned int array and indexing into it.
     *
     * @param index The integer index of the unsigned int to access
     * @return The unsigned int value at the specified index
     */
    inline operator fun get(index: Int): UInt = Memory.readUInt(value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned int array and indexing into it.
     *
     * @param index The integer index of the unsigned int to modify
     * @param value The new unsigned int value to set
     */
    inline operator fun set(index: Int, value: UInt) =
        Memory.writeUInt(this.value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned int array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned int to access
     * @return The unsigned int value at the specified index
     */
    inline operator fun get(index: Long): UInt = Memory.readUInt(value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned int array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned int to modify
     * @param value The new unsigned int value to set
     */
    inline operator fun set(index: Long, value: UInt) =
        Memory.writeUInt(this.value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned int value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned int array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned int to access
     * @return The unsigned int value at the specified index
     */
    inline operator fun get(index: NUInt): UInt = Memory.readUInt(value + index * UInt.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned int value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned int array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned int to modify
     * @param value The new unsigned int value to set
     */
    inline operator fun set(index: NUInt, value: UInt) =
        Memory.writeUInt(this.value + index * UInt.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> this
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as an unsigned int pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [UIntPtr] for type-safe memory access to unsigned int values.
 *
 * @return A [UIntPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asUIntPtr(): UIntPtr = UIntPtr(this)

/**
 * A strongly-typed pointer for unsigned long (64-bit unsigned integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for unsigned long values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class ULongPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The native unsigned integer value to add
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): ULongPtr = ULongPtr(value + other * ULong.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): ULongPtr = ULongPtr(value - other * ULong.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The unsigned long value to add
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun plus(other: ULong): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The unsigned long value to subtract
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun minus(other: ULong): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The unsigned integer value to add
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun plus(other: UInt): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The unsigned integer value to subtract
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun minus(other: UInt): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The long value to add
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun plus(other: Long): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The long value to subtract
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun minus(other: Long): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The integer value to add
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun plus(other: Int): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of an unsigned long.
     *
     * @param other The integer value to subtract
     * @return A new unsigned long pointer with the resulting address
     */
    inline operator fun minus(other: Int): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one unsigned long.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one unsigned long. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): ULongPtr = ULongPtr(value + ULong.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one unsigned long.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one unsigned long. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): ULongPtr = ULongPtr(value - ULong.SIZE_BYTES.toNUInt())

    /**
     * Accesses the unsigned long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned long array and indexing into it.
     *
     * @param index The integer index of the unsigned long to access
     * @return The unsigned long value at the specified index
     */
    inline operator fun get(index: Int): ULong = Memory.readULong(value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned long array and indexing into it.
     *
     * @param index The integer index of the unsigned long to modify
     * @param value The new unsigned long value to set
     */
    inline operator fun set(index: Int, value: ULong) =
        Memory.writeULong(this.value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned long array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned long to access
     * @return The unsigned long value at the specified index
     */
    inline operator fun get(index: Long): ULong = Memory.readULong(value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned long array and indexing into it with a long index.
     *
     * @param index The long index of the unsigned long to modify
     * @param value The new unsigned long value to set
     */
    inline operator fun set(index: Long, value: ULong) =
        Memory.writeULong(this.value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the unsigned long value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of an unsigned long array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned long to access
     * @return The unsigned long value at the specified index
     */
    inline operator fun get(index: NUInt): ULong = Memory.readULong(value + index * ULong.SIZE_BYTES.toNUInt())

    /**
     * Sets the unsigned long value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of an unsigned long array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the unsigned long to modify
     * @param value The new unsigned long value to set
     */
    inline operator fun set(index: NUInt, value: ULong) =
        Memory.writeULong(this.value + index * ULong.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> this
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as an unsigned long pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [ULongPtr] for type-safe memory access to unsigned long values.
 *
 * @return A [ULongPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asULongPtr(): ULongPtr = ULongPtr(this)

/**
 * A strongly-typed pointer for native unsigned integer (platform-dependent unsigned integer) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for native unsigned integer values,
 * including pointer arithmetic, array indexing, and type reinterpretation. The size of a native
 * unsigned integer depends on the platform (typically 32 bits on 32-bit platforms and 64 bits on 64-bit platforms).
 * It implements [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class NUIntPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The native unsigned integer value to add
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): NUIntPtr = NUIntPtr(value + other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): NUIntPtr = NUIntPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The unsigned long value to add
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun plus(other: ULong): NUIntPtr =
        NUIntPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The unsigned long value to subtract
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun minus(other: ULong): NUIntPtr =
        NUIntPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The unsigned integer value to add
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun plus(other: UInt): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The unsigned integer value to subtract
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun minus(other: UInt): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The long value to add
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun plus(other: Long): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The long value to subtract
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun minus(other: Long): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The integer value to add
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun plus(other: Int): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a native unsigned integer.
     *
     * @param other The integer value to subtract
     * @return A new native unsigned integer pointer with the resulting address
     */
    inline operator fun minus(other: Int): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one native unsigned integer.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one native unsigned integer. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): NUIntPtr = NUIntPtr(value + Pointer.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one native unsigned integer.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one native unsigned integer. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): NUIntPtr = NUIntPtr(value - Pointer.SIZE_BYTES.toNUInt())

    /**
     * Accesses the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it.
     *
     * @param index The integer index of the native unsigned integer to access
     * @return The native unsigned integer value at the specified index
     */
    inline operator fun get(index: Int): NUInt =
        Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it.
     *
     * @param index The integer index of the native unsigned integer to modify
     * @param value The new native unsigned integer value to set
     */
    inline operator fun set(index: Int, value: NUInt) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it with a long index.
     *
     * @param index The long index of the native unsigned integer to access
     * @return The native unsigned integer value at the specified index
     */
    inline operator fun get(index: Long): NUInt =
        Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it with a long index.
     *
     * @param index The long index of the native unsigned integer to modify
     * @param value The new native unsigned integer value to set
     */
    inline operator fun set(index: Long, value: NUInt) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native unsigned integer to access
     * @return The native unsigned integer value at the specified index
     */
    inline operator fun get(index: NUInt): NUInt = Memory.readNUInt(value + index * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets the native unsigned integer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native unsigned integer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native unsigned integer to modify
     * @param value The new native unsigned integer value to set
     */
    inline operator fun set(index: NUInt, value: NUInt) =
        Memory.writeNUInt(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> this
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a native unsigned integer pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [NUIntPtr] for type-safe memory access to native unsigned integer values.
 *
 * @return A [NUIntPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asNUIntPtr(): NUIntPtr = NUIntPtr(this)

// IEEE-754

/**
 * A strongly-typed pointer for float (32-bit floating-point) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for float values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class FloatPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a float.
     *
     * @param other The native unsigned integer value to add
     * @return A new float pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): FloatPtr = FloatPtr(value + other * Float.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a float.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new float pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): FloatPtr = FloatPtr(value - other * Float.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a float.
     *
     * @param other The unsigned long value to add
     * @return A new float pointer with the resulting address
     */
    inline operator fun plus(other: ULong): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a float.
     *
     * @param other The unsigned long value to subtract
     * @return A new float pointer with the resulting address
     */
    inline operator fun minus(other: ULong): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a float.
     *
     * @param other The unsigned integer value to add
     * @return A new float pointer with the resulting address
     */
    inline operator fun plus(other: UInt): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a float.
     *
     * @param other The unsigned integer value to subtract
     * @return A new float pointer with the resulting address
     */
    inline operator fun minus(other: UInt): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a float.
     *
     * @param other The long value to add
     * @return A new float pointer with the resulting address
     */
    inline operator fun plus(other: Long): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a float.
     *
     * @param other The long value to subtract
     * @return A new float pointer with the resulting address
     */
    inline operator fun minus(other: Long): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a float.
     *
     * @param other The integer value to add
     * @return A new float pointer with the resulting address
     */
    inline operator fun plus(other: Int): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a float.
     *
     * @param other The integer value to subtract
     * @return A new float pointer with the resulting address
     */
    inline operator fun minus(other: Int): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one float.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one float. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): FloatPtr = FloatPtr(value + Float.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one float.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one float. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): FloatPtr = FloatPtr(value - Float.SIZE_BYTES.toNUInt())

    /**
     * Accesses the float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a float array and indexing into it.
     *
     * @param index The integer index of the float to access
     * @return The float value at the specified index
     */
    inline operator fun get(index: Int): Float = Memory.readFloat(value + index.toNUInt() * Float.SIZE_BYTES.toNUInt())

    /**
     * Sets the float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a float array and indexing into it.
     *
     * @param index The integer index of the float to modify
     * @param value The new float value to set
     */
    inline operator fun set(index: Int, value: Float) =
        Memory.writeFloat(this.value + index.toNUInt() * Float.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a float array and indexing into it with a long index.
     *
     * @param index The long index of the float to access
     * @return The float value at the specified index
     */
    inline operator fun get(index: Long): Float = Memory.readFloat(value + index.toNUInt() * Float.SIZE_BYTES.toNUInt())

    /**
     * Sets the float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a float array and indexing into it with a long index.
     *
     * @param index The long index of the float to modify
     * @param value The new float value to set
     */
    inline operator fun set(index: Long, value: Float) =
        Memory.writeFloat(this.value + index.toNUInt() * Float.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a float array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the float to access
     * @return The float value at the specified index
     */
    inline operator fun get(index: NUInt): Float = Memory.readFloat(value + index * Float.SIZE_BYTES.toNUInt())

    /**
     * Sets the float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a float array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the float to modify
     * @param value The new float value to set
     */
    inline operator fun set(index: NUInt, value: Float) =
        Memory.writeFloat(this.value + index * Float.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> this
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a float pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [FloatPtr] for type-safe memory access to float values.
 *
 * @return A [FloatPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asFloatPtr(): FloatPtr = FloatPtr(this)

/**
 * A strongly-typed pointer for double (64-bit floating-point) values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for double values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class DoublePtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a double.
     *
     * @param other The native unsigned integer value to add
     * @return A new double pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): DoublePtr = DoublePtr(value + other * Double.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a double.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new double pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): DoublePtr = DoublePtr(value - other * Double.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a double.
     *
     * @param other The unsigned long value to add
     * @return A new double pointer with the resulting address
     */
    inline operator fun plus(other: ULong): DoublePtr =
        DoublePtr(value + (other * Double.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a double.
     *
     * @param other The unsigned long value to subtract
     * @return A new double pointer with the resulting address
     */
    inline operator fun minus(other: ULong): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a double.
     *
     * @param other The unsigned integer value to add
     * @return A new double pointer with the resulting address
     */
    inline operator fun plus(other: UInt): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a double.
     *
     * @param other The unsigned integer value to subtract
     * @return A new double pointer with the resulting address
     */
    inline operator fun minus(other: UInt): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a double.
     *
     * @param other The long value to add
     * @return A new double pointer with the resulting address
     */
    inline operator fun plus(other: Long): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a double.
     *
     * @param other The long value to subtract
     * @return A new double pointer with the resulting address
     */
    inline operator fun minus(other: Long): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toLong()).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a double.
     *
     * @param other The integer value to add
     * @return A new double pointer with the resulting address
     */
    inline operator fun plus(other: Int): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a double.
     *
     * @param other The integer value to subtract
     * @return A new double pointer with the resulting address
     */
    inline operator fun minus(other: Int): DoublePtr = DoublePtr(value - (other * Double.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one double.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one double. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): DoublePtr = DoublePtr(value + Double.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one double.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one double. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): DoublePtr = DoublePtr(value - Double.SIZE_BYTES.toNUInt())

    /**
     * Accesses the double value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a double array and indexing into it.
     *
     * @param index The integer index of the double to access
     * @return The double value at the specified index
     */
    inline operator fun get(index: Int): Double =
        Memory.readDouble(value + index.toNUInt() * Double.SIZE_BYTES.toNUInt())

    /**
     * Sets the double value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a double array and indexing into it.
     *
     * @param index The integer index of the double to modify
     * @param value The new double value to set
     */
    inline operator fun set(index: Int, value: Double) =
        Memory.writeDouble(this.value + index.toNUInt() * Double.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the double value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a double array and indexing into it with a long index.
     *
     * @param index The long index of the double to access
     * @return The double value at the specified index
     */
    inline operator fun get(index: Long): Double =
        Memory.readDouble(value + index.toNUInt() * Double.SIZE_BYTES.toNUInt())

    /**
     * Sets the double value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a double array and indexing into it with a long index.
     *
     * @param index The long index of the double to modify
     * @param value The new double value to set
     */
    inline operator fun set(index: Long, value: Double) =
        Memory.writeDouble(this.value + index.toNUInt() * Double.SIZE_BYTES.toNUInt(), value)

    /**
     * Accesses the double value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a double array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the double to access
     * @return The double value at the specified index
     */
    inline operator fun get(index: NUInt): Double = Memory.readDouble(value + index * Double.SIZE_BYTES.toNUInt())

    /**
     * Sets the double value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a double array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the double to modify
     * @param value The new double value to set
     */
    inline operator fun set(index: NUInt, value: Double) =
        Memory.writeDouble(this.value + index * Double.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> this
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a double pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [DoublePtr] for type-safe memory access to double values.
 *
 * @return A [DoublePtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asDoublePtr(): DoublePtr = DoublePtr(this)

/**
 * Represents a strongly-typed pointer to a memory location containing pointers.
 *
 * This value class wraps a generic [Pointer] and provides type-safe operations
 * for working with memory that contains pointer values. It includes methods for
 * pointer arithmetic, array-like access, and type reinterpretation.
 *
 * @property value The underlying generic pointer to the memory location
 */
@JvmInline
value class PointerPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a pointer.
     *
     * @param other The native unsigned integer value to add
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): PointerPtr = PointerPtr(value + other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a pointer.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): PointerPtr = PointerPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a pointer.
     *
     * @param other The unsigned long value to add
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun plus(other: ULong): PointerPtr =
        PointerPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a pointer.
     *
     * @param other The unsigned long value to subtract
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun minus(other: ULong): PointerPtr =
        PointerPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a pointer.
     *
     * @param other The unsigned integer value to add
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun plus(other: UInt): PointerPtr =
        PointerPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a pointer.
     *
     * @param other The unsigned integer value to subtract
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun minus(other: UInt): PointerPtr =
        PointerPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a pointer.
     *
     * @param other The long value to add
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun plus(other: Long): PointerPtr = PointerPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a pointer.
     *
     * @param other The long value to subtract
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun minus(other: Long): PointerPtr = PointerPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a pointer.
     *
     * @param other The integer value to add
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun plus(other: Int): PointerPtr = PointerPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a pointer.
     *
     * @param other The integer value to subtract
     * @return A new pointer pointer with the resulting address
     */
    inline operator fun minus(other: Int): PointerPtr = PointerPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one pointer.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one pointer. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): PointerPtr = PointerPtr(value + Pointer.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one pointer.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one pointer. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): PointerPtr = PointerPtr(value - Pointer.SIZE_BYTES.toNUInt())

    /**
     * Accesses the pointer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a pointer array and indexing into it.
     *
     * @param index The integer index of the pointer to access
     * @return The pointer value at the specified index
     */
    inline operator fun get(index: Int): Pointer =
        Pointer(Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt()))

    /**
     * Sets the pointer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a pointer array and indexing into it.
     *
     * @param index The integer index of the pointer to modify
     * @param value The new pointer value to set
     */
    inline operator fun set(index: Int, value: Pointer) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value.value)

    /**
     * Accesses the pointer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a pointer array and indexing into it with a long index.
     *
     * @param index The long index of the pointer to access
     * @return The pointer value at the specified index
     */
    inline operator fun get(index: Long): Pointer =
        Pointer(Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt()))

    /**
     * Sets the pointer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a pointer array and indexing into it with a long index.
     *
     * @param index The long index of the pointer to modify
     * @param value The new pointer value to set
     */
    inline operator fun set(index: Long, value: Pointer) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value.value)

    /**
     * Accesses the pointer value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a pointer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the pointer to access
     * @return The pointer value at the specified index
     */
    inline operator fun get(index: NUInt): Pointer =
        Pointer(Memory.readNUInt(value + index * Pointer.SIZE_BYTES.toNUInt()))

    /**
     * Sets the pointer value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a pointer array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the pointer to modify
     * @param value The new pointer value to set
     */
    inline operator fun set(index: NUInt, value: Pointer) =
        Memory.writeNUInt(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value.value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> value.asNFloatPtr()
        PointerPtr::class -> this
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a pointer pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [PointerPtr] for type-safe memory access to pointer values.
 *
 * @return A [PointerPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asPointerPtr(): PointerPtr = PointerPtr(this)

/**
 * A strongly-typed pointer for native floating-point values.
 *
 * This value class wraps a [Pointer] and provides type-specific operations for native float values,
 * including pointer arithmetic, array indexing, and type reinterpretation. It implements
 * [AutoCloseable] to allow automatic resource cleanup when used with try-with-resources.
 *
 * @property value The underlying memory address as a [Pointer]
 */
@JvmInline
value class NFloatPtr(val value: Pointer) : Reinterpretable, AutoCloseable {
    /**
     * Releases the memory associated with this pointer.
     *
     * This method is called automatically when the pointer is used with try-with-resources.
     * It frees the memory allocated at the address pointed to by this pointer.
     */
    override fun close() = Memory.free(value)

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value to the pointer's address, scaled by the size of a native float.
     *
     * @param other The native unsigned integer value to add
     * @return A new native float pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): NFloatPtr = NFloatPtr(value + other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value from the pointer's address, scaled by the size of a native float.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new native float pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): NFloatPtr = NFloatPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address, scaled by the size of a native float.
     *
     * @param other The unsigned long value to add
     * @return A new native float pointer with the resulting address
     */
    inline operator fun plus(other: ULong): NFloatPtr =
        NFloatPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address, scaled by the size of a native float.
     *
     * @param other The unsigned long value to subtract
     * @return A new native float pointer with the resulting address
     */
    inline operator fun minus(other: ULong): NFloatPtr =
        NFloatPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address, scaled by the size of a native float.
     *
     * @param other The unsigned integer value to add
     * @return A new native float pointer with the resulting address
     */
    inline operator fun plus(other: UInt): NFloatPtr =
        NFloatPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address, scaled by the size of a native float.
     *
     * @param other The unsigned integer value to subtract
     * @return A new native float pointer with the resulting address
     */
    inline operator fun minus(other: UInt): NFloatPtr =
        NFloatPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address, scaled by the size of a native float.
     *
     * @param other The long value to add
     * @return A new native float pointer with the resulting address
     */
    inline operator fun plus(other: Long): NFloatPtr = NFloatPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address, scaled by the size of a native float.
     *
     * @param other The long value to subtract
     * @return A new native float pointer with the resulting address
     */
    inline operator fun minus(other: Long): NFloatPtr = NFloatPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address, scaled by the size of a native float.
     *
     * @param other The integer value to add
     * @return A new native float pointer with the resulting address
     */
    inline operator fun plus(other: Int): NFloatPtr = NFloatPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address, scaled by the size of a native float.
     *
     * @param other The integer value to subtract
     * @return A new native float pointer with the resulting address
     */
    inline operator fun minus(other: Int): NFloatPtr = NFloatPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    /**
     * Increments this pointer by one native float.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one native float. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): NFloatPtr = NFloatPtr(value + Pointer.SIZE_BYTES.toNUInt())

    /**
     * Decrements this pointer by one native float.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one native float. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): NFloatPtr = NFloatPtr(value - Pointer.SIZE_BYTES.toNUInt())

    /**
     * Retrieves a native float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native float array and indexing into it with an integer index.
     *
     * @param index The integer index of the native float to retrieve
     * @return The native float value at the specified index
     */
    inline operator fun get(index: Int): NFloat =
        Memory.readNFloat(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets a native float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native float array and indexing into it with an integer index.
     *
     * @param index The integer index of the native float to modify
     * @param value The new native float value to set
     */
    inline operator fun set(index: Int, value: NFloat) =
        Memory.writeNFloat(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Retrieves a native float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native float array and indexing into it with a long index.
     *
     * @param index The long index of the native float to retrieve
     * @return The native float value at the specified index
     */
    inline operator fun get(index: Long): NFloat =
        Memory.readNFloat(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets a native float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native float array and indexing into it with a long index.
     *
     * @param index The long index of the native float to modify
     * @param value The new native float value to set
     */
    inline operator fun set(index: Long, value: NFloat) =
        Memory.writeNFloat(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Retrieves a native float value at the specified index.
     *
     * This operator allows for array-like access to memory by treating this pointer
     * as the start of a native float array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native float to retrieve
     * @return The native float value at the specified index
     */
    inline operator fun get(index: NUInt): NFloat =
        Memory.readNFloat(value + index * Pointer.SIZE_BYTES.toNUInt())

    /**
     * Sets a native float value at the specified index.
     *
     * This operator allows for array-like modification of memory by treating this pointer
     * as the start of a native float array and indexing into it with a native unsigned integer index.
     *
     * @param index The native unsigned integer index of the native float to modify
     * @param value The new native float value to set
     */
    inline operator fun set(index: NUInt, value: NFloat) =
        Memory.writeNFloat(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value)

    /**
     * Reinterprets this pointer as a different pointer type.
     *
     * This method allows for type-safe casting between different pointer types
     * while maintaining the same underlying memory address. It's useful when
     * you need to access memory with a different type than the original pointer.
     *
     * @param T The target pointer type, must be a subtype of [Reinterpretable]
     * @return The pointer reinterpreted as the specified type
     * @throws IllegalArgumentException if the requested type is not a supported pointer type
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        NFloatPtr::class -> this
        PointerPtr::class -> value.asPointerPtr()
        CString::class -> CString(value)
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Reinterprets this pointer as a native float pointer.
 *
 * This extension function provides a convenient way to convert a generic [Pointer]
 * to a strongly-typed [NFloatPtr] for type-safe memory access to native float values.
 *
 * @return A [NFloatPtr] pointing to the same memory address as this pointer
 */
inline fun Pointer.asNFloatPtr(): NFloatPtr = NFloatPtr(this)
