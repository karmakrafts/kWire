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

import kotlin.reflect.KClass

// TODO: document this
enum class FFIType( // @formatter:off
    val type: KClass<*>,
    val size: Int
) { // @formatter:on
    // @formatter:off
    VOID    (Unit::class,   0),

    BYTE    (Byte::class,   Byte.SIZE_BYTES),
    SHORT   (Short::class,  Short.SIZE_BYTES),
    INT     (Int::class,    Int.SIZE_BYTES),
    LONG    (Long::class,   Long.SIZE_BYTES),
    NINT    (NInt::class,   Pointer.SIZE_BYTES),

    UBYTE   (UByte::class,  UByte.SIZE_BYTES),
    USHORT  (UShort::class, UShort.SIZE_BYTES),
    UINT    (UInt::class,   UInt.SIZE_BYTES),
    ULONG   (ULong::class,  ULong.SIZE_BYTES),
    NUINT   (NUInt::class,  Pointer.SIZE_BYTES),

    FLOAT   (Float::class,  Float.SIZE_BYTES),
    DOUBLE  (Double::class, Double.SIZE_BYTES),

    PTR     (Pointer::class, Pointer.SIZE_BYTES) // So we can differentiate size_t's and void*'s
    // @formatter:on
}

// TODO: document this
inline fun KClass<*>.findFFIType(): FFIType? = FFIType.entries.find { it.type == this@findFFIType }

// TODO: document this
inline fun KClass<*>.getFFIType(): FFIType =
    requireNotNull(findFFIType()) { "No matching FFI type for ${this@getFFIType}" }