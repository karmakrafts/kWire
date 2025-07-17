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
import dev.karmakrafts.kwire.compiler.util.ConstCastType
import dev.karmakrafts.kwire.compiler.util.getConstCastTypeFrom
import dev.karmakrafts.kwire.compiler.util.isConst
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrSetField
import org.jetbrains.kotlin.ir.expressions.IrSetValue
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrReturnableBlockSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.isAny
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.target

internal class ConstTypeChecker(
    context: KWirePluginContext
) : TypeChecker<Nothing?>(context) {
    override fun checkTypeArgument(parameter: IrTypeParameter, argument: IrTypeArgument, traceElement: IrElement) {
        if (!parameter.isConst() || argument !is IrType || argument.isConst()) return
        when (traceElement) {
            is IrCall -> {
                val function = traceElement.target
                reportError(
                    "Type parameter ${parameter.name.asString()} in ${function.kotlinFqName.asString()} expected const type, but got ${argument.render()}",
                    traceElement
                )
            }

            is IrDeclarationParent -> {
                reportError(
                    "Type parameter ${parameter.name.asString()} in ${traceElement.kotlinFqName.asString()} expected const type, but got ${argument.render()}",
                    traceElement
                )
            }

            else -> {
                reportError(
                    "Type parameter ${parameter.name.asString()} expected const type, but got ${argument.render()}",
                    traceElement
                )
            }
        }
    }

    private fun checkAssignment(expectedType: IrType, actualType: IrType, traceElement: IrElement) {
        val constCastType = expectedType.getConstCastTypeFrom(actualType)
        // If we're not discarding constness nor if the expected type is Any(?), we can return early
        if (constCastType != ConstCastType.REMOVE_CONSTNESS || expectedType.isAny() || expectedType.isNullableAny()) return
        reportError(
            "Expected ${expectedType.render()} but got ${actualType.render()} which discards constness", traceElement
        )
    }

    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data) // First run builtin type argument checks
        // For calls, we need to check if the arguments match their parameter constness
        val function = expression.target
        for (parameter in function.parameters) {
            if (parameter.kind == IrParameterKind.DispatchReceiver) continue
            val argument = expression.arguments[parameter] ?: continue
            checkAssignment(parameter.type, argument.type, argument)
        }
    }

    override fun visitField(declaration: IrField, data: Nothing?) {
        super.visitField(declaration, data) // First run builtin type argument checks
        // For fields, we need to check the initializer
        val initializer = declaration.initializer ?: return
        checkAssignment(declaration.type, initializer.expression.type, initializer)
    }

    override fun visitVariable(declaration: IrVariable, data: Nothing?) {
        super.visitVariable(declaration, data) // First run builtin type argument checks
        // For variables, we need to check the initializer
        val initializer = declaration.initializer ?: return
        checkAssignment(declaration.type, initializer.type, initializer)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitReturn(expression: IrReturn, data: Nothing?) {
        super.visitReturn(expression, data) // First run builtin type argument checks
        // For returns we check the returned expression type against the return type
        val targetSymbol = expression.returnTargetSymbol
        val exprType = expression.value.type
        when (targetSymbol) {
            is IrFunctionSymbol -> checkAssignment(targetSymbol.owner.returnType, exprType, expression)
            is IrReturnableBlockSymbol -> checkAssignment(targetSymbol.owner.type, exprType, expression)
        }
    }

    override fun visitTry(aTry: IrTry, data: Nothing?) {
        super.visitTry(aTry, data) // First run builtin type argument checks
        // For try expressions, we need to check the possible try- and catch- cases types against their expressions
        checkAssignment(aTry.type, aTry.tryResult.type, aTry.tryResult)
        for (branch in aTry.catches) {
            checkAssignment(aTry.type, branch.result.type, branch)
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetField(expression: IrSetField, data: Nothing?) {
        super.visitSetField(expression, data) // First run builtin type argument checks
        // For field stores we examine the field type versus the expression type being assigned
        checkAssignment(expression.symbol.owner.type, expression.value.type, expression)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitSetValue(expression: IrSetValue, data: Nothing?) {
        super.visitSetValue(expression, data) // First run builtin type argument checks
        // For variable stores we examine the variable type versus the expression type being assigned
        checkAssignment(expression.symbol.owner.type, expression.value.type, expression)
    }
}