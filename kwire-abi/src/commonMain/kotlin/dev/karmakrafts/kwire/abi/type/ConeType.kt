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

data class ConeType( // @formatter:off
    val genericType: Type,
    val typeArguments: List<TypeArgument>
) : Type by genericType { // @formatter:on
    companion object {
        const val KIND: Byte = 4

        fun deserialize(buffer: Buffer): ConeType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected cone type kind ($KIND) while deserializing but got $kind" }
            return ConeType(
                genericType = Type.deserialize(buffer),
                typeArguments = (0..<buffer.readInt()).map { TypeArgument.deserialize(buffer) })
        }
    }

    override val mangledName: String by lazy {
        val arguments = typeArguments.joinToString("") { it.mangledName }
        "${genericType.mangledName}T$arguments\$T"
    }

    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        genericType.serialize(buffer)
        buffer.writeInt(typeArguments.size)
        for (type in typeArguments) {
            type.serialize(buffer)
        }
    }
}

fun Type.withArguments(arguments: List<TypeArgument>): Type {
    return if (arguments.isNotEmpty()) ConeType(this, arguments)
    else this
}

fun Type.withArguments(vararg arguments: TypeArgument): Type {
    return if (arguments.isNotEmpty()) ConeType(this, arguments.toList())
    else this
}