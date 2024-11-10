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

import kotlinx.cinterop.*
import kotlinx.cinterop.ByteVar
import platform.darwin.*
import platform.posix.*

@ExperimentalForeignApi
internal class MacosMemorySharedLibraryDependency(
    val handle: COpaquePointer,
    val isInMemory: Boolean = false
)

@ExperimentalForeignApi
internal class MacosMemorySharedLibraryHandle(
    address: COpaquePointer,
    val size: Long,
    val dependencies: Array<MacosMemorySharedLibraryDependency> = emptyArray()
) : PosixSharedLibraryHandle(address) {
    override val isInMemory: Boolean = true
}

internal actual val C_STD_LIB: Array<String> = arrayOf("libc.dylib", "libSystem", "libSystem.dylib")

@ExperimentalForeignApi
internal expect fun relocateSymbol(address: COpaquePointerVar, relocation: relocation_info, slide: COpaquePointer)

@ExperimentalForeignApi
private fun relocateSection(imageBase: COpaquePointer, section: CPointer<section_64>, slide: COpaquePointer) {
    val numRelocations = section.pointed.nreloc
    if(numRelocations == 0U) return
    val relocations = interpretCPointer<relocation_info>(imageBase.rawValue + section.pointed.reloff.toLong()) ?: return
    for(relocationIndex in 0U..<numRelocations) {
        val relocation = relocations[relocationIndex.toLong()]
        // Skip any scattered relocations as these are not used anymore on modern macOS (> 10.0)
        if(relocation.r_address.toUInt() and R_SCATTERED == R_SCATTERED) continue
        val address = interpretCPointer<COpaquePointerVar>(imageBase.rawValue + relocation.r_address.toLong()) ?: continue
        relocateSymbol(address.pointed, relocation, slide)
    }
}

@ExperimentalForeignApi
private fun relocateSymbols(imageBase: COpaquePointer, command: CPointer<segment_command_64>, slide: COpaquePointer) {
    val numSections = command.pointed.nsects
    if(numSections == 0U) return
    var section = interpretCPointer<section_64>(command.rawValue + sizeOf<segment_command_64>()) ?: return
    for(sectionIndex in 0U..<numSections) {
        relocateSection(imageBase, section, slide)
        section = interpretCPointer(section.rawValue + sizeOf<section_64>()) ?: continue
    }
}

@ExperimentalForeignApi
private fun loadDependency(imageBase: COpaquePointer, command: CPointer<dylib_command>, dependencies: ArrayList<MacosMemorySharedLibraryDependency>) {
    val dylibPathAddress = interpretCPointer<ByteVar>(imageBase.rawValue + command.pointed.dylib.name.offset.toLong()) ?: return
    val dylibPath = dylibPathAddress.toKStringFromUtf8()
    val globalHandle = dlopen(dylibPath, RTLD_LAZY or RTLD_GLOBAL)
    if(globalHandle == null) {
        // TODO: we know this is another in-memory module, look it up and add it as a dependency
        return
    }
    dependencies += MacosMemorySharedLibraryDependency(globalHandle)
}

@ExperimentalForeignApi
internal actual fun openLib(name: String, mode: LinkMode): SharedLibraryHandle? {
    return dlopen(name, mode.posixMode)?.let(::PosixSharedLibraryHandle)
}

@ExperimentalForeignApi
internal actual fun closeLib(handle: SharedLibraryHandle) {
    require(handle is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    if(handle is MacosMemorySharedLibraryHandle) {
        munmap(handle.address, handle.size.convert())
        for(dependency in handle.dependencies) {
            if(dependency.isInMemory) {
                // TODO: close in-memory module if refcount == 1
                continue
            }
            dlclose(dependency.handle) // Close regular global shared object through kernel
        }
        return
    }
    dlclose(handle.address)
}

@ExperimentalForeignApi
internal actual fun getFunctionAddress(handle: SharedLibraryHandle, name: String): COpaquePointer? {
    require(handle is PosixSharedLibraryHandle) { "Handle must be a PosixSharedLibraryHandle" }
    if(handle is MacosMemorySharedLibraryHandle) {
        // TODO: implement this
    }
    return dlsym(handle.address, name)
}

@OptIn(ExperimentalStdlibApi::class)
@ExperimentalForeignApi
internal actual fun createLib(name: String, address: COpaquePointer, size: Long, mode: LinkMode): SharedLibraryHandle? {
    // Map new memory for holding the executable module since our source memory could be execute-protected
    val executableAddress = mmap(null, size.convert(), PROT_READ or PROT_WRITE or PROT_EXEC, MAP_PRIVATE, -1 , 0)
        ?: return null
    memcpy(executableAddress, address, size.convert())
    // Handle unmapping memory in case of error
    fun onError(): SharedLibraryHandle? {
        munmap(executableAddress, size.convert())
        return null
    }

    val header = address.reinterpret<mach_header_64>().pointed
    // Make sure the module has a valid Mach-O 64 magic value
    require(header.magic == MH_MAGIC_64) { "Mismatched file magic ${header.magic.toHexString()}" }
    var command = interpretCPointer<load_command>(address.rawValue + sizeOf<mach_header_64>())
        ?: return onError()
    val dependencies = ArrayList<MacosMemorySharedLibraryDependency>()
    val slide = interpretCPointer<COpaque>(address.rawValue) ?: return onError() // Slide == image base if the desired start address is 0
    // Iterate over all commands in the binary
    for(cmdIndex in 0U..<header.ncmds) {
        when(command.pointed.cmd) {
            LC_LOAD_DYLIB.toUInt() -> loadDependency(address, command.reinterpret(), dependencies)
            LC_SEGMENT_64.toUInt() -> relocateSymbols(address, command.reinterpret(), slide)
            else -> {}
        }
        command = interpretCPointer(command.rawValue + command.pointed.cmdsize.toLong()) ?: continue
    }

    return null
}