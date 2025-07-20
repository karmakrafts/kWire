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

import dev.karmakrafts.kwire.abi.serialization.deflate
import dev.karmakrafts.kwire.abi.serialization.inflate
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import kotlinx.io.Buffer

sealed interface Type {
    companion object {
        fun deserialize(buffer: Buffer): Type {
            val kind = buffer.peek().readByte()
            return when (kind) {
                BuiltinType.KIND -> BuiltinType.deserialize(buffer)
                ArrayType.KIND -> ArrayType.deserialize(buffer)
                StructType.KIND -> StructType.deserialize(buffer)
                ReferenceType.KIND -> ReferenceType.deserialize(buffer)
                ConeType.KIND -> ConeType.deserialize(buffer)
                else -> error("Unknown ABI type kind")
            }
        }

        fun decompressAndDeserialize(buffer: Buffer): Type = deserialize(inflate(buffer))

        fun decompressAndDeserialize(data: ByteArray): Type {
            val buffer = Buffer()
            buffer.write(data)
            return deserialize(inflate(buffer))
        }
    }

    val symbolName: SymbolName
    val mangledName: String
    val size: Int
    val alignment: Int

    fun serialize(buffer: Buffer)

    fun serializeAndCompress(): Buffer {
        val buffer = Buffer()
        serialize(buffer)
        return deflate(buffer)
    }
}