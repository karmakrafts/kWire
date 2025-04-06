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

// TODO: document this
@JvmInline
value class Pointer(val value: NUInt) {
    operator fun plus(other: NUInt): Pointer = Pointer(value + other)
    operator fun minus(other: NUInt): Pointer = Pointer(value - other)

    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String = "0x${value.toHexString()}"
}

// TODO: document this
val nullptr: Pointer = Pointer(0U.toNUInt())