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
import kotlinx.io.Buffer

data class ReferenceType(
    override val symbolName: SymbolName
) : Type {
    companion object {
        const val KIND: Byte = 3
        const val PACKAGE_DELIMITER: String = "_"

        fun deserialize(buffer: Buffer): ReferenceType {
            val kind = buffer.readByte()
            check(kind == KIND) { "Expected reference type kind ($KIND) while deserializing but got $kind" }
            return ReferenceType(SymbolName.deserialize(buffer))
        }
    }

    override val size: Int = ABI.pointerSize

    override val alignment: Int = ABI.pointerSize

    override val mangledName: String by lazy {
        val pkg = symbolName.packageSegments().joinToString(PACKAGE_DELIMITER)
        val name = symbolName.shortName
        "C$pkg$PACKAGE_DELIMITER$name\$C"
    }

    override fun serialize(buffer: Buffer) {
        buffer.writeByte(KIND)
        symbolName.serialize(buffer)
    }
}