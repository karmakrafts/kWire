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

@file:JvmName("Platform$")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

private const val UNIX_EXTENSION: String = "so"
private const val APPLE_EXTENSION: String = "dylib"

// TODO: document this
enum class Platform(
    val isAppleFamily: Boolean,
    val isLinuxFamily: Boolean,
    val isUnixoid: Boolean,
    val libraryExtension: String
) {
    // @formatter:off
    WINDOWS (false, false,  false,  "dll"),
    LINUX   (false, true,   true,   UNIX_EXTENSION),
    ANDROID (false, true,   true,   UNIX_EXTENSION),
    MACOS   (true,  false,  true,   APPLE_EXTENSION),
    IOS     (true,  false,  true,   APPLE_EXTENSION),
    TVOS    (true,  false,  true,   APPLE_EXTENSION),
    WATCHOS (true,  false,  true,   APPLE_EXTENSION);
    // @formatter:on

    companion object {
        val current: Platform = getCurrentPlatform()
    }
}

@PublishedApi
internal expect fun getCurrentPlatform(): Platform