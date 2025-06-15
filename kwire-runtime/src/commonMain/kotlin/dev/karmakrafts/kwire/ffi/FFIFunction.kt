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

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.IntrinsicCallCandidate
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt

/**
 * Represents a native function that can be called through the Foreign Function Interface (FFI).
 *
 * This class wraps a native function at a specific memory address with a given name and signature.
 * It provides methods for calling the function with different return types, delegating to the
 * corresponding methods in the [FFI] companion object.
 *
 * FFIFunction objects are typically created by the [dev.karmakrafts.kwire.SharedLibrary] class when finding or getting
 * functions from a native library.
 *
 * @property name The name of the function
 * @property address The memory address of the function
 * @property descriptor The descriptor specifying the function signature
 */
data class FFIFunction( // @formatter:off
    val name: String, 
    val address: Address,
    val descriptor: FFIDescriptor
) { // @formatter:on
    /**
     * Calls the native function with void return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     */
    @IntrinsicCallCandidate
    inline fun call(noinline args: FFIArgSpec = {}) = FFI.call(address, descriptor, args)

    /**
     * Calls the native function with byte return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callByte(noinline args: FFIArgSpec = {}): Byte = FFI.callByte(address, descriptor, args)

    /**
     * Calls the native function with short return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The short value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callShort(noinline args: FFIArgSpec = {}): Short = FFI.callShort(address, descriptor, args)

    /**
     * Calls the native function with int return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The int value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callInt(noinline args: FFIArgSpec = {}): Int = FFI.callInt(address, descriptor, args)

    /**
     * Calls the native function with long return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The long value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callLong(noinline args: FFIArgSpec = {}): Long = FFI.callLong(address, descriptor, args)

    /**
     * Calls the native function with native integer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The native integer value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callNInt(noinline args: FFIArgSpec = {}): NInt = FFI.callNInt(address, descriptor, args)

    /**
     * Calls the native function with unsigned byte return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned byte value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callUByte(noinline args: FFIArgSpec = {}): UByte = FFI.callUByte(address, descriptor, args)

    /**
     * Calls the native function with unsigned short return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned short value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callUShort(noinline args: FFIArgSpec = {}): UShort = FFI.callUShort(address, descriptor, args)

    /**
     * Calls the native function with unsigned int return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned int value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callUInt(noinline args: FFIArgSpec = {}): UInt = FFI.callUInt(address, descriptor, args)

    /**
     * Calls the native function with unsigned long return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned long value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callULong(noinline args: FFIArgSpec = {}): ULong = FFI.callULong(address, descriptor, args)

    /**
     * Calls the native function with native unsigned integer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The native unsigned integer value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callNUInt(noinline args: FFIArgSpec = {}): NUInt = FFI.callNUInt(address, descriptor, args)

    /**
     * Calls the native function with float return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The float value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callFloat(noinline args: FFIArgSpec = {}): Float = FFI.callFloat(address, descriptor, args)

    /**
     * Calls the native function with double return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The double value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callDouble(noinline args: FFIArgSpec = {}): Double = FFI.callDouble(address, descriptor, args)

    /**
     * Calls the native function with pointer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The pointer value returned by the native function
     */
    @IntrinsicCallCandidate
    inline fun callPointer(noinline args: FFIArgSpec = {}): Address = FFI.callPointer(address, descriptor, args)

    /**
     * Calls the native function with a return type specified by the type parameter.
     *
     * This method automatically selects the appropriate specialized call method based on the
     * reified type parameter. It supports all primitive types, their unsigned variants,
     * native-sized integers, and pointers.
     *
     * @param R The return type of the function call
     * @param args A lambda with receiver for specifying function arguments
     * @return The value returned by the native function, cast to type R
     * @throws IllegalStateException if the specified return type is not supported
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    @IntrinsicCallCandidate
    inline fun <reified R : Any> call(noinline args: FFIArgSpec = {}): R = when (R::class) {
        Byte::class -> callByte(args)
        Short::class -> callShort(args)
        Int::class -> callInt(args)
        Long::class -> callLong(args)
        NInt::class -> callNInt(args)
        UByte::class -> callUByte(args)
        UShort::class -> callUShort(args)
        UInt::class -> callUInt(args)
        ULong::class -> callULong(args)
        NUInt::class -> callNUInt(args)
        Float::class -> callFloat(args)
        Double::class -> callDouble(args)
        Address::class -> callPointer(args)
        else -> error("Unsupported FFI function return type ${R::class}")
    } as R

    /**
     * Calls the native function with a return type specified by the type parameter, optimized for common primitive types.
     *
     * This method is a faster alternative to [call] that only supports the most common primitive types:
     * Byte, Short, Int, Long, Float, and Double. It doesn't support unsigned types, native-sized integers,
     * or pointers, making it more efficient for the supported types.
     *
     * @param R The return type of the function call (must be one of the supported primitive types)
     * @param args A lambda with receiver for specifying function arguments
     * @return The value returned by the native function, cast to type R
     * @throws IllegalStateException if the specified return type is not supported
     */
    @IntrinsicCallCandidate
    inline fun <reified R : Any> callFast(noinline args: FFIArgSpec = {}): R = when (R::class) {
        Byte::class -> callByte(args)
        Short::class -> callShort(args)
        Int::class -> callInt(args)
        Long::class -> callLong(args)
        Float::class -> callFloat(args)
        Double::class -> callDouble(args)
        else -> error("Unsupported FFI function return type ${R::class}")
    } as R
}
