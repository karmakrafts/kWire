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

package dev.karmakrafts.kwire.abi.mangler

import dev.karmakrafts.kwire.abi.ABIConstants
import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.Type

/**
 * Utility object for name mangling operations.
 *
 * Name mangling is used to encode additional information into identifiers,
 * which is necessary for supporting features like function overloading
 * and type information encoding.
 */
object Mangler {
    /**
     * Mangles a single type into its string representation.
     *
     * @param type The type to mangle
     * @return The mangled string representation of the type
     */
    fun mangle(type: Type): String = type.mangledName

    /**
     * Mangles a list of types into a single string representation.
     *
     * @param types The list of types to mangle
     * @return The concatenated mangled string representation of all types
     */
    fun mangle(types: List<Type>): String = types.joinToString("") { mangle(it) }

    /**
     * Mangles a symbol name by converting its package name and short name
     * into a mangled representation.
     *
     * @param name The symbol name to mangle
     * @return The mangled string representation of the symbol name
     */
    fun mangle(name: SymbolName): String {
        val packageName = name.packageName
            .replace("_", ABIConstants.ESC_PACKAGE_MANGLING_DELIMITER)
            .replace(".", ABIConstants.PACKAGE_MANGLING_DELIMITER)
        val shortName = name.shortName
        return "${packageName}_$shortName"
    }

    /**
     * Mangles a function signature into a string representation.
     *
     * Function signatures are mangled in the following format:
     * `name$$RP*$$(D$$|$$)(E$$|$$)(C+$$|$$)T*`
     *
     * Where:
     * - R is the return type
     * - P* are parameter types
     * - D is the dispatch receiver type (if present)
     * - E is the extension receiver type (if present)
     * - C+ are context receiver types (if present)
     * - T* are type arguments (if present)
     *
     * @param functionName The name of the function to mangle
     * @param returnType The return type of the function
     * @param parameterTypes The list of parameter types
     * @param dispatchReceiverType The dispatch receiver type (for member functions)
     * @param extensionReceiverType The extension receiver type (for extension functions)
     * @param contextReceiverTypes The list of context receiver types
     * @param typeArguments The list of type arguments for generic functions
     * @return The mangled string representation of the function signature
     */
    fun mangleFunction(
        functionName: String,
        returnType: Type = BuiltinType.VOID,
        parameterTypes: List<Type> = emptyList(),
        dispatchReceiverType: Type? = null,
        extensionReceiverType: Type? = null,
        contextReceiverTypes: List<Type> = emptyList(),
        typeArguments: List<Type> = emptyList()
    ): String {
        var result = functionName
        // Add return type and parameter types (base signature)
        result += ABIConstants.TYPE_MANGLING_DELIMITER
        result += mangle(returnType)
        result += mangle(parameterTypes)
        // Add dispatch receiver, extension- and context-receivers (receiver signature)
        result += ABIConstants.TYPE_MANGLING_DELIMITER
        dispatchReceiverType?.let { result += mangle(it) }
        result += ABIConstants.TYPE_MANGLING_DELIMITER
        extensionReceiverType?.let { result += mangle(it) }
        result += ABIConstants.TYPE_MANGLING_DELIMITER
        result += mangle(contextReceiverTypes)
        // Add type arguments
        result += ABIConstants.TYPE_MANGLING_DELIMITER
        result += mangle(typeArguments)

        return result
    }

    /**
     * Mangles a function signature with a SymbolName into a string representation.
     *
     * This overload first mangles the SymbolName into a string and then uses that
     * as the function name for the standard function mangling process.
     *
     * @param functionName The SymbolName of the function to mangle
     * @param returnType The return type of the function
     * @param parameterTypes The list of parameter types
     * @param dispatchReceiverType The dispatch receiver type (for member functions)
     * @param extensionReceiverType The extension receiver type (for extension functions)
     * @param contextReceiverTypes The list of context receiver types
     * @param typeArguments The list of type arguments for generic functions
     * @return The mangled string representation of the function signature
     * @see mangleFunction
     */
    fun mangleFunction(
        functionName: SymbolName,
        returnType: Type = BuiltinType.VOID,
        parameterTypes: List<Type> = emptyList(),
        dispatchReceiverType: Type? = null,
        extensionReceiverType: Type? = null,
        contextReceiverTypes: List<Type> = emptyList(),
        typeArguments: List<Type> = emptyList()
    ): String = mangleFunction(
        mangle(functionName),
        returnType,
        parameterTypes,
        dispatchReceiverType,
        extensionReceiverType,
        contextReceiverTypes,
        typeArguments
    )
}