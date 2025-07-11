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

/**
 * Represents a memory address in the C type system.
 * This is a base interface for all pointer types in the KWire FFI system.
 */
@KWireCompilerApi
sealed interface Address : Pointed {
    companion object {
        /**
         * The size of a pointer in bytes on the current platform.
         */
        val SIZE_BYTES: Int get() = pointerSize
    }

    /**
     * The raw numeric value of this address.
     */
    @KWireCompilerApi
    val rawAddress: NUInt

    /**
     * Checks if this address is a null pointer.
     *
     * @return true if this address is null (0), false otherwise
     */
    @PermitsConst
    fun isNull(): Boolean = rawAddress == 0U.toNUInt()

    /**
     * Checks if this address is not a null pointer.
     *
     * @return true if this address is not null (not 0), false otherwise
     */
    @PermitsConst
    fun isNotNull(): Boolean = rawAddress != 0U.toNUInt()
}

/**
 * Reinterprets this address as a pointer to a specific type.
 *
 * @param R The type that this pointer points to
 * @return A typed pointer to the address
 */
@PermitsConst
inline fun <R : Pointed> Address.reinterpret(): @ReceiverConstness Ptr<R> = Ptr(rawAddress)

/**
 * Reinterprets this address as a pointer to a numeric type.
 *
 * @param N The numeric type that this pointer points to
 * @return A numeric pointer to the address
 */
@PermitsConst
inline fun <N : Comparable<N>> Address.reinterpretNum(): @ReceiverConstness NumPtr<N> = NumPtr(rawAddress)

/**
 * Reinterprets this address as a function pointer.
 *
 * @param F The function type that this pointer points to
 * @return A function pointer to the address
 */
@PermitsConst
inline fun <F : Function<*>> Address.reinterpretFun(): @ReceiverConstness FunPtr<F> = FunPtr(rawAddress)

/**
 * Reinterprets this address as a void pointer.
 *
 * @return A void pointer to the address
 */
@PermitsConst
inline fun Address.reinterpretVoid(): @ReceiverConstness VoidPtr = VoidPtr(rawAddress)

/**
 * Aligns this address to the specified alignment.
 *
 * @param alignment The alignment boundary in bytes
 * @return A void pointer to the aligned address
 */
@PermitsConst
inline fun Address.align(alignment: NUInt): @ReceiverConstness VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

/**
 * Converts this address to a native unsigned integer.
 *
 * @return The address as a native unsigned integer
 */
@PermitsConst
inline fun Address.asNUInt(): NUInt = rawAddress

/**
 * Converts this address to a native signed integer.
 *
 * @return The address as a native signed integer
 */
@PermitsConst
inline fun Address.asNInt(): NInt = rawAddress.value

/**
 * Converts this address to an unsigned integer.
 *
 * @return The address as an unsigned integer
 */
@PermitsConst
inline fun Address.asUInt(): UInt = rawAddress.toUInt()

/**
 * Converts this address to a signed integer.
 *
 * @return The address as a signed integer
 */
@PermitsConst
inline fun Address.asInt(): Int = rawAddress.value.toInt()

/**
 * Converts this address to an unsigned long.
 *
 * @return The address as an unsigned long
 */
@PermitsConst
inline fun Address.asULong(): ULong = rawAddress.toULong()

/**
 * Converts this address to a signed long.
 *
 * @return The address as a signed long
 */
@PermitsConst
inline fun Address.asLong(): Long = rawAddress.value.toLong()

/**
 * Adds a native unsigned integer offset to this address.
 *
 * @param other The offset to add
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.plus(other: NUInt): @ReceiverConstness VoidPtr = VoidPtr(rawAddress + other)

/**
 * Adds an integer offset to this address.
 *
 * @param other The offset to add
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.plus(other: Int): @ReceiverConstness VoidPtr = VoidPtr(rawAddress + other.toNUInt())

/**
 * Adds a long offset to this address.
 *
 * @param other The offset to add
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.plus(other: Long): @ReceiverConstness VoidPtr = VoidPtr(rawAddress + other.toNUInt())

/**
 * Subtracts a native unsigned integer offset from this address.
 *
 * @param other The offset to subtract
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.minus(other: NUInt): @ReceiverConstness VoidPtr = VoidPtr(rawAddress - other)

/**
 * Subtracts an integer offset from this address.
 *
 * @param other The offset to subtract
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.minus(other: Int): @ReceiverConstness VoidPtr = VoidPtr(rawAddress - other.toNUInt())

/**
 * Subtracts a long offset from this address.
 *
 * @param other The offset to subtract
 * @return A void pointer to the resulting address
 */
@PermitsConst
inline operator fun Address.minus(other: Long): @ReceiverConstness VoidPtr = VoidPtr(rawAddress - other.toNUInt())
