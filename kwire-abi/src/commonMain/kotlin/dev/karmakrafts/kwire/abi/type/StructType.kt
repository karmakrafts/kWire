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

import dev.karmakrafts.kwire.abi.ABIConstants
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.symbol.SymbolNameProvider
import kotlinx.io.Buffer

/**
 * Represents a structure type in the ABI system.
 *
 * A structure type consists of a symbol name and a list of field types.
 * The size of the structure is calculated as the sum of the sizes of its fields.
 * The alignment of the structure is calculated as the maximum alignment of its fields.
 *
 * @property symbolName The name of the symbol associated with this structure type
 * @property fields The list of field types in this structure
 */
open class StructType( // @formatter:off
    override val symbolName: SymbolName,
    open val fields: List<Type>
) : Type, SymbolNameProvider { // @formatter:on
    companion object {
        const val VERSION: Byte = 1

        /**
         * Deserializes a [StructType] from the given [buffer].
         *
         * @param buffer The buffer to read from
         * @return The deserialized [StructType]
         * @throws IllegalStateException if the type kind is not [ABIConstants.TYPE_KIND_STRUCT]
         */
        fun deserialize(buffer: Buffer): StructType {
            val kind = buffer.readByte()
            check(kind == ABIConstants.TYPE_KIND_STRUCT) { "Expected struct type kind (${ABIConstants.TYPE_KIND_STRUCT}) while deserializing but got $kind" }
            val version = buffer.readByte()
            check(version <= VERSION) { "Expected version $VERSION struct type but got version $version" }
            return StructType(
                symbolName = SymbolName.deserialize(buffer),
                fields = (0..<buffer.readInt()).map { Type.deserialize(buffer) })
        }
    }

    /**
     * The size of this structure type in bytes, calculated as the sum of the sizes of its fields.
     */
    override val size: Int by lazy { fields.sumOf { it.size } }

    /**
     * The alignment requirement of this structure type in bytes, calculated as the maximum alignment of its fields.
     */
    override val alignment: Int by lazy { fields.maxOf { it.alignment } }

    /**
     * The mangled name of this structure type, used for ABI compatibility.
     *
     * The mangled name is constructed by combining:
     * 1. The letter 'S'
     * 2. The package segments joined with [ABIConstants.PACKAGE_MANGLING_DELIMITER]
     * 3. Another [ABIConstants.PACKAGE_MANGLING_DELIMITER]
     * 4. The short name of the symbol
     * 5. The string '$S'
     */
    override val mangledName: String by lazy {
        val pkg = symbolName.packageSegments().joinToString(ABIConstants.PACKAGE_MANGLING_DELIMITER)
        val name = symbolName.shortName
        "S\$$pkg${ABIConstants.PACKAGE_MANGLING_DELIMITER}$name\$S"
    }

    /**
     * Returns a flattened list of all fields in this structure, recursively expanding any nested structures.
     *
     * This method performs a breadth-first traversal of the structure's fields, replacing any
     * encountered [StructType] with its fields, until all fields are non-structure types.
     *
     * @return A list of all non-structure fields in this structure and any nested structures
     */
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

    /**
     * Serializes this structure type to the given [buffer].
     *
     * @param buffer The buffer to write to
     */
    override fun serialize(buffer: Buffer) {
        buffer.writeByte(ABIConstants.TYPE_KIND_STRUCT)
        buffer.writeByte(VERSION)
        symbolName.serialize(buffer)
        buffer.writeInt(fields.size)
        for (field in fields) {
            field.serialize(buffer)
        }
    }

    override fun hashCode(): Int {
        var hash = symbolName.hashCode()
        hash = 31 * fields.hashCode() + hash
        return hash
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is StructType) false
        else symbolName == other.symbolName && fields == other.fields
    }

    override fun toString(): String = "StructType[$symbolName/$fields]"
}