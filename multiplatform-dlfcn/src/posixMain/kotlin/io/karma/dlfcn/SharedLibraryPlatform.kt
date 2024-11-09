/*
 * Copyright 2024 Karma Krafts & associates
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

package io.karma.dlfcn

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.RTLD_LAZY
import platform.posix.RTLD_NOW
import platform.posix.close
import platform.posix.dlclose
import platform.posix.dlopen
import platform.posix.dlsym

/**
 * @author Alexander Hinze
 * @since 09/11/2024
 */

@ExperimentalForeignApi
internal class PosixSharedLibraryHandle(
    val address: COpaquePointer, val fd: Int = -1
) : SharedLibraryHandle

private inline val LinkMode.posixMode: Int
    get() = when (this) {
        LinkMode.NOW -> RTLD_NOW
        else -> RTLD_LAZY
    }

@ExperimentalForeignApi
internal actual fun openLib(name: String, mode: LinkMode): SharedLibraryHandle? {
    return dlopen(name, mode.posixMode)?.let(::PosixSharedLibraryHandle)
}

@ExperimentalForeignApi
internal actual fun createLib(memory: COpaquePointer, size: Long, mode: LinkMode): SharedLibraryHandle? {
    return null // TODO: implement in-memory modules
}

@ExperimentalForeignApi
internal actual fun closeLib(lib: SharedLibraryHandle) {
    require(lib is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    dlclose(lib.address)
    if (lib.fd != -1) close(lib.fd)
}

@ExperimentalForeignApi
internal actual fun getFunctionAddress(lib: SharedLibraryHandle, name: String): COpaquePointer? {
    require(lib is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    return dlsym(lib.address, name)
}