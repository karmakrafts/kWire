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

import dev.karmakrafts.kwire.abi.ABI
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.symbol.SymbolNameProvider
import kotlinx.io.Buffer

/**
 * Represents a reference to a type in the ABI system.
 *
 * A reference type is identified by a symbol name and has a fixed size and alignment
 * equal to the ABI's pointer size. The mangled name is constructed from the symbol's
 * package segments and short name.
 *
 * @property symbolName The name of the symbol associated with this reference type
 */
data class ReferenceType(
    override val symbolName: SymbolName
) : Type, SymbolNameProvider {
    companion object {
        /**
         * The kind byte that identifies a ReferenceType during serialization/deserialization.
         */
        const val KIND: Byte = 3
        
        /**
         * The delimiter used to separate package segments in the mangled name.
         */
        const val PACKAGE_DELIMITER: String = "_"

        /**
         * Deserializes a [ReferenceType] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [ReferenceType]
         * @throws IllegalStateException if the type kind is not [KIND]
         */
        fun deserialize(buffer: Buffer): ReferenceType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected reference type kind ($KIND) while deserializing but got $kind" }
            return ReferenceType(SymbolName.deserialize(buffer))
        }
    }

    /**
     * The size of this reference type in bytes, equal to the ABI's pointer size.
     */
    override val size: Int = ABI.pointerSize

    /**
     * The alignment requirement of this reference type in bytes, equal to the ABI's pointer size.
     */
    override val alignment: Int = ABI.pointerSize

    /**
     * The mangled name of this reference type, used for ABI compatibility.
     * 
     * The mangled name is constructed by combining:
     * 1. The letter 'C'
     * 2. The package segments joined with [PACKAGE_DELIMITER]
     * 3. Another [PACKAGE_DELIMITER]
     * 4. The short name of the symbol
     * 5. The string '$C'
     */
    override val mangledName: String by lazy {
        val pkg = symbolName.packageSegments().joinToString(PACKAGE_DELIMITER)
        val name = symbolName.shortName
        "C\$$pkg$PACKAGE_DELIMITER$name\$C"
    }

    /**
     * Serializes this reference type to the given [buffer].
     *
     * The serialization format is:
     * 1. The kind byte ([KIND])
     * 2. The symbol name
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        symbolName.serialize(buffer)
    }
}