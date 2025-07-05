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

    @KWireCompilerApi
    fun createUpcallStub(
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        function: (FFIArgBuffer) -> Unit
    ): VoidPtr

    /**
     * Calls a native function at the specified address with void return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     */
    @KWireCompilerApi
    fun call(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    )

    /**
     * Calls a native function at the specified address with byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args A lambda with receiver for specifying function arguments
     * @return The byte value returned by the native function
     */
    @KWireCompilerApi
    fun callByte(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Byte

    /**
     * Calls a native function at the specified address with short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The short value returned by the native function
     */
    @KWireCompilerApi
    fun callShort(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Short

    /**
     * Calls a native function at the specified address with int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The int value returned by the native function
     */
    @KWireCompilerApi
    fun callInt(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Int

    /**
     * Calls a native function at the specified address with long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The long value returned by the native function
     */
    @KWireCompilerApi
    fun callLong(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Long

    /**
     * Calls a native function at the specified address with native integer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The native integer value returned by the native function
     */
    @KWireCompilerApi
    fun callNInt(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): NInt

    /**
     * Calls a native function at the specified address with float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The float value returned by the native function
     */
    @KWireCompilerApi
    fun callFloat(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Float

    /**
     * Calls a native function at the specified address with double return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The double value returned by the native function
     */
    @KWireCompilerApi
    fun callDouble(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): Double

    /**
     * Calls a native function at the specified address with native float return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The native float value returned by the native function
     */
    @KWireCompilerApi
    fun callNFloat(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): NFloat

    /**
     * Calls a native function at the specified address with pointer return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The pointer value returned by the native function
     */
    @KWireCompilerApi
    fun callPointer(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): VoidPtr

    /**
     * Calls a native function at the specified address with unsigned byte return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned byte value returned by the native function
     */
    @KWireCompilerApi
    fun callUByte(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): UByte

    /**
     * Calls a native function at the specified address with unsigned short return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned short value returned by the native function
     */
    @KWireCompilerApi
    fun callUShort(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): UShort

    /**
     * Calls a native function at the specified address with unsigned int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned int value returned by the native function
     */
    @KWireCompilerApi
    fun callUInt(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): UInt

    /**
     * Calls a native function at the specified address with unsigned long return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned long value returned by the native function
     */
    @KWireCompilerApi
    fun callULong(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): ULong

    /**
     * Calls a native function at the specified address with unsigned native int return type.
     *
     * @param address The memory address of the function to call
     * @param descriptor The descriptor specifying the function signature
     * @param args The buffer containing function arguments
     * @return The unsigned native int value returned by the native function
     */
    @KWireCompilerApi
    fun callNUInt(
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention = CallingConvention.CDECL,
        args: FFIArgBuffer
    ): NUInt
}

/**
 * Calls a native function at the specified address with void return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 */
inline fun FFI.call(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
) {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    call(address, descriptor, callingConvention, buffer)
    buffer.release()
}

/**
 * Calls a native function at the specified address with byte return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The byte value returned by the native function
 */
inline fun FFI.callByte(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Byte {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callByte(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with short return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The short value returned by the native function
 */
inline fun FFI.callShort(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Short {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callShort(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with int return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The int value returned by the native function
 */
inline fun FFI.callInt(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Int {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callInt(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with long return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The long value returned by the native function
 */
inline fun FFI.callLong(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Long {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callLong(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with native integer return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The native integer value returned by the native function
 */
inline fun FFI.callNInt(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): NInt {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callNInt(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with float return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The float value returned by the native function
 */
inline fun FFI.callFloat(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Float {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callFloat(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with double return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The double value returned by the native function
 */
inline fun FFI.callDouble(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): Double {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callDouble(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with native float return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The native float value returned by the native function
 */
inline fun FFI.callNFloat(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): NFloat {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callNFloat(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with pointer return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The pointer value returned by the native function
 */
inline fun FFI.callPointer(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): VoidPtr {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callPointer(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with unsigned byte return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned byte value returned by the native function
 */
inline fun FFI.callUByte(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): UByte {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callUByte(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with unsigned short return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned short value returned by the native function
 */
inline fun FFI.callUShort(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): UShort {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callUShort(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with unsigned int return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned int value returned by the native function
 */
inline fun FFI.callUInt(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): UInt {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callUInt(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with unsigned long return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The unsigned long value returned by the native function
 */
inline fun FFI.callULong(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): ULong {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callULong(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}

/**
 * Calls a native function at the specified address with native unsigned integer return type.
 *
 * @param address The memory address of the function to call
 * @param descriptor The descriptor specifying the function signature
 * @param args A lambda with receiver for specifying function arguments
 * @return The native unsigned integer value returned by the native function
 */
inline fun FFI.callNUInt(
    address: Address,
    descriptor: FFIDescriptor,
    callingConvention: CallingConvention = CallingConvention.CDECL,
    args: FFIArgBuffer.() -> Unit = {}
): NUInt {
    val buffer = FFIArgBuffer.acquire()
    buffer.args()
    val result = callNUInt(address, descriptor, callingConvention, buffer)
    buffer.release()
    return result
}