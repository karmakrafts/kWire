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

import co.touchlab.stately.collections.ConcurrentMutableMap
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("struct")
data class StructType
@Deprecated(
    message = "Don't use the constructor of StructType directly as it only exists for serialization purposes",
    replaceWith = ReplaceWith("StructType.of()")
) constructor(
    override val symbolName: SymbolName, val fields: List<Type>
) : Type {
    companion object {
        private val cache: ConcurrentMutableMap<String, StructType> = ConcurrentMutableMap()

        @Suppress("DEPRECATION")
        fun of(name: SymbolName, fields: List<Type>): StructType = cache.getOrPut(name.value) { StructType(name, fields) }

        fun of(name: String, fields: List<Type>): StructType = of(SymbolName(name), fields)
    }

    override val size: Int
        get() = fields.sumOf { it.size }

    override val alignment: Int
        get() = fields.maxOf { it.alignment }

    override fun getMangledName(): String {
        return symbolName.toString() // TODO: implement this
    }
}