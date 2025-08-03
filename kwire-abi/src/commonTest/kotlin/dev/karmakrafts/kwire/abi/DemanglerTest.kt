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
import dev.karmakrafts.kwire.abi.demangler.StructResolver
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.type.ArrayType
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.ConeType
import dev.karmakrafts.kwire.abi.type.NullableType
import dev.karmakrafts.kwire.abi.type.ReferenceType
import dev.karmakrafts.kwire.abi.type.StructType
import dev.karmakrafts.kwire.abi.type.TypeArgument
import dev.karmakrafts.kwire.abi.type.asArray
import dev.karmakrafts.kwire.abi.type.asNullable
import dev.karmakrafts.kwire.abi.type.withArguments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DemanglerTest {
    // Mock StructResolver for testing
    private val mockStructResolver: StructResolver = { symbolName ->
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
    fun `demangle method returns correct Type for NullableType`() {
        // Create a ReferenceType
        val symbolName = SymbolName("dev.karmakrafts.kwire.test.MyClass", "MyClass")
        val referenceType = ReferenceType(symbolName).asNullable()
        val mangledName = referenceType.mangledName

        val demangledTypes = Demangler.demangle(mangledName, mockStructResolver)

        assertEquals(1, demangledTypes.size, "Expected one type")
        assertTrue(demangledTypes.first() is NullableType, "Expected NullableType")

        val demangledReferenceType = demangledTypes.first() as NullableType
        val actualType = demangledReferenceType.actualType as ReferenceType
        assertEquals(symbolName.fullName, actualType.symbolName.fullName, "Full name mismatch")
        assertEquals(symbolName.shortName, actualType.symbolName.shortName, "Short name mismatch")
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

    @Test
    fun `demangleFunction returns correct DemangledFunction with default parameters`() {
        val functionName = "test"
        val mangledName = "test\$\$a\$\$\$\$\$\$\$\$"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(BuiltinType.VOID, demangledFunction.returnType, "Return type should be VOID")
        assertTrue(demangledFunction.parameterTypes.isEmpty(), "Parameter types should be empty")
        assertEquals(null, demangledFunction.dispatchReceiverType, "Dispatch receiver should be null")
        assertEquals(null, demangledFunction.extensionReceiverType, "Extension receiver should be null")
        assertTrue(demangledFunction.contextReceiverTypes.isEmpty(), "Context receivers should be empty")
        assertTrue(demangledFunction.typeArguments.isEmpty(), "Type arguments should be empty")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with return type and parameter types`() {
        val functionName = "test"
        val returnType = BuiltinType.INT
        val parameterTypes = listOf(BuiltinType.CHAR, BuiltinType.BOOL)
        val mangledName = "test\$\$dpo\$\$\$\$\$\$\$\$"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(returnType, demangledFunction.returnType, "Return type mismatch")
        assertEquals(parameterTypes.size, demangledFunction.parameterTypes.size, "Parameter types size mismatch")
        assertEquals(parameterTypes[0], demangledFunction.parameterTypes[0], "First parameter type mismatch")
        assertEquals(parameterTypes[1], demangledFunction.parameterTypes[1], "Second parameter type mismatch")
        assertEquals(null, demangledFunction.dispatchReceiverType, "Dispatch receiver should be null")
        assertEquals(null, demangledFunction.extensionReceiverType, "Extension receiver should be null")
        assertTrue(demangledFunction.contextReceiverTypes.isEmpty(), "Context receivers should be empty")
        assertTrue(demangledFunction.typeArguments.isEmpty(), "Type arguments should be empty")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with dispatch receiver`() {
        val functionName = "test"
        val dispatchReceiverType = ReferenceType(SymbolName("example.Receiver", "Receiver"))
        val mangledName = "test\$\$a\$\$C\$example_Receiver\$C\$\$\$\$\$\$"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(BuiltinType.VOID, demangledFunction.returnType, "Return type should be VOID")
        assertTrue(demangledFunction.parameterTypes.isEmpty(), "Parameter types should be empty")
        assertNotNull(demangledFunction.dispatchReceiverType, "Dispatch receiver should not be null")
        assertTrue(demangledFunction.dispatchReceiverType is ReferenceType, "Dispatch receiver should be ReferenceType")

        val demangledDispatchReceiver = demangledFunction.dispatchReceiverType
        assertEquals(
            dispatchReceiverType.symbolName.fullName,
            demangledDispatchReceiver.symbolName.fullName,
            "Dispatch receiver name mismatch"
        )
        assertEquals(
            dispatchReceiverType.symbolName.shortName,
            demangledDispatchReceiver.symbolName.shortName,
            "Dispatch receiver short name mismatch"
        )

        assertEquals(null, demangledFunction.extensionReceiverType, "Extension receiver should be null")
        assertTrue(demangledFunction.contextReceiverTypes.isEmpty(), "Context receivers should be empty")
        assertTrue(demangledFunction.typeArguments.isEmpty(), "Type arguments should be empty")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with extension receiver`() {
        val functionName = "test"
        val extensionReceiverType = ReferenceType(SymbolName("example.Extension", "Extension"))
        val mangledName = "test\$\$a\$\$\$\$C\$example_Extension\$C\$\$\$\$"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(BuiltinType.VOID, demangledFunction.returnType, "Return type should be VOID")
        assertTrue(demangledFunction.parameterTypes.isEmpty(), "Parameter types should be empty")
        assertEquals(null, demangledFunction.dispatchReceiverType, "Dispatch receiver should be null")

        assertNotNull(demangledFunction.extensionReceiverType, "Extension receiver should not be null")
        assertTrue(
            demangledFunction.extensionReceiverType is ReferenceType, "Extension receiver should be ReferenceType"
        )

        val demangledExtensionReceiver = demangledFunction.extensionReceiverType
        assertEquals(
            extensionReceiverType.symbolName.fullName,
            demangledExtensionReceiver.symbolName.fullName,
            "Extension receiver name mismatch"
        )
        assertEquals(
            extensionReceiverType.symbolName.shortName,
            demangledExtensionReceiver.symbolName.shortName,
            "Extension receiver short name mismatch"
        )

        assertTrue(demangledFunction.contextReceiverTypes.isEmpty(), "Context receivers should be empty")
        assertTrue(demangledFunction.typeArguments.isEmpty(), "Type arguments should be empty")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with context receivers`() {
        val functionName = "test"
        val contextReceiverTypes = listOf(
            ReferenceType(SymbolName("example.Context1", "Context1")),
            ReferenceType(SymbolName("example.Context2", "Context2"))
        )
        val mangledName = "test\$\$a\$\$\$\$\$\$C\$example_Context1\$CC\$example_Context2\$C\$\$"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(BuiltinType.VOID, demangledFunction.returnType, "Return type should be VOID")
        assertTrue(demangledFunction.parameterTypes.isEmpty(), "Parameter types should be empty")
        assertEquals(null, demangledFunction.dispatchReceiverType, "Dispatch receiver should be null")
        assertEquals(null, demangledFunction.extensionReceiverType, "Extension receiver should be null")

        assertEquals(
            contextReceiverTypes.size, demangledFunction.contextReceiverTypes.size, "Context receivers size mismatch"
        )

        for (i in contextReceiverTypes.indices) {
            assertTrue(
                demangledFunction.contextReceiverTypes[i] is ReferenceType, "Context receiver should be ReferenceType"
            )
            val expectedReceiver = contextReceiverTypes[i]
            val actualReceiver = demangledFunction.contextReceiverTypes[i] as ReferenceType

            assertEquals(
                expectedReceiver.symbolName.fullName,
                actualReceiver.symbolName.fullName,
                "Context receiver ${i + 1} name mismatch"
            )
            assertEquals(
                expectedReceiver.symbolName.shortName,
                actualReceiver.symbolName.shortName,
                "Context receiver ${i + 1} short name mismatch"
            )
        }

        assertTrue(demangledFunction.typeArguments.isEmpty(), "Type arguments should be empty")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with type arguments`() {
        val functionName = "test"
        val typeArguments = listOf(BuiltinType.INT, BuiltinType.CHAR)
        val mangledName = "test\$\$a\$\$\$\$\$\$\$\$dp"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")
        assertEquals(BuiltinType.VOID, demangledFunction.returnType, "Return type should be VOID")
        assertTrue(demangledFunction.parameterTypes.isEmpty(), "Parameter types should be empty")
        assertEquals(null, demangledFunction.dispatchReceiverType, "Dispatch receiver should be null")
        assertEquals(null, demangledFunction.extensionReceiverType, "Extension receiver should be null")
        assertTrue(demangledFunction.contextReceiverTypes.isEmpty(), "Context receivers should be empty")

        assertEquals(typeArguments.size, demangledFunction.typeArguments.size, "Type arguments size mismatch")
        assertEquals(typeArguments[0], demangledFunction.typeArguments[0], "First type argument mismatch")
        assertEquals(typeArguments[1], demangledFunction.typeArguments[1], "Second type argument mismatch")
    }

    @Test
    fun `demangleFunction returns correct DemangledFunction with all parameters`() {
        val functionName = "test"
        val returnType = BuiltinType.BOOL
        val parameterTypes = listOf(BuiltinType.INT, BuiltinType.CHAR)
        val dispatchReceiverType = ReferenceType(SymbolName("example.Dispatch", "Dispatch"))
        val extensionReceiverType = ReferenceType(SymbolName("example.Extension", "Extension"))
        val contextReceiverTypes = listOf(ReferenceType(SymbolName("example.Context", "Context")))
        val typeArguments = listOf(BuiltinType.FLOAT)

        val mangledName =
            "test\$\$odp\$\$C\$example_Dispatch\$C\$\$C\$example_Extension\$C\$\$C\$example_Context\$C\$\$l"

        val demangledFunction = Demangler.demangleFunction(mangledName, mockStructResolver)

        assertEquals(functionName, demangledFunction.functionName, "Function name mismatch")

        assertEquals(returnType, demangledFunction.returnType, "Return type mismatch")

        assertEquals(parameterTypes.size, demangledFunction.parameterTypes.size, "Parameter types size mismatch")
        assertEquals(parameterTypes[0], demangledFunction.parameterTypes[0], "First parameter type mismatch")
        assertEquals(parameterTypes[1], demangledFunction.parameterTypes[1], "Second parameter type mismatch")

        assertNotNull(demangledFunction.dispatchReceiverType, "Dispatch receiver should not be null")
        assertTrue(demangledFunction.dispatchReceiverType is ReferenceType, "Dispatch receiver should be ReferenceType")
        val demangledDispatchReceiver = demangledFunction.dispatchReceiverType
        assertEquals(
            dispatchReceiverType.symbolName.fullName,
            demangledDispatchReceiver.symbolName.fullName,
            "Dispatch receiver name mismatch"
        )

        assertNotNull(demangledFunction.extensionReceiverType, "Extension receiver should not be null")
        assertTrue(
            demangledFunction.extensionReceiverType is ReferenceType, "Extension receiver should be ReferenceType"
        )
        val demangledExtensionReceiver = demangledFunction.extensionReceiverType
        assertEquals(
            extensionReceiverType.symbolName.fullName,
            demangledExtensionReceiver.symbolName.fullName,
            "Extension receiver name mismatch"
        )

        assertEquals(
            contextReceiverTypes.size, demangledFunction.contextReceiverTypes.size, "Context receivers size mismatch"
        )
        assertTrue(
            demangledFunction.contextReceiverTypes[0] is ReferenceType, "Context receiver should be ReferenceType"
        )
        val demangledContextReceiver = demangledFunction.contextReceiverTypes[0] as ReferenceType
        assertEquals(
            contextReceiverTypes[0].symbolName.fullName,
            demangledContextReceiver.symbolName.fullName,
            "Context receiver name mismatch"
        )

        assertEquals(typeArguments.size, demangledFunction.typeArguments.size, "Type arguments size mismatch")
        assertEquals(typeArguments[0], demangledFunction.typeArguments[0], "Type argument mismatch")
    }
}