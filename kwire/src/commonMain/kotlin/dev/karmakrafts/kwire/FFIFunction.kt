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

package dev.karmakrafts.kwire

/**
 * Represents a native function that can be called through the Foreign Function Interface (FFI).
 *
 * This class wraps a native function at a specific memory address with a given name and signature.
 * It provides methods for calling the function with different return types, delegating to the
 * corresponding methods in the [FFI] companion object.
 *
 * FFIFunction objects are typically created by the [SharedLibrary] class when finding or getting
 * functions from a native library.
 *
 * @property name The name of the function
 * @property address The memory address of the function
 * @property descriptor The descriptor specifying the function signature
 */
data class FFIFunction( // @formatter:off
    val name: String, 
    val address: Pointer, 
    val descriptor: FFIDescriptor
) { // @formatter:on
    /**
     * Calls the native function with void return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     */
    inline fun call(noinline args: FFIArgSpec = {}) = FFI.call(address, descriptor, args)

    /**
     * Calls the native function with byte return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    inline fun callByte(noinline args: FFIArgSpec = {}): Byte = FFI.callByte(address, descriptor, args)

    /**
     * Calls the native function with short return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The short value returned by the native function
     */
    inline fun callShort(noinline args: FFIArgSpec = {}): Short = FFI.callShort(address, descriptor, args)

    /**
     * Calls the native function with int return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The int value returned by the native function
     */
    inline fun callInt(noinline args: FFIArgSpec = {}): Int = FFI.callInt(address, descriptor, args)

    /**
     * Calls the native function with long return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The long value returned by the native function
     */
    inline fun callLong(noinline args: FFIArgSpec = {}): Long = FFI.callLong(address, descriptor, args)

    /**
     * Calls the native function with native integer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The native integer value returned by the native function
     */
    inline fun callNInt(noinline args: FFIArgSpec = {}): NInt = FFI.callNInt(address, descriptor, args)

    /**
     * Calls the native function with unsigned byte return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned byte value returned by the native function
     */
    inline fun callUByte(noinline args: FFIArgSpec = {}): UByte = FFI.callUByte(address, descriptor, args)

    /**
     * Calls the native function with unsigned short return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned short value returned by the native function
     */
    inline fun callUShort(noinline args: FFIArgSpec = {}): UShort = FFI.callUShort(address, descriptor, args)

    /**
     * Calls the native function with unsigned int return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned int value returned by the native function
     */
    inline fun callUInt(noinline args: FFIArgSpec = {}): UInt = FFI.callUInt(address, descriptor, args)

    /**
     * Calls the native function with unsigned long return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned long value returned by the native function
     */
    inline fun callULong(noinline args: FFIArgSpec = {}): ULong = FFI.callULong(address, descriptor, args)

    /**
     * Calls the native function with native unsigned integer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The native unsigned integer value returned by the native function
     */
    inline fun callNUInt(noinline args: FFIArgSpec = {}): NUInt = FFI.callNUInt(address, descriptor, args)

    /**
     * Calls the native function with float return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The float value returned by the native function
     */
    inline fun callFloat(noinline args: FFIArgSpec = {}): Float = FFI.callFloat(address, descriptor, args)

    /**
     * Calls the native function with double return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The double value returned by the native function
     */
    inline fun callDouble(noinline args: FFIArgSpec = {}): Double = FFI.callDouble(address, descriptor, args)

    /**
     * Calls the native function with pointer return type.
     *
     * @param args A lambda with receiver for specifying function arguments
     * @return The pointer value returned by the native function
     */
    inline fun callPointer(noinline args: FFIArgSpec = {}): Pointer = FFI.callPointer(address, descriptor, args)
}
