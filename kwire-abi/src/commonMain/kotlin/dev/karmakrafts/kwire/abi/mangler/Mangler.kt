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

object Mangler {
    fun mangle(type: Type): String = type.mangledName

    fun mangle(types: List<Type>): String = types.joinToString("") { mangle(it) }

    fun mangle(name: SymbolName): String {
        val packageName = name.packageName
            .replace("_", ABIConstants.ESC_PACKAGE_MANGLING_DELIMITER)
            .replace(".", ABIConstants.PACKAGE_MANGLING_DELIMITER)
        val shortName = name.shortName
        return "${packageName}_$shortName"
    }

    fun mangleFunction(
        functionName: String,
        returnType: Type = BuiltinType.VOID,
        parameterTypes: List<Type> = emptyList(),
        dispatchReceiverType: Type? = null,
        extensionReceiverType: Type? = null,
        contextReceiverTypes: List<Type> = emptyList(),
        typeArguments: List<Type> = emptyList()
    ): String {
        // Function signatures are mangled in the following way:
        //
        //      name$$RP*$$(D$$|$$)(E$$|$$)(C+$$|$$)T*
        //
        // Where R and P are return type and parameter types,
        // D, E and C are dispatch-, extension- and context-receivers respectfully,
        // T are type arguments.

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

    fun mangleFunction(
        functionName: SymbolName,
        returnType: Type = BuiltinType.VOID,
        parameterTypes: List<Type> = emptyList(),
        dispatchReceiverType: Type? = null,
        extensionReceiverType: Type? = null,
        contextReceiverTypes: List<Type> = emptyList(),
        typeArguments: List<Type> = emptyList()
    ): String {
        // package_name$$RP*$$(D$$|$$)(E$$|$$)(C+$$|$$)T*
        return mangleFunction(
            mangle(functionName),
            returnType,
            parameterTypes,
            dispatchReceiverType,
            extensionReceiverType,
            contextReceiverTypes,
            typeArguments
        )
    }
}