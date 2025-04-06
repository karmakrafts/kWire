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

// TODO: document this
class FFIFunction(
    val name: String,
    val address: Pointer,
    val descriptor: FFIDescriptor
) {
    inline fun call(noinline args: FFIArgSpec = {}) = FFI.call(address, descriptor, args)
    inline fun callByte(noinline args: FFIArgSpec = {}): Byte = FFI.callByte(address, descriptor, args)
    inline fun callShort(noinline args: FFIArgSpec = {}): Short = FFI.callShort(address, descriptor, args)
    inline fun callInt(noinline args: FFIArgSpec = {}): Int = FFI.callInt(address, descriptor, args)
    inline fun callLong(noinline args: FFIArgSpec = {}): Long = FFI.callLong(address, descriptor, args)
    inline fun callNInt(noinline args: FFIArgSpec = {}): NInt = FFI.callNInt(address, descriptor, args)
    inline fun callUByte(noinline args: FFIArgSpec = {}): UByte = FFI.callUByte(address, descriptor, args)
    inline fun callUShort(noinline args: FFIArgSpec = {}): UShort = FFI.callUShort(address, descriptor, args)
    inline fun callUInt(noinline args: FFIArgSpec = {}): UInt = FFI.callUInt(address, descriptor, args)
    inline fun callULong(noinline args: FFIArgSpec = {}): ULong = FFI.callULong(address, descriptor, args)
    inline fun callNUInt(noinline args: FFIArgSpec = {}): NUInt = FFI.callNUInt(address, descriptor, args)
    inline fun callFloat(noinline args: FFIArgSpec = {}): Float = FFI.callFloat(address, descriptor, args)
    inline fun callDouble(noinline args: FFIArgSpec = {}): Double = FFI.callDouble(address, descriptor, args)
    inline fun callPointer(noinline args: FFIArgSpec = {}): Pointer = FFI.callPointer(address, descriptor, args)
}