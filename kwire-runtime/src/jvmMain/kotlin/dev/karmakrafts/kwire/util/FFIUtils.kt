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

package dev.karmakrafts.kwire.util

import org.lwjgl.system.libffi.LibFFI

internal fun getFFIError(error: Int): String = when (error) {
    LibFFI.FFI_BAD_ABI -> "Bad ABI"
    LibFFI.FFI_BAD_ARGTYPE -> "Bad argument type"
    LibFFI.FFI_BAD_TYPEDEF -> "Bad type definition"
    else -> "Unknown error ($error)"
}