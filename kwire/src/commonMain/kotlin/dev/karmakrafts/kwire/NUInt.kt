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

@file:JvmName("NUInt$")
@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

// TODO: document this
@JvmInline
value class NUInt @PublishedApi internal constructor(
    @PublishedApi internal val value: NInt
) {
    companion object {
        inline val SIZE_BYTES: Int
            get() = NInt.SIZE_BYTES
    }
}

expect inline fun UInt.toNUInt(): NUInt
expect inline fun Int.toNUInt(): NUInt
expect inline fun ULong.toNUInt(): NUInt
expect inline fun Long.toNUInt(): NUInt

expect inline val NUInt.uintValue: UInt
expect inline val NUInt.ulongValue: ULong

expect inline operator fun NUInt.compareTo(other: NUInt): Int

expect inline operator fun NUInt.plus(other: NUInt): NUInt
expect inline operator fun NUInt.minus(other: NUInt): NUInt
expect inline operator fun NUInt.times(other: NUInt): NUInt
expect inline operator fun NUInt.div(other: NUInt): NUInt
expect inline operator fun NUInt.rem(other: NUInt): NUInt

expect inline infix fun NUInt.and(other: NUInt): NUInt
expect inline infix fun NUInt.or(other: NUInt): NUInt
expect inline infix fun NUInt.xor(other: NUInt): NUInt
expect inline infix fun NUInt.shl(count: Int): NUInt
expect inline infix fun NUInt.shr(count: Int): NUInt
expect inline fun NUInt.inv(): NUInt

inline fun NUInt.toSigned(): NInt = value

@ExperimentalStdlibApi
inline fun NUInt.toHexString(): String = ulongValue.toHexString()