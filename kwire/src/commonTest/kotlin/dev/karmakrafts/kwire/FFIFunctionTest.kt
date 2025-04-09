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

import dev.karmakrafts.rakii.deferring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

/**
 * Tests for the FFIFunction class.
 * These tests use FFIFunction to wrap native functions and call them.
 */
class FFIFunctionTest {
    /**
     * Tests the FFIFunction.callInt method by calling the C runtime's abs function.
     */
    @Test
    fun `callInt with abs function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the abs function
        val absFunction = library.getFunction("abs", FFIDescriptor(FFIType.INT, FFIType.INT))

        // Call the abs function with a negative value
        val result = absFunction.callInt {
            putInt(-42)
        }

        // Verify the result is the absolute value
        assertEquals(42, result, "abs(-42) should return 42")
    }

    /**
     * Tests the FFIFunction.callDouble method by calling the C runtime's fabs function.
     */
    @Test
    fun `callDouble with fabs function returns absolute value`() = deferring {
        // Get the C math library
        val library = SharedLibrary.cMath

        // Get the fabs function
        val fabsFunction = library.getFunction("fabs", FFIDescriptor(FFIType.DOUBLE, FFIType.DOUBLE))

        // Call the fabs function with a negative value
        val result = fabsFunction.callDouble {
            putDouble(-3.14)
        }

        // Verify the result is the absolute value
        assertEquals(3.14, result, "fabs(-3.14) should return 3.14")
    }

    /**
     * Tests the FFIFunction.callFloat method by calling the C runtime's fabsf function.
     */
    @Test
    fun `callFloat with fabsf function returns absolute value`() = deferring {
        // Get the C math library
        val library = SharedLibrary.cMath

        // Get the fabsf function
        val fabsfFunction = library.getFunction("fabsf", FFIDescriptor(FFIType.FLOAT, FFIType.FLOAT))

        // Call the fabsf function with a negative value
        val result = fabsfFunction.callFloat {
            putFloat(-2.71f)
        }

        // Verify the result is the absolute value
        assertEquals(2.71f, result, "fabsf(-2.71f) should return 2.71f")
    }

    /**
     * Tests the FFIFunction.callLong method by calling the C runtime's labs function.
     */
    @Test
    fun `callLong with labs function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the labs function
        val labsFunction = library.getFunction("labs", FFIDescriptor(FFIType.LONG, FFIType.LONG))

        // Call the labs function with a negative value
        val result = labsFunction.callLong {
            putLong(-9876543210L)
        }

