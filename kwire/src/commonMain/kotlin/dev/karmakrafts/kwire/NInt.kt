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
 * Represents a platform-dependent signed integer type.
 *
 * This class provides a consistent interface for working with native integers across different platforms.
 * The size of a native integer depends on the platform (typically 32 bits on 32-bit platforms and
 * 64 bits on 64-bit platforms).
 *
 * NInt supports standard arithmetic operations, comparisons, and bitwise operations, making it
 * suitable for platform-specific memory addressing and low-level operations.
 */
expect class NInt

/**
 * Converts a standard 32-bit signed integer to a native integer.
 *
 * @return A native integer representation of this Int value
 */
expect inline fun Int.toNInt(): NInt

/**
 * Converts a standard 32-bit unsigned integer to a native integer.
 *
 * @return A native integer representation of this UInt value
 */
expect inline fun UInt.toNInt(): NInt

/**
 * Converts a standard 64-bit signed integer to a native integer.
 *
 * @return A native integer representation of this Long value
 * @note On 32-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit integer
 */
expect inline fun Long.toNInt(): NInt

/**
 * Converts a standard 64-bit unsigned integer to a native integer.
 *
 * @return A native integer representation of this ULong value
 * @note On 32-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit integer
 */
expect inline fun ULong.toNInt(): NInt

/**
 * Converts a native floating-point number to a native integer.
 *
 * @return A native integer representation of this NFloat value
 * @note This may result in truncation of the fractional part
 */
expect inline fun NFloat.toNInt(): NInt

/**
 * Gets the value of this native integer as a standard 32-bit signed integer.
 *
 * @return The Int representation of this native integer
 * @note On 64-bit platforms, this may result in truncation if the value exceeds the range of a 32-bit integer
 */
expect inline val NInt.intValue: Int

/**
 * Gets the value of this native integer as a standard 64-bit signed integer.
 *
 * @return The Long representation of this native integer
 */
expect inline val NInt.longValue: Long

/**
 * Compares this native integer with another native integer.
 *
 * @param other The native integer to compare with
 * @return A negative value if this < other, zero if this == other, or a positive value if this > other
 */
expect inline operator fun NInt.compareTo(other: NInt): Int

/**
 * Adds another native integer to this native integer.
 *
 * @param other The native integer to add
 * @return The sum of this native integer and the other native integer
 */
expect inline operator fun NInt.plus(other: NInt): NInt

/**
 * Subtracts another native integer from this native integer.
 *
 * @param other The native integer to subtract
 * @return The difference between this native integer and the other native integer
 */
expect inline operator fun NInt.minus(other: NInt): NInt

/**
 * Multiplies this native integer by another native integer.
 *
 * @param other The native integer to multiply by
 * @return The product of this native integer and the other native integer
 */
expect inline operator fun NInt.times(other: NInt): NInt

/**
 * Divides this native integer by another native integer.
 *
 * @param other The native integer to divide by
 * @return The quotient of this native integer divided by the other native integer
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NInt.div(other: NInt): NInt

/**
 * Calculates the remainder of dividing this native integer by another native integer.
 *
 * @param other The native integer to divide by
 * @return The remainder of this native integer divided by the other native integer
 * @throws ArithmeticException if the divisor is zero
 */
expect inline operator fun NInt.rem(other: NInt): NInt

expect inline operator fun NInt.inc(): NInt

expect inline operator fun NInt.dec(): NInt

/**
 * Performs a bitwise AND operation between this native integer and another native integer.
 *
 * @param other The native integer to perform the AND operation with
 * @return The result of the bitwise AND operation
 */
expect inline infix fun NInt.and(other: NInt): NInt

/**
 * Performs a bitwise OR operation between this native integer and another native integer.
 *
 * @param other The native integer to perform the OR operation with
 * @return The result of the bitwise OR operation
 */
expect inline infix fun NInt.or(other: NInt): NInt

/**
 * Performs a bitwise XOR operation between this native integer and another native integer.
 *
 * @param other The native integer to perform the XOR operation with
 * @return The result of the bitwise XOR operation
 */
expect inline infix fun NInt.xor(other: NInt): NInt

/**
 * Performs a bitwise left shift operation on this native integer.
 *
 * @param count The number of bits to shift left
 * @return The result of the bitwise left shift operation
 */
expect inline infix fun NInt.shl(count: Int): NInt

/**
 * Performs a bitwise right shift operation on this native integer.
 *
 * @param count The number of bits to shift right
 * @return The result of the bitwise right shift operation
 */
expect inline infix fun NInt.shr(count: Int): NInt

/**
 * Performs a bitwise inversion (NOT) operation on this native integer.
 *
 * @return The result of the bitwise inversion operation
 */
expect inline fun NInt.inv(): NInt

/**
 * Converts this native integer to a native unsigned integer.
 *
 * @return The native unsigned integer representation of this native integer
 */
inline fun NInt.toUnsigned(): NUInt = NUInt(this)

/**
 * Converts this native integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native integer
 */
@ExperimentalStdlibApi
inline fun NInt.toHexString(): String = if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) intValue.toHexString()
else longValue.toHexString()
