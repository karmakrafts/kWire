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