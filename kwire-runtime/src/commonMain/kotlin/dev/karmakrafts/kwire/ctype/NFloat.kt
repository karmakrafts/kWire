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
 * Represents a platform-dependent floating-point number type.
 *
 * This class provides a consistent interface for working with native floating-point numbers across different platforms.
 * The size of a native floating-point number depends on the platform (typically 32 bits on 32-bit platforms and
 * 64 bits on 64-bit platforms).
 */
@KWireCompilerApi
expect class NFloat : Number, Comparable<NFloat> {
    /**
     * Converts this NFloat value to a Byte.
     *
     * @return The Byte representation of this value.
     */
    override fun toByte(): Byte

    /**
     * Converts this NFloat value to a Double.
     *
     * @return The Double representation of this value.
     */
    override fun toDouble(): Double

    /**
     * Converts this NFloat value to a Float.
     *
     * @return The Float representation of this value.
     */
    override fun toFloat(): Float

    /**
     * Converts this NFloat value to an Int.
     *
     * @return The Int representation of this value.
     */
    override fun toInt(): Int

    /**
     * Converts this NFloat value to a Long.
     *
     * @return The Long representation of this value.
     */
    override fun toLong(): Long

    /**
     * Converts this NFloat value to a Short.
     *
     * @return The Short representation of this value.
     */
    override fun toShort(): Short

    /**
     * Compares this NFloat value with the specified NFloat value for order.
     *
     * @param other The NFloat value to be compared.
     * @return A negative integer, zero, or a positive integer as this value
     *         is less than, equal to, or greater than the specified value.
     */
    override operator fun compareTo(other: NFloat): Int

    /**
     * Adds the specified NFloat value to this value.
     *
     * @param other The value to be added.
     * @return The sum of this value and the specified value.
     */
    operator fun plus(other: NFloat): NFloat

    /**
     * Subtracts the specified NFloat value from this value.
     *
     * @param other The value to be subtracted.
     * @return The difference between this value and the specified value.
     */
    operator fun minus(other: NFloat): NFloat

    /**
     * Multiplies this value by the specified NFloat value.
     *
     * @param other The value to multiply by.
     * @return The product of this value and the specified value.
     */
    operator fun times(other: NFloat): NFloat

    /**
     * Divides this value by the specified NFloat value.
     *
     * @param other The value to divide by.
     * @return The quotient of this value divided by the specified value.
     */
    operator fun div(other: NFloat): NFloat

    /**
     * Calculates the remainder of dividing this value by the specified NFloat value.
     *
     * @param other The value to divide by.
     * @return The remainder of this value divided by the specified value.
     */
    operator fun rem(other: NFloat): NFloat

    /**
     * Returns this value as is (unary plus operation).
     *
     * @return This value.
     */
    operator fun unaryPlus(): NFloat

    /**
     * Returns the negation of this value (unary minus operation).
     *
     * @return The negated value.
     */
    operator fun unaryMinus(): NFloat
}

/**
 * Converts a Float value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Float value.
 */
@KWireCompilerApi
expect inline fun Float.toNFloat(): NFloat

/**
 * Converts a Double value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Double value.
 */
@KWireCompilerApi
expect inline fun Double.toNFloat(): NFloat

/**
 * Converts a Byte value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Byte value.
 */
@KWireCompilerApi
expect inline fun Byte.toNFloat(): NFloat

/**
 * Converts a Short value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Short value.
 */
@KWireCompilerApi
expect inline fun Short.toNFloat(): NFloat

/**
 * Converts an Int value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Int value.
 */
@KWireCompilerApi
expect inline fun Int.toNFloat(): NFloat

/**
 * Converts a Long value to a platform-dependent NFloat.
 *
 * @return The NFloat representation of this Long value.
 */
@KWireCompilerApi
expect inline fun Long.toNFloat(): NFloat

/**
 * Converts an unsigned Byte value to a platform-dependent NFloat.
 * This is done by first converting to a signed Byte and then to NFloat.
 *
 * @return The NFloat representation of this UByte value.
 */
inline fun UByte.toNFloat(): NFloat = toByte().toNFloat()

/**
 * Converts an unsigned Short value to a platform-dependent NFloat.
 * This is done by first converting to a signed Short and then to NFloat.
 *
 * @return The NFloat representation of this UShort value.
 */
inline fun UShort.toNFloat(): NFloat = toShort().toNFloat()

/**
 * Converts an unsigned Int value to a platform-dependent NFloat.
 * This is done by first converting to a signed Int and then to NFloat.
 *
 * @return The NFloat representation of this UInt value.
 */
inline fun UInt.toNFloat(): NFloat = toInt().toNFloat()

/**
 * Converts an unsigned Long value to a platform-dependent NFloat.
 * This is done by first converting to a signed Long and then to NFloat.
 *
 * @return The NFloat representation of this ULong value.
 */
inline fun ULong.toNFloat(): NFloat = toLong().toNFloat()

/**
 * Converts this NFloat value to an unsigned Byte.
 * This is done by first converting to a signed Byte and then to UByte.
 *
 * @return The UByte representation of this NFloat value.
 */
inline fun NFloat.toUByte(): UByte = toByte().toUByte()

/**
 * Converts this NFloat value to an unsigned Short.
 * This is done by first converting to a signed Short and then to UShort.
 *
 * @return The UShort representation of this NFloat value.
 */
inline fun NFloat.toUShort(): UShort = toShort().toUShort()

/**
 * Converts this NFloat value to an unsigned Int.
 * This is done by first converting to a signed Int and then to UInt.
 *
 * @return The UInt representation of this NFloat value.
 */
inline fun NFloat.toUInt(): UInt = toInt().toUInt()

/**
 * Converts this NFloat value to an unsigned Long.
 * This is done by first converting to a signed Long and then to ULong.
 *
 * @return The ULong representation of this NFloat value.
 */
inline fun NFloat.toULong(): ULong = toLong().toULong()
