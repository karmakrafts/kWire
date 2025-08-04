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

package dev.karmakrafts.kwire.abi.symbol

import dev.karmakrafts.kwire.abi.serialization.readList
import dev.karmakrafts.kwire.abi.serialization.readOptional
import dev.karmakrafts.kwire.abi.serialization.writeList
import dev.karmakrafts.kwire.abi.serialization.writeOptional
import dev.karmakrafts.kwire.abi.type.Type
import kotlinx.io.Buffer

/**
 * Represents a struct symbol in the ABI.
 *
 * A struct symbol contains information about a struct definition, including its
 * name, location in the source code, type arguments, and field types.
 *
 * @property id Unique identifier for this symbol
 * @property info Information about this symbol, including its name and location
 * @property originalInfo Original information about this symbol, if it was derived from another symbol
 * @property typeArguments List of type arguments associated with this struct
 * @property fields List of types representing the fields of this struct
 */
class StructSymbol(
    override val id: Int,
    override val info: SymbolInfo,
    override val originalInfo: SymbolInfo?,
    override val typeArguments: List<Type>,
    val fields: List<Type>
) : Symbol {
    companion object {
        /**
         * The kind identifier for struct symbols.
         */
        const val KIND: Byte = 2

        /**
         * Deserializes a StructSymbol from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized StructSymbol
         * @throws IllegalStateException if the symbol kind is not a struct symbol
         */
        fun deserialize(buffer: Buffer): StructSymbol {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected struct symbol kind ($KIND) while deserializing but got $kind" }
            return StructSymbol(
                id = buffer.readInt(),
                info = SymbolInfo.deserialize(buffer),
                originalInfo = buffer.readOptional(SymbolInfo::deserialize),
                typeArguments = buffer.readList(Type::deserialize),
                fields = buffer.readList(Type::deserialize)
            )
        }
    }

    /**
     * Serializes this StructSymbol to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        buffer.writeInt(id)
        info.serialize(buffer)
        buffer.writeOptional(originalInfo, SymbolInfo::serialize)
        buffer.writeList(typeArguments, Type::serialize)
        buffer.writeList(fields, Type::serialize)
    }
}