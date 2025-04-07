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
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.isSymbolicLink
import kotlin.io.path.readSymbolicLink
import kotlin.jvm.optionals.getOrNull

private data class PanamaSharedLibraryHandle( // @formatter:off
    val lookup: SymbolLookup,
    val arena: Arena
) : SharedLibraryHandle { // @formatter:on
    override fun close() {
        arena.close()
    }
}

private object PanamaLinker : Linker {
    private fun getRuntimePaths(): List<Path> {
        return System.getProperty("java.library.path")?.split(":")?.map(::Path) ?: emptyList()
    }

    private fun getDynamicLinkerPaths(): List<Path> {
        val platform = Platform.current
        val paths = System.getenv(
            when {
                platform == Platform.WINDOWS -> "PATH"
                platform.isLinuxFamily -> "LD_LIBRARY_PATH"
                platform.isAppleFamily -> "DYLD_LIBRARY_PATH"
                else -> return emptyList()
            }
        ) ?: return emptyList()
        return paths.split(":").map(::Path)
    }

    // Combine all possible paths and deduplicate them
    private val searchPaths: List<Path> = (getRuntimePaths() + getDynamicLinkerPaths()).toSet().toList()

    @Suppress("UnsafeDynamicallyLoadedCode")
    override fun findLibrary(names: List<String>, linkMode: LinkMode): SharedLibraryHandle? {
        val arena = Arena.ofShared()
        for (searchPath in searchPaths) {
            for (name in names) {
                try {
                    var libraryPath = searchPath / name
                    // Resolve possible symbolic links beforehand
                    if (libraryPath.isSymbolicLink()) libraryPath = libraryPath.readSymbolicLink()
                    return PanamaSharedLibraryHandle(SymbolLookup.libraryLookup(libraryPath, arena), arena)
                } catch (error: IllegalArgumentException) { // Only skip if lib name is invalid
                    continue
                }
            }
        }
        arena.close() // On failure, we clean up the Arena
        return null
    }

    override fun SharedLibraryHandle.findSymbol(name: String): Pointer? {
        checkHandle<PanamaSharedLibraryHandle>()
        return lookup.find(name).map { Pointer(it.address().toNUInt()) }.getOrNull()
    }
}

internal actual fun getPlatformLinker(): Linker = PanamaLinker