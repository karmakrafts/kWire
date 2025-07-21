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
 * Base interface for all symbols in the ABI.
 * 
 * Symbols represent named entities in the code such as functions and classes.
 * Each symbol has a unique identifier, information about its location in the source code,
 * and may have type arguments associated with it.
 */
sealed interface Symbol {
    companion object {
        /**
         * Deserializes a symbol from the given buffer.
         *
         * The method reads the first byte to determine the symbol kind and then
         * delegates to the appropriate implementation's deserialize method.
         *
         * @param buffer The buffer to read from
         * @return The deserialized symbol
         * @throws IllegalStateException if the symbol kind is unknown
         */
        fun deserialize(buffer: Buffer): Symbol {
            val kind = buffer.peek().readByte() // Peek at the first byte to figure out symbol kind
            return when (kind) {
                FunctionSymbol.KIND -> FunctionSymbol.deserialize(buffer)
                ClassSymbol.KIND -> ClassSymbol.deserialize(buffer)
                else -> error("Unknown ABI symbol kind")
            }
        }
    }

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

    /**
     * Serializes this symbol to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    fun serialize(buffer: Buffer)
}