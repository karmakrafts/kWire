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
@file:JvmName("NFloat$")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

/**
 * Represents a platform-dependent floating-point number type.
 *
 * This class provides a consistent interface for working with native floating-point numbers across different platforms.
 * The size of a native floating-point number depends on the platform (typically 32 bits on 32-bit platforms and 
 * 64 bits on 64-bit platforms). NFloat values are natively sized IEEE754 values.
 *
 * NFloat supports standard arithmetic operations and comparisons, making it
 * suitable for platform-specific floating-point calculations and low-level operations.
 */
expect class NFloat

/**
 * Converts a standard Float to a native floating-point number.
 *
 * @return A native floating-point representation of this Float value
 */
expect inline fun Float.toNFloat(): NFloat

/**
 * Converts a standard Double to a native floating-point number.
 *
 * @return A native floating-point representation of this Double value
 */
expect inline fun Double.toNFloat(): NFloat

/**
 * Converts a native integer to a native floating-point number.
 *
 * @return A native floating-point representation of this NInt value
 */
expect inline fun NInt.toNFloat(): NFloat

/**
 * Converts a native unsigned integer to a native floating-point number.
 *
 * @return A native floating-point representation of this NUInt value
 */
expect inline fun NUInt.toNFloat(): NFloat

/**
 * Gets the value of this native floating-point number as a standard Float.
 *
 * @return The Float representation of this native floating-point number
 */
expect inline val NFloat.floatValue: Float

/**
 * Gets the value of this native floating-point number as a standard Double.
 *
 * @return The Double representation of this native floating-point number
 */
expect inline val NFloat.doubleValue: Double

/**
 * Compares this native floating-point number with another native floating-point number.
 *
 * @param other The native floating-point number to compare with
 * @return A negative value if this < other, zero if this == other, or a positive value if this > other
 */
expect inline operator fun NFloat.compareTo(other: NFloat): Int

/**
 * Adds another native floating-point number to this native floating-point number.
 *
 * @param other The native floating-point number to add
 * @return The sum of this native floating-point number and the other native floating-point number
 */
expect inline operator fun NFloat.plus(other: NFloat): NFloat

/**
 * Subtracts another native floating-point number from this native floating-point number.
 *
 * @param other The native floating-point number to subtract
 * @return The difference between this native floating-point number and the other native floating-point number
 */
expect inline operator fun NFloat.minus(other: NFloat): NFloat

/**
 * Multiplies this native floating-point number by another native floating-point number.
 *
 * @param other The native floating-point number to multiply by
 * @return The product of this native floating-point number and the other native floating-point number
 */
expect inline operator fun NFloat.times(other: NFloat): NFloat

/**
 * Divides this native floating-point number by another native floating-point number.
 *
 * @param other The native floating-point number to divide by
 * @return The quotient of this native floating-point number divided by the other native floating-point number
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NFloat.div(other: NFloat): NFloat

/**
 * Calculates the remainder of dividing this native floating-point number by another native floating-point number.
 *
 * @param other The native floating-point number to divide by
 * @return The remainder of this native floating-point number divided by the other native floating-point number
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NFloat.rem(other: NFloat): NFloat
