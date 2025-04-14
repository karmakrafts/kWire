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

// @formatter:off
@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("NFloatImpl")
// @formatter:on

package dev.karmakrafts.kwire

actual typealias NFloat = Double

actual inline fun Float.toNFloat(): NFloat = toDouble()
actual inline fun Double.toNFloat(): NFloat = this
actual inline fun NInt.toNFloat(): NFloat = toDouble()
actual inline fun NUInt.toNFloat(): NFloat = value.toDouble()

actual inline val NFloat.floatValue: Float
    get() = toFloat()

actual inline val NFloat.doubleValue: Double
    get() = this

actual inline operator fun NFloat.compareTo(other: NFloat): Int = this.compareTo(other)

actual inline operator fun NFloat.plus(other: NFloat): NFloat = this + other
actual inline operator fun NFloat.minus(other: NFloat): NFloat = this - other
actual inline operator fun NFloat.times(other: NFloat): NFloat = this * other
actual inline operator fun NFloat.div(other: NFloat): NFloat = this / other
actual inline operator fun NFloat.rem(other: NFloat): NFloat = this % other
