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
@KWireCompilerApi
@JvmInline
expect value class NUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: NInt
) : Comparable<NUInt> {
    override fun toString(): String
    override operator fun compareTo(other: NUInt): Int

    inline fun toByte(): Byte
    inline fun toShort(): Short
    inline fun toInt(): Int
    inline fun toLong(): Long
    inline fun toFloat(): Float
    inline fun toDouble(): Double

    inline fun toUByte(): UByte
    inline fun toUShort(): UShort
    inline fun toUInt(): UInt
    inline fun toULong(): ULong

    inline operator fun plus(other: NUInt): NUInt
    inline operator fun minus(other: NUInt): NUInt
    inline operator fun times(other: NUInt): NUInt
    inline operator fun div(other: NUInt): NUInt
    inline operator fun rem(other: NUInt): NUInt

    inline operator fun inc(): NUInt
    inline operator fun dec(): NUInt

    inline infix fun shl(bitCount: Int): NUInt
    inline infix fun shr(bitCount: Int): NUInt
    inline infix fun and(other: NUInt): NUInt
    inline infix fun or(other: NUInt): NUInt
    inline infix fun xor(other: NUInt): NUInt
    inline fun inv(): NUInt
}

@KWireCompilerApi
expect inline fun UByte.toNUInt(): NUInt

@KWireCompilerApi
expect inline fun UShort.toNUInt(): NUInt

@KWireCompilerApi
expect inline fun UInt.toNUInt(): NUInt

@KWireCompilerApi
expect inline fun ULong.toNUInt(): NUInt

inline fun Byte.toNUInt(): NUInt = toUByte().toNUInt()
inline fun Short.toNUInt(): NUInt = toUShort().toNUInt()
inline fun Int.toNUInt(): NUInt = toUInt().toNUInt()
inline fun Long.toNUInt(): NUInt = toULong().toNUInt()

@KWireCompilerApi
expect inline fun NInt.toUnsigned(): NUInt

@KWireCompilerApi
expect inline fun NFloat.toUnsigned(): NUInt

@KWireCompilerApi
expect inline fun NUInt.toNInt(): NInt

@KWireCompilerApi
expect inline fun NUInt.toNFloat(): NFloat

/**
 * Converts this native unsigned integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native unsigned integer
 */
@ExperimentalStdlibApi
inline fun NUInt.toHexString(): String = toULong().toHexString()
