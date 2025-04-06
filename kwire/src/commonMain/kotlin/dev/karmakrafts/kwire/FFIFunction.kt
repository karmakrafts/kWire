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

package dev.karmakrafts.kwire

// TODO: document this
class FFIFunction(
    val name: String,
    val address: Pointer,
    val descriptor: FFIDescriptor
) {
    fun call(args: FFIArgSpec = {}) = FFI.call(address, descriptor, args)
    fun callByte(args: FFIArgSpec = {}): Byte = FFI.callByte(address, descriptor, args)
    fun callShort(args: FFIArgSpec = {}): Short = FFI.callShort(address, descriptor, args)
    fun callInt(args: FFIArgSpec = {}): Int = FFI.callInt(address, descriptor, args)
    fun callLong(args: FFIArgSpec = {}): Long = FFI.callLong(address, descriptor, args)
    fun callNInt(args: FFIArgSpec = {}): NInt = FFI.callNInt(address, descriptor, args)
    fun callUByte(args: FFIArgSpec = {}): UByte = FFI.callUByte(address, descriptor, args)
    fun callUShort(args: FFIArgSpec = {}): UShort = FFI.callUShort(address, descriptor, args)
    fun callUInt(args: FFIArgSpec = {}): UInt = FFI.callUInt(address, descriptor, args)
    fun callULong(args: FFIArgSpec = {}): ULong = FFI.callULong(address, descriptor, args)
    fun callNUInt(args: FFIArgSpec = {}): NUInt = FFI.callNUInt(address, descriptor, args)
    fun callFloat(args: FFIArgSpec = {}): Float = FFI.callFloat(address, descriptor, args)
    fun callDouble(args: FFIArgSpec = {}): Double = FFI.callDouble(address, descriptor, args)
    fun callPointer(args: FFIArgSpec = {}): Pointer = FFI.callPointer(address, descriptor, args)
}