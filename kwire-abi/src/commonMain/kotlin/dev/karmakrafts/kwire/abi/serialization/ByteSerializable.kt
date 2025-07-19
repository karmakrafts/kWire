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

package dev.karmakrafts.kwire.abi.serialization

import dev.karmakrafts.kwire.abi.symbol.Symbol
import dev.karmakrafts.kwire.abi.type.ArrayType
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.ConeType
import dev.karmakrafts.kwire.abi.type.ReferenceType
import dev.karmakrafts.kwire.abi.type.StructType
import dev.karmakrafts.kwire.abi.type.Type
import dev.karmakrafts.kwire.abi.type.TypeArgument
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

interface ByteSerializable {
    companion object {
        @PublishedApi
        internal val serializer: Json = Json {
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                polymorphic(Symbol::class) {
                    subclass(Symbol.Function::class)
                    subclass(Symbol.Class::class)
                }
                polymorphic(Type::class) {
                    subclass(BuiltinType::class)
                    subclass(ReferenceType::class)
                    subclass(StructType::class)
                    subclass(ConeType::class)
                    subclass(ArrayType::class)
                }
                polymorphic(TypeArgument::class) {
                    subclass(Type::class)
                    subclass(TypeArgument.Star::class)
                }
            }
        }

        inline fun <reified T : ByteSerializable> deserialize(data: ByteArray): T =
            serializer.decodeFromString(data.decodeToString())

        inline fun <reified T : ByteSerializable> decompressAndDeserialize(data: ByteArray): T = deserialize(inflate(data))
    }

    fun serialize(): ByteArray = serializer.encodeToString(this).encodeToByteArray()

    fun serializeAndCompress(): ByteArray = deflate(serialize())
}