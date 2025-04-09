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

package dev.karmakrafts.kwire

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
sealed interface FFIType {
    companion object {
        /**
         * Represents the void type (no value).
         */
        val VOID: FFIType = DefaultFFIType(Unit::class, 0)

        /**
         * Represents a signed 8-bit integer.
         */
        val BYTE: FFIType = DefaultFFIType(Byte::class, Byte.SIZE_BYTES)

        /**
         * Represents a signed 16-bit integer.
         */
        val SHORT: FFIType = DefaultFFIType(Short::class, Short.SIZE_BYTES)

        /**
         * Represents a signed 32-bit integer.
         */
        val INT: FFIType = DefaultFFIType(Int::class, Int.SIZE_BYTES)

        /**
         * Represents a signed 64-bit integer.
         */
        val LONG: FFIType = DefaultFFIType(Long::class, Long.SIZE_BYTES)

        /**
         * Represents a native signed integer with platform-specific size.
         */
        val NINT: FFIType = DefaultFFIType(NInt::class, Pointer.SIZE_BYTES)

        /**
         * Represents an unsigned 8-bit integer.
         */
        val UBYTE: FFIType = DefaultFFIType(UByte::class, UByte.SIZE_BYTES)

        /**
         * Represents an unsigned 16-bit integer.
         */
        val USHORT: FFIType = DefaultFFIType(UShort::class, UShort.SIZE_BYTES)

        /**
         * Represents an unsigned 32-bit integer.
         */
        val UINT: FFIType = DefaultFFIType(UInt::class, UInt.SIZE_BYTES)

        /**
         * Represents an unsigned 64-bit integer.
         */
        val ULONG: FFIType = DefaultFFIType(ULong::class, ULong.SIZE_BYTES)

        /**
         * Represents a native unsigned integer with platform-specific size.
         */
        val NUINT: FFIType = DefaultFFIType(NUInt::class, Pointer.SIZE_BYTES)

        /**
         * Represents a 32-bit floating-point number.
         */
        val FLOAT: FFIType = DefaultFFIType(Float::class, Float.SIZE_BYTES)

        /**
         * Represents a 64-bit floating-point number.
         */
        val DOUBLE: FFIType = DefaultFFIType(Double::class, Double.SIZE_BYTES)

        /**
         * Represents a pointer type.
         */
        val PTR: FFIType = DefaultFFIType(Pointer::class, Pointer.SIZE_BYTES)

        /**
         * An array containing all predefined FFI types for easy iteration.
         */
        val types: Array<FFIType> = arrayOf(
            VOID, BYTE, SHORT, INT, LONG, NINT, UBYTE, USHORT, UINT, ULONG, NUINT, FLOAT, DOUBLE, PTR
        )

        /**
         * Attempts to determine the FFI type corresponding to the given Kotlin class.
         *
         * @param type The Kotlin class to convert to an FFI type.
         * @return The corresponding FFI type, or null if no matching FFI type is found.
         */
        fun fromTypeOrNull(type: KClass<*>): FFIType? = when (type) {
            Unit::class -> VOID
            Byte::class -> BYTE
            Short::class -> SHORT
            Int::class -> INT
            Long::class -> LONG
            NInt::class -> NINT
            UByte::class -> UBYTE
            UShort::class -> USHORT
            UInt::class -> UINT
            ULong::class -> ULONG
            NUInt::class -> NUINT
            Float::class -> FLOAT
            Double::class -> DOUBLE
            Pointer::class -> PTR
            else -> null
        }

        /**
         * Attempts to determine the FFI type corresponding to the given reified type parameter.
         *
         * @return The corresponding FFI type, or null if no matching FFI type is found.
         */
        inline fun <reified T : Any> fromTypeOrNull(): FFIType? = fromTypeOrNull(T::class)

        /**
         * Determines the FFI type corresponding to the given Kotlin class.
         *
         * @param type The Kotlin class to convert to an FFI type.
         * @return The corresponding FFI type.
         * @throws IllegalArgumentException If no matching FFI type is found.
         */
        fun fromType(type: KClass<*>): FFIType = requireNotNull(fromTypeOrNull(type)) {
            "No matching FFI type for type $type"
        }

        /**
         * Determines the FFI type corresponding to the given reified type parameter.
         *
         * @return The corresponding FFI type.
         * @throws IllegalArgumentException If no matching FFI type is found.
         */
        inline fun <reified T : Any> fromType(): FFIType = fromType(T::class)

        /**
         * Attempts to determine the FFI type of the given instance.
         * 
         * This method handles both primitive types and array types.
         *
         * @param instance The instance to determine the FFI type of.
         * @return The corresponding FFI type, or null if no matching FFI type is found.
         */
        fun fromInstanceOrNull(instance: Any?): FFIType? = when (instance) {
            // Primitives
            is Unit -> VOID
            is Byte -> BYTE
            is Short -> SHORT
            is Int -> INT
            is Long -> LONG
            is NInt -> NINT
            is UByte -> UBYTE
            is UShort -> USHORT
            is ULong -> ULONG
            is NUInt -> NUINT
            is Float -> FLOAT
            is Double -> DOUBLE
            is Pointer -> PTR
            // Simple arrays
            is ByteArray -> BYTE.array(instance.size)
            is ShortArray -> SHORT.array(instance.size)
            is IntArray -> INT.array(instance.size)
            is LongArray -> LONG.array(instance.size)
            is NIntArray -> NINT.array(instance.size)
            is UByteArray -> UBYTE.array(instance.size)
            is UShortArray -> USHORT.array(instance.size)
            is UIntArray -> UINT.array(instance.size)
            is ULongArray -> ULONG.array(instance.size)
            is NUIntArray -> NUINT.array(instance.size)
            is PointerArray -> PTR.array(instance.size)
            else -> null
        }

        /**
         * Determines the FFI type of the given instance.
         *
         * @param instance The instance to determine the FFI type of.
         * @return The corresponding FFI type.
         * @throws IllegalArgumentException If no matching FFI type is found.
         */
        fun fromInstance(instance: Any?): FFIType = requireNotNull(fromInstanceOrNull(instance)) {
            "No matching FFI type for instance $instance"
        }
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
