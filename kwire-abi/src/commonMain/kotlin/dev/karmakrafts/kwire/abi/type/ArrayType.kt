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

import dev.karmakrafts.kwire.abi.symbol.SymbolName
import kotlinx.io.Buffer

data class ArrayType( // @formatter:off
    override val symbolName: SymbolName,
    val elementType: Type,
    val dimensions: Int
) : Type { // @formatter:on
    companion object {
        const val KIND: Byte = 1

        fun deserialize(buffer: Buffer): ArrayType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected array type kind ($KIND) while deserializing but got $kind" }
            return ArrayType(
                symbolName = SymbolName.deserialize(buffer),
                elementType = Type.deserialize(buffer),
                dimensions = buffer.readInt()
            )
        }
    }

    override val size: Int by lazy { elementType.size * dimensions }
    override val alignment: Int get() = elementType.alignment

    override val mangledName: String by lazy {
        "${"A".repeat(dimensions)}${elementType.mangledName}${"\$A".repeat(dimensions)}"
    }

    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        symbolName.serialize(buffer)
        elementType.serialize(buffer)
        buffer.writeInt(dimensions)
    }
}

fun Type.asArray(dimensions: Int): ArrayType = ArrayType(symbolName, this, dimensions)