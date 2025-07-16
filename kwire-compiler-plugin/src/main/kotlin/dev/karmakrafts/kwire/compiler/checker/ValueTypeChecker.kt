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

package dev.karmakrafts.kwire.compiler.checker

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.isValueType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.target

internal class ValueTypeChecker( // @formatter:off
    context: KWirePluginContext,
) : AbstractChecker<Nothing?>(context) { // @formatter:on
    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.target
        val parameters = function.typeParameters
        val arguments = expression.typeArguments
        for (parameterIndex in parameters.indices) {
            val parameter = parameters[parameterIndex]
            if (!parameter.isValueType()) continue
            val argument = arguments[parameterIndex] ?: continue
            if (argument.isValueType(context)) continue
            reportError(
                "Type parameter ${parameter.name.asString()} in ${function.name.asString()} expected value type, but got ${argument.render()}",
                expression
            )
        }
    }

    private fun checkTypeUsage(
        type: IrType, traceElement: IrElement
    ) {
        if (type !is IrSimpleType) return
        val clazz = type.getClass() ?: return
        val parameters = clazz.typeParameters
        val arguments = type.arguments
        for (parameterIndex in parameters.indices) {
            val parameter = parameters[parameterIndex]
            if (!parameter.isValueType()) continue
            val argument = arguments[parameterIndex]
            when (argument) {
                is IrStarProjection -> continue // Star projections are allowed
                is IrType -> if (argument.isValueType(context)) continue
                else -> {}
            }
            reportError(
                "Type parameter ${parameter.name.asString()} in ${clazz.name.asString()} expected value type, but got ${argument.render()}",
                traceElement
            )
        }
    }

    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        super.visitFunction(declaration, data)
        checkTypeUsage(declaration.returnType, declaration)
        val parameterTypes = declaration.parameters.map { it.type }
        for (type in parameterTypes) {
            checkTypeUsage(type, declaration)
        }
    }

    override fun visitField(declaration: IrField, data: Nothing?) {
        super.visitField(declaration, data)
        checkTypeUsage(declaration.type, declaration)
    }

    override fun visitVariable(declaration: IrVariable, data: Nothing?) {
        super.visitVariable(declaration, data)
        checkTypeUsage(declaration.type, declaration)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: Nothing?) {
        super.visitTypeAlias(declaration, data)
        checkTypeUsage(declaration.expandedType, declaration)
    }
}