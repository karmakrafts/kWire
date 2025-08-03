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

    fun mangleFunction(
        functionName: SymbolName,
        returnType: Type = BuiltinType.VOID,
        parameterTypes: List<Type> = emptyList(),
        dispatchReceiverType: Type? = null,
        extensionReceiverType: Type? = null,
        contextReceiverTypes: List<Type> = emptyList(),
        typeArguments: List<Type> = emptyList()
    ): String {
        // Function signatures are mangled in the following way:
        //
        //      name_RP*_(D_|_)(E_|_)(C+_|_)T*
        //
        // Where R and P are return type and parameter types,
        // D, E and C are dispatch-, extension- and context-receivers respectfully,
        // T are type arguments.

        var result = functionName.mangle()
        // Add return type and parameter types (base signature)
        result += ABIConstants.MANGLING_DELIMITER
        result += mangle(returnType)
        result += parameterTypes.joinToString("") { mangle(it) }
        // Add dispatch receiver, extension- and context-receivers (receiver signature)
        result += ABIConstants.MANGLING_DELIMITER
        dispatchReceiverType?.let { result += mangle(it) }
        result += ABIConstants.MANGLING_DELIMITER
        extensionReceiverType?.let { result += mangle(it) }
        result += ABIConstants.MANGLING_DELIMITER
        result += mangle(contextReceiverTypes)
        // Add type arguments
        result += ABIConstants.MANGLING_DELIMITER
        result += mangle(typeArguments)

        return result
    }
}