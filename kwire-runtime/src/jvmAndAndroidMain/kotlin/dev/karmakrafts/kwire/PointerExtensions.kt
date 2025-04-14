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

import java.lang.foreign.MemorySegment

/**
 * Converts this [MemorySegment] to a [Pointer].
 *
 * This extension function creates a pointer that points to the same memory address as this memory segment.
 *
 * @return A [Pointer] that points to the same memory address as this memory segment.
 */
inline fun MemorySegment.toPointer(): Pointer = Pointer(address().toNUInt())

/**
 * Converts this [Pointer] to a [MemorySegment].
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer.
 *
 * @return A [MemorySegment] that points to the same memory address as this pointer.
 */
inline fun Pointer.toMemorySegment(): MemorySegment {
    return MemorySegment.ofAddress(value.value.toLong())
}

/**
 * Converts this [Pointer] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as an [NUInt].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Pointer.toMemorySegment(size: NUInt): MemorySegment {
    return MemorySegment.ofAddress(value.value.toLong()).reinterpret(size.value.toLong())
}

/**
 * Converts this [Pointer] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as a [Long].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Pointer.toMemorySegment(size: Long): MemorySegment {
    return MemorySegment.ofAddress(value.value.toLong()).reinterpret(size)
}

/**
 * Converts this [Pointer] to a [MemorySegment] with a specified size.
 *
 * This extension function creates a memory segment that points to the same memory address as this pointer
 * and has the specified size in bytes.
 *
 * @param size The size of the memory segment in bytes as an [Int].
 * @return A [MemorySegment] that points to the same memory address as this pointer and has the specified size.
 */
inline fun Pointer.toMemorySegment(size: Int): MemorySegment {
    return MemorySegment.ofAddress(value.value.toLong()).reinterpret(size.toLong())
}
