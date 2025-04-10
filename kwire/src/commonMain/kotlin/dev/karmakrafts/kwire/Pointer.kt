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
 * Internal function to get the size of a pointer in bytes on the current platform.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate pointer size for that platform.
 *
 * @return The size of a pointer in bytes
 */
internal expect fun getPointerSize(): Int

/**
 * Marker interface for types that can be reinterpreted as other types.
 *
 * This interface is implemented by pointer types that support type reinterpretation,
 * allowing them to be cast to different pointer types while maintaining the same
 * underlying memory address.
 */
sealed interface Reinterpretable

/**
 * Represents a memory address as a platform-independent pointer.
 *
 * This value class wraps a native unsigned integer that represents a memory address.
 * It provides operations for pointer arithmetic, alignment, and type reinterpretation.
 *
 * @property value The underlying memory address as a native unsigned integer
 */
@JvmInline
value class Pointer(val value: NUInt) : Reinterpretable, AutoCloseable {
    /**
     * Companion object containing constants related to pointers.
     */
    companion object {
        /**
         * The size of a pointer in bytes on the current platform.
         *
         * This value is determined by calling [getPointerSize] and is used for
         * pointer arithmetic and memory allocation calculations.
         */
        val SIZE_BYTES: Int = getPointerSize()
    }

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a native unsigned integer
     * value directly to the pointer's address.
     *
     * @param other The native unsigned integer value to add
     * @return A new pointer with the resulting address
     */
    inline operator fun plus(other: NUInt): Pointer = Pointer(value + other)

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a native unsigned integer
     * value directly from the pointer's address.
     *
     * @param other The native unsigned integer value to subtract
     * @return A new pointer with the resulting address
     */
    inline operator fun minus(other: NUInt): Pointer = Pointer(value - other)

    /**
     * Adds an unsigned long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned long
     * value to the pointer's address. The unsigned long is converted to a native
     * unsigned integer before the addition.
     *
     * @param other The unsigned long value to add
     * @return A new pointer with the resulting address
     */
    inline operator fun plus(other: ULong): Pointer = Pointer(value + other.toNUInt())

    /**
     * Subtracts an unsigned long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned long
     * value from the pointer's address. The unsigned long is converted to a native
     * unsigned integer before the subtraction.
     *
     * @param other The unsigned long value to subtract
     * @return A new pointer with the resulting address
     */
    inline operator fun minus(other: ULong): Pointer = Pointer(value - other.toNUInt())

    /**
     * Adds an unsigned integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an unsigned integer
     * value to the pointer's address. The unsigned integer is converted to a native
     * unsigned integer before the addition.
     *
     * @param other The unsigned integer value to add
     * @return A new pointer with the resulting address
     */
    inline operator fun plus(other: UInt): Pointer = Pointer(value + other.toNUInt())

    /**
     * Subtracts an unsigned integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an unsigned integer
     * value from the pointer's address. The unsigned integer is converted to a native
     * unsigned integer before the subtraction.
     *
     * @param other The unsigned integer value to subtract
     * @return A new pointer with the resulting address
     */
    inline operator fun minus(other: UInt): Pointer = Pointer(value - other.toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding a long
     * value to the pointer's address. The long is converted to a native
     * unsigned integer before the addition.
     *
     * @param other The long value to add
     * @return A new pointer with the resulting address
     */
    inline operator fun plus(other: Long): Pointer = Pointer(value + other.toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting a long
     * value from the pointer's address. The long is converted to a native
     * unsigned integer before the subtraction.
     *
     * @param other The long value to subtract
     * @return A new pointer with the resulting address
     */
    inline operator fun minus(other: Long): Pointer = Pointer(value - other.toNUInt())

    /**
     * Adds an integer offset to this pointer.
     *
     * This operator allows for pointer arithmetic by adding an integer
     * value to the pointer's address. The integer is converted to a native
     * unsigned integer before the addition.
     *
     * @param other The integer value to add
     * @return A new pointer with the resulting address
     */
    inline operator fun plus(other: Int): Pointer = Pointer(value + other.toNUInt())

    /**
     * Subtracts an integer offset from this pointer.
     *
     * This operator allows for pointer arithmetic by subtracting an integer
     * value from the pointer's address. The integer is converted to a native
     * unsigned integer before the subtraction.
     *
     * @param other The integer value to subtract
     * @return A new pointer with the resulting address
     */
    inline operator fun minus(other: Int): Pointer = Pointer(value - other.toNUInt())

    /**
     * Increments this pointer by one unit.
     *
     * This operator allows for pointer arithmetic by incrementing the pointer's address
     * by one unit. It is equivalent to adding 1 to the pointer's address.
     *
     * @return A new pointer with the incremented address
     */
    inline operator fun inc(): Pointer = Pointer(value.inc())

    /**
     * Decrements this pointer by one unit.
     *
     * This operator allows for pointer arithmetic by decrementing the pointer's address
     * by one unit. It is equivalent to subtracting 1 from the pointer's address.
     *
     * @return A new pointer with the decremented address
     */
    inline operator fun dec(): Pointer = Pointer(value.dec())

    /**
     * Aligns this pointer to the specified memory alignment boundary.
     *
     * This method ensures that the pointer address is aligned to the specified
     * alignment boundary, which is often required for certain memory operations
     * or data types that have alignment requirements.
     *
     * @param alignment The alignment boundary to align to, defaults to [Memory.defaultAlignment]
     * @return A new pointer aligned to the specified boundary
     */
    inline fun align(alignment: NUInt = Memory.defaultAlignment): Pointer = Pointer(Memory.align(value, alignment))

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
        Pointer::class -> this
        BytePtr::class -> asBytePtr()
        ShortPtr::class -> asShortPtr()
        IntPtr::class -> asIntPtr()
        LongPtr::class -> asLongPtr()
        NIntPtr::class -> asNIntPtr()
        UBytePtr::class -> asUBytePtr()
        UShortPtr::class -> asUShortPtr()
        UIntPtr::class -> asUIntPtr()
        ULongPtr::class -> asULongPtr()
        NUIntPtr::class -> asNUIntPtr()
        FloatPtr::class -> asFloatPtr()
        DoublePtr::class -> asDoublePtr()
        NFloatPtr::class -> asNFloatPtr()
        PointerPtr::class -> asPointerPtr()
        CString::class -> CString(this)
        else -> error("Unknown pointer type ${T::class}")
    } as T

    override fun close() = Memory.free(this)

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String = "0x${value.toHexString()}"
}

/**
 * A constant representing a null pointer (address 0).
 *
 * This is equivalent to NULL or nullptr in C/C++ and can be used to represent
 * an invalid or uninitialized pointer. It is often used as a sentinel value
 * to indicate the absence of a valid memory address.
 */
val nullptr: Pointer = Pointer(0U.toNUInt())

/**
 * Returns a null pointer (address 0) of the specified reinterpretable type.
 *
 * This function provides a type-safe way to get a null pointer of a specific type.
 * It is equivalent to casting NULL or nullptr to a specific pointer type in C/C++.
 * This is useful when you need a null pointer of a specific type for function parameters
 * or when initializing pointer variables.
 *
 * @param T The target pointer type, must be a subtype of [Reinterpretable]
 * @return A null pointer of the specified type
 * @throws IllegalArgumentException if the requested type is not a supported pointer type
 */
inline fun <reified T : Reinterpretable> nullptr(): T = nullptr.reinterpret()
