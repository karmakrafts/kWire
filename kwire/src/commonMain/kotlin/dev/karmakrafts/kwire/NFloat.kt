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
@file:JvmName("NFloat$")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

expect class NFloat

expect inline fun Float.toNFloat(): NFloat
expect inline fun Double.toNFloat(): NFloat

expect inline val NFloat.floatValue: Float
expect inline val NFloat.doubleValue: Double

expect inline operator fun NFloat.compareTo(other: NFloat): Int

expect inline operator fun NFloat.plus(other: NFloat): NFloat
expect inline operator fun NFloat.minus(other: NFloat): NFloat
expect inline operator fun NFloat.times(other: NFloat): NFloat
expect inline operator fun NFloat.div(other: NFloat): NFloat
expect inline operator fun NFloat.rem(other: NFloat): NFloat