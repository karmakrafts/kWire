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
@KWireCompilerApi
expect class NInt : Number, Comparable<NInt> {
    /**
     * Converts this NInt value to a Byte.
     *
     * @return The Byte representation of this value.
     */
    override fun toByte(): Byte

    /**
     * Converts this NInt value to a Double.
     *
     * @return The Double representation of this value.
     */
    override fun toDouble(): Double

    /**
     * Converts this NInt value to a Float.
     *
     * @return The Float representation of this value.
     */
    override fun toFloat(): Float

    /**
     * Converts this NInt value to an Int.
     *
     * @return The Int representation of this value.
     */
    override fun toInt(): Int

    /**
     * Converts this NInt value to a Long.
     *
     * @return The Long representation of this value.
     */
    override fun toLong(): Long

    /**
     * Converts this NInt value to a Short.
     *
     * @return The Short representation of this value.
     */
    override fun toShort(): Short

    /**
     * Compares this NInt value with the specified NInt value for order.
     *
     * @param other The NInt value to be compared.
     * @return A negative integer, zero, or a positive integer as this value
     *         is less than, equal to, or greater than the specified value.
     */
    override operator fun compareTo(other: NInt): Int

    /**
     * Adds the specified NInt value to this value.
     *
     * @param other The value to be added.
     * @return The sum of this value and the specified value.
     */
    operator fun plus(other: NInt): NInt

    /**
     * Subtracts the specified NInt value from this value.
     *
     * @param other The value to be subtracted.
     * @return The difference between this value and the specified value.
     */
    operator fun minus(other: NInt): NInt

    /**
     * Multiplies this value by the specified NInt value.
     *
     * @param other The value to multiply by.
     * @return The product of this value and the specified value.
     */
    operator fun times(other: NInt): NInt

    /**
     * Divides this value by the specified NInt value.
     *
     * @param other The value to divide by.
     * @return The quotient of this value divided by the specified value.
     */
    operator fun div(other: NInt): NInt

    /**
     * Calculates the remainder of dividing this value by the specified NInt value.
     *
     * @param other The value to divide by.
     * @return The remainder of this value divided by the specified value.
     */
    operator fun rem(other: NInt): NInt

    /**
     * Increments this value by one.
     *
     * @return The incremented value.
     */
    operator fun inc(): NInt

    /**
     * Decrements this value by one.
     *
     * @return The decremented value.
     */
    operator fun dec(): NInt

    /**
     * Returns this value as is (unary plus operation).
     *
     * @return This value.
     */
    operator fun unaryPlus(): NInt

    /**
     * Returns the negation of this value (unary minus operation).
     *
     * @return The negated value.
     */
    operator fun unaryMinus(): NInt

    /**
     * Performs a bitwise left shift operation on this value.
     *
     * @param bitCount The number of bits to shift left.
     * @return The result of shifting this value left by the specified number of bits.
     */
    infix fun shl(bitCount: Int): NInt

    /**
     * Performs a bitwise right shift operation on this value.
     *
     * @param bitCount The number of bits to shift right.
     * @return The result of shifting this value right by the specified number of bits.
     */
    infix fun shr(bitCount: Int): NInt

    /**
     * Performs a bitwise AND operation between this value and the specified value.
     *
     * @param other The value to perform the AND operation with.
     * @return The result of the bitwise AND operation.
     */
    infix fun and(other: NInt): NInt

    /**
     * Performs a bitwise OR operation between this value and the specified value.
     *
     * @param other The value to perform the OR operation with.
     * @return The result of the bitwise OR operation.
     */
    infix fun or(other: NInt): NInt

    /**
     * Performs a bitwise XOR operation between this value and the specified value.
     *
     * @param other The value to perform the XOR operation with.
     * @return The result of the bitwise XOR operation.
     */
    infix fun xor(other: NInt): NInt

    /**
     * Performs a bitwise inversion (NOT) operation on this value.
     *
     * @return The result of the bitwise inversion operation.
     */
    fun inv(): NInt
}

/**
 * Converts a Byte value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Byte value.
 */
@KWireCompilerApi
expect inline fun Byte.toNInt(): NInt

/**
 * Converts a Short value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Short value.
 */
@KWireCompilerApi
expect inline fun Short.toNInt(): NInt

/**
 * Converts an Int value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Int value.
 */
@KWireCompilerApi
expect inline fun Int.toNInt(): NInt

/**
 * Converts a Long value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Long value.
 */
@KWireCompilerApi
expect inline fun Long.toNInt(): NInt

/**
 * Converts a Float value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Float value.
 */
@KWireCompilerApi
expect inline fun Float.toNInt(): NInt

/**
 * Converts a Double value to a platform-dependent NInt.
 *
 * @return The NInt representation of this Double value.
 */
@KWireCompilerApi
expect inline fun Double.toNInt(): NInt

/**
 * Converts an unsigned Byte value to a platform-dependent NInt.
 * This is done by first converting to a signed Int and then to NInt.
 *
 * @return The NInt representation of this UByte value.
 */
inline fun UByte.toNInt(): NInt = toInt().toNInt()

/**
 * Converts an unsigned Short value to a platform-dependent NInt.
 * This is done by first converting to a signed Short and then to NInt.
 *
 * @return The NInt representation of this UShort value.
 */
inline fun UShort.toNInt(): NInt = toShort().toNInt()

/**
 * Converts an unsigned Int value to a platform-dependent NInt.
 * This is done by first converting to a signed Int and then to NInt.
 *
 * @return The NInt representation of this UInt value.
 */
inline fun UInt.toNInt(): NInt = toInt().toNInt()

/**
 * Converts an unsigned Long value to a platform-dependent NInt.
 * This is done by first converting to a signed Long and then to NInt.
 *
 * @return The NInt representation of this ULong value.
 */
inline fun ULong.toNInt(): NInt = toLong().toNInt()

/**
 * Converts this NInt value to an unsigned Byte.
 * This is done by first converting to a signed Byte and then to UByte.
 *
 * @return The UByte representation of this NInt value.
 */
inline fun NInt.toUByte(): UByte = toByte().toUByte()

/**
 * Converts this NInt value to an unsigned Short.
 * This is done by first converting to a signed Short and then to UShort.
 *
 * @return The UShort representation of this NInt value.
 */
inline fun NInt.toUShort(): UShort = toShort().toUShort()

/**
 * Converts this NInt value to an unsigned Int.
 * This is done by first converting to a signed Int and then to UInt.
 *
 * @return The UInt representation of this NInt value.
 */
inline fun NInt.toUInt(): UInt = toInt().toUInt()

/**
 * Converts this NInt value to an unsigned Long.
 * This is done by first converting to a signed Long and then to ULong.
 *
 * @return The ULong representation of this NInt value.
 */
inline fun NInt.toULong(): ULong = toLong().toULong()

/**
 * Converts this native integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native integer
 */
@ExperimentalStdlibApi
inline fun NInt.toHexString(): String = if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) toInt().toHexString()
else toLong().toHexString()
