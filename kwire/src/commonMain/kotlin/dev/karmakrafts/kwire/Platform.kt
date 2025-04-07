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

/**
 * Internal function to get the current platform.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate Platform enum value for the current operating system.
 *
 * @return The Platform enum value representing the current operating system
 */
@PublishedApi
internal expect fun getCurrentPlatform(): Platform

/**
 * Enumeration of supported operating system platforms.
 *
 * This enum provides information about different platforms that the library supports,
 * including their family relationships and the file extension used for shared libraries
 * on each platform.
 *
 * @property isAppleFamily Whether this platform is part of the Apple family (macOS, iOS, etc.)
 * @property isLinuxFamily Whether this platform is part of the Linux family
 * @property isUnixoid Whether this platform follows Unix-like conventions
 * @property libraryExtension The file extension used for shared libraries on this platform
 */
enum class Platform( // @formatter:off
    val isAppleFamily: Boolean,
    val isLinuxFamily: Boolean,
    val isUnixoid: Boolean,
    val libraryExtension: String
) { // @formatter:on
    // @formatter:off
    WINDOWS (false, false,  false,  "dll"),
    LINUX   (false, true,   true,   UNIX_EXTENSION),
    ANDROID (false, true,   true,   UNIX_EXTENSION),
    MACOS   (true,  false,  true,   APPLE_EXTENSION),
    IOS     (true,  false,  true,   APPLE_EXTENSION),
    TVOS    (true,  false,  true,   APPLE_EXTENSION),
    WATCHOS (true,  false,  true,   APPLE_EXTENSION);
    // @formatter:on

    /**
     * Companion object that provides access to the current platform.
     *
     * This allows for static access to the current platform through the Platform class,
     * e.g., `Platform.current` instead of requiring an instance.
     */
    companion object {
        /**
         * The current platform on which the application is running.
         *
         * This property is initialized once when first accessed and provides
         * information about the platform-specific characteristics.
         */
        val current: Platform = getCurrentPlatform()
    }
}