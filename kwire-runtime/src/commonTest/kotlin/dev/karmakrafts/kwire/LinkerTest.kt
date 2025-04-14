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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LinkerTest {
    @Test
    fun `findLibrary returns handle when library exists`() {
        // Get platform-specific C runtime library names
        val libraryNames = when {
            Platform.current == Platform.WINDOWS -> listOf("msvcrt.dll")
            Platform.current.isLinuxFamily -> listOf("libc.so.6", "libc.so")
            Platform.current.isAppleFamily -> listOf("libSystem.dylib")
            else -> listOf("libc.so") // Fallback for other platforms
        }

        // Test with both link modes
        val lazyHandle = Linker.findLibrary(libraryNames, LinkMode.LAZY)
        assertNotNull(lazyHandle, "C runtime library should be found with LAZY linking")
        lazyHandle.close()

        val nowHandle = Linker.findLibrary(libraryNames, LinkMode.NOW)
        assertNotNull(nowHandle, "C runtime library should be found with NOW linking")
        nowHandle.close()
    }

    @Test
    fun `findLibrary returns null when library does not exist`() {
        val nonExistentLibrary = Linker.findLibrary(listOf("non_existent_library"), LinkMode.LAZY)
        assertNull(nonExistentLibrary, "Non-existent library should not be found")
    }

    @Test
    fun `findSymbol returns pointer when symbol exists`() {
        // Get platform-specific C runtime library names
        val libraryNames = when {
            Platform.current == Platform.WINDOWS -> listOf("msvcrt.dll")
            Platform.current.isLinuxFamily -> listOf("libc.so.6", "libc.so")
            Platform.current.isAppleFamily -> listOf("libSystem.dylib")
            else -> listOf("libc.so") // Fallback for other platforms
        }

        val handle = Linker.findLibrary(libraryNames, LinkMode.LAZY)
        assertNotNull(handle, "C runtime library should be found")

        // "exit" is a standard C function that should be available in all C runtime libraries
        val exitSymbol = with(Linker) { handle.findSymbol("exit") }
        assertNotNull(exitSymbol, "exit symbol should be found in C runtime library")

        handle.close()
    }

    @Test
    fun `findSymbol returns null when symbol does not exist`() {
        // Get platform-specific C runtime library names
        val libraryNames = when {
            Platform.current == Platform.WINDOWS -> listOf("msvcrt.dll")
            Platform.current.isLinuxFamily -> listOf("libc.so.6", "libc.so")
            Platform.current.isAppleFamily -> listOf("libSystem.dylib")
            else -> listOf("libc.so") // Fallback for other platforms
        }

        val handle = Linker.findLibrary(libraryNames, LinkMode.LAZY)
        assertNotNull(handle, "C runtime library should be found")

        // Test with a symbol that definitely doesn't exist
        val nonExistentSymbol = with(Linker) { handle.findSymbol("this_symbol_does_not_exist_12345") }
        assertNull(nonExistentSymbol, "Non-existent symbol should not be found")

        handle.close()
    }
}
