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

import dev.karmakrafts.kwire.abi.type.TypeArgument.Concrete.Companion.KIND
import dev.karmakrafts.kwire.abi.type.TypeArgument.Star.KIND
import kotlinx.io.Buffer

/**
 * Represents a type argument in a generic type in the ABI system.
 *
 * Type arguments can be either a wildcard (*) represented by [Star],
 * or a concrete type represented by [Concrete].
 */
sealed interface TypeArgument {
    companion object {
        /**
         * Deserializes a [TypeArgument] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [TypeArgument] (either [Star] or [Concrete])
         * @throws IllegalStateException if the type argument kind is unknown
         */
        fun deserialize(buffer: Buffer): TypeArgument {
            val kind = buffer.peek().readByte()
            return when (kind) {
                Star.KIND -> Star.deserialize(buffer)
                Concrete.KIND -> Concrete.deserialize(buffer)
                else -> error("Unknown ABI type argument kind")
            }
        }
    }

    /**
     * The mangled name of this type argument, used for ABI compatibility.
     */
    val mangledName: String

    /**
     * Serializes this type argument to the given [buffer].
     *
     * @param buffer The buffer to write to
     */
    fun serialize(buffer: Buffer)

    /**
     * Represents a wildcard type argument (*) in the ABI system.
     */
    data object Star : TypeArgument {
        /**
         * The kind byte that identifies a Star type argument during serialization/deserialization.
         */
        const val KIND: Byte = 0

        /**
         * The mangled name of this wildcard type argument, which is always "_".
         */
        override val mangledName: String = "_"

        /**
         * Serializes this wildcard type argument to the given [buffer].
         *
         * The serialization format is simply the kind byte ([KIND]).
         *
         * @param buffer The buffer to write to
         */
        override fun serialize(buffer: Buffer) {
            buffer.writeByte(KIND)
        }

        /**
         * Deserializes a [Star] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The [Star] singleton
         * @throws IllegalStateException if the type argument kind is not [KIND]
         */
        fun deserialize(buffer: Buffer): Star {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected star type argument kind ($KIND) while deserializing but got $kind" }
            return Star
        }
    }

    /**
     * Represents a concrete type argument in the ABI system.
     *
     * @property type The concrete type
     */
    data class Concrete(val type: Type) : TypeArgument {
        companion object {
            /**
             * The kind byte that identifies a Concrete type argument during serialization/deserialization.
             */
            const val KIND: Byte = 1

            /**
             * Deserializes a [Concrete] from the given [buffer].
             *
             * @param buffer The buffer to read from
             * @return The deserialized [Concrete]
             * @throws IllegalStateException if the type argument kind is not [KIND]
             */
            fun deserialize(buffer: Buffer): Concrete {
                val kind = buffer.readByte()
                check(kind == Star.KIND) { "Expected concrete type argument kind (${KIND}) while deserializing but got $kind" }
                return Concrete(Type.deserialize(buffer))
            }
        }

        /**
         * The mangled name of this concrete type argument, which is the same as the mangled name of the underlying type.
         */
        override val mangledName: String get() = type.mangledName

        /**
         * Serializes this concrete type argument to the given [buffer].
         *
         * The serialization format is:
         * 1. The kind byte ([KIND])
         * 2. The concrete type
         *
         * @param buffer The buffer to write to
         */
        override fun serialize(buffer: Buffer) {
            buffer.writeByte(KIND)
            type.serialize(buffer)
        }
    }
}