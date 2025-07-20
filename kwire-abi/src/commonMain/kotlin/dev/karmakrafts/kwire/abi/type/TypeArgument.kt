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

sealed interface TypeArgument {
    companion object {
        fun deserialize(buffer: Buffer): TypeArgument {
            val kind = buffer.peek().readByte()
            return when (kind) {
                Star.KIND -> Star.deserialize(buffer)
                Concrete.KIND -> Concrete.deserialize(buffer)
                else -> error("Unknown ABI type argument kind")
            }
        }
    }

    val mangledName: String

    fun serialize(buffer: Buffer)

    data object Star : TypeArgument {
        const val KIND: Byte = 0

        override val mangledName: String = "_"

        override fun serialize(buffer: Buffer) {
            buffer.writeByte(KIND)
        }

        fun deserialize(buffer: Buffer): Star {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected star type argument kind ($KIND) while deserializing but got $kind" }
            return Star
        }
    }

    data class Concrete(val type: Type) : TypeArgument {
        companion object {
            const val KIND: Byte = 1

            fun deserialize(buffer: Buffer): Concrete {
                val kind = buffer.readByte()
                check(kind == Star.KIND) { "Expected concrete type argument kind (${KIND}) while deserializing but got $kind" }
                return Concrete(Type.deserialize(buffer))
            }
        }

        override val mangledName: String get() = type.mangledName

        override fun serialize(buffer: Buffer) {
            buffer.writeByte(KIND)
            type.serialize(buffer)
        }
    }
}