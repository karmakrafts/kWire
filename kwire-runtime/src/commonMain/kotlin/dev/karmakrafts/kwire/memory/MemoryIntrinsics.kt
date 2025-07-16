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

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Struct
import dev.karmakrafts.kwire.meta.ValueType
import kotlin.reflect.KProperty1

/**
 * Create an instance of the given value type and
 * zero-initialize it.
 */
@KWireIntrinsic(KWireIntrinsic.Type.DEFAULT)
fun <@ValueType T> default(): T = throw KWirePluginNotAppliedException()

/**
 * This will calculate the size of the given type in bytes.
 * For regular Kotlin reference types, this will always return the
 * size of a pointer.
 *
 * @param T The type to calculate the size in memory of.
 * @return The size in memory of the given type in bytes.
 */
@KWireIntrinsic(KWireIntrinsic.Type.SIZE_OF)
fun <@ValueType T> sizeOf(): NUInt = throw KWirePluginNotAppliedException()

/**
 * This will calculate the alignment of the given type in bytes.
 * For regular Kotlin reference types, this will always return the
 * alignment of a pointer.
 *
 * @param T The type to calculate the alignment in memory of.
 * @return The alignment in memory of the given type in bytes.
 */
@KWireIntrinsic(KWireIntrinsic.Type.ALIGN_OF)
fun <@ValueType T> alignOf(): NUInt = throw KWirePluginNotAppliedException()

/**
 * This will calculate the offset of the given field within
 * the enclosing structure type in bytes.
 *
 * @param field The field to calculate the offset of.
 * @return The offset of the given field in its enclosing structure in bytes.
 */
@KWireIntrinsic(KWireIntrinsic.Type.OFFSET_OF)
fun offsetOf(field: KProperty1<out Struct, *>): NUInt = throw KWirePluginNotAppliedException()