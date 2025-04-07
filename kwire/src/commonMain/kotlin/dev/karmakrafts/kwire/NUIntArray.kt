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

import kotlin.jvm.JvmInline

@JvmInline
value class NUIntArray @PublishedApi internal constructor(
    @PublishedApi internal val value: NIntArray
) {
    inline val size: Int
        get() = value.size

    inline operator fun contains(value: NUInt): Boolean = value.value in this.value

    inline operator fun get(index: Int): NUInt = NUInt(value[index])

    inline operator fun set(index: Int, value: NUInt) {
        this.value[index] = value.value
    }

    inline operator fun plus(other: NUIntArray): NUIntArray = NUIntArray(value + other.value)
    inline operator fun minus(other: NUIntArray): NUIntArray = NUIntArray(value - other.value)

    inline fun asSequence(): Sequence<NUInt> = value.asSequence().map(::NUInt)

    inline fun asNIntArray(): NIntArray = value
}

inline fun nUIntArray(size: Int): NUIntArray = NUIntArray(nIntArray(size))

inline fun nUIntArray(size: Int, crossinline initializer: (Int) -> NUInt): NUIntArray {
    return NUIntArray(nIntArray(size) {
        initializer(it).value
    })
}