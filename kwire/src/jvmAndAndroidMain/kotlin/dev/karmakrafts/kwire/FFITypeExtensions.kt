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

import java.lang.foreign.MemoryLayout
import java.lang.foreign.ValueLayout

/**
 * Converts this [FFIType] to a Java [MemoryLayout].
 *
 * This extension function maps each FFI type to its corresponding Java memory layout.
 * The mapping is as follows:
 * - BYTE, UBYTE -> JAVA_BYTE
 * - SHORT, USHORT -> JAVA_SHORT
 * - INT, UINT -> JAVA_INT
 * - LONG, ULONG -> JAVA_LONG
 * - NINT -> JAVA_INT or JAVA_LONG depending on the platform
 * - NUINT -> JAVA_INT or JAVA_LONG depending on the platform
 * - PTR -> ADDRESS
 * - FLOAT -> JAVA_FLOAT
 * - DOUBLE -> JAVA_DOUBLE
 *
 * @return A [MemoryLayout] that represents the memory layout of this FFI type.
 * @throws IllegalStateException if this FFI type has no valid memory layout.
 */
fun FFIType.getMemoryLayout(): MemoryLayout = when (this) {
    FFIType.BYTE, FFIType.UBYTE -> ValueLayout.JAVA_BYTE
    FFIType.SHORT, FFIType.USHORT -> ValueLayout.JAVA_SHORT
    FFIType.INT, FFIType.UINT -> ValueLayout.JAVA_INT
    FFIType.LONG, FFIType.ULONG -> ValueLayout.JAVA_LONG
    FFIType.NINT -> if (Pointer.SIZE_BYTES == Int.SIZE_BYTES) ValueLayout.JAVA_INT else ValueLayout.JAVA_LONG
    FFIType.NUINT -> if (Pointer.SIZE_BYTES == UInt.SIZE_BYTES) ValueLayout.JAVA_INT else ValueLayout.JAVA_LONG
    FFIType.PTR -> ValueLayout.ADDRESS
    FFIType.FLOAT -> ValueLayout.JAVA_FLOAT
    FFIType.DOUBLE -> ValueLayout.JAVA_DOUBLE
    else -> throw IllegalStateException("$this has no valid memory layout")
}
