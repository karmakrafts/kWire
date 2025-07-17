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
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.target

internal abstract class TypeChecker<D>(
    context: KWirePluginContext
) : AbstractChecker<D>(context) {
    open fun checkTypeUsage(type: IrType, traceElement: IrElement) {
        // By default, type usages
        if (type !is IrSimpleType) return
        val clazz = type.getClass() ?: return
        val entries = clazz.typeParameters.zip(type.arguments)
        for ((parameter, argument) in entries) {
            checkTypeArgument(parameter, argument, clazz)
        }
    }

    abstract fun checkTypeArgument(parameter: IrTypeParameter, argument: IrTypeArgument, traceElement: IrElement)

    override fun visitCall(expression: IrCall, data: D) {
        super.visitCall(expression, data)
        val function = expression.target
        val entries = function.typeParameters.zip(expression.typeArguments)
        for ((parameter, argument) in entries) {
            checkTypeArgument(parameter, argument ?: continue, expression)
        }
    }

    override fun visitFunction(declaration: IrFunction, data: D) {
        super.visitFunction(declaration, data)
        checkTypeUsage(declaration.returnType, declaration)
        val parameterTypes = declaration.parameters.map { it.type }
        for (type in parameterTypes) {
            checkTypeUsage(type, declaration)
        }
    }

    override fun visitField(declaration: IrField, data: D) {
        super.visitField(declaration, data)
        checkTypeUsage(declaration.type, declaration)
    }

    override fun visitVariable(declaration: IrVariable, data: D) {
        super.visitVariable(declaration, data)
        checkTypeUsage(declaration.type, declaration)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: D) {
        super.visitTypeAlias(declaration, data)
        checkTypeUsage(declaration.expandedType, declaration)
    }
}