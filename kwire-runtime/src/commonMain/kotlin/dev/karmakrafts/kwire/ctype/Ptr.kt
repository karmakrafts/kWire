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
import dev.karmakrafts.kwire.meta.ValueType
import kotlin.jvm.JvmInline

internal expect val pointerSize: Int

/**
 * Represents a typed pointer to a memory location in the C type system.
 *
 * This class provides a type-safe way to work with pointers in the KWire FFI system.
 *
 * @param T The type that this pointer points to, which must be either a builtin type,
 *  a structural type or another pointer.
 */
@KWireCompilerApi
@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class Ptr<@ValueType T>
@KWireCompilerApi @PublishedApi internal constructor(
    @param:KWireCompilerApi
    @property:KWireCompilerApi
    val rawAddress: NUInt
) {
    companion object {
        /**
         * The size of a pointer in bytes on the current platform.
         */
        val SIZE_BYTES: Int get() = pointerSize
    }

    @ConstCallable
    @DiscardsConstness
    inline fun discardConst(): Ptr<T> = this

    /**
     * Reinterprets this pointer as a pointer to a different type.
     *
     * @param R The new type that this pointer will point to
     * @return A pointer to the same address but with a different pointed type
     */
    @ConstCallable
    inline fun <@ValueType R> reinterpret(): @InheritsConstness Ptr<R> = Ptr(rawAddress)

    /**
     * Aligns this pointer to the specified alignment.
     *
     * @param alignment The alignment boundary in bytes
     * @return A pointer to the aligned address with the same pointed type
     */
    @ConstCallable
    inline fun align(alignment: NUInt): @InheritsConstness Ptr<T> = Ptr(Memory.align(rawAddress, alignment))

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
     * @param other The offset to add in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: NUInt): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Adds an integer offset to this pointer.
     *
     * @param other The offset to add in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Int): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Adds a long offset to this pointer.
     *
     * @param other The offset to add in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_PLUS)
    operator fun plus(other: Long): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts a native unsigned integer offset from this pointer.
     *
     * @param other The offset to subtract in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: NUInt): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts an integer offset from this pointer.
     *
     * @param other The offset to subtract in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Int): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Subtracts a long offset from this pointer.
     *
     * @param other The offset to subtract in units of the pointed type size
     * @return A pointer to the resulting address with the same pointed type
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_MINUS)
    operator fun minus(other: Long): @InheritsConstness Ptr<T> = throw KWirePluginNotAppliedException()

    /**
     * Dereferences this pointer to access the value it points to.
     *
     * @return The value at the memory location pointed to by this pointer
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    fun deref(): T = throw KWirePluginNotAppliedException()

    /**
     * Dereferences this pointer with an offset to access the value at the specified index.
     *
     * @param index The index to access, in units of the pointed type size
     * @return The value at the memory location (pointer + index)
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: NUInt): T = throw KWirePluginNotAppliedException()

    /**
     * Dereferences this pointer with an offset to access the value at the specified index.
     *
     * @param index The index to access, in units of the pointed type size
     * @return The value at the memory location (pointer + index)
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Int): T = throw KWirePluginNotAppliedException()

    /**
     * Dereferences this pointer with an offset to access the value at the specified index.
     *
     * @param index The index to access, in units of the pointed type size
     * @return The value at the memory location (pointer + index)
     */
    @ConstCallable
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_DEREF)
    operator fun get(index: Long): T = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the memory location pointed to by this pointer.
     *
     * @param value The value to set at the memory location
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    fun set(value: T): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the memory location (pointer + index).
     *
     * @param index The index to access, in units of the pointed type size
     * @param value The value to set at the memory location
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: NUInt, value: T): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the memory location (pointer + index).
     *
     * @param index The index to access, in units of the pointed type size
     * @param value The value to set at the memory location
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Int, value: T): Unit = throw KWirePluginNotAppliedException()

    /**
     * Sets the value at the memory location (pointer + index).
     *
     * @param index The index to access, in units of the pointed type size
     * @param value The value to set at the memory location
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_SET)
    operator fun set(index: Long, value: T): Unit = throw KWirePluginNotAppliedException()

    @ConstCallable
    inline fun isNull(): Boolean = rawAddress == 0.toNUInt()

    @ConstCallable
    inline fun isNotNull(): Boolean = rawAddress != 0.toNUInt()

    /**
     * Returns a string representation of this pointer in hexadecimal format.
     *
     * @return A string representation of the pointer address in hexadecimal format
     */
    @ConstCallable
    override fun toString(): String = "0x${rawAddress.toHexString()}"
}

inline fun <@ValueType T> nullptr(): Ptr<T> = Ptr(0.toNUInt())

/**
 * Converts a native unsigned integer to a typed pointer.
 *
 * @param T The type that the resulting pointer will point to
 * @return A pointer with the address specified by this native unsigned integer
 */
inline fun <@ValueType T> NUInt.asPtr(): Ptr<T> = Ptr(this)

/**
 * Converts an unsigned long to a typed pointer.
 *
 * @param T The type that the resulting pointer will point to
 * @return A pointer with the address specified by this unsigned long
 */
inline fun <@ValueType T> ULong.asPtr(): Ptr<T> = Ptr(toNUInt())

/**
 * Converts a signed long to a typed pointer.
 *
 * @param T The type that the resulting pointer will point to
 * @return A pointer with the address specified by this signed long
 */
inline fun <@ValueType T> Long.asPtr(): Ptr<T> = Ptr(toNUInt())

/**
 * Converts an unsigned integer to a typed pointer.
 *
 * @param T The type that the resulting pointer will point to
 * @return A pointer with the address specified by this unsigned integer
 */
inline fun <@ValueType T> UInt.asPtr(): Ptr<T> = Ptr(toNUInt())

/**
 * Converts a signed integer to a typed pointer.
 *
 * @param T The type that the resulting pointer will point to
 * @return A pointer with the address specified by this signed integer
 */
inline fun <@ValueType T> Int.asPtr(): Ptr<T> = Ptr(toNUInt())

/**
 * Creates a pointer to this object.
 *
 * This is a compiler intrinsic that creates a pointer to the memory location
 * where this object is stored.
 *
 * @param T The type of the object being referenced
 * @return A pointer to this object
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <@ValueType T> T.ref(): Ptr<T> = throw KWirePluginNotAppliedException()

/**
 * Creates a const pointer to this object.
 *
 * This is a compiler intrinsic that creates a pointer to the memory location
 * where this object is stored.
 *
 * @param T The type of the object being referenced
 * @return A pointer to this object
 */
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <@ValueType T> T.constRef(): @Const Ptr<T> = throw KWirePluginNotAppliedException()