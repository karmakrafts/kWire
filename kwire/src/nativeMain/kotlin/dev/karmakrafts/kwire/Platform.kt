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

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform as KotlinPlatform

@OptIn(ExperimentalNativeApi::class)
@PublishedApi
internal actual fun getCurrentPlatform(): Platform = when (KotlinPlatform.osFamily) {
    OsFamily.WINDOWS -> Platform.WINDOWS
    OsFamily.LINUX -> Platform.LINUX
    OsFamily.MACOSX -> Platform.MACOS
    OsFamily.TVOS -> Platform.TVOS
    OsFamily.WATCHOS -> Platform.WATCHOS
    OsFamily.IOS -> Platform.IOS
    OsFamily.ANDROID -> Platform.ANDROID
    else -> throw IllegalStateException("Unsupported platform")
}