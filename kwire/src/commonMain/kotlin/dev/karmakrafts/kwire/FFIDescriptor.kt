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

/**
 * Descriptor for a foreign function interface (FFI) function signature.
 *
 * This class describes the signature of a native function, including its return type
 * and parameter types. It is used when calling native functions through the FFI system
 * to specify the expected types of the function's return value and parameters.
 *
 * @property returnType The return type of the function, defaults to [FFIType.VOID]
 * @property parameterTypes List of parameter types for the function, defaults to an empty list
 */
@Suppress("NOTHING_TO_INLINE")
data class FFIDescriptor( // @formatter:off
    val returnType: FFIType = FFIType.VOID,
    val parameterTypes: List<FFIType> = emptyList()
) { // @formatter:on
    /**
     * Constructs a descriptor with a return type and variable number of parameter types.
     *
     * @param returnType The return type of the function
     * @param parameterTypes Variable number of parameter types for the function
     */
    constructor(returnType: FFIType, vararg parameterTypes: FFIType) : this(returnType, parameterTypes.toList())

    /**
     * Constructs a descriptor using Kotlin classes for the return type and parameter types.
     *
     * This constructor converts the provided Kotlin classes to their corresponding [FFIType]
     * using the [getFFIType] extension function.
     *
     * @param returnType The Kotlin class representing the return type
     * @param parameterTypes List of Kotlin classes representing parameter types
     * @throws IllegalArgumentException if any of the classes cannot be mapped to an [FFIType]
     */
    constructor(returnType: KClass<*>, parameterTypes: List<KClass<*>>) : this(
        FFIType.fromType(returnType), parameterTypes.map(FFIType::fromType))

    /**
     * Constructs a descriptor using Kotlin classes for the return type and variable number of parameter types.
     *
     * This constructor converts the provided Kotlin classes to their corresponding [FFIType]
     * using the [getFFIType] extension function.
     *
     * @param returnType The Kotlin class representing the return type
     * @param parameterTypes Variable number of Kotlin classes representing parameter types
     * @throws IllegalArgumentException if any of the classes cannot be mapped to an [FFIType]
     */
    constructor(returnType: KClass<*>, vararg parameterTypes: KClass<*>) : this(
        returnType, parameterTypes.toList()
    )
}
