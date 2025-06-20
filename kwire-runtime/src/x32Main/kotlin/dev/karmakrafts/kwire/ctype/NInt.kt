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

@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire.ctype

import dev.karmakrafts.kwire.KWireCompilerApi

@KWireCompilerApi
actual typealias NInt = Int

@KWireCompilerApi
actual inline fun Byte.toNInt(): NInt = toInt()

@KWireCompilerApi
actual inline fun Short.toNInt(): NInt = toInt()

@KWireCompilerApi
actual inline fun Int.toNInt(): NInt = this

@KWireCompilerApi
actual inline fun Long.toNInt(): NInt = toInt()

@KWireCompilerApi
actual inline fun Float.toNInt(): NInt = toInt()

@KWireCompilerApi
actual inline fun Double.toNInt(): NInt = toInt()