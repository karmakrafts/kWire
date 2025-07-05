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
import kotlin.jvm.JvmInline

@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class VoidPtr @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address {
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Comparable<N>> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)
    inline fun <F : Function<*>> reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)

    inline fun align(alignment: NUInt): VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.toUInt()
    inline fun asInt(): Int = rawAddress.value.toInt()
    inline fun asULong(): ULong = rawAddress.toULong()
    inline fun asLong(): Long = rawAddress.value.toLong()

    inline operator fun plus(other: NUInt): VoidPtr = VoidPtr(rawAddress + other)
    inline operator fun plus(other: Int): VoidPtr = VoidPtr(rawAddress + other.toNUInt())
    inline operator fun plus(other: Long): VoidPtr = VoidPtr(rawAddress + other.toNUInt())

    inline operator fun minus(other: NUInt): VoidPtr = VoidPtr(rawAddress - other)
    inline operator fun minus(other: Int): VoidPtr = VoidPtr(rawAddress - other.toNUInt())
    inline operator fun minus(other: Long): VoidPtr = VoidPtr(rawAddress - other.toNUInt())

    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

inline fun NUInt.asVoidPtr(): VoidPtr = VoidPtr(this)
inline fun ULong.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun Long.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun UInt.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
inline fun Int.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())