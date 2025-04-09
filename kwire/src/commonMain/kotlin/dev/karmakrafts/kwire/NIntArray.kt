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
@file:JvmName("NIntArray$")
// @formatter:on

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

/**
 * A platform-specific array of [NInt] values.
 *
 * This class provides a cross-platform way to work with arrays of integer numbers.
 * The actual implementation varies by platform, but the interface remains consistent.
 */
expect class NIntArray

/**
 * Creates a new [NIntArray] with the specified [size].
 *
 * @param size The size of the array to create.
 * @return A new [NIntArray] of the specified size.
 */
expect inline fun nIntArray(size: Int): NIntArray

/**
 * Creates a new [NIntArray] with the specified [size] and initializes it using the provided [initializer] function.
 *
 * @param size The size of the array to create.
 * @param initializer A function that takes an index and returns the [NInt] value to store at that index.
 * @return A new initialized [NIntArray] of the specified size.
 */
expect inline fun nIntArray(size: Int, noinline initializer: (Int) -> NInt): NIntArray

/**
 * Gets the number of elements in the array.
 */
expect inline val NIntArray.size: Int

/**
 * Converts an [IntArray] to an [NIntArray].
 *
 * @return A new [NIntArray] containing the same values as this [IntArray].
 */
expect inline fun IntArray.toNIntArray(): NIntArray

/**
 * Converts a [LongArray] to an [NIntArray].
 *
 * @return A new [NIntArray] containing the same values as this [LongArray].
 */
expect inline fun LongArray.toNIntArray(): NIntArray

/**
 * Converts this [NIntArray] to an [IntArray].
 *
 * @return A new [IntArray] containing the same values as this [NIntArray].
 */
expect inline val NIntArray.intArrayValue: IntArray

/**
 * Converts this [NIntArray] to a [LongArray].
 *
 * @return A new [LongArray] containing the same values as this [NIntArray].
 */
expect inline val NIntArray.longArrayValue: LongArray

/**
 * Checks if the array contains the specified [value].
 *
 * @param value The value to check for.
 * @return `true` if the array contains the specified value, `false` otherwise.
 */
expect inline operator fun NIntArray.contains(value: NInt): Boolean

/**
 * Gets the element at the specified [index].
 *
 * @param index The index of the element to retrieve.
 * @return The element at the specified index.
 * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
 */
expect inline operator fun NIntArray.get(index: Int): NInt

/**
 * Sets the element at the specified [index] to the specified [value].
 *
 * @param index The index of the element to set.
 * @param value The value to set.
 * @throws IndexOutOfBoundsException if the index is out of bounds of this array.
 */
expect inline operator fun NIntArray.set(index: Int, value: NInt)

/**
 * Returns a new array that is a concatenation of this array and the [other] array.
 *
 * @param other The array to concatenate with this array.
 * @return A new array that contains all elements of this array followed by all elements of the [other] array.
 */
expect inline operator fun NIntArray.plus(other: NIntArray): NIntArray

/**
 * Returns a new array containing all elements of this array except those that are present in the [other] array.
 *
 * @param other The array containing elements to exclude.
 * @return A new array containing elements from this array that are not present in the [other] array.
 */
expect inline operator fun NIntArray.minus(other: NIntArray): NIntArray

/**
 * Creates a [Sequence] that iterates over all elements in this array.
 *
 * @return A sequence that yields all elements of this array.
 */
expect inline fun NIntArray.asSequence(): Sequence<NInt>

/**
 * Converts this [NIntArray] to an [NUIntArray].
 *
 * @return A new [NUIntArray] containing the same values as this [NIntArray] but interpreted as unsigned integers.
 */
inline fun NIntArray.asNUIntArray(): NUIntArray = NUIntArray(this)
