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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("cone")
class ConeType( // @formatter:off
    val type: Type,
    val typeArguments: List<TypeArgument>
) : Type by type { // @formatter:on
    override val mangledName: String by lazy {
        val arguments = typeArguments.joinToString("") { it.mangledName }
        "${type.mangledName}T$arguments\$T"
    }
}

fun Type.withArguments(arguments: List<TypeArgument>): ConeType = ConeType(this, arguments)

fun Type.withArguments(vararg arguments: TypeArgument): ConeType = ConeType(this, arguments.toList())