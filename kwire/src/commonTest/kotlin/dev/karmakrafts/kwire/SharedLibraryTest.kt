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

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SharedLibraryTest {
    @Test
    fun `Open and close`() {
        var library: SharedLibrary? = SharedLibrary.cRuntime
        assertNotNull(library, "C runtime library should be available")

        library = SharedLibrary.tryOpen("IAMNOTALIBRARY")
        assertNull(library, "Non-existent library should not be loaded")
    }

    @Test
    fun `Find address returns null when no function is found`() {
        val library = SharedLibrary.cRuntime
        assertNull(library.findFunctionAddress("nonsense"), "Non-existent function should not be found")
    }

    @Test
    fun `Find address returns address when function is found`() {
        val library = SharedLibrary.cRuntime
        assertNotNull(library.findFunctionAddress("exit"), "Standard 'exit' function should be found in C runtime")
    }

    @Test
    fun `Find function returns null when no function is found`() {
        val library = SharedLibrary.cRuntime
        assertNull(
            library.findFunction("nonsense", FFIDescriptor(FFIType.VOID)),
            "Non-existent function should not be found regardless of signature"
        )
    }

    @Test
    fun `Find function returns address when function is found`() {
        val library = SharedLibrary.cRuntime
        assertNotNull(
            library.findFunction("exit", FFIDescriptor(FFIType.VOID, FFIType.INT)),
            "Standard 'exit' function with correct signature should be found in C runtime"
        )
    }

    @Test
    fun `Get address throws exception when no function is found`() {
        val library = SharedLibrary.cRuntime
        assertFails("Getting non-existent function address should throw an exception") {
            library.getFunctionAddress("nonsense")
        }
    }

    @Test
    fun `Get function throws exception when no function is found`() {
        val library = SharedLibrary.cRuntime
        assertFails("Getting non-existent function should throw an exception") {
            library["nonsense", FFIType.VOID]
        }
    }
}
