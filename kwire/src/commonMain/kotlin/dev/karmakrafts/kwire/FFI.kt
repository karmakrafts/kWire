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
@file:JvmName("FFI$")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

internal expect fun getPlatformFFI(): FFI

// TODO: document this
interface FFI {
    companion object : FFI by getPlatformFFI()

    fun call(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {})

    fun callByte(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Byte
    fun callShort(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Short
    fun callInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Int
    fun callLong(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Long
    fun callNInt(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): NInt
    fun callFloat(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Float
    fun callDouble(address: Pointer, descriptor: FFIDescriptor, args: FFIArgSpec = {}): Double
}

inline fun FFI.callUByte(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UByte =
    callByte(address, descriptor, args).toUByte()

inline fun FFI.callUShort(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UShort =
    callShort(address, descriptor, args).toUShort()

inline fun FFI.callUInt(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): UInt =
    callInt(address, descriptor, args).toUInt()

inline fun FFI.callULong(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): ULong =
    callLong(address, descriptor, args).toULong()

inline fun FFI.callNUInt(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): NUInt =
    callNInt(address, descriptor, args).toUnsigned()

inline fun FFI.callPointer(address: Pointer, descriptor: FFIDescriptor, noinline args: FFIArgSpec = {}): Pointer =
    Pointer(callNUInt(address, descriptor, args))