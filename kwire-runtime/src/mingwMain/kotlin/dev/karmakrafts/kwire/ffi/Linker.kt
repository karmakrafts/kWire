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

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.toPtr
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.reinterpret
import platform.windows.FreeLibrary
import platform.windows.GetProcAddress
import platform.windows.HMODULE
import platform.windows.LoadLibraryW

@OptIn(ExperimentalForeignApi::class)
private data class WindowsSharedLibraryHandle(
    override val name: String, val handle: HMODULE
) : SharedLibraryHandle {
    override fun close() {
        FreeLibrary(handle)
    }
}

@OptIn(ExperimentalForeignApi::class)
private object WindowsLinker : Linker {
    override fun findLibrary(names: List<String>, linkMode: LinkMode): SharedLibraryHandle? {
        for (name in names) {
            val handle = LoadLibraryW(name) ?: continue
            return WindowsSharedLibraryHandle(name, handle)
        }
        return null
    }

    override fun SharedLibraryHandle.findSymbol(name: String): @Const Ptr<CVoid> {
        checkHandle<WindowsSharedLibraryHandle>()
        return GetProcAddress(handle, name)?.reinterpret<COpaque>().toPtr()
    }
}

internal actual fun getPlatformLinker(): Linker = WindowsLinker