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
            BuiltinType.INT, BuiltinType.LONG, BuiltinType.INT.asArray(1)
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

    @Test
    fun `mangleFunction returns correct mangled name with default parameters`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val expectedMangledName = "example_Test.test_a____"

        val mangledName = Mangler.mangleFunction(functionName)

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with return type and parameter types`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val returnType = BuiltinType.INT
        val parameterTypes = listOf<Type>(BuiltinType.CHAR, BuiltinType.BOOL)
        val expectedMangledName = "example_Test.test_dpo____"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName, returnType = returnType, parameterTypes = parameterTypes
        )

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with dispatch receiver`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val dispatchReceiverType = ReferenceType(SymbolName("example.Receiver", "Receiver"))
        val expectedMangledName = "example_Test.test_a_C\$example_Receiver\$C___"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName, dispatchReceiverType = dispatchReceiverType
        )

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with extension receiver`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val extensionReceiverType = ReferenceType(SymbolName("example.Extension", "Extension"))
        val expectedMangledName = "example_Test.test_a__C\$example_Extension\$C__"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName, extensionReceiverType = extensionReceiverType
        )

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with context receivers`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val contextReceiverTypes = listOf<Type>(
            ReferenceType(SymbolName("example.Context1", "Context1")),
            ReferenceType(SymbolName("example.Context2", "Context2"))
        )
        val expectedMangledName = "example_Test.test_a___C\$example_Context1\$CC\$example_Context2\$C_"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName, contextReceiverTypes = contextReceiverTypes
        )

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with type arguments`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val typeArguments = listOf<Type>(
            BuiltinType.INT, BuiltinType.CHAR
        )
        val expectedMangledName = "example_Test.test_a____dp"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName, typeArguments = typeArguments
        )

        assertEquals(expectedMangledName, mangledName)
    }

    @Test
    fun `mangleFunction returns correct mangled name with all parameters`() {
        val functionName = SymbolName("example.Test.test", "Test.test")
        val returnType = BuiltinType.BOOL
        val parameterTypes = listOf<Type>(BuiltinType.INT, BuiltinType.CHAR)
        val dispatchReceiverType = ReferenceType(SymbolName("example.Dispatch", "Dispatch"))
        val extensionReceiverType = ReferenceType(SymbolName("example.Extension", "Extension"))
        val contextReceiverTypes = listOf<Type>(ReferenceType(SymbolName("example.Context", "Context")))
        val typeArguments = listOf<Type>(BuiltinType.FLOAT)

        val expectedMangledName =
            "example_Test.test_odp_C\$example_Dispatch\$C_C\$example_Extension\$C_C\$example_Context\$C_l"

        val mangledName = Mangler.mangleFunction(
            functionName = functionName,
            returnType = returnType,
            parameterTypes = parameterTypes,
            dispatchReceiverType = dispatchReceiverType,
            extensionReceiverType = extensionReceiverType,
            contextReceiverTypes = contextReceiverTypes,
            typeArguments = typeArguments
        )

        assertEquals(expectedMangledName, mangledName)
    }
}