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
    override fun toByte(): Byte
    override fun toDouble(): Double
    override fun toFloat(): Float
    override fun toInt(): Int
    override fun toLong(): Long
    override fun toShort(): Short

    override operator fun compareTo(other: NInt): Int

    operator fun plus(other: NInt): NInt
    operator fun minus(other: NInt): NInt
    operator fun times(other: NInt): NInt
    operator fun div(other: NInt): NInt
    operator fun rem(other: NInt): NInt

    operator fun inc(): NInt
    operator fun dec(): NInt

    operator fun unaryPlus(): NInt
    operator fun unaryMinus(): NInt

    infix fun shl(bitCount: Int): NInt
    infix fun shr(bitCount: Int): NInt
    infix fun and(other: NInt): NInt
    infix fun or(other: NInt): NInt
    infix fun xor(other: NInt): NInt
    fun inv(): NInt
}

@KWireCompilerApi
expect inline fun Byte.toNInt(): NInt

@KWireCompilerApi
expect inline fun Short.toNInt(): NInt

@KWireCompilerApi
expect inline fun Int.toNInt(): NInt

@KWireCompilerApi
expect inline fun Long.toNInt(): NInt

@KWireCompilerApi
expect inline fun Float.toNInt(): NInt

@KWireCompilerApi
expect inline fun Double.toNInt(): NInt

inline fun UByte.toNInt(): NInt = toInt().toNInt()
inline fun UShort.toNInt(): NInt = toShort().toNInt()
inline fun UInt.toNInt(): NInt = toInt().toNInt()
inline fun ULong.toNInt(): NInt = toLong().toNInt()

inline fun NInt.toUByte(): UByte = toByte().toUByte()
inline fun NInt.toUShort(): UShort = toShort().toUShort()
inline fun NInt.toUInt(): UInt = toInt().toUInt()
inline fun NInt.toULong(): ULong = toLong().toULong()

/**
 * Converts this native integer to a hexadecimal string representation.
 *
 * @return A string containing the hexadecimal representation of this native integer
 */
@ExperimentalStdlibApi
inline fun NInt.toHexString(): String = if (Address.SIZE_BYTES == Int.SIZE_BYTES) toInt().toHexString()
else toLong().toHexString()
