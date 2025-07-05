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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.posix.atexit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.WeakReference

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
private object ShutdownHandlerImpl : ShutdownHandler {
    private val objects: ArrayDeque<WeakReference<AutoCloseable>> = ArrayDeque()
    private val objectsMutex: Mutex = Mutex()

    init {
        atexit(staticCFunction<Unit> { // @formatter:off
            ShutdownHandlerImpl.apply {
                closeAll()
            }
        }) // @formatter:on
    }

    private fun closeAll() = runBlocking {
        objectsMutex.withLock {
            while (objects.isNotEmpty()) {
                objects.removeFirst().value?.close()
            }
            objects.clear()
        }
    }

    override fun register(closeable: AutoCloseable) = runBlocking {
        objectsMutex.withLock {
            objects += WeakReference(closeable)
        }
    }

    override fun unregister(closeable: AutoCloseable): Unit = runBlocking {
        objectsMutex.withLock {
            objects.find { it.value == closeable }?.let(objects::remove)
        }
    }
}

internal actual fun getPlatformShutdownHandler(): ShutdownHandler = ShutdownHandlerImpl