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

@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire.ffi

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.Platform
import dev.karmakrafts.kwire.ShutdownHandler
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ffi.SharedLibrary.Companion.open
import dev.karmakrafts.kwire.ffi.SharedLibrary.Companion.tryOpen
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Internal interface representing a platform-specific handle to a shared library.
 *
 * This interface abstracts the platform-specific details of shared library handles,
 * allowing the SharedLibrary class to work consistently across different platforms.
 *
 * @property name The name or path of the shared library
 */
internal interface SharedLibraryHandle : AutoCloseable {
    val name: String
}

/**
 * Type-checking utility for SharedLibraryHandle instances.
 *
 * This function uses Kotlin contracts to help the compiler understand type relationships
 * when working with different implementations of SharedLibraryHandle.
 *
 * @param T The specific type of SharedLibraryHandle to check for
 */
@OptIn(ExperimentalContracts::class)
internal inline fun <reified T : SharedLibraryHandle> SharedLibraryHandle.checkHandle() {
    contract {
        returns() implies (this@checkHandle is T)
    }
}

/**
 * Represents a dynamically loaded shared library.
 *
 * This class provides methods for loading shared libraries and accessing functions within them.
 * It wraps platform-specific library handles and provides a consistent API for working with
 * shared libraries across different platforms.
 *
 * SharedLibrary instances are typically created using one of the static [open] or [tryOpen] methods.
 * Once a library is loaded, functions can be accessed using [findFunction], [getFunction], or the
 * convenience operator functions.
 *
 * @property handle The platform-specific handle to the loaded library
 */
