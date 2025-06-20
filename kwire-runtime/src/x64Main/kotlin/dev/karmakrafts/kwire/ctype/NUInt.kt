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

// Ignore @JvmInline warning due to @OptionalExpectation
@KWireCompilerApi
actual value class NUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: NInt
) : Comparable<NUInt> {
    actual override fun toString(): String = value.toString()
    actual override operator fun compareTo(other: NUInt): Int = value.compareTo(other.value)

    actual inline fun toByte(): Byte = value.toByte()
    actual inline fun toShort(): Short = value.toShort()
    actual inline fun toInt(): Int = value.toInt()
    actual inline fun toLong(): Long = value
    actual inline fun toFloat(): Float = value.toFloat()
    actual inline fun toDouble(): Double = value.toDouble()

    actual inline fun toUByte(): UByte = value.toUByte()
    actual inline fun toUShort(): UShort = value.toUShort()
    actual inline fun toUInt(): UInt = value.toUInt()
    actual inline fun toULong(): ULong = value.toULong()

    actual inline operator fun plus(other: NUInt): NUInt = (value.toULong() + other.value.toULong()).toNUInt()
    actual inline operator fun minus(other: NUInt): NUInt = (value.toULong() - other.value.toULong()).toNUInt()
    actual inline operator fun times(other: NUInt): NUInt = (value.toULong() * other.value.toULong()).toNUInt()
    actual inline operator fun div(other: NUInt): NUInt = (value.toULong() / other.value.toULong()).toNUInt()
    actual inline operator fun rem(other: NUInt): NUInt = (value.toULong() % other.value.toULong()).toNUInt()

    actual inline operator fun inc(): NUInt = value.toULong().inc().toNUInt()
    actual inline operator fun dec(): NUInt = value.toULong().dec().toNUInt()

    actual inline infix fun shl(bitCount: Int): NUInt = (value.toULong() shl bitCount).toNUInt()
    actual inline infix fun shr(bitCount: Int): NUInt = (value.toULong() shr bitCount).toNUInt()
    actual inline infix fun and(other: NUInt): NUInt = (value.toULong() and other.value.toULong()).toNUInt()
    actual inline infix fun or(other: NUInt): NUInt = (value.toULong() or other.value.toULong()).toNUInt()
    actual inline infix fun xor(other: NUInt): NUInt = (value.toULong() xor other.value.toULong()).toNUInt()
    actual inline fun inv(): NUInt = value.toULong().inv().toNUInt()
}

@KWireCompilerApi
actual inline fun UByte.toNUInt(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun UShort.toNUInt(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun UInt.toNUInt(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun ULong.toNUInt(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun NInt.toUnsigned(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun NFloat.toUnsigned(): NUInt = NUInt(toLong())

@KWireCompilerApi
actual inline fun NUInt.toNInt(): NInt = value

@KWireCompilerApi
actual inline fun NUInt.toNFloat(): NFloat = value.toDouble()