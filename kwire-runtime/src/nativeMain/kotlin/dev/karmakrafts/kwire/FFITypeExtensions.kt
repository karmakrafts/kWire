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

import dev.karmakrafts.kwire.ffi.FFI_TYPE_DOUBLE
import dev.karmakrafts.kwire.ffi.FFI_TYPE_FLOAT
import dev.karmakrafts.kwire.ffi.FFI_TYPE_POINTER
import dev.karmakrafts.kwire.ffi.FFI_TYPE_SINT16
import dev.karmakrafts.kwire.ffi.FFI_TYPE_SINT32
import dev.karmakrafts.kwire.ffi.FFI_TYPE_SINT64
import dev.karmakrafts.kwire.ffi.FFI_TYPE_SINT8
import dev.karmakrafts.kwire.ffi.FFI_TYPE_UINT16
import dev.karmakrafts.kwire.ffi.FFI_TYPE_UINT32
import dev.karmakrafts.kwire.ffi.FFI_TYPE_UINT64
import dev.karmakrafts.kwire.ffi.FFI_TYPE_UINT8
import dev.karmakrafts.kwire.ffi.FFI_TYPE_VOID
import dev.karmakrafts.kwire.ffi.ffi_type
import dev.karmakrafts.kwire.ffi.ffi_type_double
import dev.karmakrafts.kwire.ffi.ffi_type_float
import dev.karmakrafts.kwire.ffi.ffi_type_pointer
import dev.karmakrafts.kwire.ffi.ffi_type_sint16
import dev.karmakrafts.kwire.ffi.ffi_type_sint32
import dev.karmakrafts.kwire.ffi.ffi_type_sint64
import dev.karmakrafts.kwire.ffi.ffi_type_sint8
import dev.karmakrafts.kwire.ffi.ffi_type_uint16
import dev.karmakrafts.kwire.ffi.ffi_type_uint32
import dev.karmakrafts.kwire.ffi.ffi_type_uint64
import dev.karmakrafts.kwire.ffi.ffi_type_uint8
import dev.karmakrafts.kwire.ffi.ffi_type_void
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Converts this [FFIType] to a native libffi type address.
 *
 * This extension function maps each FFI type to its corresponding native libffi type address.
 * For array types (dimensions > 0), it always returns a pointer type, as arrays are passed by reference.
 * The mapping is as follows:
 * - VOID -> ffi_type_void
 * - BYTE -> ffi_type_sint8
 * - SHORT -> ffi_type_sint16
 * - INT -> ffi_type_sint32
 * - LONG -> ffi_type_sint64
 * - NINT -> ffi_type_sint32 or ffi_type_sint64 depending on the platform's pointer size
 * - UBYTE -> ffi_type_uint8
 * - USHORT -> ffi_type_uint16
 * - UINT -> ffi_type_uint32
 * - ULONG -> ffi_type_uint64
 * - NUINT -> ffi_type_uint32 or ffi_type_uint64 depending on the platform's pointer size
 * - FLOAT -> ffi_type_float
 * - DOUBLE -> ffi_type_double
 * - PTR -> ffi_type_pointer
 *
 * @return A [ffi_type] that represents the native libffi type address for this FFI type.
 * @throws IllegalStateException if this FFI type is not supported.
 */
@ExperimentalForeignApi
fun FFIType.getFFITypeAddress(): ffi_type {
    if (dimensions > 0) return ffi_type_pointer // For arrays, we always pass pointers
    return when (this) {
        FFIType.VOID -> ffi_type_void
        FFIType.BYTE -> ffi_type_sint8
        FFIType.SHORT -> ffi_type_sint16
        FFIType.INT -> ffi_type_sint32
        FFIType.LONG -> ffi_type_sint64
        FFIType.NINT -> if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) ffi_type_sint32 else ffi_type_sint64
        FFIType.UBYTE -> ffi_type_uint8
        FFIType.USHORT -> ffi_type_uint16
        FFIType.UINT -> ffi_type_uint32
        FFIType.ULONG -> ffi_type_uint64
        FFIType.NUINT -> if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) ffi_type_uint32 else ffi_type_uint64
        FFIType.FLOAT -> ffi_type_float
        FFIType.DOUBLE -> ffi_type_double
        FFIType.PTR -> ffi_type_pointer
        else -> error("Unsupported FFI type: $this")
    }
}

/**
 * Converts this [FFIType] to a native libffi type index.
 *
 * This extension function maps each FFI type to its corresponding native libffi type index.
 * For array types (dimensions > 0), it always returns a pointer type index, as arrays are passed by reference.
 * The mapping is as follows:
 * - VOID -> FFI_TYPE_VOID
 * - BYTE -> FFI_TYPE_SINT8
 * - SHORT -> FFI_TYPE_SINT16
 * - INT -> FFI_TYPE_SINT32
 * - LONG -> FFI_TYPE_SINT64
 * - NINT -> FFI_TYPE_SINT32 or FFI_TYPE_SINT64 depending on the platform's pointer size
 * - UBYTE -> FFI_TYPE_UINT8
 * - USHORT -> FFI_TYPE_UINT16
 * - UINT -> FFI_TYPE_UINT32
 * - ULONG -> FFI_TYPE_UINT64
 * - NUINT -> FFI_TYPE_UINT32 or FFI_TYPE_UINT64 depending on the platform's pointer size
 * - FLOAT -> FFI_TYPE_FLOAT
 * - DOUBLE -> FFI_TYPE_DOUBLE
 * - PTR -> FFI_TYPE_POINTER
 *
 * @return An integer that represents the native libffi type index for this FFI type.
 * @throws IllegalStateException if this FFI type is not supported.
 */
@ExperimentalForeignApi
fun FFIType.getFFITypeIndex(): Int {
    if (dimensions > 0) return FFI_TYPE_POINTER
    return when (this) {
        FFIType.VOID -> FFI_TYPE_VOID
        FFIType.BYTE -> FFI_TYPE_SINT8
        FFIType.SHORT -> FFI_TYPE_SINT16
        FFIType.INT -> FFI_TYPE_SINT32
        FFIType.LONG -> FFI_TYPE_SINT64
        FFIType.NINT -> if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) FFI_TYPE_SINT32 else FFI_TYPE_SINT64
        FFIType.UBYTE -> FFI_TYPE_UINT8
        FFIType.USHORT -> FFI_TYPE_UINT16
        FFIType.UINT -> FFI_TYPE_UINT32
        FFIType.ULONG -> FFI_TYPE_UINT64
        FFIType.NUINT -> if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) FFI_TYPE_UINT32 else FFI_TYPE_UINT64
        FFIType.FLOAT -> FFI_TYPE_FLOAT
        FFIType.DOUBLE -> FFI_TYPE_DOUBLE
        FFIType.PTR -> FFI_TYPE_POINTER
        else -> error("Unsupported FFI type: $this")
    }
}
