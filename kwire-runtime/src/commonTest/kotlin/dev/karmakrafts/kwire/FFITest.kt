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

/**
 * Tests for the FFI class using the call* functions directly.
 * These tests avoid using FFIFunction and instead use the FFI.call* functions directly.
 */
class FFITest {
    /**
     * Tests the FFI.callInt function by calling the C runtime's abs function.
     */
    @Test
    fun `callInt with abs function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the address of the abs function
        val absAddress = library.getFunctionAddress("abs")

        // Create a descriptor for the abs function (takes an int, returns an int)
        val descriptor = FFIDescriptor(FFIType.INT, FFIType.INT)

        // Call the abs function with a negative value
        val result = FFI.callInt(absAddress, descriptor) {
            putInt(-42)
        }

        // Verify the result is the absolute value
        assertEquals(42, result, "abs(-42) should return 42")
    }

    /**
     * Tests the FFI.callDouble function by calling the C runtime's fabs function.
     */
    @Test
    fun `callDouble with fabs function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cMath

        // Get the address of the fabs function
        val fabsAddress = library.getFunctionAddress("fabs")

        // Create a descriptor for the fabs function (takes a double, returns a double)
        val descriptor = FFIDescriptor(FFIType.DOUBLE, FFIType.DOUBLE)

        // Call the fabs function with a negative value
        val result = FFI.callDouble(fabsAddress, descriptor) {
            putDouble(-3.14)
        }

        // Verify the result is the absolute value
        assertEquals(3.14, result, "fabs(-3.14) should return 3.14")
    }

    /**
     * Tests the FFI.callFloat function by calling the C runtime's fabsf function.
     */
    @Test
    fun `callFloat with fabsf function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cMath

        // Get the address of the fabsf function
        val fabsfAddress = library.getFunctionAddress("fabsf")

        // Create a descriptor for the fabsf function (takes a float, returns a float)
        val descriptor = FFIDescriptor(FFIType.FLOAT, FFIType.FLOAT)

        // Call the fabsf function with a negative value
        val result = FFI.callFloat(fabsfAddress, descriptor) {
            putFloat(-2.71f)
        }

        // Verify the result is the absolute value
        assertEquals(2.71f, result, "fabsf(-2.71f) should return 2.71f")
    }

    /**
     * Tests the FFI.callLong function by calling the C runtime's labs function.
     */
    @Test
    fun `callLong with labs function returns absolute value`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the address of the labs function
        val labsAddress = library.getFunctionAddress("labs")

        // Create a descriptor for the labs function (takes a long, returns a long)
        val descriptor = FFIDescriptor(FFIType.LONG, FFIType.LONG)

        // Call the labs function with a negative value
        val result = FFI.callLong(labsAddress, descriptor) {
            putLong(-9876543210L)
        }

        // Verify the result is the absolute value
        assertEquals(9876543210L, result, "labs(-9876543210L) should return 9876543210L")
    }

    /**
     * Tests the FFI.callPointer function by calling the C runtime's malloc function.
     */
    @Test
    fun `callPointer with malloc function returns valid pointer`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the address of the malloc function
        val mallocAddress = library.getFunctionAddress("malloc")

        // Create a descriptor for the malloc function (takes a size_t, returns a pointer)
        val descriptor = FFIDescriptor(FFIType.PTR, FFIType.NUINT)

        // Call the malloc function to allocate 100 bytes
        val result = FFI.callPointer(mallocAddress, descriptor) {
            putNUInt(100U.toNUInt())
        }

        // Verify the result is not null
        assertNotNull(result, "malloc(100) should return a valid pointer")
        assertTrue(result != nullptr, "malloc(100) should not return NULL")

        // Free the allocated memory
        Memory.free(result)
    }

    /**
     * Tests the FFI.call function (void return) by calling the C runtime's free function.
     */
    @Test
    fun `call with free function frees memory`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the address of the malloc and free functions
        val mallocAddress = library.getFunctionAddress("malloc")
        val freeAddress = library.getFunctionAddress("free")

        // Create descriptors for the malloc and free functions
        val mallocDescriptor = FFIDescriptor(FFIType.PTR, FFIType.NUINT)
        val freeDescriptor = FFIDescriptor(FFIType.VOID, FFIType.PTR)

        // Allocate memory using malloc
        val ptr = FFI.callPointer(mallocAddress, mallocDescriptor) {
            putNUInt(100U.toNUInt())
        }

        // Verify the pointer is valid
        assertNotNull(ptr, "malloc(100) should return a valid pointer")

        // Free the memory using free
        FFI.call(freeAddress, freeDescriptor) {
            putPointer(ptr)
        }

        // No assertion needed for void return, just verifying it doesn't crash
    }

    /**
     * Tests the FFI.callUInt extension function by calling the C runtime's strlen function.
     */
    @Test
    fun `callUInt with strlen function returns string length`() = deferring {
        // Get the C runtime library
        val library = SharedLibrary.cRuntime

        // Get the address of the strlen function
        val strlenAddress = library.getFunctionAddress("strlen")

        // Create a descriptor for the strlen function (takes a pointer, returns a size_t)
        val descriptor = FFIDescriptor(FFIType.NUINT, FFIType.PTR)

        // Create a test string
        val testString = "Hello, World!"
        val stringPtr by dropping { CString.allocate(testString) }

        // Call the strlen function
        val result = FFI.callNUInt(strlenAddress, descriptor) {
            putPointer(stringPtr.address)
        }

        // Verify the result is the length of the string
        assertEquals(
            testString.length.toUInt().toNUInt(),
            result,
            "strlen(\"$testString\") should return ${testString.length}"
        )
    }
}
