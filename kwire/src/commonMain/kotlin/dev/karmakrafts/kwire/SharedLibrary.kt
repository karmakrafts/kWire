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

internal interface SharedLibraryHandle : AutoCloseable

@OptIn(ExperimentalContracts::class)
internal inline fun <reified T : SharedLibraryHandle> SharedLibraryHandle.checkHandle() {
    contract {
        returns() implies (this@checkHandle is T)
    }
}

// TODO: document this
class SharedLibrary internal constructor(
    private val handle: SharedLibraryHandle
) : AutoCloseable {
    companion object {
        // TODO: document this
        fun tryOpen(name: String, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary? {
            return Linker.findLibrary(name, linkMode)?.let(::SharedLibrary)
        }

        // TODO: document this
        fun open(name: String, linkMode: LinkMode = LinkMode.LAZY): SharedLibrary {
            return requireNotNull(tryOpen(name, linkMode)) { "Could not open library $name" }
        }
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
    fun closeOnExit() = ShutdownHandler.register(this)

    override fun close() = handle.close()
}