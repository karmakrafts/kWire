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
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Represents a platform-dependent unsigned integer type.
 *
 * This value class wraps a [NInt] and provides a consistent interface for working with
 * native unsigned integers across different platforms. The size of a native unsigned integer
 * depends on the platform (typically 32 bits on 32-bit platforms and 64 bits on 64-bit platforms).
 *
 * NUInt supports standard arithmetic operations, comparisons, and bitwise operations, making it
 * suitable for platform-specific memory addressing and low-level operations.
 *
 * @property value The underlying [NInt] value
 */
@JvmInline
value class NUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: NInt
)

/**
 * Converts a standard 32-bit unsigned integer to a native unsigned integer.
 *
 * @return A native unsigned integer representation of this UInt value
 */
expect inline fun UInt.toNUInt(): NUInt

/**
 * Converts a standard 32-bit signed integer to a native unsigned integer.
 *
 * @return A native unsigned integer representation of this Int value
 */
expect inline fun Int.toNUInt(): NUInt

/**
 * Converts a standard 64-bit unsigned integer to a native unsigned integer.
 *
 * @return A native unsigned integer representation of this ULong value
 * @note On 32-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit unsigned integer
 */
expect inline fun ULong.toNUInt(): NUInt

/**
 * Converts a standard 64-bit signed integer to a native unsigned integer.
 *
 * @return A native unsigned integer representation of this Long value
 * @note On 32-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit unsigned integer
 */
expect inline fun Long.toNUInt(): NUInt

/**
 * Converts a native floating-point number to a native unsigned integer.
 *
 * @return A native unsigned integer representation of this NFloat value
 * @note This may result in truncation of the fractional part
 * @note Negative values will be converted to their unsigned representation
 */
expect inline fun NFloat.toNUInt(): NUInt

/**
 * Gets the value of this native unsigned integer as a standard 32-bit unsigned integer.
 *
 * @return The UInt representation of this native unsigned integer
 * @note On 64-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit unsigned integer
 */
expect inline val NUInt.uintValue: UInt

/**
 * Gets the value of this native unsigned integer as a standard 64-bit unsigned integer.
 *
 * @return The ULong representation of this native unsigned integer
 */
expect inline val NUInt.ulongValue: ULong

/**
 * Compares this native unsigned integer with another native unsigned integer.
 *
 * @param other The native unsigned integer to compare with
 * @return A negative value if this < other, zero if this == other, or a positive value if this > other
 */
expect inline operator fun NUInt.compareTo(other: NUInt): Int

/**
 * Adds another native unsigned integer to this native unsigned integer.
 *
 * @param other The native unsigned integer to add
 * @return The sum of this native unsigned integer and the other native unsigned integer
 */
expect inline operator fun NUInt.plus(other: NUInt): NUInt

/**
 * Subtracts another native unsigned integer from this native unsigned integer.
 *
 * @param other The native unsigned integer to subtract
 * @return The difference between this native unsigned integer and the other native unsigned integer
 */
expect inline operator fun NUInt.minus(other: NUInt): NUInt

/**
 * Multiplies this native unsigned integer by another native unsigned integer.
 *
 * @param other The native unsigned integer to multiply by
 * @return The product of this native unsigned integer and the other native unsigned integer
 */
expect inline operator fun NUInt.times(other: NUInt): NUInt

/**
 * Divides this native unsigned integer by another native unsigned integer.
 *
 * @param other The native unsigned integer to divide by
 * @return The quotient of this native unsigned integer divided by the other native unsigned integer
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NUInt.div(other: NUInt): NUInt

/**
 * Calculates the remainder of dividing this native unsigned integer by another native unsigned integer.
 *
 * @param other The native unsigned integer to divide by
 * @return The remainder of this native unsigned integer divided by the other native unsigned integer
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NUInt.rem(other: NUInt): NUInt

/**
 * Performs a bitwise AND operation between this native unsigned integer and another native unsigned integer.
 *
 * @param other The native unsigned integer to perform the AND operation with
 * @return The result of the bitwise AND operation
 */
expect inline infix fun NUInt.and(other: NUInt): NUInt

/**
 * Performs a bitwise OR operation between this native unsigned integer and another native unsigned integer.
 *
 * @param other The native unsigned integer to perform the OR operation with
 * @return The result of the bitwise OR operation
 */
expect inline infix fun NUInt.or(other: NUInt): NUInt

/**
 * Performs a bitwise XOR operation between this native unsigned integer and another native unsigned integer.
 *
 * @param other The native unsigned integer to perform the XOR operation with
 * @return The result of the bitwise XOR operation
 */
expect inline infix fun NUInt.xor(other: NUInt): NUInt

/**
 * Performs a bitwise left shift operation on this native unsigned integer.
 *
 * @param count The number of bits to shift left
 * @return The result of the bitwise left shift operation
 */
expect inline infix fun NUInt.shl(count: Int): NUInt

/**
 * Performs a bitwise right shift operation on this native unsigned integer.
 *
 * @param count The number of bits to shift right
 * @return The result of the bitwise right shift operation
 */
expect inline infix fun NUInt.shr(count: Int): NUInt

/**
 * Performs a bitwise inversion (NOT) operation on this native unsigned integer.
 *
 * @return The result of the bitwise inversion operation
 */
expect inline fun NUInt.inv(): NUInt

/**
 * Converts this native unsigned integer to a native signed integer.
 *
 * @return The native signed integer representation of this native unsigned integer
 */
inline fun NUInt.toSigned(): NInt = value

/**
 * Converts this native unsigned integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native unsigned integer
 */
@ExperimentalStdlibApi
inline fun NUInt.toHexString(): String = ulongValue.toHexString()
