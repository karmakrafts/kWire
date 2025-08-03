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

import dev.karmakrafts.kwire.KWireCompilerApi
import kotlin.jvm.JvmInline

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
@Suppress("WRONG_MODIFIER_TARGET")
@KWireCompilerApi
@JvmInline
expect value class NUInt @PublishedApi internal inline constructor(
    @PublishedApi internal val value: NInt
) : Comparable<NUInt> {
    /**
     * Returns a string representation of this NUInt value.
     *
     * @return A string representation of this value.
     */
    override fun toString(): String

    /**
     * Compares this NUInt value with the specified NUInt value for order.
     *
     * @param other The NUInt value to be compared.
     * @return A negative integer, zero, or a positive integer as this value
     *         is less than, equal to, or greater than the specified value.
     */
    override operator fun compareTo(other: NUInt): Int

    /**
     * Converts this NUInt value to a Byte.
     *
     * @return The Byte representation of this value.
     */
    inline fun toByte(): Byte

    /**
     * Converts this NUInt value to a Short.
     *
     * @return The Short representation of this value.
     */
    inline fun toShort(): Short

    /**
     * Converts this NUInt value to an Int.
     *
     * @return The Int representation of this value.
     */
    inline fun toInt(): Int

    /**
     * Converts this NUInt value to a Long.
     *
     * @return The Long representation of this value.
     */
    inline fun toLong(): Long

    /**
     * Converts this NUInt value to a Float.
     *
     * @return The Float representation of this value.
     */
    inline fun toFloat(): Float

    /**
     * Converts this NUInt value to a Double.
     *
     * @return The Double representation of this value.
     */
    inline fun toDouble(): Double

    /**
     * Converts this NUInt value to an unsigned Byte.
     *
     * @return The UByte representation of this value.
     */
    inline fun toUByte(): UByte

    /**
     * Converts this NUInt value to an unsigned Short.
     *
     * @return The UShort representation of this value.
     */
    inline fun toUShort(): UShort

    /**
     * Converts this NUInt value to an unsigned Int.
     *
     * @return The UInt representation of this value.
     */
    inline fun toUInt(): UInt

    /**
     * Converts this NUInt value to an unsigned Long.
     *
     * @return The ULong representation of this value.
     */
    inline fun toULong(): ULong

    /**
     * Adds the specified NUInt value to this value.
     *
     * @param other The value to be added.
     * @return The sum of this value and the specified value.
     */
    inline operator fun plus(other: NUInt): NUInt

    /**
     * Subtracts the specified NUInt value from this value.
     *
     * @param other The value to be subtracted.
     * @return The difference between this value and the specified value.
     */
    inline operator fun minus(other: NUInt): NUInt

    /**
     * Multiplies this value by the specified NUInt value.
     *
     * @param other The value to multiply by.
     * @return The product of this value and the specified value.
     */
    inline operator fun times(other: NUInt): NUInt

    /**
     * Divides this value by the specified NUInt value.
     *
     * @param other The value to divide by.
     * @return The quotient of this value divided by the specified value.
     */
    inline operator fun div(other: NUInt): NUInt

    /**
     * Calculates the remainder of dividing this value by the specified NUInt value.
     *
     * @param other The value to divide by.
     * @return The remainder of this value divided by the specified value.
     */
    inline operator fun rem(other: NUInt): NUInt

    /**
     * Increments this value by 1.
     *
     * @return The value incremented by 1.
     */
    inline operator fun inc(): NUInt

    /**
     * Decrements this value by 1.
     *
     * @return The value decremented by 1.
     */
    inline operator fun dec(): NUInt

    /**
     * Performs a bitwise left shift operation on this value.
     *
     * @param bitCount The number of bits to shift left.
     * @return The result of shifting this value left by the specified number of bits.
     */
    inline infix fun shl(bitCount: Int): NUInt

    /**
     * Performs a bitwise right shift operation on this value.
     *
     * @param bitCount The number of bits to shift right.
     * @return The result of shifting this value right by the specified number of bits.
     */
    inline infix fun shr(bitCount: Int): NUInt

    /**
     * Performs a bitwise AND operation between this value and the specified value.
     *
     * @param other The value to perform the AND operation with.
     * @return The result of the bitwise AND operation.
     */
    inline infix fun and(other: NUInt): NUInt

    /**
     * Performs a bitwise OR operation between this value and the specified value.
     *
     * @param other The value to perform the OR operation with.
     * @return The result of the bitwise OR operation.
     */
    inline infix fun or(other: NUInt): NUInt

    /**
     * Performs a bitwise XOR operation between this value and the specified value.
     *
     * @param other The value to perform the XOR operation with.
     * @return The result of the bitwise XOR operation.
     */
    inline infix fun xor(other: NUInt): NUInt

    /**
     * Performs a bitwise inversion (NOT) operation on this value.
     *
     * @return The result of the bitwise inversion operation.
     */
    inline fun inv(): NUInt
}

