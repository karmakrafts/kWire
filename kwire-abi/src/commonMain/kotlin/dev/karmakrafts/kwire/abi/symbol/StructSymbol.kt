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

class StructSymbol(
    override val id: Int,
    override val info: SymbolInfo,
    override val originalInfo: SymbolInfo?,
    override val typeArguments: List<Type>,
    val fields: List<Type>
) : Symbol {
    companion object {
        /**
         * The kind identifier for function symbols.
         */
        const val KIND: Byte = 2

        /**
         * Deserializes a StructSymbol from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized ClassSymbol
         * @throws IllegalStateException if the symbol kind is not a class symbol
         */
        fun deserialize(buffer: Buffer): StructSymbol {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected class symbol kind ($KIND) while deserializing but got $kind" }
            return StructSymbol(
                id = buffer.readInt(),
                info = SymbolInfo.deserialize(buffer),
                originalInfo = buffer.readOptional(SymbolInfo::deserialize),
                typeArguments = buffer.readList(Type::deserialize),
                fields = buffer.readList(Type::deserialize)
            )
        }
    }

    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        buffer.writeInt(id)
        info.serialize(buffer)
        buffer.writeOptional(originalInfo, SymbolInfo::serialize)
        buffer.writeList(typeArguments, Type::serialize)
        buffer.writeList(fields, Type::serialize)
    }
}