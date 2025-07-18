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

import com.v7878.foreign.MemoryLayout
import com.v7878.foreign.ValueLayout
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.toHexString

/**
 * Determines the appropriate [ValueLayout] for pointer types based on the provided parameters.
 *
 * This function is used internally to get the correct memory layout for pointer-related types
 * (PTR, NINT, NUINT) based on the platform's pointer size and the segment usage preference.
 *
 * @param useSegments When true, returns ValueLayout.ADDRESS which is appropriate for memory segments.
 *                    When false, returns a value layout based on the platform's pointer size
 *                    (JAVA_INT for 32-bit platforms, JAVA_LONG for 64-bit platforms).
 * @return The appropriate [ValueLayout] for representing pointers in memory.
 */
private fun getPointerLayout(useSegments: Boolean = false): ValueLayout {
    if (useSegments) return ValueLayout.ADDRESS
    return if (Address.SIZE_BYTES == Int.SIZE_BYTES) ValueLayout.JAVA_INT
    else ValueLayout.JAVA_LONG
}

/**
 * Converts this [FFIType] to a Java [MemoryLayout].
 *
 * This extension function maps each FFI type to its corresponding Java memory layout.
 * For array types (dimensions > 0), it always returns a pointer layout, as arrays are passed by reference.
 * The mapping is as follows:
 * - BYTE, UBYTE -> JAVA_BYTE
 * - SHORT, USHORT -> JAVA_SHORT
 * - INT, UINT -> JAVA_INT
 * - LONG, ULONG -> JAVA_LONG
 * - NINT, NUINT -> JAVA_INT or JAVA_LONG depending on the platform's pointer size
 * - PTR -> ADDRESS when useSegments is true, otherwise JAVA_INT or JAVA_LONG depending on the platform's pointer size
 * - FLOAT -> JAVA_FLOAT
 * - DOUBLE -> JAVA_DOUBLE
 * - Array types (dimensions > 0) -> Same as PTR, using the specified useSegments parameter
 *
 * @param useSegments When true, pointer types (PTR) and array types will use ValueLayout.ADDRESS. When false,
 *                    they will use JAVA_INT or JAVA_LONG based on the platform's pointer size.
 *                    Note that NINT and NUINT always use the platform's pointer size representation
 *                    regardless of this parameter.
 * @return A [MemoryLayout] that represents the memory layout of this FFI type.
 * @throws IllegalStateException if this FFI type has no valid memory layout.
 */
fun FFIType.getMemoryLayout(useSegments: Boolean = false): MemoryLayout {
    return when (this) {
        FFIType.BYTE, FFIType.UBYTE -> ValueLayout.JAVA_BYTE
        FFIType.SHORT, FFIType.USHORT -> ValueLayout.JAVA_SHORT
        FFIType.INT, FFIType.UINT -> ValueLayout.JAVA_INT
        FFIType.LONG, FFIType.ULONG -> ValueLayout.JAVA_LONG
        FFIType.NINT, FFIType.NUINT -> getPointerLayout(false)
        FFIType.PTR -> getPointerLayout(useSegments)
        FFIType.FLOAT -> ValueLayout.JAVA_FLOAT
        FFIType.DOUBLE -> ValueLayout.JAVA_DOUBLE
        else -> throw IllegalStateException("$this has no valid memory layout")
    }
}

internal fun FFIType.toLibFFI(): VoidPtr = when (this) {
    FFIType.VOID -> LibFFI.ffi_type_void
    FFIType.BYTE -> LibFFI.ffi_type_sint8
    FFIType.SHORT -> LibFFI.ffi_type_sint16
    FFIType.INT -> LibFFI.ffi_type_sint32
    FFIType.LONG -> LibFFI.ffi_type_sint64
    FFIType.NINT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_sint32 else LibFFI.ffi_type_sint64
    FFIType.UBYTE -> LibFFI.ffi_type_uint8
    FFIType.USHORT -> LibFFI.ffi_type_uint16
    FFIType.UINT -> LibFFI.ffi_type_uint32
    FFIType.ULONG -> LibFFI.ffi_type_uint64
    FFIType.NUINT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_uint64 else LibFFI.ffi_type_uint64
    FFIType.FLOAT -> LibFFI.ffi_type_float
    FFIType.DOUBLE -> LibFFI.ffi_type_double
    FFIType.NFLOAT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_float else LibFFI.ffi_type_double
    FFIType.PTR -> LibFFI.ffi_type_pointer
}

@OptIn(ExperimentalStdlibApi::class)
internal fun Address.toFFI(): FFIType = when (this) {
    LibFFI.ffi_type_sint8 -> FFIType.BYTE
    LibFFI.ffi_type_sint16 -> FFIType.SHORT
    LibFFI.ffi_type_sint32 -> FFIType.INT
    LibFFI.ffi_type_sint64 -> FFIType.LONG
    LibFFI.ffi_type_uint8 -> FFIType.UBYTE
    LibFFI.ffi_type_uint16 -> FFIType.USHORT
    LibFFI.ffi_type_uint32 -> FFIType.UINT
    LibFFI.ffi_type_uint64 -> FFIType.ULONG
    LibFFI.ffi_type_float -> FFIType.FLOAT
    LibFFI.ffi_type_double -> FFIType.DOUBLE
    LibFFI.ffi_type_pointer -> FFIType.PTR
    else -> error("Incompatible FFI type at 0x${rawAddress.toHexString()}")
}