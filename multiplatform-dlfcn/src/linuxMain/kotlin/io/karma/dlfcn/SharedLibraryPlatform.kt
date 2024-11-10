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
import kotlinx.cinterop.convert
import platform.posix.*

internal actual val C_STD_LIB: Array<String> = arrayOf("libc.so", "libc.so.6")

@ExperimentalForeignApi
internal class LinuxMemorySharedLibraryHandle(
    address: COpaquePointer,
    internal val shmFd: Int
) : PosixSharedLibraryHandle(address) {
    override val isInMemory: Boolean = true
}

@ExperimentalForeignApi
internal actual fun openLib(name: String, mode: LinkMode): SharedLibraryHandle? {
    return dlopen(name, mode.posixMode)?.let(::PosixSharedLibraryHandle)
}

@ExperimentalForeignApi
internal actual fun closeLib(handle: SharedLibraryHandle) {
    require(handle is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    dlclose(handle.address)
    if(handle is LinuxMemorySharedLibraryHandle) close(handle.shmFd)
}

@ExperimentalForeignApi
internal actual fun getFunctionAddress(handle: SharedLibraryHandle, name: String): COpaquePointer? {
    require(handle is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    return dlsym(handle.address, name)
}

@ExperimentalForeignApi
internal actual fun createLib(name: String, address: COpaquePointer, size: Long, mode: LinkMode): SharedLibraryHandle? {
    val shmFd = shm_open(
        "/$name", O_RDWR or O_CREAT, (S_IRUSR or S_IWUSR or S_IXUSR
                or S_IRGRP or S_IXGRP
                or S_IROTH or S_IXOTH).convert()
    )
    if (shmFd == -1) return null
    if (ftruncate(shmFd, size) != 0) {
        close(shmFd)
        return null
    }
    val shmAddress = mmap(null, size.convert(), PROT_READ or PROT_WRITE, MAP_SHARED, shmFd, 0)
    if (shmAddress == null) {
        close(shmFd)
        return null
    }
    memcpy(shmAddress, address, size.convert())
    if (msync(shmAddress, size.convert(), MS_SYNC) != 0 || munmap(shmAddress, size.convert()) != 0) {
        close(shmFd)
        return null
    }
    val moduleAddress = dlopen("/dev/shm/$name", mode.posixMode)
    return if (moduleAddress == null) {
        close(shmFd)
        null
    }
    else LinuxMemorySharedLibraryHandle(moduleAddress, shmFd)
}