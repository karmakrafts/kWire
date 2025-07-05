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

/**
 * A platform-specific array of [NFloat] values.
 *
 * This class provides a cross-platform way to work with arrays of floating-point numbers.
 * The actual implementation varies by platform, but the interface remains consistent.
 */
expect class NFloatArray

/**
 * Creates a new [NFloatArray] with the specified [size].
 *
 * @param size The size of the array to create.
 * @return A new [NFloatArray] of the specified size.
 */
expect inline fun nFloatArray(size: Int): NFloatArray

/**
 * Creates a new [NFloatArray] with the specified [size] and initializes it using the provided [initializer] function.
 *
 * @param size The size of the array to create.
 * @param initializer A function that takes an index and returns the [NFloat] value to store at that index.
 * @return A new initialized [NFloatArray] of the specified size.
 */
expect inline fun nFloatArray(size: Int, noinline initializer: (Int) -> NFloat): NFloatArray

/**
 * Gets the number of elements in the array.
 */
expect inline val NFloatArray.size: Int

/**
 * Converts a [FloatArray] to an [NFloatArray].
 *
 * @return A new [NFloatArray] containing the same values as this [FloatArray].
 */
expect inline fun FloatArray.toNFloatArray(): NFloatArray

/**
 * Converts a [DoubleArray] to an [NFloatArray].
 *
 * @return A new [NFloatArray] containing the same values as this [DoubleArray].
 */
expect inline fun DoubleArray.toNFloatArray(): NFloatArray

/**
 * Converts this [NFloatArray] to a [FloatArray].
 *
 * @return A new [FloatArray] containing the same values as this [NFloatArray].
 */
expect inline val NFloatArray.floatArrayValue: FloatArray

/**
 * Converts this [NFloatArray] to a [DoubleArray].
 *
 * @return A new [DoubleArray] containing the same values as this [NFloatArray].
 */
expect inline val NFloatArray.doubleArrayValue: DoubleArray

/**
 * Checks if the array contains the specified [value].
 *
 * @param value The value to check for.
 * @return `true` if the array contains the specified value, `false` otherwise.
 */
expect inline operator fun NFloatArray.contains(value: NFloat): Boolean

/**
 * Gets the element at the specified [index].
 *
 * @param index The index of the element to retrieve.
 * @return The element at the specified index.
 * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
 */
expect inline operator fun NFloatArray.get(index: Int): NFloat

/**
 * Sets the element at the specified [index] to the specified [value].
 *
 * @param index The index of the element to set.
 * @param value The value to set.
 * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
 */
expect inline operator fun NFloatArray.set(index: Int, value: NFloat)

/**
 * Returns a new array that is a concatenation of this array and the [other] array.
 *
 * @param other The array to concatenate with this array.
 * @return A new array that contains all elements of this array followed by all elements of the [other] array.
 */
expect inline operator fun NFloatArray.plus(other: NFloatArray): NFloatArray

/**
 * Returns a new array containing all elements of this array except those that are present in the [other] array.
 *
 * @param other The array containing elements to exclude.
 * @return A new array containing elements from this array that are not present in the [other] array.
 */
expect inline operator fun NFloatArray.minus(other: NFloatArray): NFloatArray

/**
 * Creates a [Sequence] that iterates over all elements in this array.
 *
 * @return A sequence that yields all elements of this array.
 */
expect inline fun NFloatArray.asSequence(): Sequence<NFloat>

/**
 * Creates a new [NFloatArray] containing the specified [values].
 *
 * @param values The values to include in the array.
 * @return A new [NFloatArray] containing the specified values.
 */
inline fun nFloatArrayOf(vararg values: NFloat): NFloatArray = nFloatArray(values.size, values::get)

/**
 * Converts this [NFloatArray] to an [Array] of [NFloat] values.
 *
 * @return A new [Array] containing the same values as this [NFloatArray].
 */
inline fun NFloatArray.toTypedArray(): Array<NFloat> = Array(size, ::get)

/**
 * Converts this [Array] of [NFloat] values to an [NFloatArray].
 *
 * @return A new [NFloatArray] containing the same values as this [Array].
 */
inline fun Array<NFloat>.toNFloatArray(): NFloatArray = nFloatArray(size, ::get)
