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

package dev.karmakrafts.kwire.ctype

import kotlin.jvm.JvmInline

/**
 * A platform-specific array of [NUInt] values.
 *
 * This class provides a cross-platform way to work with arrays of unsigned integer numbers.
 * It wraps an [NIntArray] and interprets the values as unsigned integers.
 */
@JvmInline
value class NUIntArray @PublishedApi internal constructor(
    @PublishedApi internal val value: NIntArray
) {
    /**
     * Gets the number of elements in the array.
     */
    inline val size: Int
        get() = value.size

    /**
     * Checks if the array contains the specified [value].
     *
     * @param value The value to check for.
     * @return `true` if the array contains the specified value, `false` otherwise.
     */
    inline operator fun contains(value: NUInt): Boolean = value.value in this.value

    /**
     * Gets the element at the specified [index].
     *
     * @param index The index of the element to retrieve.
     * @return The element at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
     */
    inline operator fun get(index: Int): NUInt = NUInt(value[index])

    /**
     * Sets the element at the specified [index] to the specified [value].
     *
     * @param index The index of the element to set.
     * @param value The value to set.
     * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
     */
    inline operator fun set(index: Int, value: NUInt) {
        this.value[index] = value.value
    }

    /**
     * Returns a new array that is a concatenation of this array and the [other] array.
     *
     * @param other The array to concatenate with this array.
     * @return A new array that contains all elements of this array followed by all elements of the [other] array.
     */
    inline operator fun plus(other: NUIntArray): NUIntArray = NUIntArray(value + other.value)

    /**
     * Returns a new array containing all elements of this array except those that are present in the [other] array.
     *
     * @param other The array containing elements to exclude.
     * @return A new array containing elements from this array that are not present in the [other] array.
     */
    inline operator fun minus(other: NUIntArray): NUIntArray = NUIntArray(value - other.value)

    /**
     * Creates a [Sequence] that iterates over all elements in this array.
     *
     * @return A sequence that yields all elements of this array.
     */
    inline fun asSequence(): Sequence<NUInt> = value.asSequence().map(::NUInt)

    /**
     * Converts this [NUIntArray] to an [NIntArray].
     *
     * @return The underlying [NIntArray] that this [NUIntArray] wraps.
     */
    inline fun asNIntArray(): NIntArray = value

    /**
     * Converts this [NUIntArray] to an [Array] of [NUInt] values.
     *
     * @return A new [Array] containing all elements from this array.
     */
    inline fun toTypedArray(): Array<NUInt> = Array(size, ::get)
}

/**
 * Creates a new [NUIntArray] with the specified [size].
 *
 * @param size The size of the array to create.
 * @return A new [NUIntArray] of the specified size.
 */
inline fun nUIntArray(size: Int): NUIntArray = NUIntArray(nIntArray(size))

/**
 * Creates a new [NUIntArray] with the specified [size] and initializes it using the provided [initializer] function.
 *
 * @param size The size of the array to create.
 * @param initializer A function that takes an index and returns the [NUInt] value to store at that index.
 * @return A new initialized [NUIntArray] of the specified size.
 */
inline fun nUIntArray(size: Int, crossinline initializer: (Int) -> NUInt): NUIntArray {
    return NUIntArray(nIntArray(size) {
        initializer(it).value
    })
}

/**
 * Converts an [Array] of [NUInt] values to an [NUIntArray].
 *
 * @return A new [NUIntArray] containing all elements from this array.
 */
inline fun Array<NUInt>.toNUIntArray(): NUIntArray = nUIntArray(size, ::get)
