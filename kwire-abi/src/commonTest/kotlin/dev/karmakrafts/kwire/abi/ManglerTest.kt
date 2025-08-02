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

import dev.karmakrafts.kwire.abi.mangler.Mangler
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.ReferenceType
import dev.karmakrafts.kwire.abi.type.StructType
import dev.karmakrafts.kwire.abi.type.Type
import dev.karmakrafts.kwire.abi.type.asArray
import dev.karmakrafts.kwire.abi.type.asNullable
import kotlin.test.Test
import kotlin.test.assertEquals

class ManglerTest {
    @Test
    fun `mangle method returns correct mangled name for all BuiltinTypes`() {
        // Test all possible BuiltinType values
        for (builtinType in BuiltinType.entries) {
            val expectedMangledName = builtinType.mangledName
            
            val mangledName = Mangler.mangle(builtinType)
            
            assertEquals(expectedMangledName, mangledName, "Failed for BuiltinType.${builtinType.name}")
        }
    }
    
    @Test
    fun `mangle method returns correct mangled name for ArrayType`() {
        val elementType = BuiltinType.INT
        val arrayType = elementType.asArray(2)
        val expectedMangledName = "A\$A\$d\$A"
        
        val mangledName = Mangler.mangle(arrayType)
        
        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangle method returns correct mangled name for NullableType`() {
        val elementType = BuiltinType.INT
        val arrayType = elementType.asArray(2).asNullable()
        val expectedMangledName = "A\$A\$d\$AN"

        val mangledName = Mangler.mangle(arrayType)

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangle method returns correct mangled name for ReferenceType`() {
        val elementType = ReferenceType(SymbolName("dog.Woof", "Woof"))
        val arrayType = elementType.asArray(2)
        val expectedMangledName = "A\$A\$C\$dog_Woof\$C\$A"

        val mangledName = Mangler.mangle(arrayType)

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangle method returns correct mangled name for StructType`() {
        val elementType = StructType(SymbolName("fox.Ahhh", "Ahhh"), listOf(BuiltinType.INT))
        val arrayType = elementType.asArray(2)
        val expectedMangledName = "A\$A\$S\$fox_Ahhh\$S\$A"

        val mangledName = Mangler.mangle(arrayType)

        assertEquals(expectedMangledName, mangledName)
    }
    
    @Test
    fun `mangle method returns correct mangled name for list of types`() {
        val types = listOf<Type>(
            BuiltinType.INT,
            BuiltinType.LONG,
            BuiltinType.INT.asArray(1)
        )
        val expectedMangledName = "deA\$d\$A" // "d" + "e" + "Ad$A"
        
        val mangledName = Mangler.mangle(types)
        
        assertEquals(expectedMangledName, mangledName)
    }
    
    @Test
    fun `mangle method returns empty string for empty list`() {
        val types = emptyList<Type>()
        val expectedMangledName = ""
        
        val mangledName = Mangler.mangle(types)
        
        assertEquals(expectedMangledName, mangledName)
    }
}