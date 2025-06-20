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

package dev.karmakrafts.kwire.ctype

import com.v7878.foreign.MemorySegment

/**
 * Converts this [MemorySegment] to a [Address].
 *
 * This extension function creates a pointer that points to the same memory address as this memory segment.
 *
 * @return A [Address] that points to the same memory address as this memory segment.
 */
inline fun MemorySegment.toPtr(): VoidPtr = address().asVoidPtr()

/**
 * Converts this [Address] to a [MemorySegment].
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer.
 *
 * @return A [MemorySegment] that points to the same memory address as this pointer.
 */
inline fun Address.toMemorySegment(): MemorySegment {
    return MemorySegment.ofAddress(rawAddress.value)
}

/**
 * Converts this [Address] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as an [NUInt].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Address.toMemorySegment(size: NUInt): MemorySegment {
    return MemorySegment.ofAddress(rawAddress.value).reinterpret(size.value)
}

/**
 * Converts this [Address] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as a [Long].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Address.toMemorySegment(size: Long): MemorySegment {
    return MemorySegment.ofAddress(rawAddress.value).reinterpret(size)
}

/**
 * Converts this [Address] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as an [Int].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Address.toMemorySegment(size: Int): MemorySegment {
    return MemorySegment.ofAddress(rawAddress.value).reinterpret(size.toLong())
}
