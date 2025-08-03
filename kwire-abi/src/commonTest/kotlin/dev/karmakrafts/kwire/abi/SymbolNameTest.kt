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

import dev.karmakrafts.kwire.abi.symbol.SymbolName
import kotlinx.io.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SymbolNameTest {
    @Test
    fun `packageName property returns correct value`() {
        val fullName = "dev.karmakrafts.kwire.abi.symbol.SymbolName"
        val shortName = "SymbolName"
        val symbolName = SymbolName(fullName, shortName)

        val expectedPackageName = "dev.karmakrafts.kwire.abi.symbol"
        assertEquals(expectedPackageName, symbolName.packageName)
    }

    @Test
    fun `segments method returns correct segments`() {
        val fullName = "dev.karmakrafts.kwire.abi.symbol.SymbolName"
        val shortName = "SymbolName"
        val symbolName = SymbolName(fullName, shortName)

        val expectedSegments = listOf("dev", "karmakrafts", "kwire", "abi", "symbol", "SymbolName")
        assertEquals(expectedSegments, symbolName.segments())
    }

    @Test
    fun `packageSegments method returns correct segments`() {
        val fullName = "dev.karmakrafts.kwire.abi.symbol.SymbolName"
        val shortName = "SymbolName"
        val symbolName = SymbolName(fullName, shortName)

        val expectedSegments = listOf("dev", "karmakrafts", "kwire", "abi", "symbol")
        assertEquals(expectedSegments, symbolName.packageSegments())
    }

    @Test
    fun `nameSegments method returns correct segments`() {
        val fullName = "dev.karmakrafts.kwire.abi.symbol.Outer.Inner"
        val shortName = "Outer.Inner"
        val symbolName = SymbolName(fullName, shortName)

        val expectedSegments = listOf("Outer", "Inner")
        assertEquals(expectedSegments, symbolName.nameSegments())
    }

    @Test
    fun `serialize and deserialize methods work correctly`() {
        val fullName = "dev.karmakrafts.kwire.abi.symbol.SymbolName"
        val shortName = "SymbolName"
        val originalSymbolName = SymbolName(fullName, shortName)

        val buffer = Buffer()
        originalSymbolName.serialize(buffer)

        val deserializedSymbolName = SymbolName.deserialize(buffer)

        assertEquals(originalSymbolName, deserializedSymbolName)
        assertEquals(originalSymbolName.fullName, deserializedSymbolName.fullName)
        assertEquals(originalSymbolName.shortName, deserializedSymbolName.shortName)
    }

    @Test
    fun `equals and hashCode work correctly`() {
        val symbolName1 = SymbolName("dev.karmakrafts.kwire.abi.symbol.SymbolName", "SymbolName")
        val symbolName2 = SymbolName("dev.karmakrafts.kwire.abi.symbol.SymbolName", "SymbolName")
        val symbolName3 = SymbolName("dev.karmakrafts.kwire.abi.symbol.OtherSymbol", "OtherSymbol")

        assertEquals(symbolName1, symbolName2)
        assertEquals(symbolName1.hashCode(), symbolName2.hashCode())

        assertNotEquals(symbolName1, symbolName3)
        assertNotEquals(symbolName1.hashCode(), symbolName3.hashCode())
    }
}