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

package dev.karmakrafts.kwire

import dev.karmakrafts.kwire.abi.symbol.SymbolTable

@KWireCompilerApi
interface ModuleData {
    companion object {
        /**
         * Allows accessing the current modules [ModuleData] that is
         * created by the kWire compiler.
         * This allows retrieving dependency information and symbol
         * table data among other things.
         *
         * @return The current modules [ModuleData] instance.
         *  The returned instance is initialized lazily.
         */
        @KWireIntrinsic(KWireIntrinsic.Type.ABI_GET_MODULE_DATA)
        fun get(): ModuleData = throw KWirePluginNotAppliedException()
    }

    @KWireCompilerApi
    val name: String

    @KWireCompilerApi
    val dependencies: List<ModuleData>

    @KWireCompilerApi
    val symbolTableData: ByteArray

    /**
     * Decompress and parse a [SymbolTable] instance from the
     * [ModuleData.symbolTableData] in the given module data instance.
     */
    fun loadSymbolTable(): SymbolTable = SymbolTable.decompressAndDeserialize(symbolTableData)

    /**
     * Recursively decompress and parse all symbol tables from
     * the given [ModuleData] instance, including the extension
     * receiver of this function and all dependencies of the module tree flattened.
     */
    fun loadAllSymbolTables(): SymbolTable {
        var symbolTable = loadSymbolTable()
        val queue = ArrayDeque<ModuleData>()
        queue += dependencies
        while (queue.isNotEmpty()) {
            val module = queue.removeFirst()
            symbolTable = symbolTable + module.loadSymbolTable()
            queue += module.dependencies
        }
        return symbolTable
    }
}