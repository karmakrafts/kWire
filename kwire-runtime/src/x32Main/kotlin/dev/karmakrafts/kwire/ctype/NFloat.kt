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

actual typealias NFloat = Float

actual inline fun Float.toNFloat(): NFloat = this
actual inline fun Double.toNFloat(): NFloat = toFloat()
actual inline fun NInt.toNFloat(): NFloat = toFloat()
actual inline fun NUInt.toNFloat(): NFloat = value.toFloat()

actual inline val NFloat.floatValue: Float
    get() = this

actual inline val NFloat.doubleValue: Double
    get() = toDouble()

actual inline operator fun NFloat.compareTo(other: NFloat): Int = this.compareTo(other)

actual inline operator fun NFloat.plus(other: NFloat): NFloat = this + other
actual inline operator fun NFloat.minus(other: NFloat): NFloat = this - other
actual inline operator fun NFloat.times(other: NFloat): NFloat = this * other
actual inline operator fun NFloat.div(other: NFloat): NFloat = this / other
actual inline operator fun NFloat.rem(other: NFloat): NFloat = this % other
