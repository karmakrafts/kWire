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

package dev.karmakrafts.kwire

internal actual fun getPointerSize(): Int {
    return if (System.getProperty("sun.arch.data.model")?.toIntOrNull() == 64) Long.SIZE_BYTES
    else Int.SIZE_BYTES
}

internal actual inline fun Pointer.toPlatformRepresentation(): Any {
    return toMemorySegment()
}