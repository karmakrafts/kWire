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

/**
 * Represents an untyped (void) pointer to a memory location in the C type system.
 *
 * This class provides a way to work with void pointers in the KWire FFI system.
 * It implements the [Address] interface and provides methods for pointer arithmetic,
 * type conversion, and reinterpretation to other pointer types.
 */
@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class VoidPtr
@KWireCompilerApi
@PublishedApi
internal constructor(
    @param:KWireCompilerApi
    @property:KWireCompilerApi
    override val rawAddress: NUInt
) : Address {
    companion object {
        /**
         * A null void pointer (address 0).
         */
        val nullptr: VoidPtr = VoidPtr(0U.toNUInt())
    }

    @ConstCallable
    @DiscardsConstness
    inline fun discardConst(): VoidPtr = this

    /**
     * Reinterprets this void pointer as a pointer to a specific type.
     *
     * @param R The type that this pointer will point to
     * @return A typed pointer to the same address
     */
    @ConstCallable
    inline fun <R : Pointed> reinterpret(): @InheritsConstness Ptr<R> = Ptr(rawAddress)

    /**
     * Reinterprets this void pointer as a pointer to a numeric type.
     *
     * @param N The numeric type that this pointer will point to
     * @return A numeric pointer to the same address
     */
    @ConstCallable
    inline fun <N : Comparable<N>> reinterpretNum(): @InheritsConstness NumPtr<N> = NumPtr(rawAddress)

    /**
     * Reinterprets this void pointer as a function pointer.
     *
     * @param F The function type that this pointer will point to
     * @return A function pointer to the same address
     */
    @ConstCallable
    inline fun <F : Function<*>> reinterpretFun(): @InheritsConstness FunPtr<F> = FunPtr(rawAddress)

    /**
     * Aligns this pointer to the specified alignment.
     *
     * @param alignment The alignment boundary in bytes
     * @return A void pointer to the aligned address
     */
    @ConstCallable
    inline fun align(alignment: NUInt): @InheritsConstness VoidPtr = VoidPtr(Memory.align(rawAddress, alignment))

    /**
     * Converts this pointer to a native unsigned integer.
     *
     * @return The pointer address as a native unsigned integer
     */
    @ConstCallable
    inline fun asNUInt(): NUInt = rawAddress

    /**
     * Converts this pointer to a native signed integer.
     *
     * @return The pointer address as a native signed integer
     */
    @ConstCallable
    inline fun asNInt(): NInt = rawAddress.value

    /**
     * Converts this pointer to an unsigned integer.
     *
     * @return The pointer address as an unsigned integer
     */
    @ConstCallable
    inline fun asUInt(): UInt = rawAddress.toUInt()

    /**
     * Converts this pointer to a signed integer.
     *
     * @return The pointer address as a signed integer
     */
    @ConstCallable
    inline fun asInt(): Int = rawAddress.value.toInt()

    /**
     * Converts this pointer to an unsigned long.
     *
     * @return The pointer address as an unsigned long
     */
    @ConstCallable
    inline fun asULong(): ULong = rawAddress.toULong()

    /**
     * Converts this pointer to a signed long.
     *
     * @return The pointer address as a signed long
     */
    @ConstCallable
    inline fun asLong(): Long = rawAddress.value.toLong()

    /**
     * Adds a native unsigned integer offset to this pointer.
     *
     * @param other The offset to add in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun plus(other: NUInt): @InheritsConstness VoidPtr = VoidPtr(rawAddress + other)

    /**
     * Adds an integer offset to this pointer.
     *
     * @param other The offset to add in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun plus(other: Int): @InheritsConstness VoidPtr = VoidPtr(rawAddress + other.toNUInt())

    /**
     * Adds a long offset to this pointer.
     *
     * @param other The offset to add in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun plus(other: Long): @InheritsConstness VoidPtr = VoidPtr(rawAddress + other.toNUInt())

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * @param other The offset to subtract in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun minus(other: NUInt): @InheritsConstness VoidPtr = VoidPtr(rawAddress - other)

    /**
     * Subtracts an integer offset from this pointer.
     *
     * @param other The offset to subtract in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun minus(other: Int): @InheritsConstness VoidPtr = VoidPtr(rawAddress - other.toNUInt())

    /**
     * Subtracts a long offset from this pointer.
     *
     * @param other The offset to subtract in bytes
     * @return A void pointer to the resulting address
     */
    @ConstCallable
    inline operator fun minus(other: Long): @InheritsConstness VoidPtr = VoidPtr(rawAddress - other.toNUInt())

    /**
     * Returns a string representation of this pointer.
     *
     * @return A hexadecimal string representation of the pointer address
     */
    @ConstCallable
    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

/**
 * Converts a native unsigned integer to a void pointer.
 *
 * @return A void pointer with the address represented by this native unsigned integer
 */
inline fun NUInt.asVoidPtr(): VoidPtr = VoidPtr(this)

/**
 * Converts an unsigned long to a void pointer.
 *
 * @return A void pointer with the address represented by this unsigned long
 */
inline fun ULong.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())

/**
 * Converts a signed long to a void pointer.
 *
 * @return A void pointer with the address represented by this signed long
 */
inline fun Long.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())

/**
 * Converts an unsigned integer to a void pointer.
 *
 * @return A void pointer with the address represented by this unsigned integer
 */
inline fun UInt.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())

/**
 * Converts a signed integer to a void pointer.
 *
 * @return A void pointer with the address represented by this signed integer
 */
inline fun Int.asVoidPtr(): VoidPtr = VoidPtr(toNUInt())
