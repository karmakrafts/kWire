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

@ConsistentCopyVisibility
data class SymbolTable internal constructor(
    val entries: List<Symbol>
) {
    companion object {
        fun deserialize(buffer: Buffer): SymbolTable {
            return SymbolTable((0..<buffer.readInt()).map { Symbol.deserialize(buffer) })
        }

        fun decompressAndDeserialize(buffer: Buffer): SymbolTable = deserialize(inflate(buffer))

        fun decompressAndDeserialize(data: ByteArray): SymbolTable {
            val buffer = Buffer()
            buffer.write(data)
            return decompressAndDeserialize(buffer)
        }
    }

    operator fun plus(other: SymbolTable): SymbolTable = SymbolTable(entries + other.entries)

    fun serialize(buffer: Buffer) {
        buffer.writeInt(entries.size)
        for (entry in entries) {
            entry.serialize(buffer)
        }
    }

    fun serializeAndCompress(): Buffer {
        val buffer = Buffer()
        serialize(buffer)
        return deflate(buffer)
    }
}