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
 * Represents a pointer to a function in native code.
 *
 * This class provides a type-safe way to work with function pointers in native code.
 * It allows for reinterpretation to other pointer types, alignment, and arithmetic operations.
 *
 * @param F The function type this pointer points to
 * @property rawAddress The raw memory address of the function pointer
 */
@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class FunPtr<F : Function<*>>
@KWireCompilerApi
@PublishedApi
internal constructor(
    @param:KWireCompilerApi
    @property:KWireCompilerApi
    override val rawAddress: NUInt
) : Address {
    /**
     * Reinterprets this function pointer as a pointer to a pointed type.
     *
     * @param R The pointed type to reinterpret to
     * @return A pointer to the pointed type
     */
    inline fun <R : Pointed> reinterpret(): Ptr<R> = Ptr(rawAddress)

    /**
     * Reinterprets this function pointer as a numeric pointer.
     *
     * @param N The numeric type to reinterpret to
     * @return A numeric pointer
     */
    inline fun <N : Comparable<N>> reinterpretNum(): NumPtr<N> = NumPtr(rawAddress)

    /**
     * Reinterprets this function pointer as a void pointer.
     *
     * @return A void pointer
     */
    inline fun reinterpretVoid(): VoidPtr = VoidPtr(rawAddress)

    /**
     * Reinterprets this function pointer as another function pointer type.
     *
     * @param F The function type to reinterpret to
     * @return A function pointer of the specified type
     */
    inline fun <F : Function<*>> reinterpretFun(): FunPtr<F> = FunPtr(rawAddress)

    /**
     * Aligns this function pointer to the specified alignment.
     *
     * @param alignment The alignment to align to
     * @return An aligned function pointer
     */
    inline fun align(alignment: NUInt): FunPtr<F> = FunPtr(Memory.align(rawAddress, alignment))

    /**
     * Converts this function pointer to a native unsigned integer.
     *
     * @return The raw address as a native unsigned integer
     */
    inline fun asNUInt(): NUInt = rawAddress

    /**
     * Converts this function pointer to a native signed integer.
     *
     * @return The raw address as a native signed integer
     */
    inline fun asNInt(): NInt = rawAddress.value

    /**
     * Converts this function pointer to an unsigned integer.
     *
     * @return The raw address as an unsigned integer
     */
    inline fun asUInt(): UInt = rawAddress.toUInt()

    /**
     * Converts this function pointer to a signed integer.
     *
     * @return The raw address as a signed integer
     */
    inline fun asInt(): Int = rawAddress.value.toInt()

    /**
     * Converts this function pointer to an unsigned long.
     *
     * @return The raw address as an unsigned long
     */
    inline fun asULong(): ULong = rawAddress.toULong()

    /**
     * Converts this function pointer to a signed long.
     *
     * @return The raw address as a signed long
     */
    inline fun asLong(): Long = rawAddress.value.toLong()

    /**
     * Adds the specified offset to this function pointer.
     *
     * @param other The offset to add as a native unsigned integer
     * @return A new function pointer with the offset added
     */
    inline operator fun plus(other: NUInt): FunPtr<F> = FunPtr(rawAddress + other)

    /**
     * Adds the specified offset to this function pointer.
     *
     * @param other The offset to add as an integer
     * @return A new function pointer with the offset added
     */
    inline operator fun plus(other: Int): FunPtr<F> = FunPtr(rawAddress + other.toNUInt())

    /**
     * Adds the specified offset to this function pointer.
     *
     * @param other The offset to add as a long
     * @return A new function pointer with the offset added
     */
    inline operator fun plus(other: Long): FunPtr<F> = FunPtr(rawAddress + other.toNUInt())

    /**
     * Subtracts the specified offset from this function pointer.
     *
     * @param other The offset to subtract as a native unsigned integer
     * @return A new function pointer with the offset subtracted
     */
    inline operator fun minus(other: NUInt): FunPtr<F> = FunPtr(rawAddress - other)

    /**
     * Subtracts the specified offset from this function pointer.
     *
     * @param other The offset to subtract as an integer
     * @return A new function pointer with the offset subtracted
     */
    inline operator fun minus(other: Int): FunPtr<F> = FunPtr(rawAddress - other.toNUInt())

    /**
     * Subtracts the specified offset from this function pointer.
     *
     * @param other The offset to subtract as a long
     * @return A new function pointer with the offset subtracted
     */
    inline operator fun minus(other: Long): FunPtr<F> = FunPtr(rawAddress - other.toNUInt())

    /**
     * Returns a string representation of this function pointer.
     *
     * @return A hexadecimal string representation of the raw address
     */
    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

/**
 * Invokes the function pointed to by this function pointer.
 *
 * This operator allows calling a native function through its function pointer.
 * The KWire compiler plugin will replace this with the actual function call.
 *
 * @param R The return type of the function
 * @param F The function type
 * @param args The arguments to pass to the function
 * @return The result of the function call
 * @throws KWirePluginNotAppliedException if the KWire compiler plugin is not applied
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_INVOKE)
operator fun <R, F : Function<R>> FunPtr<F>.invoke(vararg args: Any?): R = throw KWirePluginNotAppliedException()

/**
 * Creates a function pointer to this function using the C calling convention.
 *
 * This function is used to obtain a function pointer that can be passed to native code.
 * The KWire compiler plugin will replace this with the actual function pointer creation.
 *
 * @param F The function type
 * @return A function pointer to this function using the C calling convention
 * @throws KWirePluginNotAppliedException if the KWire compiler plugin is not applied
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.ref(): @CDecl FunPtr<F> = throw KWirePluginNotAppliedException()

/**
 * Creates a function pointer to this function using the "this call" calling convention.
 *
 * This function is used to obtain a function pointer that can be passed to native code.
 * The KWire compiler plugin will replace this with the actual function pointer creation.
 *
 * @param F The function type
 * @return A function pointer to this function using the "this call" calling convention
 * @throws KWirePluginNotAppliedException if the KWire compiler plugin is not applied
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refThisCall(): @ThisCall FunPtr<F> = throw KWirePluginNotAppliedException()

/**
 * Creates a function pointer to this function using the "stdcall" calling convention.
 *
 * This function is used to obtain a function pointer that can be passed to native code.
 * The KWire compiler plugin will replace this with the actual function pointer creation.
 *
 * @param F The function type
 * @return A function pointer to this function using the "stdcall" calling convention
 * @throws KWirePluginNotAppliedException if the KWire compiler plugin is not applied
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refStdCall(): @StdCall FunPtr<F> = throw KWirePluginNotAppliedException()

/**
 * Creates a function pointer to this function using the "fastcall" calling convention.
 *
 * This function is used to obtain a function pointer that can be passed to native code.
 * The KWire compiler plugin will replace this with the actual function pointer creation.
 *
 * @param F The function type
 * @return A function pointer to this function using the "fastcall" calling convention
 * @throws KWirePluginNotAppliedException if the KWire compiler plugin is not applied
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refFastCall(): @FastCall FunPtr<F> = throw KWirePluginNotAppliedException()

/**
 * Converts a native unsigned integer to a function pointer.
 *
 * @param F The function type this pointer points to
 * @return A function pointer with the address of this native unsigned integer
 */
inline fun <F : Function<*>> NUInt.asFunPtr(): FunPtr<F> = FunPtr(this)

/**
 * Converts an unsigned long to a function pointer.
 *
 * @param F The function type this pointer points to
 * @return A function pointer with the address of this unsigned long
 */
inline fun <F : Function<*>> ULong.asFunPtr(): FunPtr<F> = FunPtr(toNUInt())

/**
 * Converts a signed long to a function pointer.
 *
 * @param F The function type this pointer points to
 * @return A function pointer with the address of this signed long
 */
inline fun <F : Function<*>> Long.asFunPtr(): FunPtr<F> = FunPtr(toNUInt())

/**
 * Converts an unsigned integer to a function pointer.
 *
 * @param F The function type this pointer points to
 * @return A function pointer with the address of this unsigned integer
 */
inline fun <F : Function<*>> UInt.asFunPtr(): FunPtr<F> = FunPtr(toNUInt())

/**
 * Converts a signed integer to a function pointer.
 *
 * @param F The function type this pointer points to
 * @return A function pointer with the address of this signed integer
 */
inline fun <F : Function<*>> Int.asFunPtr(): FunPtr<F> = FunPtr(toNUInt())
