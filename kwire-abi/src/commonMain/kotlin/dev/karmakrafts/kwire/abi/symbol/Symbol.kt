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
import dev.karmakrafts.kwire.abi.serialization.BinarySerializable
import dev.karmakrafts.kwire.abi.serialization.PolymorphicBinaryDeserializer
import dev.karmakrafts.kwire.abi.type.Type
import kotlinx.io.Source

/**
 * Base interface for all symbols in the ABI.
 *
 * Symbols represent named entities in the code such as functions and classes.
 * Each symbol has a unique identifier, information about its location in the source code,
 * and may have type arguments associated with it.
 */
sealed interface Symbol : BinarySerializable {
    companion object : PolymorphicBinaryDeserializer<Symbol, Byte>( // @formatter:off
        Source::readByte,
        mapOf(
            ABIConstants.SYMBOL_KIND_CLASS to ClassSymbol,
            ABIConstants.SYMBOL_KIND_STRUCT to StructSymbol,
            ABIConstants.SYMBOL_KIND_FUNCTION to FunctionSymbol
        )
    ) // @formatter:on

    /**
     * Unique identifier for this symbol.
     */
    val id: Int

    /**
     * Information about this symbol, including its name and location in the source code.
     */
    val info: SymbolInfo

    /**
     * Original information about this symbol, if it was derived from another symbol.
     * This is null for symbols that are not derived.
     */
    val originalInfo: SymbolInfo?

    /**
     * List of type arguments associated with this symbol.
     */
    val typeArguments: List<Type>
}