        // Verify the result is the absolute value
        assertEquals(9876543210L, result, "labs(-9876543210L) should return 9876543210L")
    }

    /**
     * Tests the FFIFunction.callPointer method by calling the C runtime's malloc function.
     */
    @Test
    fun `callPointer with malloc function returns valid pointer`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the malloc function
        val mallocFunction = library.getFunction("malloc", FFIDescriptor(FFIType.PTR, FFIType.NUINT))

        // Call the malloc function to allocate 100 bytes
        val result = mallocFunction.callPointer {
            putNUInt(100U.toNUInt())
        }

        // Verify the result is not null
        assertNotNull(result, "malloc(100) should return a valid pointer")
        assertTrue(result != nullptr, "malloc(100) should not return NULL")

        // Free the allocated memory
        Memory.free(result)
    }

    /**
     * Tests the FFIFunction.call method (void return) by calling the C runtime's free function.
     */
    @Test
    fun `call with free function frees memory`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the malloc and free functions
        val mallocFunction = library.getFunction("malloc", FFIDescriptor(FFIType.PTR, FFIType.NUINT))
        val freeFunction = library.getFunction("free", FFIDescriptor(FFIType.VOID, FFIType.PTR))

        // Allocate memory using malloc
        val ptr = mallocFunction.callPointer {
            putNUInt(100U.toNUInt())
        }

        // Verify the pointer is valid
        assertNotNull(ptr, "malloc(100) should return a valid pointer")

        // Free the memory using free
        freeFunction.call {
            putPointer(ptr)
        }

        // No assertion needed for void return, just verifying it doesn't crash
    }

    /**
     * Tests the FFIFunction.callUInt extension method by calling the C runtime's strlen function.
     */
    @Test
    fun `callNUInt with strlen function returns string length`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the strlen function
        val strlenFunction = library.getFunction("strlen", FFIDescriptor(FFIType.NUINT, FFIType.PTR))

        // Create a test string
        val testString = "Hello, World!"
        val stringPtr by dropping { CString.allocate(testString) }

        // Call the strlen function
        val result = strlenFunction.callNUInt {
            putPointer(stringPtr.address)
        }

        // Verify the result is the length of the string
        assertEquals(
            testString.length.toUInt().toNUInt(),
            result,
            "strlen(\"$testString\") should return ${testString.length}"
        )
    }

    /**
     * Tests the generic FFIFunction.call<R> method with various return types.
     */
    @Test
    fun `generic call method works with various return types`() = deferring {
        // Get the C runtime and math libraries
        val cRuntime = SharedLibrary.cRuntime
        val cMath = SharedLibrary.cMath

        // Test with Int return type (abs)
        val absFunction = cRuntime.getFunction("abs", FFIDescriptor(FFIType.INT, FFIType.INT))
        val absResult = absFunction.call<Int> {
            putInt(-42)
        }
        assertEquals(42, absResult, "abs(-42) should return 42")

        // Test with Double return type (fabs)
        val fabsFunction = cMath.getFunction("fabs", FFIDescriptor(FFIType.DOUBLE, FFIType.DOUBLE))
        val fabsResult = fabsFunction.call<Double> {
            putDouble(-3.14)
        }
        assertEquals(3.14, fabsResult, "fabs(-3.14) should return 3.14")

        // Test with Float return type (fabsf)
        val fabsfFunction = cMath.getFunction("fabsf", FFIDescriptor(FFIType.FLOAT, FFIType.FLOAT))
        val fabsfResult = fabsfFunction.call<Float> {
            putFloat(-2.71f)
        }
        assertEquals(2.71f, fabsfResult, "fabsf(-2.71f) should return 2.71f")

        // Test with Long return type (labs)
        val labsFunction = cRuntime.getFunction("labs", FFIDescriptor(FFIType.LONG, FFIType.LONG))
        val labsResult = labsFunction.call<Long> {
            putLong(-9876543210L)
        }
        assertEquals(9876543210L, labsResult, "labs(-9876543210L) should return 9876543210L")

        // Test with Pointer return type (malloc)
        val mallocFunction = cRuntime.getFunction("malloc", FFIDescriptor(FFIType.PTR, FFIType.NUINT))
        val mallocResult = mallocFunction.call<Pointer> {
            putNUInt(100U.toNUInt())
        }
        assertNotNull(mallocResult, "malloc(100) should return a valid pointer")
        Memory.free(mallocResult)
    }

    /**
     * Tests the generic FFIFunction.callFast<R> method with common primitive return types.
     */
    @Test
    fun `callFast method works with common primitive return types`() = deferring {
        // Get the C runtime and math libraries
        val cRuntime = SharedLibrary.cRuntime
        val cMath = SharedLibrary.cMath

        // Test with Int return type (abs)
        val absFunction = cRuntime.getFunction("abs", FFIDescriptor(FFIType.INT, FFIType.INT))
        val absResult = absFunction.callFast<Int> {
            putInt(-42)
        }
        assertEquals(42, absResult, "abs(-42) should return 42")

        // Test with Double return type (fabs)
        val fabsFunction = cMath.getFunction("fabs", FFIDescriptor(FFIType.DOUBLE, FFIType.DOUBLE))
        val fabsResult = fabsFunction.callFast<Double> {
            putDouble(-3.14)
        }
        assertEquals(3.14, fabsResult, "fabs(-3.14) should return 3.14")

        // Test with Float return type (fabsf)
        val fabsfFunction = cMath.getFunction("fabsf", FFIDescriptor(FFIType.FLOAT, FFIType.FLOAT))
        val fabsfResult = fabsfFunction.callFast<Float> {
            putFloat(-2.71f)
        }
        assertEquals(2.71f, fabsfResult, "fabsf(-2.71f) should return 2.71f")

        // Test with Long return type (labs)
        val labsFunction = cRuntime.getFunction("labs", FFIDescriptor(FFIType.LONG, FFIType.LONG))
        val labsResult = labsFunction.callFast<Long> {
            putLong(-9876543210L)
        }
        assertEquals(9876543210L, labsResult, "labs(-9876543210L) should return 9876543210L")
    }

    /**
     * Tests that callFast throws an error for unsupported return types.
     */
    @Test
    fun `callFast throws error for unsupported return types`(): Unit = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the strlen function
        val strlenFunction = library.getFunction("strlen", FFIDescriptor(FFIType.NUINT, FFIType.PTR))

        // Create a test string
        val testString = "Hello, World!"
        val stringPtr by dropping { CString.allocate(testString) }

        // Verify that calling callFast with NUInt (not supported by callFast) throws an error
        assertFailsWith<IllegalStateException> {
            strlenFunction.callFast<NUInt> {
                putPointer(stringPtr.address)
            }
        }
    }
}
