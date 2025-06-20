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

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr

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
@KWireCompilerApi
interface FFI {
    /**
     * Companion object that delegates to the platform-specific FFI implementation.
     *
     * This allows for static access to FFI functionality through the FFI class,
     * e.g., `FFI.call(...)` instead of requiring an instance.
     */
    @KWireCompilerApi
    companion object : FFI by getPlatformFFI()

    /**
     * Calls a native function at the specified address with void return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     */
    @KWireCompilerApi
    fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer)

    /**
     * Calls a native function at the specified address with void return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     */
    fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}) {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        call(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    @KWireCompilerApi
    fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Byte

    /**
     * Calls a native function at the specified address with byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Byte {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callByte(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The short value returned by the native function
     */
    @KWireCompilerApi
    fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Short

    /**
     * Calls a native function at the specified address with short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The short value returned by the native function
     */
    fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Short {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callShort(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The int value returned by the native function
     */
    @KWireCompilerApi
    fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Int

    /**
     * Calls a native function at the specified address with int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The int value returned by the native function
     */
    fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Int {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callInt(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The long value returned by the native function
     */
    @KWireCompilerApi
    fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Long

    /**
     * Calls a native function at the specified address with long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The long value returned by the native function
     */
    fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Long {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callLong(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with native integer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The native integer value returned by the native function
     */
    @KWireCompilerApi
    fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NInt

    /**
     * Calls a native function at the specified address with native integer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The native integer value returned by the native function
     */
    fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): NInt {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callNInt(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The float value returned by the native function
     */
    @KWireCompilerApi
    fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Float

    /**
     * Calls a native function at the specified address with float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The float value returned by the native function
     */
    fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Float {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callFloat(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with double return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The double value returned by the native function
     */
    @KWireCompilerApi
    fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Double

    /**
     * Calls a native function at the specified address with double return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The double value returned by the native function
     */
    fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Double {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callDouble(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with native float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The native float value returned by the native function
     */
    @KWireCompilerApi
    fun callNFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NFloat

    /**
     * Calls a native function at the specified address with native float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The native float value returned by the native function
     */
    fun callNFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): NFloat {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callNFloat(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with pointer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The pointer value returned by the native function
     */
    @KWireCompilerApi
    fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): VoidPtr

    /**
     * Calls a native function at the specified address with pointer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The pointer value returned by the native function
     */
    fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): VoidPtr {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callPointer(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with unsigned byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned byte value returned by the native function
     */
    @KWireCompilerApi
    fun callUByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UByte

    /**
     * Calls a native function at the specified address with unsigned byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned byte value returned by the native function
     */
    fun callUByte(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): UByte {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callUByte(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with unsigned short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned short value returned by the native function
     */
    @KWireCompilerApi
    fun callUShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UShort

    /**
     * Calls a native function at the specified address with unsigned short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned short value returned by the native function
     */
    fun callUShort(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): UShort {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callUShort(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with unsigned int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned int value returned by the native function
     */
    @KWireCompilerApi
    fun callUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UInt

    /**
     * Calls a native function at the specified address with unsigned int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned int value returned by the native function
     */
    fun callUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): UInt {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callUInt(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with unsigned long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned long value returned by the native function
     */
    @KWireCompilerApi
    fun callULong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): ULong

    /**
     * Calls a native function at the specified address with unsigned long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The unsigned long value returned by the native function
     */
    fun callULong(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): ULong {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callULong(address, descriptor, buffer)
    }

    /**
     * Calls a native function at the specified address with unsigned native int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned native int value returned by the native function
     */
    @KWireCompilerApi
    fun callNUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NUInt

    /**
     * Calls a native function at the specified address with native unsigned integer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The native unsigned integer value returned by the native function
     */
    fun callNUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec = {}): NUInt {
        val buffer = FFIArgBuffer.get()
        buffer.args()
        return callNUInt(address, descriptor, buffer)
    }
}
