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

import java.lang.foreign.Arena
import java.lang.foreign.SymbolLookup
import kotlin.jvm.optionals.getOrNull

private class PanamaSharedLibraryHandle(
    val lookup: SymbolLookup
) : SharedLibraryHandle {
    override fun close() {
        // Noop since we always load from the global symbol table using jdk.incubator.foreign
    }
}

private object PanamaLinker : Linker {
    @Suppress("UnsafeDynamicallyLoadedCode")
    override fun findLibrary(name: String, linkMode: LinkMode): SharedLibraryHandle? {
        return try {
            PanamaSharedLibraryHandle(SymbolLookup.libraryLookup(name, Arena.global()))
        } catch (error: Throwable) {
            null
        }
    }

    override fun SharedLibraryHandle.findSymbol(name: String): Pointer? {
        checkHandle<PanamaSharedLibraryHandle>()
        return lookup.find(name)
            .map { Pointer(it.address().toNUInt()) }
            .getOrNull()
    }
}

internal actual fun getPlatformLinker(): Linker = PanamaLinker