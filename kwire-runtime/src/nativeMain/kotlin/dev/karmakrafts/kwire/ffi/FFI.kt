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
import dev.karmakrafts.kwire.ctype.toNUInt
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
            (address + types.take(index).sumOf { it.size }).toCPointer()
        })
    }

    override fun createUpcallStub( // @formatter:off
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        function: (FFIArgBuffer) -> Unit
    ): VoidPtr { // @formatter:on
        TODO("Not yet implemented")
    }

    override fun call( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ) = memScoped { // @formatter:on
        ffi_call(descriptor.toCif(this).ptr, address.toCFunctionPointer(), null, args.allocate(this))
    }

    override fun callByte( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Byte = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ByteVar>().value
    }

    override fun callShort( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Short = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ShortVar>().value
    }

    override fun callInt( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Int = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<IntVar>().value
    }

    override fun callLong( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Long = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<LongVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNInt( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NInt = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<ptrdiff_tVar>().value.convert()
    }

    override fun callUByte( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UByte = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint8_tVar>().value
    }

    override fun callUShort( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UShort = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint16_tVar>().value
    }

    override fun callUInt( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UInt = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint32_tVar>().value
    }

    override fun callULong( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): ULong = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<uint64_tVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNUInt( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NUInt = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<size_tVar>().value.toNUInt()
    }

    override fun callFloat( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Float = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<FloatVar>().value
    }

    override fun callDouble( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Double = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<DoubleVar>().value
    }

    @OptIn(UnsafeNumber::class)
    override fun callNFloat( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NFloat = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<nfloat_tVar>().value.toNFloat()
    }

    override fun callPointer( // @formatter:off
        address: Address,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): VoidPtr = memScoped { // @formatter:on
        val result = alloc<ffi_type>()
        ffi_call(
            descriptor.toCif(this).ptr, address.toCFunctionPointer(), result.ptr, args.allocate(this)
        )
        result.reinterpret<COpaquePointerVar>().value!!.reinterpret<COpaque>().toPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = NativeFFI