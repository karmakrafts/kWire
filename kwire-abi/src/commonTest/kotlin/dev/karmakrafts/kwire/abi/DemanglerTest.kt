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

import dev.karmakrafts.kwire.abi.demangler.Demangler
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.type.ArrayType
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.ConeType
import dev.karmakrafts.kwire.abi.type.ReferenceType
import dev.karmakrafts.kwire.abi.type.StructType
import dev.karmakrafts.kwire.abi.type.Type
import dev.karmakrafts.kwire.abi.type.TypeArgument
import dev.karmakrafts.kwire.abi.type.asArray
import dev.karmakrafts.kwire.abi.type.withArguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemanglerTest {
    // Mock StructResolver for testing
    private val mockStructResolver: (SymbolName) -> List<Type> = { symbolName ->
        when (symbolName.shortName) {
            "Point" -> listOf(BuiltinType.INT, BuiltinType.INT)
            "Rectangle" -> listOf(
                StructType(
                    SymbolName("dev.karmakrafts.kwire.test.Point", "Point"), listOf(BuiltinType.INT, BuiltinType.INT)
                ), StructType(
                    SymbolName("dev.karmakrafts.kwire.test.Point", "Point"), listOf(BuiltinType.INT, BuiltinType.INT)
                )
            )

            else -> emptyList()
        }
    }

    @Test
    fun `demangle method returns correct Type for all BuiltinTypes`() {
        // Test all possible BuiltinType values
        for (builtinType in BuiltinType.entries) {
            val mangledName = builtinType.mangledName

            val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

            assertEquals(1, demangledTypes.size, "Expected one type for ${builtinType.name}")
            assertEquals(builtinType, demangledTypes.first(), "Failed for BuiltinType.${builtinType.name}")
        }
    }

    @Test
    fun `demangle method returns correct Type for ReferenceType`() {
        // Create a ReferenceType
        val symbolName = SymbolName("dev.karmakrafts.kwire.test.MyClass", "MyClass")
        val referenceType = ReferenceType(symbolName)
        val mangledName = referenceType.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ReferenceType, "Expected ReferenceType")

        val demangledReferenceType = demangledTypes.first() as ReferenceType
        assertEquals(symbolName.fullName, demangledReferenceType.symbolName.fullName, "Full name mismatch")
        assertEquals(symbolName.shortName, demangledReferenceType.symbolName.shortName, "Short name mismatch")
    }

    @Test
    fun `demangle method returns correct Type for StructType`() {
        // Create a StructType with a name that our mockStructResolver recognizes
        val symbolName = SymbolName("dev.karmakrafts.kwire.test.Point", "Point")
        val structType = StructType(symbolName, emptyList()) // Fields will be resolved by the mockStructResolver
        val mangledName = structType.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is StructType, "Expected StructType")

        val demangledStructType = demangledTypes.first() as StructType
        assertEquals(symbolName.fullName, demangledStructType.symbolName.fullName, "Full name mismatch")
        assertEquals(symbolName.shortName, demangledStructType.symbolName.shortName, "Short name mismatch")

        // Verify that the fields were resolved correctly
        assertEquals(2, demangledStructType.fields.size, "Expected 2 fields")
        assertEquals(BuiltinType.INT, demangledStructType.fields[0], "First field should be INT")
        assertEquals(BuiltinType.INT, demangledStructType.fields[1], "Second field should be INT")
    }

    @Test
    fun `demangle method returns correct Type for ArrayType with single dimension`() {
        // Create an ArrayType with a single dimension
        val elementType = BuiltinType.INT
        val arrayType = elementType.asArray(1)
        val mangledName = arrayType.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ArrayType, "Expected ArrayType")

        val demangledArrayType = demangledTypes.first() as ArrayType
        assertEquals(1, demangledArrayType.dimensions, "Expected 1 dimension")
        assertEquals(elementType, demangledArrayType.elementType, "Element type mismatch")
    }

    @Test
    fun `demangle method returns correct Type for ArrayType with multiple dimensions`() {
        // Create an ArrayType with multiple dimensions
        val elementType = BuiltinType.FLOAT
        val dimensions = 3
        val arrayType = elementType.asArray(dimensions)
        val mangledName = arrayType.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ArrayType, "Expected ArrayType")

        val demangledArrayType = demangledTypes.first() as ArrayType
        assertEquals(dimensions, demangledArrayType.dimensions, "Dimension mismatch")
        assertEquals(elementType, demangledArrayType.elementType, "Element type mismatch")
    }

    @Test
    fun `demangle method returns correct Type for type with concrete type arguments`() {
        // Create a type with concrete type arguments
        val baseType = BuiltinType.PTR
        val typeArg = TypeArgument.Concrete(BuiltinType.INT)
        val typeWithArgs = baseType.withArguments(typeArg)
        val mangledName = typeWithArgs.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ConeType, "Expected ConeType")

        val demangledConeType = demangledTypes.first() as ConeType
        assertEquals(baseType, demangledConeType.genericType, "Generic type mismatch")
        assertEquals(1, demangledConeType.typeArguments.size, "Expected 1 type argument")

        val demangledTypeArg = demangledConeType.typeArguments.first()
        assertTrue(demangledTypeArg is TypeArgument.Concrete, "Expected Concrete type argument")

        val demangledConcreteArg = demangledTypeArg
        assertEquals(BuiltinType.INT, demangledConcreteArg.type, "Type argument mismatch")
    }

    @Test
    fun `demangle method returns correct Type for type with wildcard type arguments`() {
        // Create a type with wildcard type arguments
        val baseType = ReferenceType(SymbolName("dev.karmakrafts.kwire.test.List", "List"))
        val typeArg = TypeArgument.Star
        val typeWithArgs = baseType.withArguments(typeArg)
        val mangledName = typeWithArgs.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ConeType, "Expected ConeType")

        val demangledConeType = demangledTypes.first() as ConeType
        assertTrue(demangledConeType.genericType is ReferenceType, "Expected ReferenceType as generic type")
        assertEquals(1, demangledConeType.typeArguments.size, "Expected 1 type argument")

        val demangledTypeArg = demangledConeType.typeArguments.first()
        assertTrue(demangledTypeArg is TypeArgument.Star, "Expected Star type argument")
    }

    @Test
    fun `demangle method returns correct Type for type with multiple type arguments`() {
        // Create a type with multiple type arguments
        val baseType = ReferenceType(SymbolName("dev.karmakrafts.kwire.test.Map", "Map"))
        val typeArg1 = TypeArgument.Concrete(BuiltinType.CHAR)
        val typeArg2 = TypeArgument.Concrete(BuiltinType.INT)
        val typeWithArgs = baseType.withArguments(typeArg1, typeArg2)
        val mangledName = typeWithArgs.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is ConeType, "Expected ConeType")

        val demangledConeType = demangledTypes.first() as ConeType
        assertTrue(demangledConeType.genericType is ReferenceType, "Expected ReferenceType as generic type")
        assertEquals(2, demangledConeType.typeArguments.size, "Expected 2 type arguments")

        val demangledTypeArg1 = demangledConeType.typeArguments[0]
        val demangledTypeArg2 = demangledConeType.typeArguments[1]

        assertTrue(demangledTypeArg1 is TypeArgument.Concrete, "Expected Concrete type argument")
        assertTrue(demangledTypeArg2 is TypeArgument.Concrete, "Expected Concrete type argument")

        val demangledConcreteArg1 = demangledTypeArg1
        val demangledConcreteArg2 = demangledTypeArg2

        assertEquals(BuiltinType.CHAR, demangledConcreteArg1.type, "First type argument mismatch")
        assertEquals(BuiltinType.INT, demangledConcreteArg2.type, "Second type argument mismatch")
    }
}