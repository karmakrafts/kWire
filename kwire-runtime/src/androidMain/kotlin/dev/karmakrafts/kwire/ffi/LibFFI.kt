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

import com.v7878.foreign.MemorySegment
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asLong
import dev.karmakrafts.kwire.ctype.asVoidPtr
import dev.karmakrafts.kwire.ctype.toMemorySegment
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.ctype.toPtr
import dev.karmakrafts.kwire.memory.Memory
import dev.karmakrafts.kwire.util.AndroidNativePlatform
import java.lang.invoke.MethodHandle

// Since we don't have LWJGL on Android, we need to bind against libffi ourselves
@Suppress("NOTHING_TO_INLINE")
internal object LibFFI {
    const val FFI_OK: Int = 0
    const val FFI_BAD_TYPEDEF: Int = 1
    const val FFI_BAD_ABI: Int = 2
    const val FFI_BAD_ARGTYPE: Int = 3

    private val library: SharedLibrary = SharedLibrary.open("ffi").apply { closeOnExit() }

    val ffi_type_void: VoidPtr = library.getVariable("ffi_type_void").toPtr()
    val ffi_type_sint8: VoidPtr = library.getVariable("ffi_type_sint8").toPtr()
    val ffi_type_sint16: VoidPtr = library.getVariable("ffi_type_sint16").toPtr()
    val ffi_type_sint32: VoidPtr = library.getVariable("ffi_type_sint32").toPtr()
    val ffi_type_sint64: VoidPtr = library.getVariable("ffi_type_sint64").toPtr()
    val ffi_type_uint8: VoidPtr = library.getVariable("ffi_type_uint8").toPtr()
    val ffi_type_uint16: VoidPtr = library.getVariable("ffi_type_uint16").toPtr()
    val ffi_type_uint32: VoidPtr = library.getVariable("ffi_type_uint32").toPtr()
    val ffi_type_uint64: VoidPtr = library.getVariable("ffi_type_uint64").toPtr()
    val ffi_type_float: VoidPtr = library.getVariable("ffi_type_float").toPtr()
    val ffi_type_double: VoidPtr = library.getVariable("ffi_type_double").toPtr()
    val ffi_type_pointer: VoidPtr = library.getVariable("ffi_type_pointer").toPtr()

    // @formatter:off
    @PublishedApi
    internal val _ffi_prep_cif: MethodHandle = library.getFunction("ffi_prep_cif",
        FFIType.INT, FFIType.PTR, FFIType.INT, FFIType.UINT, FFIType.PTR, FFIType.PTR)
    @PublishedApi
    internal val _ffi_call: MethodHandle = library.getFunction("ffi_call",
        FFIType.VOID, FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.PTR)
    @PublishedApi
    internal val _ffi_closure_alloc: MethodHandle = library.getFunction("ffi_closure_alloc",
        FFIType.PTR, FFIType.NUINT, FFIType.PTR)
    @PublishedApi
    internal val _ffi_closure_free: MethodHandle = library.getFunction("ffi_closure_free",
        FFIType.VOID, FFIType.PTR)
    @PublishedApi
    internal val _ffi_prep_closure_loc: MethodHandle = library.getFunction("ffi_prep_closure_loc",
        FFIType.INT, FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.PTR, FFIType.PTR)

    val cifSize: NUInt by lazy { AndroidNativePlatform.getFFICIFSize().toNUInt() }
    val closureSize: NUInt by lazy { AndroidNativePlatform.getFFIClosureSize().toNUInt() }
    val defaultAbi: Int by lazy { AndroidNativePlatform.getFFIDefaultABI() }

    inline fun ffi_cif_alloc(): VoidPtr = Memory.allocate(cifSize)

    inline fun ffi_cif_free(cif: Address) = Memory.free(cif)

    inline fun ffi_prep_cif(cif: Address, abi: Int, nargs: Int, rtype: Address, atypes: Address): Int =
        _ffi_prep_cif.invokeExact(
            cif.toMemorySegment(),
            abi,
            nargs,
            rtype.toMemorySegment(),
            atypes.toMemorySegment()
        ) as Int

    inline fun ffi_cif_get_return_type(cif: Address): VoidPtr =
        AndroidNativePlatform.getFFICIFReturnType(cif.asLong()).asVoidPtr()

    inline fun ffi_call(cif: Address, fn: Address, rvalue: Address, avalues: Address) =
        _ffi_call.invokeExact(
            cif.toMemorySegment(),
            fn.toMemorySegment(),
            rvalue.toMemorySegment(),
            avalues.toMemorySegment()
        ) as Unit

    inline fun ffi_closure_alloc(code: Address): VoidPtr =
        (_ffi_closure_alloc.invokeExact(closureSize.toLong(), code.toMemorySegment()) as MemorySegment).toPtr()

    inline fun ffi_closure_free(closure: Address) =
        _ffi_closure_free.invokeExact(closure.toMemorySegment()) as Unit

    inline fun ffi_prep_closure_loc(closure: Address, cif: Address, fn: Address, userdata: Address, codeloc: Address): Int =
        _ffi_prep_closure_loc.invokeExact(
            closure.toMemorySegment(),
            cif.toMemorySegment(),
            fn.toMemorySegment(),
            userdata.toMemorySegment(),
            codeloc.toMemorySegment()
        ) as Int
    // @formatter:on
}