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

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import dev.karmakrafts.kwire.memory.Memory
import dev.karmakrafts.kwire.memory.sizeOf
import kotlin.jvm.JvmInline

@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class NumPtr<N : Number> @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address, Pointed {
    companion object {
        @PublishedApi
        internal val nullptr: NumPtr<Number> = NumPtr(0U.toNUInt())
    }

    inline var value: N
        get() = deref()
        set(value) {
            set(value)
        }

    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Number> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)

    inline fun align(alignment: NUInt): NumPtr<N> = NumPtr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.uintValue
    inline fun asInt(): Int = rawAddress.value.intValue
    inline fun asULong(): ULong = rawAddress.ulongValue
    inline fun asLong(): Long = rawAddress.value.longValue

    inline operator fun plus(other: NUInt): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>() * other))
    inline operator fun plus(other: Int): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>() * other.toNUInt()))
    inline operator fun plus(other: Long): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>() * other.toNUInt()))

    inline operator fun minus(other: NUInt): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>() * other))
    inline operator fun minus(other: Int): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>() * other.toNUInt()))
    inline operator fun minus(other: Long): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>() * other.toNUInt()))

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): N = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    fun set(value: N): Unit = throw KWirePluginNotAppliedException()

    inline operator fun get(index: NUInt): N = (this + index).deref()
    inline operator fun get(index: Int): N = (this + index).deref()
    inline operator fun get(index: Long): N = (this + index).deref()

    inline operator fun set(index: NUInt, value: N) = (this + index).set(value)
    inline operator fun set(index: Int, value: N) = (this + index).set(value)
    inline operator fun set(index: Long, value: N) = (this + index).set(value)

    inline operator fun equals(other: NumPtr<*>): Boolean = rawAddress == other.rawAddress
    override fun toString(): String = "0x${rawAddress.toHexString()}"

    override fun equals(other: Any?): Boolean = when (other) {
        is Ptr<*> -> rawAddress == other.rawAddress
        is NumPtr<*> -> rawAddress == other.rawAddress
        is VoidPtr -> rawAddress == other.rawAddress
        else -> false
    }

    override fun hashCode(): Int = rawAddress.hashCode()
}

inline fun <N : Number> NUInt.asNumPtr(): NumPtr<N> = NumPtr(this)
inline fun <N : Number> ULong.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Number> Long.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Number> UInt.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Number> Int.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <N : Number> N.ref(): NumPtr<N> = throw KWirePluginNotAppliedException()