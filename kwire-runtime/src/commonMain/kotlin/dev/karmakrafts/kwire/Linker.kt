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

/**
 * Internal function to get the platform-specific implementation of the Linker interface.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate Linker implementation for that platform.
 *
 * @return The platform-specific Linker implementation
 */
internal expect fun getPlatformLinker(): Linker

/**
 * Interface for dynamic linking operations.
 *
 * This interface provides methods for finding and loading shared libraries
 * and locating symbols (functions or variables) within those libraries.
 * It serves as the primary mechanism for dynamic linking in the library.
 */
internal interface Linker {
    /**
     * Companion object that delegates to the platform-specific Linker implementation.
     *
     * This allows for static access to Linker functionality through the Linker class,
     * e.g., `Linker.findLibrary(...)` instead of requiring an instance.
     */
    companion object : Linker by getPlatformLinker()

    /**
     * Attempts to find and load a shared library.
     *
     * @param names A list of possible names for the library to load
     * @param linkMode The mode to use when linking the library (NOW for immediate loading of all symbols, LAZY for on-demand loading)
     * @return A handle to the loaded library, or null if the library could not be found or loaded
     */
    fun findLibrary(names: List<String>, linkMode: LinkMode): SharedLibraryHandle?

    /**
     * Attempts to find a symbol (function or variable) in a shared library.
     *
     * This method searches for a symbol with the specified name within the context
     * of the provided SharedLibraryHandle. The symbol lookup is platform-specific:
     * - On POSIX systems, it uses dlsym
     * - On Windows, it uses GetProcAddress
     * - On JVM/Android, it uses Java's Foreign Function & Memory API (Panama)
     *
     * The method performs a type check on the handle to ensure it's the correct
     * platform-specific implementation before attempting to look up the symbol.
     *
     * @param name The name of the symbol to find. This should be the exact symbol name
     *             as it appears in the library (including any name mangling if applicable).
     * @return A pointer to the symbol if found, or null if the symbol could not be found
     *         in the library or if an error occurred during lookup.
     */
    fun SharedLibraryHandle.findSymbol(name: String): Pointer?
}
