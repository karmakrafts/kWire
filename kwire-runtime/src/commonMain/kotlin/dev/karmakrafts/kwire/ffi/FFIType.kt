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

@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(ExperimentalUnsignedTypes::class)

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ctype.Address

@KWireCompilerApi
enum class FFIType(val size: Int) {
    // @formatter:off
    VOID    (0),
    BYTE    (Byte.SIZE_BYTES),
    SHORT   (Short.SIZE_BYTES),
    INT     (Int.SIZE_BYTES),
    LONG    (Long.SIZE_BYTES),
    NINT    (Address.SIZE_BYTES),
    UBYTE   (UByte.SIZE_BYTES),
    USHORT  (UShort.SIZE_BYTES),
    UINT    (UInt.SIZE_BYTES),
    ULONG   (ULong.SIZE_BYTES),
    NUINT   (Address.SIZE_BYTES),
    FLOAT   (Float.SIZE_BYTES),
    DOUBLE  (Double.SIZE_BYTES),
    NFLOAT  (Address.SIZE_BYTES),
    PTR     (Address.SIZE_BYTES)
    // @formatter:on
}