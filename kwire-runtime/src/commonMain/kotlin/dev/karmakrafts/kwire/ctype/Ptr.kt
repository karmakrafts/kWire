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

@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(ExperimentalStdlibApi::class)

package dev.karmakrafts.kwire.ctype

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import dev.karmakrafts.kwire.memory.Memory
import dev.karmakrafts.kwire.memory.sizeOf
import kotlin.jvm.JvmInline

internal expect val pointerSize: Int

sealed interface Pointed

sealed interface Address {
    companion object {
        val SIZE_BYTES: Int get() = pointerSize
    }

    val rawAddress: NUInt
}

inline fun <R : Pointed> Address.reinterpret(): Ptr<R> = Ptr(rawAddress)
inline fun <N : Number> Address.reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
inline fun Address.reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)

inline fun Address.align(alignment: NUInt): VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

inline fun Address.asNUInt(): NUInt = rawAddress
inline fun Address.asNInt(): NInt = rawAddress.value
inline fun Address.asUInt(): UInt = rawAddress.uintValue
inline fun Address.asInt(): Int = rawAddress.value.intValue
inline fun Address.asULong(): ULong = rawAddress.ulongValue
inline fun Address.asLong(): Long = rawAddress.value.longValue

inline operator fun Address.plus(other: NUInt): VoidPtr = VoidPtr(rawAddress + other)
inline operator fun Address.plus(other: Int): VoidPtr = VoidPtr(rawAddress + other.toNUInt())
inline operator fun Address.plus(other: Long): VoidPtr = VoidPtr(rawAddress + other.toNUInt())

inline operator fun Address.minus(other: NUInt): VoidPtr = VoidPtr(rawAddress - other)
inline operator fun Address.minus(other: Int): VoidPtr = VoidPtr(rawAddress - other.toNUInt())
inline operator fun Address.minus(other: Long): VoidPtr = VoidPtr(rawAddress - other.toNUInt())

@JvmInline
value class VoidPtr @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address, Pointed {
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

@JvmInline
value class Ptr<T : Pointed> @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address, Pointed {
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Number> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)

    inline fun align(alignment: NUInt): Ptr<T> = Ptr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.uintValue
    inline fun asInt(): Int = rawAddress.value.intValue
    inline fun asULong(): ULong = rawAddress.ulongValue
    inline fun asLong(): Long = rawAddress.value.longValue

    inline operator fun plus(other: NUInt): Ptr<T> = Ptr(rawAddress + (sizeOf<T>().toNUInt() * other))
    inline operator fun plus(other: Int): Ptr<T> = Ptr(rawAddress + (sizeOf<T>().toNUInt() * other.toNUInt()))
    inline operator fun plus(other: Long): Ptr<T> = Ptr(rawAddress + (sizeOf<T>().toNUInt() * other.toNUInt()))

    inline operator fun minus(other: NUInt): Ptr<T> = Ptr(rawAddress - (sizeOf<T>().toNUInt() * other))
    inline operator fun minus(other: Int): Ptr<T> = Ptr(rawAddress - (sizeOf<T>().toNUInt() * other.toNUInt()))
    inline operator fun minus(other: Long): Ptr<T> = Ptr(rawAddress - (sizeOf<T>().toNUInt() * other.toNUInt()))

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): T = throw KWirePluginNotAppliedException()

    inline operator fun equals(other: Ptr<*>): Boolean = rawAddress == other.rawAddress
    override fun toString(): String = "0x${rawAddress.toHexString()}"

    override fun equals(other: Any?): Boolean = when (other) {
        is Ptr<*> -> rawAddress == other.rawAddress
        is NumPtr<*> -> rawAddress == other.rawAddress
        is VoidPtr -> rawAddress == other.rawAddress
        else -> false
    }

    override fun hashCode(): Int = rawAddress.hashCode()
}

inline fun <T : Pointed> NUInt.asPtr(): Ptr<T> = Ptr(this)
inline fun <T : Pointed> ULong.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> Long.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> UInt.asPtr(): Ptr<T> = Ptr(toNUInt())
inline fun <T : Pointed> Int.asPtr(): Ptr<T> = Ptr(toNUInt())

@KWireIntrinsic(KWireIntrinsic.Type.ADDRESS_OF)
fun <T : Pointed> T.ref(): Ptr<T> = throw KWirePluginNotAppliedException()

@JvmInline
value class NumPtr<N : Number> @PublishedApi internal constructor(
    override val rawAddress: NUInt
) : Address, Pointed {
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)
    inline fun <N : Number> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)

    inline fun align(alignment: NUInt): NumPtr<N> = NumPtr(Memory.align(rawAddress, alignment))

    inline fun asNUInt(): NUInt = rawAddress
    inline fun asNInt(): NInt = rawAddress.value
    inline fun asUInt(): UInt = rawAddress.uintValue
    inline fun asInt(): Int = rawAddress.value.intValue
    inline fun asULong(): ULong = rawAddress.ulongValue
    inline fun asLong(): Long = rawAddress.value.longValue

    inline operator fun plus(other: NUInt): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>().toNUInt() * other))
    inline operator fun plus(other: Int): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>().toNUInt() * other.toNUInt()))
    inline operator fun plus(other: Long): NumPtr<N> = NumPtr(rawAddress + (sizeOf<N>().toNUInt() * other.toNUInt()))

    inline operator fun minus(other: NUInt): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>().toNUInt() * other))
    inline operator fun minus(other: Int): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>().toNUInt() * other.toNUInt()))
    inline operator fun minus(other: Long): NumPtr<N> = NumPtr(rawAddress - (sizeOf<N>().toNUInt() * other.toNUInt()))

    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): N = throw KWirePluginNotAppliedException()

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

@KWireIntrinsic(KWireIntrinsic.Type.ADDRESS_OF)
fun <N : Number> N.ref(): NumPtr<N> = throw KWirePluginNotAppliedException()

@KWireIntrinsic(KWireIntrinsic.Type.PTR_NULL)
fun <P : Address> nullptr(): P = throw KWirePluginNotAppliedException()