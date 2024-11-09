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
import kotlinx.cinterop.reinterpret
import platform.windows.FreeLibrary
import platform.windows.GetProcAddress
import platform.windows.LoadLibraryW

/**
 * @author Alexander Hinze
 * @since 09/11/2024
 */

internal actual val C_STD_LIB: Array<String> = arrayOf("msvcrt.dll")

@ExperimentalForeignApi
internal actual fun openLib(name: String, mode: LinkMode): COpaquePointer? {
    return LoadLibraryW(name)
}

@ExperimentalForeignApi
internal actual fun createLib(memory: COpaquePointer, size: Long, mode: LinkMode): COpaquePointer? {
    return null // TODO: implement in-memory modules
}

@ExperimentalForeignApi
internal actual fun closeLib(lib: COpaquePointer) {
    FreeLibrary(lib.reinterpret())
}

@ExperimentalForeignApi
internal actual fun getFunctionAddress(lib: COpaquePointer, name: String): COpaquePointer? {
    return GetProcAddress(lib.reinterpret(), name)
}