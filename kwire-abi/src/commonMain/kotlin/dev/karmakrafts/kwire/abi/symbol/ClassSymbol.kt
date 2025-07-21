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

import dev.karmakrafts.kwire.abi.type.Type
import kotlinx.io.Buffer

/**
 * Represents a class symbol in the ABI.
 *
 * A class symbol contains information about a class definition, including its
 * name, location in the source code, and type arguments.
 *
 * @property id Unique identifier for this symbol
 * @property info Information about this symbol, including its name and location
 * @property originalInfo Original information about this symbol, if it was derived from another symbol
 * @property typeArguments List of type arguments associated with this class
 */
data class ClassSymbol(
    override val id: Int,
    override val info: SymbolInfo,
    override val originalInfo: SymbolInfo?,
    override val typeArguments: List<Type>
) : Symbol {
    companion object {
        /**
         * The kind identifier for class symbols.
         */
        const val KIND: Byte = 1

        /**
         * Deserializes a ClassSymbol from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized ClassSymbol
         * @throws IllegalStateException if the symbol kind is not a class symbol
         */
        fun deserialize(buffer: Buffer): ClassSymbol {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected class symbol kind ($KIND) while deserializing but got $kind" }
            return ClassSymbol(
                id = buffer.readInt(),
                info = SymbolInfo.deserialize(buffer),
                originalInfo = if (buffer.readByte() == 1.toByte()) SymbolInfo.deserialize(buffer) else null,
                typeArguments = (0..<buffer.readInt()).map { Type.deserialize(buffer) })
        }
    }

    /**
     * Serializes this ClassSymbol to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        buffer.writeInt(id)
        info.serialize(buffer)
        buffer.writeByte(if (originalInfo != null) 1 else 0)
        originalInfo?.serialize(buffer)
        buffer.writeInt(typeArguments.size)
        for (type in typeArguments) {
            type.serialize(buffer)
        }
    }
}