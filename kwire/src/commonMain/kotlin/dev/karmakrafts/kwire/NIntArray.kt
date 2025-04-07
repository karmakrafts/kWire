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
@file:JvmName("NIntArray$")
// @formatter:on

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

// TODO: document this
expect class NIntArray
expect inline fun nIntArray(size: Int): NIntArray
expect inline fun nIntArray(size: Int, noinline initializer: (Int) -> NInt): NIntArray

expect inline val NIntArray.size: Int

expect inline fun IntArray.toNIntArray(): NIntArray
expect inline fun LongArray.toNIntArray(): NIntArray

expect inline val NIntArray.intArrayValue: IntArray
expect inline val NIntArray.longArrayValue: LongArray

expect inline operator fun NIntArray.get(index: Int): NInt
expect inline operator fun NIntArray.set(index: Int, value: NInt)

expect inline operator fun NIntArray.plus(other: NIntArray): NIntArray
expect inline operator fun NIntArray.minus(other: NIntArray): NIntArray

expect inline fun NIntArray.asSequence(): Sequence<NInt>

inline fun NIntArray.asNUIntArray(): NUIntArray = NUIntArray(this)