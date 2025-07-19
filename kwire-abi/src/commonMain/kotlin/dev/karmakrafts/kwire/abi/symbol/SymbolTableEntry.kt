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

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
sealed interface SymbolTableEntry {
    val id: Int
    val info: SymbolInfo
    val originalInfo: SymbolInfo?

    @Serializable
    @SerialName("fn")
    data class Function(
        override val id: Int, override val info: SymbolInfo, override val originalInfo: SymbolInfo?
    ) : SymbolTableEntry

    @Serializable
    @SerialName("cl")
    data class Class(
        override val id: Int, override val info: SymbolInfo, override val originalInfo: SymbolInfo?
    ) : SymbolTableEntry
}