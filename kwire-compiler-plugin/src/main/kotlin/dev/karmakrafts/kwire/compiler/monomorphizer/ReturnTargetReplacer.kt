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

package dev.karmakrafts.kwire.compiler.monomorphizer

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.symbols.IrReturnTargetSymbol
import org.jetbrains.kotlin.ir.visitors.IrTransformer

private class ReturnTargetReplacer( // @formatter:off
    private val originalReturnTarget: IrReturnTargetSymbol,
    private val newReturnTarget: IrReturnTargetSymbol
) : IrTransformer<Nothing?>() { // @formatter:on
    override fun visitReturn(expression: IrReturn, data: Nothing?): IrExpression {
        if (expression.returnTargetSymbol == originalReturnTarget) {
            expression.returnTargetSymbol = newReturnTarget
        }
        return super.visitReturn(expression, data)
    }
}

internal fun IrFunction.replaceReturnTargets(originalReturnTarget: IrReturnTargetSymbol) {
    transform(ReturnTargetReplacer(originalReturnTarget, symbol), null)
}