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
    private val mangledName: String,
    override val size: Int,
    override val alignment: Int
) : Type {
    // @formatter:off
    BYTE    (true,  "Byte",    "a", Byte.SIZE_BYTES),
    SHORT   (true,  "Short",   "b", Short.SIZE_BYTES),
    INT     (true,  "Int",     "c", Int.SIZE_BYTES),
    LONG    (true,  "Long",    "d", Long.SIZE_BYTES),
    NINT    (false, "NInt",    "e", ABI.pointerSize),

    UBYTE   (true,  "UByte",   "f", UByte.SIZE_BYTES),
    USHORT  (true,  "UShort",  "g", UShort.SIZE_BYTES),
    UINT    (true,  "UInt",    "h", UInt.SIZE_BYTES),
    ULONG   (true,  "ULong",   "i", ULong.SIZE_BYTES),
    NUINT   (false, "NUInt",   "j", ABI.pointerSize),

    FLOAT   (true,  "Float",   "k", Float.SIZE_BYTES),
    DOUBLE  (true,  "Double",  "l", Double.SIZE_BYTES),
    NFLOAT  (false, "NFloat",  "m", ABI.pointerSize),

    BOOL    (true,  "Boolean", "n", ABI.booleanSize),
    CHAR    (true,  "Char",    "o", Char.SIZE_BYTES),
    PTR     (false, "Ptr",     "p", ABI.pointerSize)
    // @formatter:on
    ;

    constructor(
        isBuiltIn: Boolean,
        symbolName: String,
        mangledName: String,
        size: Int
    ) : this(SymbolName(
        if(isBuiltIn) "${ABIConstants.KOTLIN_PACKAGE}.$symbolName"
        else "${ABIConstants.CTYPE_PACKAGE}.$symbolName"
    ), mangledName, size, size)

    override fun getMangledName(): String = mangledName
}