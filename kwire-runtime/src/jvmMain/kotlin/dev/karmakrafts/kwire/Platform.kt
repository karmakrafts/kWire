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

@file:JvmName("PlatformImpl")

package dev.karmakrafts.kwire

private val platform: Platform by lazy {
    val host = System.getProperty("os.name").lowercase()
    when {
        "mac" in host || "os x" in host -> Platform.MACOS
        "windows" in host -> Platform.WINDOWS
        "linux" in host -> Platform.LINUX
        "android" in host -> Platform.ANDROID // Support Android JVM-style targets
        else -> throw IllegalStateException("Unsupported JVM host platform '$host'")
    }
}

@PublishedApi
internal actual fun getCurrentPlatform(): Platform = platform