@KWireCompilerApi
class SharedLibrary internal constructor(
    private val handle: SharedLibraryHandle
) : AutoCloseable {
    /**
     * Companion object providing static methods for creating SharedLibrary instances.
     *
     * This allows for convenient access to library loading functionality without
     * requiring an instance of the class.
     */
    @KWireCompilerApi
    companion object {
        internal val openLibraries: ConcurrentMutableMap<String, SharedLibrary> = ConcurrentMutableMap()

        /**
         * Lazily loaded reference to the C runtime library.
         *
         * This property provides access to the standard C runtime library for the current platform.
         * It automatically selects the appropriate library name based on the platform:
         * - Windows: "msvcrt.dll"
         * - Linux: "libc.so.6" or "libc.so"
         * - macOS/iOS/other Apple platforms: "libSystem.dylib"
         *
         * The library is registered to be closed on application exit.
         *
         * @throws IllegalStateException if the current platform is not supported
         */
        val cRuntime: SharedLibrary by lazy {
            val platform = Platform.current
            open(
                when {
                    platform == Platform.WINDOWS -> listOf("msvcrt.dll")
                    platform.isLinuxFamily -> listOf("libc.so.6", "libc.so")
                    platform.isAppleFamily -> listOf("libSystem.dylib")
                    else -> throw IllegalStateException("Unsupported host platform")
                }
            ).apply {
                closeOnExit()
            }
        }

        /**
         * Lazily loaded reference to the math library.
         *
         * This property provides access to the standard math library functions.
         * On Linux platforms, it loads the dedicated math library ("libm.so.6" or "libm.so") and falls back to the CRT if that fails.
         * On other platforms, it falls back to the C runtime library which typically
         * includes math functions.
         *
         * @return A SharedLibrary instance representing the math library
         */
        val cMath: SharedLibrary by lazy {
            when (Platform.current) {
                Platform.LINUX -> tryOpen("libm.so.6", "libm.so")?.apply {
                    closeOnExit()
                } ?: cRuntime

                else -> cRuntime
            }
        }

        /**
         * Attempts to load a shared library from a list of possible names.
         *
         * This method tries to find and load a shared library using the provided list of names.
         * It will try each name in the list until it finds a library that can be loaded.
         * If none of the libraries can be loaded, it returns null.
         *
         * @param names A list of possible names for the library to load
         * @param linkMode The mode to use when linking the library (NOW for immediate loading of all symbols, LAZY for on-demand loading)
         * @param closeOnExit When true, automatically closes the library instance.
         * @return A SharedLibrary instance if the library was found and loaded, or null otherwise
         */
        fun tryOpen(
            names: List<String>, linkMode: LinkMode = LinkMode.LAZY, closeOnExit: Boolean = true
        ): SharedLibrary? {
            for (name in names) {
                if (name !in openLibraries) continue
                return openLibraries[name]
            }
            return Linker.findLibrary(names, linkMode)?.let {
                openLibraries.getOrPut(it.name) {
                    SharedLibrary(it)
                }.apply {
                    if (closeOnExit) closeOnExit()
                }
            }
        }

        /**
         * Attempts to load a shared library from a variable number of possible names.
         *
         * This is a convenience method that converts the vararg parameter to a list and calls
         * [tryOpen] with that list and [LinkMode.LAZY].
         *
         * @param names Variable number of possible names for the library to load
         * @return A SharedLibrary instance if the library was found and loaded, or null otherwise
         */
        inline fun tryOpen(vararg names: String): SharedLibrary? = tryOpen(names.toList(), LinkMode.LAZY)

        /**
         * Loads a shared library from a list of possible names.
         *
         * This method tries to find and load a shared library using the provided list of names.
         * It will try each name in the list until it finds a library that can be loaded.
         * If none of the libraries can be loaded, it throws an exception.
         *
         * @param names A list of possible names for the library to load
         * @param linkMode The mode to use when linking the library (NOW for immediate loading of all symbols, LAZY for on-demand loading)
         * @param closeOnExit When true, automatically closes the library instance.
         * @return A SharedLibrary instance representing the loaded library
         * @throws IllegalArgumentException if none of the libraries could be loaded
         */
        @KWireCompilerApi
        fun open(
            names: List<String>, linkMode: LinkMode = LinkMode.LAZY, closeOnExit: Boolean = true
        ): SharedLibrary {
            return requireNotNull(tryOpen(names, linkMode, closeOnExit)) { "Could not open library $names" }
        }

        /**
         * Loads a shared library from a variable number of possible names.
         *
         * This is a convenience method that converts the vararg parameter to a list and calls
         * [open] with that list and [LinkMode.LAZY].
         *
         * @param names Variable number of possible names for the library to load
         * @return A SharedLibrary instance representing the loaded library
         * @throws IllegalArgumentException if none of the libraries could be loaded
         */
        inline fun open(vararg names: String): SharedLibrary = open(names.toList(), LinkMode.LAZY)
    }

    /**
     * Attempts to find the memory address of a function in this library.
     *
     * This method searches for a function with the given name in the loaded library
     * and returns its memory address if found, or null if the function does not exist.
     *
     * @param name The name of the function to find
     * @return The memory address of the function, or null if the function was not found
     */
    fun findFunctionAddress(name: String): @Const Ptr<CVoid> {
        return with(Linker) { handle.findSymbol(name) }
    }

    /**
     * Gets the memory address of a function in this library.
     *
     * This method searches for a function with the given name in the loaded library
     * and returns its memory address. If the function does not exist, it throws an exception.
     *
     * @param name The name of the function to get
     * @return The memory address of the function
     * @throws IllegalArgumentException if the function was not found in the library
     */
    fun getFunctionAddress(name: String): @Const Ptr<CVoid> =
        requireNotNull(findFunctionAddress(name)) { "Could not find function '$name' in library ${handle.name}" }

    /**
     * Registers this library to be closed when the application exits.
     *
     * This method ensures that the library's resources are properly released when
     * the application terminates, even if [close] is not explicitly called.
     *
     * @return This SharedLibrary instance for method chaining
     */
    fun closeOnExit() = ShutdownHandler.register(this, "shared-lib-${handle.name}")

    /**
     * Closes this library, releasing any resources associated with it.
     *
     * This method should be called when the library is no longer needed to prevent
     * resource leaks. Alternatively, [closeOnExit] can be used to automatically
     * close the library when the application exits.
     */
    override fun close() {
        val name = handle.name
        if (name !in openLibraries) return // Don't close any library twice
        handle.close()
        openLibraries -= name
    }
}
