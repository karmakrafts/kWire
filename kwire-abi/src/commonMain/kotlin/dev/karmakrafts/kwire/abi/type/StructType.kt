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
import dev.karmakrafts.kwire.abi.type.ReferenceType.Companion.PACKAGE_DELIMITER
import kotlinx.io.Buffer

data class StructType( // @formatter:off
    override val symbolName: SymbolName,
    val fields: List<Type>
) : Type { // @formatter:on
    companion object {
        const val KIND: Byte = 2

        fun deserialize(buffer: Buffer): StructType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected struct type kind ($KIND) while deserializing but got $kind" }
            return StructType(
                symbolName = SymbolName.deserialize(buffer),
                fields = (0..<buffer.readInt()).map { Type.deserialize(buffer) })
        }
    }

    override val size: Int by lazy { fields.sumOf { it.size } }
    override val alignment: Int by lazy { fields.maxOf { it.alignment } }

    override val mangledName: String by lazy {
        val pkg = symbolName.packageSegments().joinToString(PACKAGE_DELIMITER)
        val name = symbolName.shortName
        "S$pkg$PACKAGE_DELIMITER$name\$S"
    }

    fun getFlatFields(): List<Type> {
        val flatFields = ArrayList<Type>()
        val queue = ArrayDeque<Type>()
        queue += fields
        while (queue.isNotEmpty()) {
            when (val field = queue.removeLast()) {
                is StructType -> queue += field.fields
                else -> flatFields += field
            }
        }
        return flatFields
    }

    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        symbolName.serialize(buffer)
        buffer.writeInt(fields.size)
        for (field in fields) {
            field.serialize(buffer)
        }
    }
}