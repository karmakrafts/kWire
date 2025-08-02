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
 * Represents the name of a symbol with both fully qualified and short forms.
 *
 * This class stores both the fully qualified name (including package) and the
 * short name (local name) of a symbol, and provides methods to work with these names.
 *
 * @property fullName The fully qualified name of the symbol, including package.
 * @property shortName The local name of the symbol, including nested classes.
 */
data class SymbolName( // @formatter:off
    val fullName: String,
    val shortName: String
) { // @formatter:on
    companion object {
        /**
         * The separator used in symbol names to separate segments.
         */
        const val SEPARATOR = "."

        // TODO: document this
        const val MANGLED_SEPARATOR = "_"

        /**
         * Deserializes a SymbolName from the given buffer.
         *
         * @param buffer The buffer to read from
         * @return The deserialized SymbolName
         */
        fun deserialize(buffer: Buffer): SymbolName {
            val fullNameLength = buffer.readInt()
            val fullName = buffer.readString(fullNameLength.toLong())
            val shortNameLength = buffer.readInt()
            val shortName = buffer.readString(shortNameLength.toLong())
            return SymbolName(fullName, shortName)
        }

        // TODO: document this
        fun demangle(value: String): SymbolName {
            // Compute package segments
            val lastPackageSeparator = value.lastIndexOf(MANGLED_SEPARATOR)
            val packageChunk = value.substring(0, lastPackageSeparator)
            val packageSegments = packageChunk.split(MANGLED_SEPARATOR)
            // Compute short name of symbol
            val shortName = value.substring(lastPackageSeparator + 1)
            val packageName = packageSegments.joinToString(SEPARATOR)
            return SymbolName("$packageName$SEPARATOR$shortName", shortName)
        }
    }

    /**
     * The package name part of the fully qualified name.
     *
     * This is derived by removing the short name from the full name.
     */
    val packageName: String
        get() = fullName.replace("$SEPARATOR$shortName", "")

    /**
     * Splits the fully qualified name into segments using the separator.
     *
     * @return A list of name segments
     */
    fun segments(): List<String> = fullName.split(SEPARATOR)

    /**
     * Splits the package name into segments using the separator.
     *
     * @return A list of package segments
     */
    fun packageSegments(): List<String> = packageName.split(SEPARATOR)

    /**
     * Splits the short name into segments using the separator.
     *
     * @return A list of name segments
     */
    fun nameSegments(): List<String> = shortName.split(SEPARATOR)

    // TODO: document this
    fun mangle(): String {
        return "${packageSegments().joinToString(MANGLED_SEPARATOR)}$MANGLED_SEPARATOR$shortName"
    }

    /**
     * Serializes this SymbolName to the given buffer.
     *
     * @param buffer The buffer to write to
     */
    fun serialize(buffer: Buffer) {
        buffer.writeInt(fullName.length)
        buffer.writeString(fullName)
        buffer.writeInt(shortName.length)
        buffer.writeString(shortName)
    }
}