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
import kotlinx.cinterop.UnsafeNumber
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar

/**
 * Gets a pointer to the element at the specified [index] in a pinned [NIntArray].
 *
 * This function allows direct memory access to elements in a native integer array.
 * The array must be pinned to prevent it from being moved by the garbage collector.
 *
 * @param index The index of the element to get a pointer to.
 * @return A pointer to the element at the specified index, or null if the index is out of bounds.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
expect inline fun Pinned<NIntArray>.addressOf(index: Int): CPointer<ptrdiff_tVar>?

/**
 * Gets a pointer to the element at the specified [index] in a pinned [NUIntArray].
 *
 * This function allows direct memory access to elements in a native unsigned integer array.
 * The array must be pinned to prevent it from being moved by the garbage collector.
 *
 * @param index The index of the element to get a pointer to.
 * @return A pointer to the element at the specified index, or null if the index is out of bounds.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
expect inline fun Pinned<NUIntArray>.addressOf(index: Int): CPointer<size_tVar>?

/**
 * Gets a pointer to the element at the specified [index] in a pinned [NFloatArray].
 *
 * This function allows direct memory access to elements in a native floating-point array.
 * The array must be pinned to prevent it from being moved by the garbage collector.
 *
 * @param index The index of the element to get a pointer to.
 * @return A pointer to the element at the specified index, or null if the index is out of bounds.
 * @note The returned pointer is not type-safe and should be used with caution.
 */
// TODO: make the returned pointer type safe
@ExperimentalForeignApi
expect inline fun Pinned<NFloatArray>.addressOf(index: Int): COpaquePointer?
