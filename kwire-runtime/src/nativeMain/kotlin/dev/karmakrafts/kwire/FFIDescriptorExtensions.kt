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

import dev.karmakrafts.kwire.ffi.FFI_DEFAULT_ABI
import dev.karmakrafts.kwire.ffi.ffi_cif
import dev.karmakrafts.kwire.ffi.ffi_prep_cif
import dev.karmakrafts.kwire.ffi.ffi_type
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOfPointersTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr

@ExperimentalForeignApi
inline fun FFIDescriptor.getResultFFITypeAddress(): CPointer<ffi_type> = returnType.getFFITypeAddress().ptr

@ExperimentalForeignApi
inline fun FFIDescriptor.getArrayFFITypeAddresses(scope: NativePlacement): CArrayPointer<CPointerVar<ffi_type>> {
    return scope.allocArrayOfPointersTo(parameterTypes.map { it.getFFITypeAddress() })
}

@ExperimentalForeignApi
inline fun FFIDescriptor.toCif(scope: NativePlacement): ffi_cif {
    val cif = scope.alloc<ffi_cif>()
    ffi_prep_cif(
        cif.ptr,
        FFI_DEFAULT_ABI,
        parameterTypes.size.convert(),
        getResultFFITypeAddress(),
        getArrayFFITypeAddresses(scope)
    )
    return cif
}