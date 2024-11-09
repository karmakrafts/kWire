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
import platform.posix.MAP_SHARED
import platform.posix.MS_SYNC
import platform.posix.O_CREAT
import platform.posix.O_RDWR
import platform.posix.PROT_READ
import platform.posix.PROT_WRITE
import platform.posix.S_IRGRP
import platform.posix.S_IROTH
import platform.posix.S_IRUSR
import platform.posix.close
import platform.posix.dlopen
import platform.posix.ftruncate
import platform.posix.memcpy
import platform.posix.mmap
import platform.posix.msync
import platform.posix.munmap
import platform.posix.shm_open

internal actual val C_STD_LIB: Array<String> = arrayOf("libc.so", "libc.so.6")

@ExperimentalForeignApi
internal actual fun createLib(name: String, address: COpaquePointer, size: Long, mode: LinkMode): SharedLibraryHandle? {
    val shmFd = shm_open(
        "/$name", O_RDWR or O_CREAT, (S_IRUSR or S_IRGRP or S_IROTH).convert()
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
    else PosixSharedLibraryHandle(moduleAddress, shmFd)
}