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
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import kotlin.reflect.KClass

/**
 * Represents a Foreign Function Interface (FFI) type used for interoperability with native code.
 *
 * This interface defines the common properties that all FFI types must have, whether they are
 * primitive types or array types. It is used to describe the type information needed for
 * marshalling data between Kotlin and native code.
 *
 * @property elementType The base type of this FFI type. For primitive types, this is the type itself.
 * @property size The size of this type in bytes.
 * @property dimensions The number of array dimensions. For primitive types, this is 0.
 */
@KWireCompilerApi
interface FFIType {
    @KWireCompilerApi
    companion object {
        /**
         * Represents the void type (no value).
         */
        @KWireCompilerApi
        val VOID: FFIType = DefaultFFIType(Unit::class, 0)

        /**
         * Represents a signed 8-bit integer.
         */
        @KWireCompilerApi
        val BYTE: FFIType = DefaultFFIType(Byte::class, Byte.SIZE_BYTES)

        /**
         * Represents a signed 16-bit integer.
         */
        @KWireCompilerApi
        val SHORT: FFIType = DefaultFFIType(Short::class, Short.SIZE_BYTES)

        /**
         * Represents a signed 32-bit integer.
         */
        @KWireCompilerApi
        val INT: FFIType = DefaultFFIType(Int::class, Int.SIZE_BYTES)

        /**
         * Represents a signed 64-bit integer.
         */
        @KWireCompilerApi
        val LONG: FFIType = DefaultFFIType(Long::class, Long.SIZE_BYTES)

        /**
         * Represents a native signed integer with platform-specific size.
         */
        @KWireCompilerApi
        val NINT: FFIType = DefaultFFIType(NInt::class, Address.SIZE_BYTES)

        /**
         * Represents an unsigned 8-bit integer.
         */
        @KWireCompilerApi
        val UBYTE: FFIType = DefaultFFIType(UByte::class, UByte.SIZE_BYTES)

        /**
         * Represents an unsigned 16-bit integer.
         */
        @KWireCompilerApi
        val USHORT: FFIType = DefaultFFIType(UShort::class, UShort.SIZE_BYTES)

        /**
         * Represents an unsigned 32-bit integer.
         */
        @KWireCompilerApi
        val UINT: FFIType = DefaultFFIType(UInt::class, UInt.SIZE_BYTES)

        /**
         * Represents an unsigned 64-bit integer.
         */
        @KWireCompilerApi
        val ULONG: FFIType = DefaultFFIType(ULong::class, ULong.SIZE_BYTES)

        /**
         * Represents a native unsigned integer with platform-specific size.
         */
        @KWireCompilerApi
        val NUINT: FFIType = DefaultFFIType(NUInt::class, Address.SIZE_BYTES)

        /**
         * Represents a 32-bit floating-point number.
         */
        @KWireCompilerApi
        val FLOAT: FFIType = DefaultFFIType(Float::class, Float.SIZE_BYTES)

        /**
         * Represents a 64-bit floating-point number.
         */
        @KWireCompilerApi
        val DOUBLE: FFIType = DefaultFFIType(Double::class, Double.SIZE_BYTES)

        /**
         * Represents a native floating-point number.
         */
        @KWireCompilerApi
        val NFLOAT: FFIType = DefaultFFIType(NFloat::class, Address.SIZE_BYTES)

        /**
         * Represents a pointer type.
         */
        @KWireCompilerApi
        val PTR: FFIType = DefaultFFIType(Address::class, Address.SIZE_BYTES)

        /**
         * An array containing all predefined FFI types for easy iteration.
         */
        val types: Array<FFIType> = arrayOf(
            VOID, BYTE, SHORT, INT, LONG, NINT, UBYTE, USHORT, UINT, ULONG, NUINT, FLOAT, DOUBLE, PTR
        )
    }

    val elementType: FFIType
    val size: Int
    val dimensions: Int
}

/**
 * Creates an array type from this FFI type with the specified number of elements.
 *
 * This extension function allows for easy creation of array types from existing FFI types.
 * It increments the dimensions count to track the array nesting level.
 *
 * @param elementCount The number of elements in the array.
 * @return A new [FFIType] representing an array of the original type.
 */
@KWireCompilerApi
inline fun FFIType.array(elementCount: Int): FFIType = FFIArrayType(this, elementCount, dimensions + 1)

/**
 * Default implementation of the FFI type interface for primitive types.
 *
 * This class represents primitive FFI types with a specific size in bytes.
 * For primitive types, the element type is the type itself, and the dimensions count is 0.
 *
 * @property size The size of this type in bytes.
 */
internal data class DefaultFFIType( // @formatter:off
    val kotlinType: KClass<*>,
    override val size: Int
) : FFIType { // @formatter:on
    override val elementType: FFIType = this
    override val dimensions: Int = 0
}

/**
 * Represents an array type in the Foreign Function Interface (FFI) system.
 *
 * This class is used to describe array types for FFI operations. It contains information
 * about the element type, the number of elements, and the number of dimensions.
 * The size of the array type is calculated as the product of the element size and element count.
 *
 * @property elementType The type of elements in the array.
 * @property elementCount The number of elements in the array.
 * @property dimensions The number of array dimensions (nesting level).
 */
@PublishedApi
internal data class FFIArrayType( // @formatter:off
    override val elementType: FFIType, 
    val elementCount: Int, 
    override val dimensions: Int
) : FFIType { // @formatter:on
    /**
     * The size of this array type in bytes, calculated as the product of the element size and element count.
     */
    override val size: Int = elementType.size * elementCount
}
