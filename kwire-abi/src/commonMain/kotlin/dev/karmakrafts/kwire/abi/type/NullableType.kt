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

package dev.karmakrafts.kwire.abi.type

/**
 * Represents a nullable version of a [Type].
 * 
 * This class wraps another type and delegates all [Type] interface methods to it,
 * except for [mangledName] which appends 'N' to the wrapped type's mangled name
 * to indicate nullability in the ABI.
 *
 * @property actualType The underlying non-nullable type
 */
data class NullableType(val actualType: Type) : Type by actualType {
    /**
     * The mangled name for this nullable type.
     * 
     * The mangled name is created by appending 'N' to the end of the wrapped type's
     * mangled name, indicating that this is a nullable version of that type.
     */
    override val mangledName: String by lazy {
        "${actualType.mangledName}N"
    }
}

/**
 * Extension function to create a nullable version of any [Type].
 *
 * @receiver The type to make nullable
 * @return A [NullableType] wrapping the receiver type
 */
fun Type.asNullable(): NullableType = NullableType(this)