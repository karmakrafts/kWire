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
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.convert
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

actual typealias NFloatArray = FloatArray

actual inline fun nFloatArray(size: Int): NFloatArray = NFloatArray(size)
actual inline fun nFloatArray(size: Int, noinline initializer: (Int) -> NFloat): NFloatArray =
    NFloatArray(size, initializer)

actual inline val NFloatArray.size: Int
    get() = this.size

actual inline fun FloatArray.toNFloatArray(): NFloatArray = this
actual inline fun DoubleArray.toNFloatArray(): NFloatArray = NFloatArray(size) { this@toNFloatArray[it].toFloat() }

actual inline val NFloatArray.floatArrayValue: FloatArray
    get() = this

actual inline val NFloatArray.doubleArrayValue: DoubleArray
    get() = DoubleArray(size) { this@doubleArrayValue[it].toDouble() }

actual inline operator fun NFloatArray.contains(value: NFloat): Boolean {
    for (current in this) {
        if (current != value) continue
        return true
    }
    return false
}

actual inline operator fun NFloatArray.get(index: Int): NFloat = this[index]

actual inline operator fun NFloatArray.set(index: Int, value: NFloat) {
    this[index] = value
}

@OptIn(ExperimentalForeignApi::class)
actual inline operator fun NFloatArray.plus(other: NFloatArray): NFloatArray = NFloatArray(size + other.size).apply {
    usePinned { destArray ->
        this@plus.usePinned { sourceArray ->
            memcpy(destArray.addressOf(0), sourceArray.addressOf(0), (this@plus.size * sizeOf<FloatVar>()).convert())
        }
        other.usePinned { sourceArray ->
            memcpy(destArray.addressOf(this@plus.size), sourceArray.addressOf(0), (other.size * sizeOf<FloatVar>()).convert())
        }
    }
}

actual inline operator fun NFloatArray.minus(other: NFloatArray): NFloatArray = NFloatArray(size - other.size).apply {
    var index = 0
    for (value in this@minus) {
        if (value in other) continue
        this[index++] = value
    }
}

actual inline fun NFloatArray.asSequence(): Sequence<NFloat> = this.iterator().asSequence()