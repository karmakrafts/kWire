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

import dev.karmakrafts.kwire.memory.Memory
import kotlin.jvm.JvmInline

@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class VoidPtr @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address, Pointed {
    companion object {
        @PublishedApi
        internal val nullptr: VoidPtr = VoidPtr(0U.toNUInt())
    }

    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Number> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)

    inline fun align(alignment: NUInt): VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.uintValue
    inline fun asInt(): Int = rawAddress.value.intValue
    inline fun asULong(): ULong = rawAddress.ulongValue
    inline fun asLong(): Long = rawAddress.value.longValue

    inline operator fun VoidPtr.plus(other: NUInt): VoidPtr = VoidPtr(rawAddress + other)
    inline operator fun VoidPtr.plus(other: Int): VoidPtr = VoidPtr(rawAddress + other.toNUInt())
    inline operator fun VoidPtr.plus(other: Long): VoidPtr = VoidPtr(rawAddress + other.toNUInt())

    inline operator fun VoidPtr.minus(other: NUInt): VoidPtr = VoidPtr(rawAddress - other)
    inline operator fun VoidPtr.minus(other: Int): VoidPtr = VoidPtr(rawAddress - other.toNUInt())
    inline operator fun VoidPtr.minus(other: Long): VoidPtr = VoidPtr(rawAddress - other.toNUInt())

    inline operator fun equals(other: VoidPtr): Boolean = rawAddress == other.rawAddress
    override fun toString(): String = "0x${rawAddress.toHexString()}"

    override fun equals(other: Any?): Boolean = when (other) {
        is Ptr<*> -> rawAddress == other.rawAddress
        is NumPtr<*> -> rawAddress == other.rawAddress
        is VoidPtr -> rawAddress == other.rawAddress
        else -> false
    }

    override fun hashCode(): Int = rawAddress.hashCode()
}

inline fun NUInt.asVoidPtr(): VoidPtr = VoidPtr(this)
inline fun ULong.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun Long.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun UInt.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun Int.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())