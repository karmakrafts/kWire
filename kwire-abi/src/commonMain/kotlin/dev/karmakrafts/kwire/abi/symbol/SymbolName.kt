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

/**
 * @param fullName The fully qualified name of the symbol.
 * @param shortName The local name of the symbol, including nested classes.
 */
data class SymbolName( // @formatter:off
    val fullName: String,
    val shortName: String
) { // @formatter:on
    companion object {
        const val SEPARATOR = "."

        fun deserialize(buffer: Buffer): SymbolName {
            val fullNameLength = buffer.readInt()
            val fullName = buffer.readString(fullNameLength.toLong())
            val shortNameLength = buffer.readInt()
            val shortName = buffer.readString(shortNameLength.toLong())
            return SymbolName(fullName, shortName)
        }
    }

    val packageName: String
        get() = fullName.replace(shortName, "")

    fun segments(): List<String> = fullName.split(SEPARATOR)

    fun packageSegments(): List<String> = packageName.split(SEPARATOR)

    fun nameSegments(): List<String> = shortName.split(SEPARATOR)

    fun serialize(buffer: Buffer) {
        buffer.writeInt(fullName.length)
        buffer.writeString(fullName)
        buffer.writeInt(shortName.length)
        buffer.writeString(shortName)
    }
}