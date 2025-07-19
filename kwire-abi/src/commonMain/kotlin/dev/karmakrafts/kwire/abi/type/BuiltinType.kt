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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("builtin")
enum class BuiltinType(
    override val symbolName: SymbolName,
    override val mangledName: String,
    override val size: Int,
    override val alignment: Int
) : Type {
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
        val voidPtr: ConeType = ptrOf(VOID)
        val starPtr: ConeType = ptrOf(TypeArgument.Star)

        fun ptrOf(type: TypeArgument): ConeType = ConeType(PTR, listOf(type))
    }

    constructor(
        isBuiltIn: Boolean,
        symbolName: String,
        mangledName: String,
        size: Int
    ) : this(SymbolName(
        if(isBuiltIn) "${ABIConstants.KOTLIN_PACKAGE}.$symbolName"
        else "${ABIConstants.CTYPE_PACKAGE}.$symbolName"
    ), mangledName, size, size)
}