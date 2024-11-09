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

/**
 * @author Alexander Hinze
 * @since 09/11/2024
 */

internal interface SharedLibraryHandle

value class LinkMode private constructor(internal val flag: Int) {
    companion object {
        val LAZY: LinkMode = LinkMode(1)
        val NOW: LinkMode = LinkMode(2)
    }
}

internal expect val C_STD_LIB: Array<String>

@ExperimentalForeignApi
internal expect fun openLib(name: String, mode: LinkMode): SharedLibraryHandle?

@ExperimentalForeignApi
internal expect fun createLib(memory: COpaquePointer, size: Long, mode: LinkMode): SharedLibraryHandle?

@ExperimentalForeignApi
internal expect fun closeLib(handle: SharedLibraryHandle)

@ExperimentalForeignApi
internal expect fun getFunctionAddress(handle: SharedLibraryHandle, name: String): COpaquePointer?