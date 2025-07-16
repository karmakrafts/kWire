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
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.visitors.IrTransformer

internal class ParameterRefReplacer(
    private val substitutions: Map<IrValueSymbol, IrValueSymbol>
) : IrTransformer<Nothing?>() {
    override fun visitValueAccess(expression: IrValueAccessExpression, data: Nothing?): IrExpression {
        if (expression.symbol in substitutions) {
            expression.symbol = substitutions[expression.symbol]!!
        }
        return super.visitValueAccess(expression, data)
    }
}

internal fun IrFunction.replaceParameterRefs(substitutions: Map<IrValueSymbol, IrValueSymbol>) {
    transform(ParameterRefReplacer(substitutions), null)
}