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

@file:JvmName("SharedLibrary$")

package dev.karmakrafts.kwire

import dev.karmakrafts.kwire.SharedLibrary.Companion.open
import dev.karmakrafts.kwire.SharedLibrary.Companion.tryOpen
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

internal interface SharedLibraryHandle : AutoCloseable

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
@Suppress("NOTHING_TO_INLINE")
class SharedLibrary internal constructor(
    private val handle: SharedLibraryHandle
) : AutoCloseable {
    /**
     * Companion object providing static methods for creating SharedLibrary instances.
     *
     * This allows for convenient access to library loading functionality without
     * requiring an instance of the class.
     */
    companion object {
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
         * @return A SharedLibrary instance if the library was found and loaded, or null otherwise
         */
        fun tryOpen(names: List<String>, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary? {
            return Linker.findLibrary(names, linkMode)?.let(::SharedLibrary)
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
         * @return A SharedLibrary instance representing the loaded library
         * @throws IllegalArgumentException if none of the libraries could be loaded
         */
        fun open(names: List<String>, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary {
            return requireNotNull(tryOpen(names, linkMode)) { "Could not open library $names" }
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
    fun findFunctionAddress(name: String): Pointer? {
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
    fun getFunctionAddress(name: String): Pointer =
        requireNotNull(findFunctionAddress(name)) { "Could not find function $name in library $name" }

    /**
     * Attempts to find a function in this library with the specified signature.
     *
     * This method searches for a function with the given name in the loaded library
     * and creates an [FFIFunction] with the specified descriptor if found.
     * If the function does not exist, it returns null.
     *
     * @param name The name of the function to find
     * @param descriptor The descriptor specifying the function signature
     * @return An [FFIFunction] representing the function, or null if the function was not found
     */
    fun findFunction(name: String, descriptor: FFIDescriptor): FFIFunction? {
        return findFunctionAddress(name)?.let { address ->
            FFIFunction(name, address, descriptor)
        }
    }

    /**
     * Gets a function from this library with the specified signature.
     *
     * This method searches for a function with the given name in the loaded library
     * and creates an [FFIFunction] with the specified descriptor.
     * If the function does not exist, it throws an exception.
     *
     * @param name The name of the function to get
     * @param descriptor The descriptor specifying the function signature
     * @return An [FFIFunction] representing the function
     * @throws IllegalArgumentException if the function was not found in the library
     */
    fun getFunction(name: String, descriptor: FFIDescriptor): FFIFunction =
        FFIFunction(name, getFunctionAddress(name), descriptor)

    /**
     * Gets a function from this library with the specified signature using [FFIType] parameters.
     *
     * This operator provides a convenient syntax for getting a function from the library.
     * It creates an [FFIDescriptor] from the provided return type and parameter types,
     * then calls [getFunction] with that descriptor.
     *
     * Example usage:
     * ```
     * val exitFunc = library["exit", FFIType.VOID, FFIType.INT]
     * ```
     *
     * @param name The name of the function to get
     * @param returnType The return type of the function as an [FFIType]
     * @param parameterTypes The parameter types of the function as [FFIType]s
     * @return An [FFIFunction] representing the function
     * @throws IllegalArgumentException if the function was not found in the library
     */
    inline operator fun get(name: String, returnType: FFIType, vararg parameterTypes: FFIType): FFIFunction {
        return getFunction(name, FFIDescriptor(returnType, *parameterTypes))
    }

    /**
     * Gets a function from this library with the specified signature using [KClass] parameters.
     *
     * This operator provides a convenient syntax for getting a function from the library.
     * It creates an [FFIDescriptor] from the provided return type and parameter types,
     * then calls [getFunction] with that descriptor.
     *
     * Example usage:
     * ```
     * val exitFunc = library["exit", Unit::class, Int::class]
     * ```
     *
     * @param name The name of the function to get
     * @param returnType The return type of the function as a [KClass]
     * @param parameterTypes The parameter types of the function as [KClass]es
     * @return An [FFIFunction] representing the function
     * @throws IllegalArgumentException if the function was not found in the library
     * @throws IllegalArgumentException if any of the classes cannot be mapped to an [FFIType]
     */
    inline operator fun get(name: String, returnType: KClass<*>, vararg parameterTypes: KClass<*>): FFIFunction {
        return getFunction(name, FFIDescriptor(returnType, *parameterTypes))
    }

    /**
     * Registers this library to be closed when the application exits.
     *
     * This method ensures that the library's resources are properly released when
     * the application terminates, even if [close] is not explicitly called.
     *
     * @return This SharedLibrary instance for method chaining
     */
    fun closeOnExit() = ShutdownHandler.register(this)

    /**
     * Closes this library, releasing any resources associated with it.
     *
     * This method should be called when the library is no longer needed to prevent
     * resource leaks. Alternatively, [closeOnExit] can be used to automatically
     * close the library when the application exits.
     */
    override fun close() = handle.close()
}
