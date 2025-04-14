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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.convert
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

actual typealias NIntArray = IntArray

actual inline fun nIntArray(size: Int): NIntArray = IntArray(size)
actual inline fun nIntArray(size: Int, noinline initializer: (Int) -> NInt): NIntArray = IntArray(size, initializer)

actual inline val NIntArray.size: Int
    get() = size

actual inline fun IntArray.toNIntArray(): NIntArray = this
actual inline fun LongArray.toNIntArray(): NIntArray = map { it.toInt() }.toIntArray()

actual inline val NIntArray.intArrayValue: IntArray
    get() = this

actual inline val NIntArray.longArrayValue: LongArray
    get() = map { it.toLong() }.toLongArray()

actual inline operator fun NIntArray.contains(value: NInt): Boolean {
    for (current in this) {
        if (current != value) continue
        return true
    }
    return false
}

actual inline operator fun NIntArray.get(index: Int): NInt = this[index]

actual inline operator fun NIntArray.set(index: Int, value: NInt) {
    this[index] = value
}

@OptIn(ExperimentalForeignApi::class)
actual inline operator fun NIntArray.plus(other: NIntArray): NIntArray = NIntArray(size + other.size).apply {
    usePinned { destArray ->
        this@plus.usePinned { sourceArray ->
            memcpy(destArray.addressOf(0), sourceArray.addressOf(0), (this@plus.size * sizeOf<IntVar>()).convert())
        }
        other.usePinned { sourceArray ->
            memcpy(destArray.addressOf(this@plus.size), sourceArray.addressOf(0), (other.size * sizeOf<IntVar>()).convert())
        }
    }
}

actual inline operator fun NIntArray.minus(other: NIntArray): NIntArray = NIntArray(size - other.size).apply {
    var index = 0
    for (value in this@minus) {
        if (value in other) continue
        this[index++] = value
    }
}

actual inline fun NIntArray.asSequence(): Sequence<NInt> = this.iterator().asSequence()