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

// @formatter:off
@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("NFloatImpl")
// @formatter:on

package dev.karmakrafts.kwire.ctype

import dev.karmakrafts.kwire.KWireCompilerApi

@KWireCompilerApi
actual typealias NFloat = Double

@KWireCompilerApi
actual inline fun Float.toNFloat(): NFloat = toDouble()

@KWireCompilerApi
actual inline fun Double.toNFloat(): NFloat = this

@KWireCompilerApi
actual inline fun Byte.toNFloat(): NFloat = toDouble()

@KWireCompilerApi
actual inline fun Short.toNFloat(): NFloat = toDouble()

@KWireCompilerApi
actual inline fun Int.toNFloat(): NFloat = toDouble()

@KWireCompilerApi
actual inline fun Long.toNFloat(): NFloat = toDouble()
