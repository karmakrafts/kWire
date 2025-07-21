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

import dev.karmakrafts.kwire.abi.serialization.deflate
import dev.karmakrafts.kwire.abi.serialization.inflate
import kotlinx.io.Buffer

/**
 * A collection of symbols that can be serialized and deserialized.
 *
 * The symbol table stores a list of symbols and provides methods for serialization,
 * deserialization, and compression of the data.
 *
 * @property entries The list of symbols in this table
 */
@ConsistentCopyVisibility
data class SymbolTable internal constructor(
    val entries: List<Symbol>
) {
    companion object {
        /**
         * Deserializes a SymbolTable from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized SymbolTable
         */
        fun deserialize(buffer: Buffer): SymbolTable {
            return SymbolTable((0..<buffer.readInt()).map { Symbol.deserialize(buffer) })
        }

        /**
         * Decompresses and deserializes a SymbolTable from the given buffer.
         *
         * @param buffer The compressed buffer to read from
         * @return The deserialized SymbolTable
         */
        fun decompressAndDeserialize(buffer: Buffer): SymbolTable = deserialize(inflate(buffer))

        /**
         * Decompresses and deserializes a SymbolTable from the given byte array.
         *
         * @param data The compressed byte array to read from
         * @return The deserialized SymbolTable
         */
        fun decompressAndDeserialize(data: ByteArray): SymbolTable {
            val buffer = Buffer()
            buffer.write(data)
            return decompressAndDeserialize(buffer)
        }
    }

    /**
     * Combines this symbol table with another symbol table.
     *
     * @param other The other symbol table to combine with
     * @return A new symbol table containing entries from both tables
     */
    operator fun plus(other: SymbolTable): SymbolTable = SymbolTable(entries + other.entries)

    /**
     * Serializes this SymbolTable to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    fun serialize(buffer: Buffer) {
        buffer.writeInt(entries.size)
        for (entry in entries) {
            entry.serialize(buffer)
        }
    }

    /**
     * Serializes and compresses this SymbolTable.
     *
     * @return A buffer containing the compressed serialized data
     */
    fun serializeAndCompress(): Buffer {
        val buffer = Buffer()
        serialize(buffer)
        return deflate(buffer)
    }
}