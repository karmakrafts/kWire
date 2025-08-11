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
import dev.karmakrafts.kwire.abi.serialization.BinarySerializable
import dev.karmakrafts.kwire.abi.serialization.PolymorphicBinaryDeserializer
import dev.karmakrafts.kwire.abi.serialization.deflate
import dev.karmakrafts.kwire.abi.serialization.inflate
import dev.karmakrafts.kwire.abi.symbol.SymbolNameProvider
import kotlinx.io.Buffer
import kotlinx.io.Source

/**
 * Base interface for all types in the ABI system.
 *
 * This interface defines the common properties and methods that all types must implement,
 * including serialization and deserialization capabilities.
 */
sealed interface Type : SymbolNameProvider, BinarySerializable {
    companion object : PolymorphicBinaryDeserializer<Type, Byte>( // @formatter:off
        Source::readByte,
        mapOf(
            ABIConstants.TYPE_KIND_BUILTIN to BuiltinType,
            ABIConstants.TYPE_KIND_ARRAY to ArrayType,
            ABIConstants.TYPE_KIND_STRUCT to StructType,
            ABIConstants.TYPE_KIND_REFERENCE to ReferenceType,
            ABIConstants.TYPE_KIND_CONE to ConeType,
            ABIConstants.TYPE_KIND_NULLABLE to NullableType
        )
    ) { // @formatter:on
        /**
         * Decompresses and deserializes a [Type] from the given [buffer].
         *
         * @param buffer The compressed buffer to read from
         * @return The deserialized [Type]
         */
        fun decompressAndDeserialize(buffer: Buffer): Type = deserialize(inflate(buffer))

        /**
         * Decompresses and deserializes a [Type] from the given byte array.
         *
         * @param data The compressed byte array to read from
         * @return The deserialized [Type]
         */
        fun decompressAndDeserialize(data: ByteArray): Type {
            val buffer = Buffer()
            buffer.write(data)
            return deserialize(inflate(buffer))
        }
    }

    /**
     * The mangled name of this type, used for ABI compatibility.
     */
    val mangledName: String

    /**
     * The size of this type in bytes.
     */
    val size: Int

    /**
     * The alignment requirement of this type in bytes.
     */
    val alignment: Int

    /**
     * Serializes and compresses this type.
     *
     * @return A buffer containing the compressed serialized type
     */
    fun serializeAndCompress(): Buffer {
        val buffer = Buffer()
        serialize(buffer)
        return deflate(buffer)
    }
}