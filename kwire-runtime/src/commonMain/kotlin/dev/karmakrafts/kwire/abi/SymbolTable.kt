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

package dev.karmakrafts.kwire.abi

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal expect fun inflate(data: ByteArray): ByteArray

@ConsistentCopyVisibility
@Serializable
data class SymbolTable internal constructor(
    val entries: List<SymbolTableEntry>
) {
    companion object {
        private val serializer: Json = Json {
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                polymorphic(SymbolTableEntry::class) {
                    subclass(SymbolTableEntry.Function::class)
                    subclass(SymbolTableEntry.Class::class)
                }
            }
        }

        fun decompress(data: ByteArray): SymbolTable = serializer.decodeFromString(inflate(data).decodeToString())
    }

    operator fun plus(other: SymbolTable): SymbolTable = SymbolTable(entries + other.entries)
}

/**
 * Decompress and parse a [SymbolTable] instance from the
 * [ModuleData.symbolTableData] in the given module data instance.
 */
fun ModuleData.loadSymbolTable(): SymbolTable = SymbolTable.decompress(symbolTableData)

/**
 * Recursively decompress and parse all symbol tables from
 * the given [ModuleData] instance, including the extension
 * receiver of this function and all dependencies of the module tree flattened.
 */
fun ModuleData.loadAllSymbolTables(): SymbolTable {
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