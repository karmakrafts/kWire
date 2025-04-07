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

import java.lang.foreign.MemoryLayout

/**
 * Converts this [Struct] to a Java [MemoryLayout].
 *
 * This extension function creates a memory layout that represents the structure of this [Struct].
 * It maps each field's type to its corresponding memory layout and then creates a struct layout
 * from those layouts.
 *
 * @return A [MemoryLayout] that represents the memory layout of this struct.
 */
fun Struct.getMemoryLayout(): MemoryLayout {
    return MemoryLayout.structLayout(*fields.map { it.type.getMemoryLayout() }.toTypedArray())
}
