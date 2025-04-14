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

import kotlin.math.min

/**
 * A non-owning view into a region of memory.
 *
 * This class represents a slice or view of memory, providing access to a
 * specific region defined by an address and size. It doesn't own the memory
 * it points to and doesn't handle memory allocation or deallocation.
 *
 * Slice is a fundamental type in the library, providing basic operations for
 * memory manipulation such as copying, comparing, and creating sub-slices.
 *
 * @property address The memory address where the slice begins
 * @property size The size of the slice in native unsigned integer format
 */
data class Slice(
    val address: Pointer, val size: NUInt
) {
    /**
     * Converts this memory slice to a StringSlice.
     *
     * This method creates a StringSlice that references the same underlying memory as this slice.
     * A StringSlice provides string-specific operations and implements CharSequence.
     *
     * @return A StringSlice representing the same memory region
     */
    inline fun toStringSlice(): StringSlice = StringSlice(address, size)

    /**
     * Copies the content of this slice to another slice.
     *
     * This method copies the entire content of this slice to the destination slice.
     * The destination slice must be at least as large as this slice.
     *
     * @param other The destination slice to copy to
     * @throws IllegalArgumentException if the destination slice is smaller than this slice
     */
    inline fun copyTo(other: Slice) {
        require(size >= other.size) { "Target slice size must be equal or greater than the source slice size" }
        Memory.copy(address, other.address, size)
    }

    /**
     * Copies the content of this slice to another slice, handling overlapping memory regions.
     *
     * This method is similar to [copyTo] but safely handles the case where the source and
     * destination memory regions overlap. The destination slice must be at least as large as this slice.
     *
     * @param other The destination slice to copy to
     * @throws IllegalArgumentException if the destination slice is smaller than this slice
     */
    inline fun copyToOverlapping(other: Slice) {
        require(size >= other.size) { "Target slice size must be equal or greater than the source slice size" }
        Memory.copyOverlapping(address, other.address, size)
    }

    /**
     * Compares this slice with another lexicographically.
     *
     * This method compares the content of two slices. It returns a negative value
     * if this slice is lexicographically less than the other, zero if they are equal,
     * or a positive value if this slice is lexicographically greater than the other.
     * If the sizes are different, it returns -1.
     *
     * @param other The slice to compare with
     * @return A negative value if this slice is less than the other, zero if they are equal,
     *         or a positive value if this slice is greater than the other
     */
    inline fun compareTo(other: Slice): Int {
        return if (size != other.size) -1
        else Memory.compare(address, other.address, size)
    }

    /**
     * Returns a sub-slice of this slice.
     *
     * This method extracts a portion of the slice starting at [startIndex] (inclusive)
     * and ending at [endIndex] (exclusive), and returns it as a new Slice.
     *
     * @param startIndex The start index (inclusive)
     * @param endIndex The end index (exclusive)
     * @return A new Slice containing the specified portion of the original slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline fun subSlice(startIndex: Int, endIndex: Int): Slice {
        val newSize = (endIndex - startIndex).toNUInt()
        require(newSize <= size) { "Subslice size must be equal or greater than source slice size" }
        return Slice(address + startIndex, newSize)
    }

    /**
     * Returns a sub-slice of this slice.
     *
     * This is a convenience overload that accepts an IntRange.
     *
     * @param range The range of indices to include in the sub-slice
     * @return A new Slice containing the specified portion of the original slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline fun subSlice(range: IntRange): Slice = subSlice(range.first, range.last + 1)

    /**
     * Returns a sub-slice of this slice.
     *
     * This operator overload allows using the range indexing syntax (e.g., `slice[1..5]`).
     *
     * @param range The range of indices to include in the sub-slice
     * @return A new Slice containing the specified portion of the original slice
     * @throws IllegalArgumentException if the range is invalid or out of bounds
     */
    inline operator fun get(range: IntRange): Slice = subSlice(range.first, range.last + 1)

    /**
     * Compares this slice with another for content equality.
     *
     * This method compares the content of two slices. It returns true if the
     * slices have the same size and the same content, false otherwise.
     *
     * @param other The slice to compare with
     * @return True if the slices have the same content, false otherwise
     */
    inline fun contentEquals(other: Slice): Boolean {
        return if (size != other.size) false
        else Memory.compare(address, other.address, size) == 0
    }

    /**
     * Compares this slice with another for address equality.
     *
     * This method checks if two slices point to the same memory address,
     * regardless of their size or content.
     *
     * @param other The slice to compare with
     * @return True if the slices point to the same memory address, false otherwise
     */
    inline fun addressEquals(other: Slice): Boolean {
        return address == other.address
    }

    /**
     * Converts this slice to a string representation.
     *
     * This method creates a string representation of the slice, including its address,
     * size, and a preview of its content (up to 512 bytes) in hexadecimal format.
     *
     * @return A string representation of the slice
     */
    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        // @formatter:off
        val contentPreview = Memory.readBytes(address, min(512, size.value.intValue))
            .joinToString(", ") { "0x${it.toHexString()}" }
        // @formatter:on
        return "$address($size) [$contentPreview]"
    }
}
