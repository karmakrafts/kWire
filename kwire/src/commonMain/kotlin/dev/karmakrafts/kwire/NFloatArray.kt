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
@file:JvmName("NFloatArray$")
// @formatter:on

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

// TODO: document this
expect class NFloatArray

expect inline fun nFloatArray(size: Int): NFloatArray
expect inline fun nFloatArray(size: Int, noinline initializer: (Int) -> NFloat): NFloatArray

expect inline val NFloatArray.size: Int

expect inline fun FloatArray.toNFloatArray(): NFloatArray
expect inline fun DoubleArray.toNFloatArray(): NFloatArray

expect inline val NFloatArray.floatArrayValue: FloatArray
expect inline val NFloatArray.doubleArrayValue: DoubleArray

expect inline operator fun NFloatArray.contains(value: NFloat): Boolean

expect inline operator fun NFloatArray.get(index: Int): NFloat
expect inline operator fun NFloatArray.set(index: Int, value: NFloat)

expect inline operator fun NFloatArray.plus(other: NFloatArray): NFloatArray
expect inline operator fun NFloatArray.minus(other: NFloatArray): NFloatArray

expect inline fun NFloatArray.asSequence(): Sequence<NFloat>