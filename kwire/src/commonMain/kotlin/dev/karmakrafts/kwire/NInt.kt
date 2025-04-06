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

@file:JvmName("NInt$")
@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

// TODO: document this
expect class NInt {
    companion object
}

// TODO: document this
expect val NInt.Companion.SIZE_BYTES: Int

expect inline fun Int.toNInt(): NInt
expect inline fun UInt.toNInt(): NInt
expect inline fun Long.toNInt(): NInt
expect inline fun ULong.toNInt(): NInt

expect inline val NInt.intValue: Int
expect inline val NInt.longValue: Long

expect inline operator fun NInt.compareTo(other: NInt): Int

expect inline operator fun NInt.plus(other: NInt): NInt
expect inline operator fun NInt.minus(other: NInt): NInt
expect inline operator fun NInt.times(other: NInt): NInt
expect inline operator fun NInt.div(other: NInt): NInt
expect inline operator fun NInt.rem(other: NInt): NInt

expect inline infix fun NInt.and(other: NInt): NInt
expect inline infix fun NInt.or(other: NInt): NInt
expect inline infix fun NInt.xor(other: NInt): NInt
expect inline infix fun NInt.shl(count: Int): NInt
expect inline infix fun NInt.shr(count: Int): NInt
expect inline fun NInt.inv(): NInt

inline fun NInt.toUnsigned(): NUInt = NUInt(this)

@ExperimentalStdlibApi
inline fun NInt.toHexString(): String = longValue.toHexString()