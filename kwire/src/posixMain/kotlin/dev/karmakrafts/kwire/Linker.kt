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

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.dlclose
import platform.posix.dlopen
import platform.posix.dlsym

@OptIn(ExperimentalForeignApi::class)
private data class PosixSharedLibraryHandle(
    override val name: String,
    val handle: COpaquePointer
) : SharedLibraryHandle {
    override fun close() {
        dlclose(handle)
    }
}

@OptIn(ExperimentalForeignApi::class)
private object PosixLinker : Linker {
    override fun findLibrary(names: List<String>, linkMode: LinkMode): SharedLibraryHandle? {
        for (name in names) {
            val handle = dlopen(name, linkMode.getDLLinkMode()) ?: continue
            return PosixSharedLibraryHandle(name, handle)
        }
        return null
    }

    override fun SharedLibraryHandle.findSymbol(name: String): Pointer? {
        checkHandle<PosixSharedLibraryHandle>()
        return dlsym(handle, name)?.toPointer()
    }
}

internal actual fun getPlatformLinker(): Linker = PosixLinker