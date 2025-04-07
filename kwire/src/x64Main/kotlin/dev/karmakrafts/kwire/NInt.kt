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

actual typealias NInt = Long

actual inline fun Int.toNInt(): NInt = toLong()
actual inline fun UInt.toNInt(): NInt = toLong()
actual inline fun Long.toNInt(): NInt = this
actual inline fun ULong.toNInt(): NInt = toLong()

actual inline val NInt.intValue: Int
    get() = this.toInt()

actual inline val NInt.longValue: Long
    get() = this

actual inline operator fun NInt.compareTo(other: NInt): Int = this.compareTo(other)

actual inline operator fun NInt.plus(other: NInt): NInt = this + other
actual inline operator fun NInt.minus(other: NInt): NInt = this - other
actual inline operator fun NInt.times(other: NInt): NInt = this * other
actual inline operator fun NInt.div(other: NInt): NInt = this / other
actual inline operator fun NInt.rem(other: NInt): NInt = this % other

actual inline infix fun NInt.and(other: NInt): NInt = this and other
actual inline infix fun NInt.or(other: NInt): NInt = this or other
actual inline infix fun NInt.xor(other: NInt): NInt = this xor other
actual inline infix fun NInt.shl(count: Int): NInt = this shl count
actual inline infix fun NInt.shr(count: Int): NInt = this shr count
actual inline fun NInt.inv(): NInt = this.inv()