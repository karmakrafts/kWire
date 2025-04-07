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

// TODO: document this
@Suppress("NOTHING_TO_INLINE")
class SharedLibrary internal constructor(
    private val handle: SharedLibraryHandle
) : AutoCloseable {
    companion object {
        // TODO: document this
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

        // TODO: document this
        fun tryOpen(names: List<String>, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary? {
            return Linker.findLibrary(names, linkMode)?.let(::SharedLibrary)
        }

        // TODO: document this
        inline fun tryOpen(vararg names: String): SharedLibrary? = tryOpen(names.toList(), LinkMode.LAZY)

        // TODO: document this
        fun open(names: List<String>, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary {
            return requireNotNull(tryOpen(names, linkMode)) { "Could not open library $names" }
        }

        // TODO: document this
        inline fun open(vararg names: String): SharedLibrary = open(names.toList(), LinkMode.LAZY)
    }

    // TODO: document this
    fun findFunctionAddress(name: String): Pointer? {
        return with(Linker) { handle.findSymbol(name) }
    }

    // TODO: document this
    fun getFunctionAddress(name: String): Pointer =
        requireNotNull(findFunctionAddress(name)) { "Could not find function $name in library $name" }

    // TODO: document this
    fun findFunction(name: String, descriptor: FFIDescriptor): FFIFunction? {
        return findFunctionAddress(name)?.let { address ->
            FFIFunction(name, address, descriptor)
        }
    }

    // TODO: document this
    fun getFunction(name: String, descriptor: FFIDescriptor): FFIFunction =
        FFIFunction(name, getFunctionAddress(name), descriptor)

    // TODO: document this
    inline operator fun get(name: String, returnType: FFIType, vararg parameterTypes: FFIType): FFIFunction {
        return getFunction(name, FFIDescriptor(returnType, *parameterTypes))
    }

    // TODO: document this
    inline operator fun get(name: String, returnType: KClass<*>, vararg parameterTypes: KClass<*>): FFIFunction {
        return getFunction(name, FFIDescriptor(returnType, *parameterTypes))
    }

    // TODO: document this
    fun closeOnExit() = ShutdownHandler.register(this)

    override fun close() = handle.close()
}