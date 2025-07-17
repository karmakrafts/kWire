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
import dev.karmakrafts.kwire.compiler.util.isConst
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
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
}