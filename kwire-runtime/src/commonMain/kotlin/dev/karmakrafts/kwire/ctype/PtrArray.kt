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

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import kotlin.jvm.JvmInline

@JvmInline
value class PtrArray<P : Address> @PublishedApi internal constructor(
    val value: NUIntArray
) {
    constructor(size: Int, initializer: (Int) -> P) : this(nUIntArray(size) { initializer(it).rawAddress })
    constructor(size: Int) : this(nUIntArray(size))

    inline val size: Int
        get() = value.size

    // Implemented in compiler because class-level generics cannot be reified
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_ARRAY_GET)
    operator fun get(index: Int): P = throw KWirePluginNotAppliedException()

    // Implemented in compiler because class-level generics cannot be reified
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_ARRAY_SET)
    operator fun set(index: Int, value: P): Unit = throw KWirePluginNotAppliedException()
}

inline fun <P : Address> ptrArrayOf(vararg pointers: P): PtrArray<P> = PtrArray(pointers.size) { pointers[it] }
