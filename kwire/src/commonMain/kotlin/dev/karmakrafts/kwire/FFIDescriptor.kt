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

package dev.karmakrafts.kwire

import kotlin.reflect.KClass

// TODO: document this
@Suppress("NOTHING_TO_INLINE", "WRONG_MODIFIER_TARGET")
data class FFIDescriptor( // @formatter:off
    val returnType: FFIType = FFIType.VOID,
    val parameterTypes: List<FFIType> = emptyList()
) { // @formatter:on
    inline constructor(returnType: FFIType, vararg parameterTypes: FFIType) : this(returnType, parameterTypes.toList())

    inline constructor(returnType: KClass<*>, parameterTypes: List<KClass<*>>) : this(
        returnType.getFFIType(), parameterTypes.map { it.getFFIType() })

    inline constructor(returnType: KClass<*>, vararg parameterTypes: KClass<*>) : this(
        returnType, parameterTypes.toList()
    )
}