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

import dev.karmakrafts.kwire.abi.ABIConstants
import dev.karmakrafts.kwire.abi.serialization.BinaryDeserializer
import dev.karmakrafts.kwire.abi.serialization.readList
import dev.karmakrafts.kwire.abi.serialization.readOptional
import dev.karmakrafts.kwire.abi.serialization.writeList
import dev.karmakrafts.kwire.abi.serialization.writeOptional
import dev.karmakrafts.kwire.abi.type.Type
import kotlinx.io.Buffer

/**
 * Represents a function symbol in the ABI.
 *
 * A function symbol contains information about a function definition, including its
 * name, location in the source code, type arguments, return type, and parameter types.
 *
 * @property id Unique identifier for this symbol
 * @property info Information about this symbol, including its name and location
 * @property originalInfo Original information about this symbol, if it was derived from another symbol
 * @property typeArguments List of type arguments associated with this function
 * @property returnType The return type of the function
 * @property parameterTypes List of parameter types for the function
 */
data class FunctionSymbol(
    override val id: Int,
    override val info: SymbolInfo,
    override val originalInfo: SymbolInfo?,
    override val typeArguments: List<Type>,
    val returnType: Type,
    val parameterTypes: List<Type>,
    val dispatchReceiverType: Type?,
    val extensionReceiverType: Type?,
    val contextReceiverTypes: List<Type>
) : Symbol {
    companion object : BinaryDeserializer<FunctionSymbol> {
        const val VERSION: Byte = 1

        /**
         * Deserializes a FunctionSymbol from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized FunctionSymbol
         * @throws IllegalStateException if the symbol kind is not a function symbol
         */
        override fun deserialize(buffer: Buffer): FunctionSymbol {
            val kind = buffer.readByte()
            check(kind == ABIConstants.SYMBOL_KIND_FUNCTION) { "Expected function symbol kind (${ABIConstants.SYMBOL_KIND_FUNCTION}) while deserializing but got $kind" }
            val version = buffer.readByte()
            check(version <= VERSION) { "Expected version $VERSION function symbol but got version $version" }
            return FunctionSymbol(
                id = buffer.readInt(),
                info = SymbolInfo.deserialize(buffer),
                originalInfo = buffer.readOptional(SymbolInfo::deserialize),
                typeArguments = buffer.readList(Type::deserialize),
                returnType = Type.deserialize(buffer),
                parameterTypes = buffer.readList(Type::deserialize),
                dispatchReceiverType = buffer.readOptional(Type::deserialize),
                extensionReceiverType = buffer.readOptional(Type::deserialize),
                contextReceiverTypes = buffer.readList(Type::deserialize)
            )
        }
    }

    /**
     * Serializes this FunctionSymbol to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(ABIConstants.SYMBOL_KIND_FUNCTION)
        buffer.writeByte(VERSION)
        buffer.writeInt(id)
        info.serialize(buffer)
        buffer.writeOptional(originalInfo, SymbolInfo::serialize)
        buffer.writeList(typeArguments, Type::serialize)
        returnType.serialize(buffer)
        buffer.writeList(parameterTypes, Type::serialize)
        buffer.writeOptional(dispatchReceiverType, Type::serialize)
        buffer.writeOptional(extensionReceiverType, Type::serialize)
        buffer.writeList(contextReceiverTypes, Type::serialize)
    }
}