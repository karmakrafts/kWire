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

import dev.karmakrafts.kwire.abi.demangler.StructResolver
import dev.karmakrafts.kwire.abi.symbol.StructSymbol
import dev.karmakrafts.kwire.abi.symbol.SymbolTable

/**
 * Boilerplate for compiler generated module data to reduce
 * code generation logic complexity.
 */
@KWireCompilerApi
abstract class AbstractModuleData : ModuleData {
    val symbolTable: SymbolTable by lazy { loadAllSymbolTables() }

    /**
     * A struct resolver which uses a [symbolTable] lookup into the
     * current modules symbol table to determine structure fields.
     * This may be used when demangling signatures using [dev.karmakrafts.kwire.abi.demangler.Demangler.demangle]
     * or similar functions.
     */
    val structResolver: StructResolver = { symbolName ->
        symbolTable.findSymbol<StructSymbol> {
            val originalInfo = it.originalInfo
            if (originalInfo != null && originalInfo.name == symbolName) {
                return@findSymbol true
            }
            return@findSymbol it.info.name == symbolName
        }?.fields ?: emptyList()
    }
}