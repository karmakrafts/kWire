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

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.memory.Memory

internal expect val pointerSize: Int

@KWireCompilerApi
sealed interface Address : Pointed {
    companion object {
        val SIZE_BYTES: Int get() = pointerSize
    }

    val rawAddress: NUInt
}

inline fun <R : Pointed> Address.reinterpret(): Ptr<R> = Ptr(rawAddress)
inline fun <N : Comparable<N>> Address.reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
inline fun <F : Function<*>> Address.reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)
inline fun Address.reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)

inline fun Address.align(alignment: NUInt): VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

inline fun Address.asNUInt(): NUInt = rawAddress
inline fun Address.asNInt(): NInt = rawAddress.value
inline fun Address.asUInt(): UInt = rawAddress.toUInt()
inline fun Address.asInt(): Int = rawAddress.value.toInt()
inline fun Address.asULong(): ULong = rawAddress.toULong()
inline fun Address.asLong(): Long = rawAddress.value.toLong()

inline operator fun Address.plus(other: NUInt): VoidPtr = VoidPtr(rawAddress + other)
inline operator fun Address.plus(other: Int): VoidPtr = VoidPtr(rawAddress + other.toNUInt())
inline operator fun Address.plus(other: Long): VoidPtr = VoidPtr(rawAddress + other.toNUInt())

inline operator fun Address.minus(other: NUInt): VoidPtr = VoidPtr(rawAddress - other)
inline operator fun Address.minus(other: Int): VoidPtr = VoidPtr(rawAddress - other.toNUInt())
inline operator fun Address.minus(other: Long): VoidPtr = VoidPtr(rawAddress - other.toNUInt())