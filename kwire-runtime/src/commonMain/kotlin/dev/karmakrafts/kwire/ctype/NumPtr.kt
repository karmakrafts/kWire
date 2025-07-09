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

/**
 * Represents a pointer to a numeric value in the C type system.
 * This class is specialized for numeric types that implement Comparable.
 *
 * @param N The numeric type that this pointer points to
 */
@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class NumPtr<N : Comparable<N>>
@KWireCompilerApi
@PublishedApi
internal constructor(
    /**
     * The raw numeric value of this address.
     */
    @param:KWireCompilerApi
    @property:KWireCompilerApi
    override val rawAddress: NUInt
) : Address {
    /**
     * Reinterprets this numeric pointer as a pointer to a specific type.
     *
     * @param R The type that this pointer will point to
     * @return A typed pointer to the same address
     */
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)

    /**
     * Reinterprets this numeric pointer as a pointer to a different numeric type.
     *
     * @param N The numeric type that this pointer will point to
     * @return A numeric pointer to the same address
     */
    inline fun <N : Comparable<N>> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)

    /**
     * Reinterprets this numeric pointer as a void pointer.
     *
     * @return A void pointer to the same address
     */
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)

    /**
     * Reinterprets this numeric pointer as a function pointer.
     *
     * @param F The function type that this pointer will point to
     * @return A function pointer to the same address
     */
    inline fun <F : Function<*>> reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)

    /**
     * Aligns this numeric pointer to the specified alignment.
     *
     * @param alignment The alignment boundary in bytes
     * @return A numeric pointer to the aligned address
     */
    inline fun align(alignment: NUInt): NumPtr<N> = NumPtr(Memory.align(rawAddress, alignment))

    /**
     * Converts this numeric pointer to a native unsigned integer.
     *
     * @return The address as a native unsigned integer
     */
    inline fun asNUInt(): NUInt = rawAddress

    /**
     * Converts this numeric pointer to a native signed integer.
     *
     * @return The address as a native signed integer
     */
    inline fun asNInt(): NInt = rawAddress.value

    /**
     * Converts this numeric pointer to an unsigned integer.
     *
     * @return The address as an unsigned integer
     */
    inline fun asUInt(): UInt = rawAddress.toUInt()

    /**
     * Converts this numeric pointer to a signed integer.
     *
     * @return The address as a signed integer
     */
    inline fun asInt(): Int = rawAddress.value.toInt()

    /**
     * Converts this numeric pointer to an unsigned long.
     *
     * @return The address as an unsigned long
     */
    inline fun asULong(): ULong = rawAddress.toULong()

    /**
     * Converts this numeric pointer to a signed long.
     *
     * @return The address as a signed long
     */
    inline fun asLong(): Long = rawAddress.value.toLong()

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * @param other The offset to add
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: NUInt): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Adds an integer offset to this pointer.
     *
     * @param other The offset to add
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Int): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Adds a long offset to this pointer.
     *
     * @param other The offset to add
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Long): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * @param other The offset to subtract
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: NUInt): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts an integer offset from this pointer.
     *
     * @param other The offset to subtract
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Int): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts a long offset from this pointer.
     *
     * @param other The offset to subtract
     * @return A numeric pointer to the resulting address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Long): NumPtr<N> = throw KWirePluginNotAppliedException()

    /**
     * Dereferences this pointer to get the value it points to.
     *
     * @return The value at the address pointed to by this pointer
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): N = throw KWirePluginNotAppliedException()

    /**
     * Gets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @return The value at the calculated address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: NUInt): N = throw KWirePluginNotAppliedException()

    /**
     * Gets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @return The value at the calculated address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Int): N = throw KWirePluginNotAppliedException()

    /**
     * Gets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @return The value at the calculated address
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Long): N = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the address pointed to by this pointer.
     *
     * @param value The value to set
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    fun set(value: N): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @param value The value to set
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: NUInt, value: N): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @param value The value to set
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Int, value: N): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the specified index relative to this pointer.
     *
     * @param index The index to access, in elements (not bytes)
     * @param value The value to set
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Long, value: N): Unit = throw KWirePluginNotAppliedException()

    /**
     * Returns a string representation of this pointer in hexadecimal format.
     *
     * @return A string in the format "0xXXXXXXXX" where X is a hexadecimal digit
     */
    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

/**
 * Converts a native unsigned integer to a numeric pointer.
 *
 * @param N The numeric type that the resulting pointer will point to
 * @return A numeric pointer with the address specified by this integer
 */
inline fun <N : Comparable<N>> NUInt.asNumPtr(): NumPtr<N> = NumPtr(this)

/**
 * Converts an unsigned long to a numeric pointer.
 *
 * @param N The numeric type that the resulting pointer will point to
 * @return A numeric pointer with the address specified by this unsigned long
 */
inline fun <N : Comparable<N>> ULong.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

/**
 * Converts a signed long to a numeric pointer.
 *
 * @param N The numeric type that the resulting pointer will point to
 * @return A numeric pointer with the address specified by this long
 */
inline fun <N : Comparable<N>> Long.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

/**
 * Converts an unsigned integer to a numeric pointer.
 *
 * @param N The numeric type that the resulting pointer will point to
 * @return A numeric pointer with the address specified by this unsigned integer
 */
inline fun <N : Comparable<N>> UInt.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

/**
 * Converts a signed integer to a numeric pointer.
 *
 * @param N The numeric type that the resulting pointer will point to
 * @return A numeric pointer with the address specified by this integer
 */
inline fun <N : Comparable<N>> Int.asNumPtr(): NumPtr<N> = NumPtr(toNUInt())

/**
 * Creates a pointer to this numeric value.
 * This is equivalent to the C/C++ address-of operator (&).
 *
 * @param N The numeric type of the value
 * @return A pointer to this value
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <N : Comparable<N>> N.ref(): NumPtr<N> = throw KWirePluginNotAppliedException()
