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

package dev.karmakrafts.kwire.abi.type

import dev.karmakrafts.kwire.abi.ABIConstants
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.symbol.SymbolNameProvider
import dev.karmakrafts.kwire.abi.type.ArrayType.Companion.KIND
import kotlinx.io.Buffer

/**
 * Represents an array type in the ABI system.
 *
 * An array type consists of an element type and a number of dimensions.
 * The size of the array is calculated as the element type's size multiplied by the number of dimensions.
 * The alignment of the array is the same as the element type's alignment.
 *
 * @property elementType The type of elements in the array
 * @property dimensions The number of dimensions in the array
 */
data class ArrayType( // @formatter:off
    val elementType: Type,
    val dimensions: Int
) : Type, SymbolNameProvider { // @formatter:on
    companion object {
        val arrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.Array", "Array")
        val byteArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.ByteArray", "ByteArray")
        val shortArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.ShortArray", "ShortArray")
        val intArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.IntArray", "IntArray")
        val longArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.LongArray", "LongArray")
        val nIntArrayName: SymbolName = SymbolName("${ABIConstants.CTYPE_PACKAGE}.NIntArray", "NIntArray")
        val uByteArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.UByteArray", "UByteArray")
        val uShortArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.UShortArray", "UShortArray")
        val uIntArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.UIntArray", "UIntArray")
        val uLongArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.ULongArray", "ULongArray")
        val nUIntArrayName: SymbolName = SymbolName("${ABIConstants.CTYPE_PACKAGE}.NUIntArray", "NUIntArray")
        val floatArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.FloatArray", "FloatArray")
        val doubleArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.DoubleArray", "DoubleArray")
        val nFloatArrayName: SymbolName = SymbolName("${ABIConstants.CTYPE_PACKAGE}.NFloatArray", "NFloatArray")
        val charArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.CharArray", "CharArray")
        val booleanArrayName: SymbolName = SymbolName("${ABIConstants.KOTLIN_PACKAGE}.BooleanArray", "BooleanArray")
        val ptrArrayName: SymbolName = SymbolName("${ABIConstants.CTYPE_PACKAGE}.PtrArray", "PtrArray")

        /**
         * The kind byte that identifies an ArrayType during serialization/deserialization.
         */
        const val KIND: Byte = 1

        /**
         * Deserializes an [ArrayType] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [ArrayType]
         * @throws IllegalStateException if the type kind is not [KIND]
         */
        fun deserialize(buffer: Buffer): ArrayType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected array type kind ($KIND) while deserializing but got $kind" }
            return ArrayType(
                elementType = Type.deserialize(buffer), dimensions = buffer.readInt()
            )
        }
    }

    override val symbolName: SymbolName by lazy {
        when (elementType) {
            BuiltinType.BYTE -> byteArrayName
            BuiltinType.SHORT -> shortArrayName
            BuiltinType.INT -> intArrayName
            BuiltinType.LONG -> longArrayName
            BuiltinType.NINT -> nIntArrayName
            BuiltinType.UBYTE -> uByteArrayName
            BuiltinType.USHORT -> uShortArrayName
            BuiltinType.UINT -> uIntArrayName
            BuiltinType.ULONG -> uLongArrayName
            BuiltinType.NUINT -> nUIntArrayName
            BuiltinType.FLOAT -> floatArrayName
            BuiltinType.DOUBLE -> doubleArrayName
            BuiltinType.NFLOAT -> nFloatArrayName
            BuiltinType.PTR -> ptrArrayName
            BuiltinType.CHAR -> charArrayName
            BuiltinType.BOOL -> booleanArrayName
            else -> arrayName
        }
    }

    /**
     * The size of this array type in bytes, calculated as the element type's size multiplied by the number of dimensions.
     */
    override val size: Int by lazy { elementType.size * dimensions }

    /**
     * The alignment requirement of this array type in bytes, which is the same as the element type's alignment.
     */
    override val alignment: Int get() = elementType.alignment

    /**
     * The mangled name of this array type, used for ABI compatibility.
     *
     * The mangled name is constructed by prepending 'A' repeated [dimensions] times to the element type's mangled name,
     * and appending '$A' repeated [dimensions] times.
     */
    override val mangledName: String by lazy {
        "${"A\$".repeat(dimensions)}${elementType.mangledName}\$A"
    }

    /**
     * Serializes this array type to the given [buffer].
     *
     * The serialization format is:
     * 1. The kind byte ([KIND])
     * 2. The symbol name
     * 3. The element type
     * 4. The number of dimensions
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        elementType.serialize(buffer)
        buffer.writeInt(dimensions)
    }
}

/**
 * Extension function to convert any [Type] to an [ArrayType] with the specified number of dimensions.
 *
 * @param dimensions The number of dimensions for the array
 * @return A new [ArrayType] with this type as the element type and the specified number of dimensions
 */
fun Type.asArray(dimensions: Int): ArrayType = ArrayType(this, dimensions)