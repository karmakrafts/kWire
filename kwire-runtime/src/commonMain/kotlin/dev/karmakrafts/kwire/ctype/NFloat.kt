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
 * 64 bits on 64-bit platforms). NFloat values are natively sized IEEE754 values.
 *
 * NFloat supports standard arithmetic operations and comparisons, making it
 * suitable for platform-specific floating-point calculations and low-level operations.
 */
@KWireCompilerApi
expect class NFloat : Number, Comparable<NFloat> {
    override fun toByte(): Byte
    override fun toDouble(): Double
    override fun toFloat(): Float
    override fun toInt(): Int
    override fun toLong(): Long
    override fun toShort(): Short

    override operator fun compareTo(other: NFloat): Int

    operator fun plus(other: NFloat): NFloat
    operator fun minus(other: NFloat): NFloat
    operator fun times(other: NFloat): NFloat
    operator fun div(other: NFloat): NFloat
    operator fun rem(other: NFloat): NFloat

    operator fun unaryPlus(): NFloat
    operator fun unaryMinus(): NFloat
}

@KWireCompilerApi
expect inline fun Float.toNFloat(): NFloat

@KWireCompilerApi
expect inline fun Double.toNFloat(): NFloat

@KWireCompilerApi
expect inline fun Byte.toNFloat(): NFloat

@KWireCompilerApi
expect inline fun Short.toNFloat(): NFloat

@KWireCompilerApi
expect inline fun Int.toNFloat(): NFloat

@KWireCompilerApi
expect inline fun Long.toNFloat(): NFloat

inline fun UByte.toNFloat(): NFloat = toByte().toNFloat()
inline fun UShort.toNFloat(): NFloat = toShort().toNFloat()
inline fun UInt.toNFloat(): NFloat = toInt().toNFloat()
inline fun ULong.toNFloat(): NFloat = toLong().toNFloat()

inline fun NFloat.toUByte(): UByte = toByte().toUByte()
inline fun NFloat.toUShort(): UShort = toShort().toUShort()
inline fun NFloat.toUInt(): UInt = toInt().toUInt()
inline fun NFloat.toULong(): ULong = toLong().toULong()