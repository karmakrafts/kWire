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

import dev.karmakrafts.kwire.abi.ABI
import dev.karmakrafts.kwire.abi.ABIConstants
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.symbol.SymbolNameProvider
import dev.karmakrafts.kwire.abi.type.BuiltinType.Companion.KIND
import kotlinx.io.Buffer

/**
 * Represents built-in primitive types in the ABI system.
 *
 * This enum defines all the primitive types supported by the ABI, including:
 * - Void/Unit type
 * - Signed integer types (BYTE, SHORT, INT, LONG, NINT)
 * - Unsigned integer types (UBYTE, USHORT, UINT, ULONG, NUINT)
 * - Floating point types (FLOAT, DOUBLE, NFLOAT)
 * - Other types (BOOL, CHAR, PTR)
 *
 * Each built-in type has a symbol name, mangled name, size, and alignment.
 *
 * @property symbolName The name of the symbol associated with this built-in type
 * @property mangledName The mangled name of this built-in type, used for ABI compatibility
 * @property size The size of this built-in type in bytes
 * @property alignment The alignment requirement of this built-in type in bytes
 */
enum class BuiltinType(
    override val symbolName: SymbolName,
    override val mangledName: String,
    override val size: Int,
    override val alignment: Int
) : Type, SymbolNameProvider {
    // @formatter:off
    VOID    (true,  "Unit",    "a", 0),

    BYTE    (true,  "Byte",    "b", Byte.SIZE_BYTES),
    SHORT   (true,  "Short",   "c", Short.SIZE_BYTES),
    INT     (true,  "Int",     "d", Int.SIZE_BYTES),
    LONG    (true,  "Long",    "e", Long.SIZE_BYTES),
    NINT    (false, "NInt",    "f", ABI.pointerSize),

    UBYTE   (true,  "UByte",   "g", UByte.SIZE_BYTES),
    USHORT  (true,  "UShort",  "h", UShort.SIZE_BYTES),
    UINT    (true,  "UInt",    "i", UInt.SIZE_BYTES),
    ULONG   (true,  "ULong",   "j", ULong.SIZE_BYTES),
    NUINT   (false, "NUInt",   "k", ABI.pointerSize),

    FLOAT   (true,  "Float",   "l", Float.SIZE_BYTES),
    DOUBLE  (true,  "Double",  "m", Double.SIZE_BYTES),
    NFLOAT  (false, "NFloat",  "n", ABI.pointerSize),

    BOOL    (true,  "Boolean", "o", ABI.booleanSize),
    CHAR    (true,  "Char",    "p", Char.SIZE_BYTES),
    PTR     (false, "Ptr",     "q", ABI.pointerSize)
    // @formatter:on
    ;

    companion object {
        /**
         * The kind byte that identifies a BuiltinType during serialization/deserialization.
         */
        const val KIND: Byte = 0

        /**
         * Deserializes a [BuiltinType] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [BuiltinType]
         * @throws IllegalStateException if the type kind is not [KIND]
         */
        fun deserialize(buffer: Buffer): BuiltinType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected builtin type kind ($KIND) while deserializing but got $kind" }
            return BuiltinType.entries[buffer.readByte().toInt()]
        }
    }

    /**
     * Secondary constructor for creating a BuiltinType with a simple string symbol name.
     *
     * @param isBuiltIn Whether this type is a built-in Kotlin type (true) or a C type (false)
     * @param symbolName The simple name of the symbol
     * @param mangledName The mangled name of this built-in type
     * @param size The size of this built-in type in bytes (also used as alignment)
     */
    constructor( // @formatter:off
        isBuiltIn: Boolean,
        symbolName: String,
        mangledName: String,
        size: Int
    ) : this( // @formatter:on
        SymbolName(
            if (isBuiltIn) "${ABIConstants.KOTLIN_PACKAGE}.$symbolName"
            else "${ABIConstants.CTYPE_PACKAGE}.$symbolName", symbolName
        ), mangledName, size, size
    )

    /**
     * Serializes this built-in type to the given [buffer].
     *
     * The serialization format is:
     * 1. The kind byte ([KIND])
     * 2. The ordinal of this enum constant as a byte
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        buffer.writeByte(ordinal.toByte())
    }
}

/**
 * Extension function to check if a [Type] is a pointer type.
 *
 * @return `true` if this type is a pointer type, `false` otherwise
 */
fun Type.isPtr(): Boolean = when (this) {
    BuiltinType.PTR -> true
    is ConeType -> genericType.isPtr()
    else -> false
}