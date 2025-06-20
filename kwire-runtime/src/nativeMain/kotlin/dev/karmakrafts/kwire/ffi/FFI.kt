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
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.toCFunctionPointer
import dev.karmakrafts.kwire.ctype.toCPointer
import dev.karmakrafts.kwire.ctype.toNFloat
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
import platform.posix.nfloat_tVar
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar
import platform.posix.uint16_tVar
import platform.posix.uint32_tVar
import platform.posix.uint64_tVar
import platform.posix.uint8_tVar

@OptIn(ExperimentalForeignApi::class)
private object NativeFFI : FFI {
    @OptIn(UnsafeNumber::class)
    @Suppress("NOTHING_TO_INLINE")
    private inline fun FFIArgBuffer.allocate(scope: NativePlacement): CArrayPointer<COpaquePointerVar> {
        return scope.allocArrayOf(types.mapIndexed { index, type ->
            getAddress(index).toCPointer()
        })
    }

    override fun createUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Unit): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createByteUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Byte): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createShortUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Short): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createIntUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Int): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createLongUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Long): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUByteUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UByte): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUShortUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UShort): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUIntUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UInt): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createULongUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> ULong): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createFloatUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Float): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createDoubleUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Double): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer) = memScoped {
        ffi_call(descriptor.toCif(this).ptr, address.toCFunctionPointer(), null, args.allocate(this))
    }

    override fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Byte = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ByteVar>().value
    }

    override fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Short = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ShortVar>().value
    }

    override fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Int = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<IntVar>().value
    }

    override fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Long = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<LongVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NInt = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ptrdiff_tVar>().value.convert()
    }

    override fun callUByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UByte = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint8_tVar>().value
    }

    override fun callUShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UShort = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint16_tVar>().value
    }

    override fun callUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UInt = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint32_tVar>().value
    }

    override fun callULong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): ULong = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint64_tVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NUInt = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<size_tVar>().value.convert()
    }

    override fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Float = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<FloatVar>().value
    }

    override fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Double = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<DoubleVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NFloat = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<nfloat_tVar>().value.toNFloat()
    }

    override fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): VoidPtr = memScoped {
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<COpaquePointerVar>().value!!.reinterpret<COpaque>().toPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = NativeFFI