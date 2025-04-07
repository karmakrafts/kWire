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

actual inline fun UInt.toNUInt(): NUInt = NUInt(toLong())
actual inline fun Int.toNUInt(): NUInt = NUInt(toLong())
actual inline fun ULong.toNUInt(): NUInt = NUInt(toLong())
actual inline fun Long.toNUInt(): NUInt = NUInt(this)
actual inline fun NFloat.toNUInt(): NUInt = NUInt(toLong())

actual inline val NUInt.uintValue: UInt
    get() = value.intValue.toUInt()

actual inline val NUInt.ulongValue: ULong
    get() = value.longValue.toULong()

actual inline operator fun NUInt.compareTo(other: NUInt): Int = value.compareTo(other.value)

actual inline operator fun NUInt.plus(other: NUInt): NUInt = NUInt((ulongValue + other.ulongValue).toLong())
actual inline operator fun NUInt.minus(other: NUInt): NUInt = NUInt((ulongValue - other.ulongValue).toLong())
actual inline operator fun NUInt.times(other: NUInt): NUInt = NUInt((ulongValue * other.ulongValue).toLong())
actual inline operator fun NUInt.div(other: NUInt): NUInt = NUInt((ulongValue / other.ulongValue).toLong())
actual inline operator fun NUInt.rem(other: NUInt): NUInt = NUInt((ulongValue % other.ulongValue).toLong())

actual inline infix fun NUInt.and(other: NUInt): NUInt = NUInt((ulongValue and other.ulongValue).toLong())
actual inline infix fun NUInt.or(other: NUInt): NUInt = NUInt((ulongValue or other.ulongValue).toLong())
actual inline infix fun NUInt.xor(other: NUInt): NUInt = NUInt((ulongValue xor other.ulongValue).toLong())
actual inline infix fun NUInt.shl(count: Int): NUInt = NUInt((ulongValue shl count).toLong())
actual inline infix fun NUInt.shr(count: Int): NUInt = NUInt((ulongValue shr count).toLong())
actual inline fun NUInt.inv(): NUInt = NUInt(ulongValue.inv().toLong())
