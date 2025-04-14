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
 * Internal function to get the platform-specific implementation of the FFI interface.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate FFI implementation for that platform.
 *
 * @return The platform-specific FFI implementation
 */
internal expect fun getPlatformFFI(): FFI

/**
 * Interface for Foreign Function Interface (FFI) operations.
 *
 * This interface provides methods for calling native functions at specific memory addresses
 * with various return types. It serves as the primary mechanism for interacting with
 * native code from Kotlin.
 */
interface FFI {
    /**
     * Companion object that delegates to the platform-specific FFI implementation.
     *
     * This allows for static access to FFI functionality through the FFI class,
     * e.g., `FFI.call(...)` instead of requiring an instance.
     */
    companion object : FFI by getPlatformFFI()

    /**
     * Calls a native function at the specified address with void return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     */
    @IntrinsicCallCandidate
    fun call(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {})

    /**
     * Calls a native function at the specified address with byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callByte(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Byte

    /**
     * Calls a native function at the specified address with short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The short value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callShort(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Short

    /**
     * Calls a native function at the specified address with int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The int value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Int

    /**
     * Calls a native function at the specified address with long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The long value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callLong(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Long

    /**
     * Calls a native function at the specified address with native integer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The native integer value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callNInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): NInt

    /**
     * Calls a native function at the specified address with float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The float value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callFloat(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Float

    /**
     * Calls a native function at the specified address with double return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The double value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callDouble(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Double

    /**
     * Calls a native function at the specified address with pointer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The pointer value returned by the native function
     */
    @IntrinsicCallCandidate
    fun callPointer(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Pointer
}

/**
 * Calls a native function at the specified address with unsigned byte return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned byte value returned by the native function
 */
inline fun FFI.callUByte(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UByte =
    callByte(address, descriptor, args).toUByte()

/**
 * Calls a native function at the specified address with unsigned short return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned short value returned by the native function
 */
inline fun FFI.callUShort(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UShort =
    callShort(address, descriptor, args).toUShort()

/**
 * Calls a native function at the specified address with unsigned int return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned int value returned by the native function
 */
inline fun FFI.callUInt(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UInt =
    callInt(address, descriptor, args).toUInt()

/**
 * Calls a native function at the specified address with unsigned long return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned long value returned by the native function
 */
inline fun FFI.callULong(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): ULong =
    callLong(address, descriptor, args).toULong()

/**
 * Calls a native function at the specified address with native unsigned integer return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The native unsigned integer value returned by the native function
 */
inline fun FFI.callNUInt(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): NUInt =
    callNInt(address, descriptor, args).toUnsigned()
