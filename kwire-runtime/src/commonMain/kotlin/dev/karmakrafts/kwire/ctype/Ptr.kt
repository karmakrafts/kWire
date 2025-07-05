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
import dev.karmakrafts.kwire.memory.sizeOf
import kotlin.jvm.JvmInline

@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class Ptr<T : Pointed> @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address {
    inline var value: T
        get() = deref()
        set(value) {
            set(value)
        }

    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Comparable<N>> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)
    inline fun <F : Function<*>> reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)

    inline fun align(alignment: NUInt): Ptr<T> = Ptr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.toUInt()
    inline fun asInt(): Int = rawAddress.value.toInt()
    inline fun asULong(): ULong = rawAddress.toULong()
    inline fun asLong(): Long = rawAddress.value.toLong()

    inline operator fun plus(other: NUInt): Ptr<T> = Ptr(rawAddress + (sizeOf<T>() * other))
    inline operator fun plus(other: Int): Ptr<T> = Ptr(rawAddress + (sizeOf<T>() * other.toNUInt()))
    inline operator fun plus(other: Long): Ptr<T> = Ptr(rawAddress + (sizeOf<T>() * other.toNUInt()))

    inline operator fun minus(other: NUInt): Ptr<T> = Ptr(rawAddress - (sizeOf<T>() * other))
    inline operator fun minus(other: Int): Ptr<T> = Ptr(rawAddress - (sizeOf<T>() * other.toNUInt()))
    inline operator fun minus(other: Long): Ptr<T> = Ptr(rawAddress - (sizeOf<T>() * other.toNUInt()))

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): T = throw KWirePluginNotAppliedException()

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    fun set(value: T): Unit = throw KWirePluginNotAppliedException()

    inline operator fun get(index: NUInt): T = (this + index).deref()
    inline operator fun get(index: Int): T = (this + index).deref()
    inline operator fun get(index: Long): T = (this + index).deref()

    inline operator fun set(index: NUInt, value: T) = (this + index).set(value)
    inline operator fun set(index: Int, value: T) = (this + index).set(value)
    inline operator fun set(index: Long, value: T) = (this + index).set(value)

    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

inline fun <T : Pointed> NUInt.asPtr(): Ptr<T> = Ptr(this)
inline fun <T : Pointed> ULong.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> Long.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> UInt.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> Int.asPtr(): Ptr<T> = Ptr(toNUInt())

@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <T : Pointed> T.ref(): Ptr<T> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.PTR_NULL)
fun <P : Address> nullptr(): P = throw KWirePluginNotAppliedException()