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
 * A strongly-typed wrapper for C-style strings (null-terminated character arrays).
 *
 * This value class wraps a [Pointer] to a C-style string and provides type-specific operations
 * for string manipulation, including allocation, comparison, and conversion to Kotlin strings.
 * It implements [CharSequence] to allow standard string operations and [AutoCloseable] to enable
 * automatic resource cleanup when used with try-with-resources.
 *
 * @property address The underlying memory address as a [Pointer] to the C-style string
 */
@JvmInline
value class CString @PublishedApi internal constructor(val address: Pointer) : Reinterpretable, CharSequence, AutoCloseable {
    /**
     * Provides factory methods for creating and allocating C-style strings.
     */
    companion object {
        /**
         * Allocates memory for a C-style string of the specified length.
         *
         * This method allocates memory for a string of the given length plus one byte for the null terminator.
         * The allocated memory is initialized to zero.
         *
         * @param length The length of the string to allocate (excluding the null terminator)
         * @return A new [CString] instance pointing to the allocated memory
         */
        inline fun allocate(length: NUInt): CString = CString(Memory.splat(0, length + 1.toNUInt()))

        /**
         * Allocates memory for a C-style string of the specified length.
         *
         * This is a convenience overload that converts the Long length to a [NUInt] before allocation.
         *
         * @param length The length of the string to allocate (excluding the null terminator)
         * @return A new [CString] instance pointing to the allocated memory
         */
        inline fun allocate(length: Long): CString = allocate(length.toNUInt())

        /**
         * Allocates memory for a C-style string of the specified length.
         *
         * This is a convenience overload that converts the Int length to a [NUInt] before allocation.
         *
         * @param length The length of the string to allocate (excluding the null terminator)
         * @return A new [CString] instance pointing to the allocated memory
         */
        inline fun allocate(length: Int): CString = allocate(length.toNUInt())

        /**
         * Allocates memory for a C-style string and initializes it with the content of a Kotlin string.
         *
         * This method allocates memory for a string of the same length as the input string plus one byte
         * for the null terminator, and then copies the bytes of the input string to the allocated memory.
         *
         * @param value The Kotlin string to convert to a C-style string
         * @return A new [CString] instance containing the same content as the input string
         */
        inline fun allocate(value: String): CString = allocate(value.length).apply {
            Memory.writeBytes(address, value.encodeToByteArray())
        }
    }

    /**
     * Returns the length of this C-style string.
     *
     * This property returns the length of the string (excluding the null terminator)
     * as an Int. It uses the [Memory.strlen] function to calculate the length.
     *
     * @return The length of the string as an Int
     */
    override val length: Int
        get() = Memory.strlen(address).value.intValue

    /**
     * Returns the length of this C-style string as a Long.
     *
     * This property is similar to [length] but returns the length as a Long value,
     * which can be useful when working with very large strings.
     *
     * @return The length of the string as a Long
     */
    inline val longLength: Long
        get() = Memory.strlen(address).value.longValue

    /**
     * Returns the length of this C-style string as a native unsigned integer.
     *
     * This property returns the raw result of [Memory.strlen] as a [NUInt] without conversion,
     * which is useful for native API calls that expect unsigned size values.
     *
     * @return The length of the string as a [NUInt]
     */
    inline val nativeLength: NUInt
        get() = Memory.strlen(address)

    /**
     * Compares this C-style string with another for content equality.
     *
     * This method uses the [Memory.strcmp] function to compare the content of two C-style strings.
     * It returns true if the strings have the same content, false otherwise.
     *
     * @param other The C-style string to compare with
     * @return True if the strings have the same content, false otherwise
     */
    inline fun contentEquals(other: CString): Boolean {
        return Memory.strcmp(address, other.address) == 0
    }

    /**
     * Compares this C-style string with another lexicographically.
     *
     * This method uses the [Memory.strcmp] function to compare the content of two C-style strings.
     * It returns a negative value if this string is lexicographically less than the other string,
     * zero if the strings are equal, or a positive value if this string is lexicographically greater
     * than the other string.
     *
     * @param other The C-style string to compare with
     * @return A negative value if this string is less than the other, zero if they are equal,
     *         or a positive value if this string is greater than the other
     */
    inline fun compare(other: CString): Int {
        return Memory.strcmp(address, other.address)
    }

    /**
     * Converts this C-style string to a Kotlin ByteArray.
     *
     * This method reads the bytes of the C-style string (excluding the null terminator)
     * into a Kotlin ByteArray.
     *
     * @return A ByteArray containing the bytes of the C-style string
     */
    inline fun toByteArray(): ByteArray = Memory.readBytes(address, length)

    /**
     * Returns the character at the specified index in this C-style string.
     *
     * This method accesses the byte at the specified index in the underlying memory
     * and converts it to a Char.
     *
     * @param index The index of the character to return
     * @return The character at the specified index
     */
    override operator fun get(index: Int): Char {
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Returns the character at the specified index in this C-style string.
     *
     * This is a convenience overload that accepts a Long index, which can be useful
     * when working with very large strings.
     *
     * @param index The index of the character to return as a Long
     * @return The character at the specified index
     */
    inline operator fun get(index: Long): Char {
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Returns the character at the specified index in this C-style string.
     *
     * This is a convenience overload that accepts a [NUInt] index, which is useful
     * when working with native unsigned integer types.
     *
     * @param index The index of the character to return as a [NUInt]
     * @return The character at the specified index
     */
    inline operator fun get(index: NUInt): Char {
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Creates a deep copy of this C-style string.
     *
     * This method allocates new memory for a string of the same length as this string,
     * and then copies the content of this string to the newly allocated memory.
     *
     * @return A new [CString] instance containing the same content as this string
     */
    inline fun copy(): CString = CString(Memory.allocate(Memory.strlen(address))).apply {
        Memory.strcpy(this@CString.address, address)
    }

    /**
     * Converts this C-style string to a [StringSlice].
     *
     * This method creates a [StringSlice] that references the same underlying memory as this C-style string.
     * A [StringSlice] provides additional string manipulation capabilities and can represent a portion of a string
     * without copying the data.
     *
     * @return A [StringSlice] representing the same string data
     */
    inline fun asSlice(): StringSlice = StringSlice.fromCString(address)

    /**
     * Returns a subsequence of this C-style string.
     *
     * This method extracts a portion of the C-style string starting at [startIndex] (inclusive)
     * and ending at [endIndex] (exclusive), and returns it as a Kotlin CharSequence.
     *
     * @param startIndex The start index (inclusive)
     * @param endIndex The end index (exclusive)
     * @return A CharSequence containing the specified portion of the C-style string
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return Memory.readBytes(address + startIndex, endIndex - startIndex).decodeToString()
    }

    /**
     * Releases the memory associated with this C-style string.
     *
     * This method is called automatically when the C-style string is used with try-with-resources.
     * It frees the memory allocated for the string.
     */
    override fun close() = Memory.free(address)

    /**
     * Compares this C-style string with another for equality.
     *
     * This operator overload delegates to [contentEquals] to compare the content of two C-style strings.
     * It returns true if the strings have the same content, false otherwise.
     *
     * @param other The C-style string to compare with
     * @return True if the strings have the same content, false otherwise
     */
    inline operator fun equals(other: CString): Boolean = contentEquals(other)

    /**
     * Compares this C-style string with another object for equality.
     *
     * This method overrides the standard equals method to provide proper equality comparison
     * for C-style strings. It returns true only if the other object is also a C-style string
     * and has the same content as this string.
     *
     * @param other The object to compare with
     * @return True if the other object is a C-style string with the same content, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (other !is CString) false
        else contentEquals(other)
    }

    /**
     * Converts this C-style string to a Kotlin String.
     *
     * This method reads the bytes of the C-style string and decodes them to a Kotlin String.
     *
     * @return A Kotlin String containing the same content as this C-style string
     */
    override fun toString(): String = toByteArray().decodeToString()

    /**
     * Computes a hash code for this C-style string.
     *
     * This method calculates a hash code based on the content of the C-style string.
     * It iterates through each byte of the string and computes a hash value using
     * the standard algorithm (31 * result + current byte). This ensures that two
     * C-style strings with the same content will have the same hash code.
     *
     * @return The hash code value for this C-style string
     */
    override fun hashCode(): Int {
        var result = 0
        val bytePtr = address.asBytePtr()
        var index = 0U.toNUInt()
        while (index < nativeLength) {
            result = 31 * result + bytePtr[index]
            ++index
        }
        return result
    }

    /**
     * Reinterprets this C-style string as another [Reinterpretable] type.
     *
     * This method allows for type conversion between different pointer types in the library.
     * It converts the underlying address of this C-style string to the specified type.
     * This is useful when you need to pass the same memory address to functions expecting
     * different pointer types.
     *
     * @param T The target [Reinterpretable] type to convert to
     * @return The reinterpreted value as type T
     * @throws IllegalStateException if the requested type is not supported
     */
    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> address
        BytePtr::class -> address.asBytePtr()
        ShortPtr::class -> address.asShortPtr()
        IntPtr::class -> address.asIntPtr()
        LongPtr::class -> address.asLongPtr()
        NIntPtr::class -> address.asNIntPtr()
        UBytePtr::class -> address.asUBytePtr()
        UShortPtr::class -> address.asUShortPtr()
        UIntPtr::class -> address.asUIntPtr()
        ULongPtr::class -> address.asULongPtr()
        NUIntPtr::class -> address.asNUIntPtr()
        FloatPtr::class -> address.asFloatPtr()
        DoublePtr::class -> address.asDoublePtr()
        PointerPtr::class -> address.asPointerPtr()
        CString::class -> this
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

/**
 * Converts a [BytePtr] pointing to a C-style string to a Kotlin String.
 *
 * This extension function reads the bytes from the memory pointed to by this [BytePtr]
 * up to the null terminator, and decodes them to a Kotlin String.
 *
 * @return A Kotlin String containing the content of the C-style string
 */
inline fun BytePtr.toKString(): String =
    Memory.readBytes(value, Memory.strlen(value).toSigned().intValue).decodeToString()
