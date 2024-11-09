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

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

import io.karma.dlfcn.SharedLibrary
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.size_t
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private val platformPair: String = "${Platform.osFamily.name.lowercase()}-${Platform.cpuArchitecture.name.lowercase()}"

private val libraryExtension: String = when(Platform.osFamily) {
    OsFamily.WINDOWS -> "dll"
    OsFamily.MACOSX -> "dylib"
    else -> "so"
}

private fun MemScope.allocCString(value: String): CPointer<ByteVar> {
    return allocArrayOf(value.encodeToByteArray() + 0)
}

@Test
fun `Load and unload libc`() {
    SharedLibrary.openCStdLib().use {
        assertNotNull(it)
    }
}

@Test
fun `Call into libc to use memcpy`() = memScoped {
    SharedLibrary.openCStdLib().use {
        assertNotNull(it)
        val value = "Hello World!"
        val sourceBuffer = allocCString(value)
        val destBuffer = allocArray<ByteVar>(value.length + 1)
        it.findFunction<(COpaquePointer?, COpaquePointer?, size_t) -> COpaquePointer?>("memcpy")(
            destBuffer,
            sourceBuffer,
            (value.length + 1).convert()
        )
        assertEquals(value, destBuffer.toKString())
    }
}

@Test
fun `Load and unload testlib`() {
    SharedLibrary.open("testlib/testlib-$platformPair.$libraryExtension").use {
        assertNotNull(it)
    }
}

@Test
fun `Call into testlib to use testlib_test1`() {
    SharedLibrary.open("testlib/testlib-$platformPair.$libraryExtension").use {
        assertNotNull(it)
        assertEquals(1337, it.findFunction<() -> Int>("testlib_test1")())
    }
}

@Test
fun `Call into testlib to use testlib_test2`() {
    SharedLibrary.open("testlib/testlib-$platformPair.$libraryExtension").use {
        assertNotNull(it)
        val function = it.findFunction<(Int, Int) -> Int>("testlib_test2")
        assertEquals(1, function(44, 44))
        assertEquals(0, function(10, 20))
    }
}