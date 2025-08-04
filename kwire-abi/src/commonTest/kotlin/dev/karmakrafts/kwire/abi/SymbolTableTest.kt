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

import dev.karmakrafts.kwire.abi.symbol.ClassSymbol
import dev.karmakrafts.kwire.abi.symbol.Symbol
import dev.karmakrafts.kwire.abi.symbol.SymbolInfo
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.symbol.SymbolTable
import dev.karmakrafts.kwire.abi.type.BuiltinType
import kotlinx.io.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SymbolTableTest {
    
    // Helper function to create a test symbol
    private fun createTestSymbol(id: Int, name: String): Symbol {
        val symbolName = SymbolName("dev.karmakrafts.kwire.abi.test.$name", name)
        val symbolInfo = SymbolInfo(symbolName, 1, 1, "TestFile.kt")
        return ClassSymbol(
            id = id,
            info = symbolInfo,
            originalInfo = null,
            typeArguments = listOf(BuiltinType.INT, BuiltinType.BOOL)
        )
    }
    
    // Helper function to create a test symbol table
    private fun createTestSymbolTable(symbolCount: Int = 3): SymbolTable {
        val symbols = (1..symbolCount).map { createTestSymbol(it, "TestSymbol$it") }
        return SymbolTable(symbols)
    }
    
    @Test
    fun `serialize and deserialize methods work correctly`() {
        val originalTable = createTestSymbolTable()
        
        val buffer = Buffer()
        originalTable.serialize(buffer)
        
        val deserializedTable = SymbolTable.deserialize(buffer)
        
        assertEquals(originalTable.entries.size, deserializedTable.entries.size)
        
        // Compare each symbol in the tables
        originalTable.entries.forEachIndexed { index, originalSymbol ->
            val deserializedSymbol = deserializedTable.entries[index]
            
            assertEquals(originalSymbol.id, deserializedSymbol.id)
            assertEquals(originalSymbol.info.name.fullName, deserializedSymbol.info.name.fullName)
            assertEquals(originalSymbol.info.name.shortName, deserializedSymbol.info.name.shortName)
            assertEquals(originalSymbol.info.line, deserializedSymbol.info.line)
            assertEquals(originalSymbol.info.column, deserializedSymbol.info.column)
            assertEquals(originalSymbol.info.file, deserializedSymbol.info.file)
            
            // Compare type arguments
            assertEquals(originalSymbol.typeArguments.size, deserializedSymbol.typeArguments.size)
            originalSymbol.typeArguments.forEachIndexed { typeIndex, originalType ->
                val deserializedType = deserializedSymbol.typeArguments[typeIndex]
                assertEquals(originalType.mangledName, deserializedType.mangledName)
                assertEquals(originalType.size, deserializedType.size)
                assertEquals(originalType.alignment, deserializedType.alignment)
            }
        }
    }
    
    @Test
    fun `serializeAndCompress and decompressAndDeserialize methods work correctly`() {
        val originalTable = createTestSymbolTable()
        
        val compressedBuffer = originalTable.serializeAndCompress()
        val deserializedTable = SymbolTable.decompressAndDeserialize(compressedBuffer)
        
        assertEquals(originalTable.entries.size, deserializedTable.entries.size)
        
        // Compare each symbol in the tables
        originalTable.entries.forEachIndexed { index, originalSymbol ->
            val deserializedSymbol = deserializedTable.entries[index]
            assertEquals(originalSymbol.id, deserializedSymbol.id)
            assertEquals(originalSymbol.info.name.fullName, deserializedSymbol.info.name.fullName)
        }
    }
    
    // Note: We're not testing the ByteArray version of decompressAndDeserialize separately
    // since it internally just creates a Buffer and calls the Buffer version,
    // which we're already testing in the test above.
    
    @Test
    fun `plus operator combines symbol tables correctly`() {
        val table1 = createTestSymbolTable(2)
        val table2 = createTestSymbolTable(3)
        
        val combinedTable = table1 + table2
        
        assertEquals(table1.entries.size + table2.entries.size, combinedTable.entries.size)
        
        // Check that all symbols from both tables are present
        table1.entries.forEach { symbol1 ->
            assertTrue(combinedTable.entries.any { it.id == symbol1.id })
        }
        
        table2.entries.forEach { symbol2 ->
            assertTrue(combinedTable.entries.any { it.id == symbol2.id })
        }
    }
    
    @Test
    fun `empty symbol table serialization and deserialization works correctly`() {
        val emptyTable = SymbolTable(emptyList())
        
        val buffer = Buffer()
        emptyTable.serialize(buffer)
        
        val deserializedTable = SymbolTable.deserialize(buffer)
        
        assertTrue(deserializedTable.entries.isEmpty())
    }
    
    @Test
    fun `large symbol table serialization and deserialization works correctly`() {
        val largeTable = createTestSymbolTable(100)
        
        val buffer = Buffer()
        largeTable.serialize(buffer)
        
        val deserializedTable = SymbolTable.deserialize(buffer)
        
        assertEquals(largeTable.entries.size, deserializedTable.entries.size)
        
        // Check a few random symbols
        listOf(1, 25, 50, 75, 100).forEach { index ->
            val originalSymbol = largeTable.entries[index - 1]
            val deserializedSymbol = deserializedTable.entries[index - 1]
            
            assertEquals(originalSymbol.id, deserializedSymbol.id)
            assertEquals(originalSymbol.info.name.fullName, deserializedSymbol.info.name.fullName)
        }
    }
}