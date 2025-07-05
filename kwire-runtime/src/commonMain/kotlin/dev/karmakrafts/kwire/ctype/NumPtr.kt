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
import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import dev.karmakrafts.kwire.memory.Memory
import kotlin.jvm.JvmInline

@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class NumPtr<N : Comparable<N>> @PublishedApi internal constructor(
    @KWireCompilerApi override val rawAddress: NUInt
) : Address {
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Comparable<N>> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)
    inline fun <F : Function<*>> reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)

    inline fun align(alignment: NUInt): NumPtr<N> = NumPtr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.toUInt()
    inline fun asInt(): Int = rawAddress.value.toInt()
    inline fun asULong(): ULong = rawAddress.toULong()
    inline fun asLong(): Long = rawAddress.value.toLong()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: NUInt): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Int): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Long): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: NUInt): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Int): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Long): NumPtr<N> = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): N = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: NUInt): N = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Int): N = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Long): N = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    fun set(value: N): Unit = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: NUInt, value: N): Unit = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Int, value: N): Unit = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Long, value: N): Unit = throw KWirePluginNotAppliedException()

    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

inline fun <N : Comparable<N>> NUInt.asNumPtr(): NumPtr<N> = NumPtr(this)
inline fun <N : Comparable<N>> ULong.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Comparable<N>> Long.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Comparable<N>> UInt.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())
inline fun <N : Comparable<N>> Int.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <N : Comparable<N>> N.ref(): NumPtr<N> = throw KWirePluginNotAppliedException()