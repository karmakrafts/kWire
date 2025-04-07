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

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar

@ExperimentalForeignApi
actual inline fun Pinned<NIntArray>.addressOf(index: Int): CPointer<ptrdiff_tVar>? = this.addressOf(index)

@ExperimentalForeignApi
actual inline fun Pinned<NUIntArray>.addressOf(index: Int): CPointer<size_tVar>? =
    get().value.usePinned { it.addressOf(0) }.reinterpret()

// TODO: make the returned pointer type safe
@ExperimentalForeignApi
actual inline fun Pinned<NFloatArray>.addressOf(index: Int): COpaquePointer? = this.addressOf(index)