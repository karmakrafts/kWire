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

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedDeque

internal actual fun getPlatformShutdownHandler(): ShutdownHandler = object : ShutdownHandler {
    private val objects: ConcurrentLinkedDeque<Pair<Any, WeakReference<AutoCloseable>>> = ConcurrentLinkedDeque()

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            while (objects.isNotEmpty()) {
                val (_, ref) = objects.removeFirst()
                ref.get()?.close()
            }
            objects.clear()
        }.apply {
            name = "kwire-shutdown-handler"
        })
    }

    override fun register(closeable: AutoCloseable, key: Any) {
        if (objects.any { it.first == key }) return
        objects += Pair(key, WeakReference(closeable))
    }

    override fun unregister(closeable: AutoCloseable) {
        objects.removeIf { it.second.get() === closeable }
    }
}