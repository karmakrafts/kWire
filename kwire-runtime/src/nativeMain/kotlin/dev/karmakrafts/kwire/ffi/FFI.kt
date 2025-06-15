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

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.toCFunctionPointer
import dev.karmakrafts.kwire.ctype.toCPointer
import dev.karmakrafts.kwire.ctype.toPtr
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.posix.ptrdiff_tVar

@OptIn(ExperimentalForeignApi::class)
private object NativeFFI : FFI {
    @OptIn(UnsafeNumber::class)
    @Suppress("NOTHING_TO_INLINE")
    private inline fun FFIArgBuffer.allocate(scope: NativePlacement): CArrayPointer<COpaquePointerVar> {
        return scope.allocArrayOf(types.mapIndexed { index, type ->
            getAddress(index).toCPointer()
        })
    }

    override fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec) = memScoped {
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(descriptor.toCif(this).ptr, address.toCFunctionPointer(), null, argBuffer.allocate(this))
    }

    override fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Byte = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<ByteVar>().value
    }

    override fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Short = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<ShortVar>().value
    }

    override fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Int = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<IntVar>().value
    }

    override fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Long = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<LongVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): NInt = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<ptrdiff_tVar>().value.convert()
    }

    override fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Float = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<FloatVar>().value
    }

    override fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): Double = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<DoubleVar>().value
    }

    override fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgSpec): VoidPtr = memScoped {
        val result = alloc<ffi_type>()
        val argBuffer = FFIArgBuffer.get()
        argBuffer.args()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, argBuffer.allocate(this)
        )
        result.reinterpret<COpaquePointerVar>().value!!.reinterpret<COpaque>().toPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = NativeFFI