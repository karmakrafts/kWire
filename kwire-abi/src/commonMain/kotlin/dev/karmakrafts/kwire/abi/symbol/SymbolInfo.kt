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

package dev.karmakrafts.kwire.abi.symbol

import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlinx.io.writeString

data class SymbolInfo( // @formatter:off
    val name: SymbolName,
    val line: Int,
    val column: Int,
    val file: String
) { // @formatter:on
    companion object {
        fun deserialize(buffer: Buffer): SymbolInfo {
            val name = SymbolName.deserialize(buffer)
            val line = buffer.readInt()
            val column = buffer.readInt()
            val fileNameLength = buffer.readInt()
            val fileName = buffer.readString(fileNameLength.toLong())
            return SymbolInfo(name, line, column, fileName)
        }
    }

    fun toTraceString(): String = "$file:$line:$column"

    fun serialize(buffer: Buffer) {
        name.serialize(buffer)
        buffer.writeInt(line)
        buffer.writeInt(column)
        buffer.writeInt(file.length)
        buffer.writeString(file)
    }
}