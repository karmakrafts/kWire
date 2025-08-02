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

import kotlinx.io.Buffer

/**
 * Represents a generic type with type arguments in the ABI system.
 *
 * A cone type consists of a generic type and a list of type arguments.
 * Most properties are delegated to the generic type, except for [mangledName] which
 * incorporates the type arguments.
 *
 * Examples of cone types include `List<String>`, `Map<Int, String>`, etc.
 *
 * @property genericType The generic type (e.g., List, Map)
 * @property typeArguments The type arguments (e.g., String, Int)
 */
data class ConeType( // @formatter:off
    val genericType: Type,
    val typeArguments: List<TypeArgument>
) : Type by genericType { // @formatter:on
    companion object {
        /**
         * The kind byte that identifies a ConeType during serialization/deserialization.
         */
        const val KIND: Byte = 4

        /**
         * Deserializes a [ConeType] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [ConeType]
         * @throws IllegalStateException if the type kind is not [KIND]
         */
        fun deserialize(buffer: Buffer): ConeType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected cone type kind ($KIND) while deserializing but got $kind" }
            return ConeType(
                genericType = Type.deserialize(buffer),
                typeArguments = (0..<buffer.readInt()).map { TypeArgument.deserialize(buffer) })
        }
    }

    /**
     * The mangled name of this cone type, used for ABI compatibility.
     * 
     * If there are no type arguments, returns the generic type's mangled name.
     * Otherwise, constructs a mangled name by combining the generic type's mangled name
     * with the mangled names of all type arguments, enclosed in "T" and "$T" markers.
     */
    override val mangledName: String by lazy {
        if(typeArguments.isEmpty()) return@lazy genericType.mangledName
        val arguments = typeArguments.joinToString("") { it.mangledName }
        "${genericType.mangledName}T\$$arguments\$T"
    }

    /**
     * Serializes this cone type to the given [buffer].
     *
     * The serialization format is:
     * 1. The kind byte ([KIND])
     * 2. The generic type
     * 3. The number of type arguments
     * 4. Each type argument
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        genericType.serialize(buffer)
        buffer.writeInt(typeArguments.size)
        for (type in typeArguments) {
            type.serialize(buffer)
        }
    }
}

/**
 * Extension function to add type arguments to any [Type], converting it to a [ConeType].
 *
 * If the arguments list is empty, returns the original type unchanged.
 *
 * @param arguments The list of type arguments to add
 * @return A new [ConeType] with this type as the generic type and the specified arguments,
 *         or this type unchanged if arguments is empty
 */
fun Type.withArguments(arguments: List<TypeArgument>): ConeType = ConeType(this, arguments)

/**
 * Extension function to add type arguments to any [Type], converting it to a [ConeType].
 *
 * If no arguments are provided, returns the original type unchanged.
 *
 * @param arguments The type arguments to add
 * @return A new [ConeType] with this type as the generic type and the specified arguments,
 *         or this type unchanged if no arguments are provided
 */
fun Type.withArguments(vararg arguments: TypeArgument): ConeType = ConeType(this, arguments.toList())