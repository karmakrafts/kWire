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
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.asPtr
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

    val ffi_type_void: Ptr<CVoid> = library.getVariable("ffi_type_void").toPtr()
    val ffi_type_sint8: Ptr<CVoid> = library.getVariable("ffi_type_sint8").toPtr()
    val ffi_type_sint16: Ptr<CVoid> = library.getVariable("ffi_type_sint16").toPtr()
    val ffi_type_sint32: Ptr<CVoid> = library.getVariable("ffi_type_sint32").toPtr()
    val ffi_type_sint64: Ptr<CVoid> = library.getVariable("ffi_type_sint64").toPtr()
    val ffi_type_uint8: Ptr<CVoid> = library.getVariable("ffi_type_uint8").toPtr()
    val ffi_type_uint16: Ptr<CVoid> = library.getVariable("ffi_type_uint16").toPtr()
    val ffi_type_uint32: Ptr<CVoid> = library.getVariable("ffi_type_uint32").toPtr()
    val ffi_type_uint64: Ptr<CVoid> = library.getVariable("ffi_type_uint64").toPtr()
    val ffi_type_float: Ptr<CVoid> = library.getVariable("ffi_type_float").toPtr()
    val ffi_type_double: Ptr<CVoid> = library.getVariable("ffi_type_double").toPtr()
    val ffi_type_pointer: Ptr<CVoid> = library.getVariable("ffi_type_pointer").toPtr()

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

    inline fun ffi_cif_alloc(): Ptr<CVoid> = Memory.allocate(cifSize)

    inline fun ffi_cif_free(cif: Ptr<*>) = Memory.free(cif)

    inline fun ffi_prep_cif(cif: Ptr<*>, abi: Int, nargs: Int, rtype: Ptr<*>, atypes: Ptr<*>): Int =
        _ffi_prep_cif.invokeExact(
            cif.toMemorySegment(),
            abi,
            nargs,
            rtype.toMemorySegment(),
            atypes.toMemorySegment()
        ) as Int

    inline fun ffi_cif_get_return_type(cif: Ptr<*>): Ptr<CVoid> =
        AndroidNativePlatform.getFFICIFReturnType(cif.asLong()).asPtr()

    inline fun ffi_call(cif: Ptr<*>, fn: Ptr<*>, rvalue: Ptr<*>, avalues: Ptr<*>) =
        _ffi_call.invokeExact(
            cif.toMemorySegment(),
            fn.toMemorySegment(),
            rvalue.toMemorySegment(),
            avalues.toMemorySegment()
        ) as Unit

    inline fun ffi_closure_alloc(code: Ptr<*>): Ptr<CVoid> =
        (_ffi_closure_alloc.invokeExact(closureSize.toLong(), code.toMemorySegment()) as MemorySegment).toPtr()

    inline fun ffi_closure_free(closure: Ptr<*>) =
        _ffi_closure_free.invokeExact(closure.toMemorySegment()) as Unit

    inline fun ffi_prep_closure_loc(closure: Ptr<*>, cif: Ptr<*>, fn: Ptr<*>, userdata: Ptr<*>, codeloc: Ptr<*>): Int =
        _ffi_prep_closure_loc.invokeExact(
            closure.toMemorySegment(),
            cif.toMemorySegment(),
            fn.toMemorySegment(),
            userdata.toMemorySegment(),
            codeloc.toMemorySegment()
        ) as Int
    // @formatter:on
}