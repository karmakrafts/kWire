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

/**
 * A non-owning view into a string in memory.
 *
 * This class represents a slice or view of a string in memory, providing access to a
 * specific region of memory interpreted as a character sequence. Unlike [CString], it
 * doesn't own the memory it points to and doesn't handle memory allocation or deallocation.
 *
 * StringSlice implements [CharSequence] to allow standard string operations like character
 * access and substring extraction. It's particularly useful for working with portions of
 * strings without copying the data.
 *
 * @property address The memory address where the string slice begins
 * @property nativeLength The length of the string slice in native unsigned integer format
 */
class StringSlice( // @formatter:off
    val address: Pointer,
    length: NUInt
) : CharSequence { // @formatter:on
    /**
     * Provides factory methods for creating StringSlice instances.
     */
    companion object {
        /**
         * Creates a StringSlice from a C-style string pointer.
         *
         * This method creates a StringSlice that spans the entire C-style string,
         * from the given address to the null terminator. The length is determined
         * using [Memory.strlen].
         *
         * @param address The pointer to the C-style string
         * @return A new StringSlice instance representing the entire C-style string
         */
        inline fun fromCString(address: Pointer): StringSlice = StringSlice(address, Memory.strlen(address))
    }

    /**
     * The length of this string slice in native unsigned integer format.
     *
     * This property stores the length of the string slice as a [NUInt],
     * which is the native unsigned integer type for the platform.
     */
    val nativeLength: NUInt = length

    /**
     * Returns the length of this string slice.
     *
     * This property returns the length of the string slice as an Int.
     * It converts the native length to a standard Kotlin Int value.
     *
     * @return The length of the string slice as an Int
     */
    override val length: Int
        get() = nativeLength.value.intValue

    /**
     * Returns the length of this string slice as a Long.
     *
     * This property is similar to [length] but returns the length as a Long value,
     * which can be useful when working with very large strings.
     *
     * @return The length of the string slice as a Long
     */
    inline val longLength: Long
        get() = nativeLength.value.longValue

    /**
     * Converts this string slice to a Kotlin ByteArray.
     *
     * This method reads the bytes of the string slice into a Kotlin ByteArray.
     *
     * @return A ByteArray containing the bytes of the string slice
     */
    inline fun toByteArray(): ByteArray = Memory.readBytes(address, length)

    /**
     * Compares this string slice with another for content equality.
     *
     * This method compares the content of two string slices. It returns true if the
     * slices have the same length and the same content, false otherwise.
     *
     * @param other The string slice to compare with
     * @return True if the string slices have the same content, false otherwise
     */
    inline fun contentEquals(other: StringSlice): Boolean {
        return if (length == other.length) Memory.compare(address, other.address, nativeLength) == 0
        else false
    }

    /**
     * Compares this string slice with a C-style string for content equality.
     *
     * This method compares the content of this string slice with a C-style string.
     * It returns true if they have the same length and the same content, false otherwise.
     *
     * @param other The C-style string to compare with
     * @return True if this string slice and the C-style string have the same content, false otherwise
     */
    inline fun contentEquals(other: CString): Boolean {
        return if (length == other.length) Memory.compare(address, other.address, nativeLength) == 0
        else false
    }

    /**
     * Compares this string slice with another lexicographically.
     *
     * This method compares the content of two string slices. It returns a negative value
     * if this string slice is lexicographically less than the other, zero if they are equal,
     * or a positive value if this string slice is lexicographically greater than the other.
     * If the lengths are different, it returns -1.
     *
     * @param other The string slice to compare with
     * @return A negative value if this string slice is less than the other, zero if they are equal,
     *         or a positive value if this string slice is greater than the other
     */
    inline fun compare(other: StringSlice): Int {
        return if (length == other.length) Memory.compare(address, other.address, nativeLength)
        else -1
    }

    /**
     * Compares this string slice with a C-style string lexicographically.
     *
     * This method compares the content of this string slice with a C-style string.
     * It returns a negative value if this string slice is lexicographically less than
     * the C-style string, zero if they are equal, or a positive value if this string slice
     * is lexicographically greater than the C-style string. If the lengths are different,
     * it returns -1.
     *
     * @param other The C-style string to compare with
     * @return A negative value if this string slice is less than the C-style string,
     *         zero if they are equal, or a positive value if this string slice is greater
     *         than the C-style string
     */
    inline fun compare(other: CString): Int {
        return if (length == other.length) Memory.compare(address, other.address, nativeLength)
        else -1
    }

    /**
     * Returns the character at the specified index in this string slice.
     *
     * This method accesses the byte at the specified index in the underlying memory
     * and converts it to a Char.
     *
     * @param index The index of the character to return
     * @return The character at the specified index
     * @throws IllegalArgumentException if the index is out of bounds
     */
    override operator fun get(index: Int): Char {
        require(index in indices) { "StringSlice index $index out of bounds for length $length" }
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Returns the character at the specified index in this string slice.
     *
     * This is a convenience overload that accepts a Long index, which can be useful
     * when working with very large strings.
     *
     * @param index The index of the character to return as a Long
     * @return The character at the specified index
     * @throws IllegalArgumentException if the index is out of bounds
     */
    inline operator fun get(index: Long): Char {
        require(index >= 0L && index < nativeLength.value.longValue) { "StringSlice index $index out of bounds for length $length" }
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Returns the character at the specified index in this string slice.
     *
     * This is a convenience overload that accepts a [NUInt] index, which is useful
     * when working with native unsigned integer types.
     *
     * @param index The index of the character to return as a [NUInt]
     * @return The character at the specified index
     * @throws IllegalArgumentException if the index is out of bounds
     */
    inline operator fun get(index: NUInt): Char {
        require(index >= 0U.toNUInt() && index < nativeLength) { "StringSlice index $index out of bounds for length $length" }
        return address.asBytePtr()[index].toInt().toChar()
    }

    /**
     * Returns a subsequence of this string slice.
     *
     * This method extracts a portion of the string slice starting at [startIndex] (inclusive)
     * and ending at [endIndex] (exclusive), and returns it as a CharSequence.
     *
     * @param startIndex The start index (inclusive)
     * @param endIndex The end index (exclusive)
     * @return A CharSequence containing the specified portion of the string slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        val newLength = endIndex - startIndex
        require(newLength <= length) { "StringSlice range $startIndex..$endIndex out of bounds for length $length" }
        return StringSlice(address + startIndex, newLength.toNUInt())
    }

    /**
     * Returns a subsequence of this string slice as a StringSlice.
     *
     * This method is similar to [subSequence] but returns a StringSlice instead of a CharSequence.
     * It extracts a portion of the string slice starting at [startIndex] (inclusive)
     * and ending at [endIndex] (exclusive).
     *
     * @param startIndex The start index (inclusive)
     * @param endIndex The end index (exclusive)
     * @return A StringSlice containing the specified portion of the original string slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline fun subSlice(startIndex: Int, endIndex: Int): StringSlice {
        val newLength = endIndex - startIndex
        require(newLength <= length) { "StringSlice range $startIndex..$endIndex out of bounds for length $length" }
        return StringSlice(address + startIndex, newLength.toNUInt())
    }

    /**
     * Returns a subsequence of this string slice as a StringSlice.
     *
     * This is a convenience overload that accepts an IntRange.
     *
     * @param range The range of indices to include in the substring
     * @return A StringSlice containing the specified portion of the original string slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline fun subSlice(range: IntRange): StringSlice = subSlice(range.first, range.last + 1)

    /**
     * Returns a subsequence of this string slice as a StringSlice.
     *
     * This operator overload allows using the range indexing syntax (e.g., `slice[1..5]`).
     *
     * @param range The range of indices to include in the substring
     * @return A StringSlice containing the specified portion of the original string slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline operator fun get(range: IntRange): StringSlice = subSlice(range.first, range.last + 1)

    /**
     * Converts this string slice to a new C-style string.
     *
     * This method allocates a new C-style string with the same content as this string slice.
     * **The allocated memory must be freed when no longer needed to avoid memory leaks.**
     *
     * @return A new [CString] instance containing the same content as this string slice
     */
    inline fun intoCString(): CString = CString.allocate(nativeLength).apply {
        Memory.copy(this@StringSlice.address, address, this@StringSlice.nativeLength)
    }

    /**
     * Compares this string slice with another object for equality.
     *
     * This method overrides the standard equals method to provide proper equality comparison
     * for string slices. It returns true if the other object is also a string slice or a C-style string
     * and has the same content as this string slice.
     *
     * @param other The object to compare with
     * @return True if the other object is a string slice or a C-style string with the same content,
     *         false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is StringSlice -> contentEquals(other)
            is CString -> contentEquals(other)
            else -> false
        }
    }

    /**
     * Converts this string slice to a Kotlin String.
     *
     * This method reads the bytes of the string slice and decodes them to a Kotlin String.
     *
     * @return A Kotlin String containing the same content as this string slice
     */
    override fun toString(): String = toByteArray().decodeToString()

    /**
     * Computes a hash code for this string slice.
     *
     * This method computes a hash code based on the content of the string slice.
     * It ensures that string slices with the same content have the same hash code.
     *
     * @return A hash code value for this string slice
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
}
