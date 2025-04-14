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
 * A platform-specific array of [Pointer] values.
 *
 * This class provides a cross-platform way to work with arrays of memory pointers.
 * It wraps an [NUIntArray] and interprets the values as memory addresses.
 */
@JvmInline
value class PointerArray @PublishedApi internal constructor(
    @PublishedApi internal val value: NUIntArray
) {
    /**
     * Gets the number of elements in the array.
     */
    inline val size: Int
        get() = value.size

    /**
     * Checks if the array contains the specified [value].
     *
     * @param value The pointer value to check for.
     * @return `true` if the array contains the specified pointer, `false` otherwise.
     */
    inline operator fun contains(value: Pointer): Boolean = value.value in this.value

    /**
     * Gets the element at the specified [index].
     *
     * @param index The index of the element to retrieve.
     * @return The pointer at the specified index.
     * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
     */
    inline operator fun get(index: Int): Pointer = Pointer(value[index])

    /**
     * Sets the element at the specified [index] to the specified [value].
     *
     * @param index The index of the element to set.
     * @param value The pointer value to set.
     * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
     */
    inline operator fun set(index: Int, value: Pointer) {
        this.value[index] = value.value
    }

    /**
     * Returns a new array that is a concatenation of this array and the [other] array.
     *
     * @param other The array to concatenate with this array.
     * @return A new array that contains all elements of this array followed by all elements of the [other] array.
     */
    inline operator fun plus(other: PointerArray): PointerArray = PointerArray(value + other.value)

    /**
     * Returns a new array containing all elements of this array except those that are present in the [other] array.
     *
     * @param other The array containing elements to exclude.
     * @return A new array containing elements from this array that are not present in the [other] array.
     */
    inline operator fun minus(other: PointerArray): PointerArray = PointerArray(value - other.value)

    /**
     * Creates a [Sequence] that iterates over all elements in this array.
     *
     * @return A sequence that yields all elements of this array.
     */
    inline fun asSequence(): Sequence<Pointer> = value.asSequence().map(::Pointer)

    /**
     * Converts this [PointerArray] to an [NUIntArray].
     *
     * @return The underlying [NUIntArray] that this [PointerArray] wraps.
     */
    inline fun asNUIntArray(): NUIntArray = value

    /**
     * Converts this [PointerArray] to an [NIntArray].
     *
     * @return The [NIntArray] representation of this pointer array.
     */
    inline fun asNIntArray(): NIntArray = value.value
}

/**
 * Converts this [NIntArray] to a [PointerArray].
 *
 * @return A new [PointerArray] that wraps this array's values as pointers.
 */
inline fun NIntArray.asPointerArray(): PointerArray = PointerArray(asNUIntArray())

/**
 * Converts this [NUIntArray] to a [PointerArray].
 *
 * @return A new [PointerArray] that wraps this array's values as pointers.
 */
inline fun NUIntArray.asPointerArray(): PointerArray = PointerArray(this)

/**
 * Creates a new [PointerArray] with the specified [size].
 *
 * @param size The number of elements in the array.
 * @return A new [PointerArray] with the specified size, initialized with default values.
 */
inline fun pointerArray(size: Int): PointerArray = PointerArray(nUIntArray(size))

/**
 * Creates a new [PointerArray] with the specified [size] and initializes its elements using the provided [initializer] function.
 *
 * @param size The number of elements in the array.
 * @param initializer A function that takes an index and returns a [Pointer] value for that position.
 * @return A new [PointerArray] with the specified size, initialized with values from the initializer function.
 */
inline fun pointerArray(size: Int, crossinline initializer: (Int) -> Pointer): PointerArray =
    PointerArray(nUIntArray(size) { initializer(it).value })