/**
 * Converts an unsigned Byte value to a platform-dependent NUInt.
 *
 * @return The NUInt representation of this UByte value.
 */
@KWireCompilerApi
expect inline fun UByte.toNUInt(): NUInt

/**
 * Converts an unsigned Short value to a platform-dependent NUInt.
 *
 * @return The NUInt representation of this UShort value.
 */
@KWireCompilerApi
expect inline fun UShort.toNUInt(): NUInt

/**
 * Converts an unsigned Int value to a platform-dependent NUInt.
 *
 * @return The NUInt representation of this UInt value.
 */
@KWireCompilerApi
expect inline fun UInt.toNUInt(): NUInt

/**
 * Converts an unsigned Long value to a platform-dependent NUInt.
 *
 * @return The NUInt representation of this ULong value.
 */
@KWireCompilerApi
expect inline fun ULong.toNUInt(): NUInt

/**
 * Converts a Byte value to a platform-dependent NUInt.
 * This is done by first converting to an unsigned Byte and then to NUInt.
 *
 * @return The NUInt representation of this Byte value.
 */
inline fun Byte.toNUInt(): NUInt = toUByte().toNUInt()

/**
 * Converts a Short value to a platform-dependent NUInt.
 * This is done by first converting to an unsigned Short and then to NUInt.
 *
 * @return The NUInt representation of this Short value.
 */
inline fun Short.toNUInt(): NUInt = toUShort().toNUInt()

/**
 * Converts an Int value to a platform-dependent NUInt.
 * This is done by first converting to an unsigned Int and then to NUInt.
 *
 * @return The NUInt representation of this Int value.
 */
inline fun Int.toNUInt(): NUInt = toUInt().toNUInt()

/**
 * Converts a Long value to a platform-dependent NUInt.
 * This is done by first converting to an unsigned Long and then to NUInt.
 *
 * @return The NUInt representation of this Long value.
 */
inline fun Long.toNUInt(): NUInt = toULong().toNUInt()

/**
 * Converts a native integer to its unsigned representation.
 *
 * @return The unsigned representation of this native integer.
 */
@KWireCompilerApi
expect inline fun NInt.toUnsigned(): NUInt

/**
 * Converts a native floating-point number to an unsigned integer.
 *
 * @return The unsigned integer representation of this native floating-point number.
 */
@KWireCompilerApi
expect inline fun NFloat.toUnsigned(): NUInt

/**
 * Converts this native unsigned integer to a native integer.
 *
 * @return The native integer representation of this unsigned value.
 */
@KWireCompilerApi
expect inline fun NUInt.toNInt(): NInt

/**
 * Converts this native unsigned integer to a native floating-point number.
 *
 * @return The native floating-point representation of this unsigned value.
 */
@KWireCompilerApi
expect inline fun NUInt.toNFloat(): NFloat

/**
 * Converts this native unsigned integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native unsigned integer
 */
@ExperimentalStdlibApi
inline fun NUInt.toHexString(): String = toULong().toHexString()
