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
    actual inline fun toInt(): Int = value
    actual inline fun toLong(): Long = value.toLong()
    actual inline fun toFloat(): Float = value.toFloat()
    actual inline fun toDouble(): Double = value.toDouble()

    actual inline fun toUByte(): UByte = value.toUByte()
    actual inline fun toUShort(): UShort = value.toUShort()
    actual inline fun toUInt(): UInt = value.toUInt()
    actual inline fun toULong(): ULong = value.toULong()

    actual inline operator fun plus(other: NUInt): NUInt = (value.toUInt() + other.value.toUInt()).toNUInt()
    actual inline operator fun minus(other: NUInt): NUInt = (value.toUInt() - other.value.toUInt()).toNUInt()
    actual inline operator fun times(other: NUInt): NUInt = (value.toUInt() * other.value.toUInt()).toNUInt()
    actual inline operator fun div(other: NUInt): NUInt = (value.toUInt() / other.value.toUInt()).toNUInt()
    actual inline operator fun rem(other: NUInt): NUInt = (value.toUInt() % other.value.toUInt()).toNUInt()

    actual inline operator fun inc(): NUInt = value.toUInt().inc().toNUInt()
    actual inline operator fun dec(): NUInt = value.toUInt().dec().toNUInt()

    actual inline infix fun shl(bitCount: Int): NUInt = (value.toUInt() shl bitCount).toNUInt()
    actual inline infix fun shr(bitCount: Int): NUInt = (value.toUInt() shr bitCount).toNUInt()
    actual inline infix fun and(other: NUInt): NUInt = (value.toUInt() and other.value.toUInt()).toNUInt()
    actual inline infix fun or(other: NUInt): NUInt = (value.toUInt() or other.value.toUInt()).toNUInt()
    actual inline infix fun xor(other: NUInt): NUInt = (value.toUInt() xor other.value.toUInt()).toNUInt()
    actual inline fun inv(): NUInt = value.toUInt().inv().toNUInt()
}

@KWireCompilerApi
actual inline fun UByte.toNUInt(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun UShort.toNUInt(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun UInt.toNUInt(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun ULong.toNUInt(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun NInt.toUnsigned(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun NFloat.toUnsigned(): NUInt = NUInt(toInt())

@KWireCompilerApi
actual inline fun NUInt.toNInt(): NInt = value

@KWireCompilerApi
actual inline fun NUInt.toNFloat(): NFloat = toFloat()