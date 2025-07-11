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
@file:JvmName("NFloatArrayImpl")
// @formatter:on

package dev.karmakrafts.kwire.ctype

actual typealias NFloatArray = DoubleArray

actual inline fun nFloatArray(size: Int): NFloatArray = NFloatArray(size)
actual inline fun nFloatArray(size: Int, noinline initializer: (Int) -> NFloat): NFloatArray =
    NFloatArray(size, initializer)

actual inline val NFloatArray.size: Int
    get() = this.size

actual inline fun FloatArray.toNFloatArray(): NFloatArray = NFloatArray(size) { this@toNFloatArray[it].toDouble() }
actual inline fun DoubleArray.toNFloatArray(): NFloatArray = this

actual inline val NFloatArray.floatArrayValue: FloatArray
    get() = FloatArray(size) { this@floatArrayValue[it].toFloat() }

actual inline val NFloatArray.doubleArrayValue: DoubleArray
    get() = this

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

actual inline operator fun NFloatArray.plus(other: NFloatArray): NFloatArray = NFloatArray(size + other.size).apply {
    System.arraycopy(this@plus, 0, this, 0, this@plus.size)
    System.arraycopy(other, 0, this, this@plus.size, other.size)
}

actual inline operator fun NFloatArray.minus(other: NFloatArray): NFloatArray = NFloatArray(size - other.size).apply {
    var index = 0
    for (value in this@minus) {
        if (value in other) continue
        this[index++] = value
    }
}

actual inline fun NFloatArray.asSequence(): Sequence<NFloat> = this.iterator().asSequence()