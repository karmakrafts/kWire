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

package dev.karmakrafts.kwire.compiler.util

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter

@JvmInline
internal value class ParameterAccessor(val function: IrFunction) {
    inline var regularParameters: List<IrValueParameter>
        get() = function.parameters.filter { it.kind == IrParameterKind.Regular }
        set(value) {
            replaceKindRange(IrParameterKind.Regular, value)
        }

    inline var contextParameters: List<IrValueParameter>
        get() = function.parameters.filter { it.kind == IrParameterKind.Context }
        set(value) {
            replaceKindRange(IrParameterKind.Context, value)
        }

    inline var dispatchReceiverParameter: IrValueParameter?
        get() = function.parameters.firstOrNull { it.kind == IrParameterKind.DispatchReceiver }
        set(value) {
            val oldParameter = function.parameters.firstOrNull { it.kind == IrParameterKind.DispatchReceiver }
            oldParameter?.let { function.parameters = function.parameters - it }
            value?.let { function.parameters = listOf(it) + function.parameters }
        }

    inline var extensionReceiverParameter: IrValueParameter?
        get() = function.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        set(value) {
            val oldParameters = function.parameters
            val oldParameter = oldParameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
            oldParameter?.let { function.parameters = oldParameters - it }
            value?.let { value ->
                val newParameters =
                    oldParameters.filterNot { it.kind == IrParameterKind.ExtensionReceiver }.toMutableList()
                var index = oldParameters.indexOfLast { it.kind == IrParameterKind.Context }
                if (index == -1) index = oldParameters.indexOfLast { it.kind == IrParameterKind.DispatchReceiver }
                if (index == -1) index = 0
                newParameters.add(index, value)
                function.parameters = newParameters
            }
        }

    fun replaceKindRange(kind: IrParameterKind, parameters: List<IrValueParameter>) {
        val oldParameters = function.parameters
        val currentRange = oldParameters.filter { it.kind == kind }
        // If there is an existing range, replace it
        if (currentRange.isNotEmpty()) {
            val newParameters = (function.parameters - currentRange).toMutableList()
            val index = oldParameters.indexOf(currentRange.first())
            newParameters.addAll(index, parameters)
            function.parameters = newParameters
            return
        }
        // Otherwise we need to find the right place to inject by preceding kind
        var index = -1
        val kinds = IrParameterKind.entries.take(kind.ordinal + 1).reversed()
        for (currentKind in kinds) {
            index = oldParameters.indexOfLast { it.kind == currentKind }
            if (index != -1) break
        }
        if (index == -1) index = 0
        val newParameters = oldParameters.toMutableList()
        newParameters.addAll(index + 1, parameters)
        function.parameters = newParameters
    }
}

internal val IrFunction.parameterAccessor: ParameterAccessor
    get() = ParameterAccessor(this)