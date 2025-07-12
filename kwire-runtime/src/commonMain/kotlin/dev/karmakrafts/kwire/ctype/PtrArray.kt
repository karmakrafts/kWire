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
import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException
import kotlin.jvm.JvmInline

/**
 * Represents an array of pointers in the C type system.
 *
 * This class provides a type-safe way to work with arrays of pointers in the KWire FFI system.
 * It wraps a [NUIntArray] to store the raw addresses of the pointers.
 *
 * @param P The type of address that the pointers in this array represent
 */
@JvmInline
value class PtrArray<P : Ptr<*>> @PublishedApi internal constructor(
    /**
     * The underlying array of native unsigned integers that store the raw addresses.
     */
    @param:KWireCompilerApi @property:KWireCompilerApi val value: NUIntArray
) {
    /**
     * Creates a new pointer array with the specified size and initializer function.
     *
     * @param size The size of the array to create
     * @param initializer A function that returns a pointer for each index
     */
    constructor(size: Int, initializer: (Int) -> P) : this(nUIntArray(size) { initializer(it).rawAddress })

    /**
     * Creates a new empty pointer array with the specified size.
     *
     * @param size The size of the array to create
     */
    constructor(size: Int) : this(nUIntArray(size))

    /**
     * The number of elements in this pointer array.
     */
    inline val size: Int
        get() = value.size

    /**
     * Gets the pointer at the specified index in this array.
     *
     * @param index The index of the element to retrieve
     * @return The pointer at the specified index
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_ARRAY_GET)
    operator fun get(index: Int): P = throw KWirePluginNotAppliedException()

    /**
     * Sets the pointer at the specified index in this array.
     *
     * @param index The index of the element to set
     * @param value The pointer value to set at the specified index
     */
    @KWireIntrinsic(KWireIntrinsic.Type.PTR_ARRAY_SET)
    operator fun set(index: Int, value: P): Unit = throw KWirePluginNotAppliedException()
}

/**
 * Creates a pointer array containing the specified pointers.
 *
 * @param P The type of address that the pointers in this array represent
 * @param pointers The pointers to include in the array
 * @return A new pointer array containing the specified pointers
 */
inline fun <P : Ptr<*>> ptrArrayOf(vararg pointers: P): PtrArray<P> = PtrArray(pointers.size) { pointers[it] }
