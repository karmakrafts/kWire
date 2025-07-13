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
import dev.karmakrafts.kwire.compiler.util.resolveFromReceiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrNull

internal abstract class TypeUsageChecker(
    context: KWirePluginContext
) : AbstractChecker<Nothing?>(context) {
    abstract fun checkType(declaration: IrElement, type: IrType)

    private fun checkTypeRecursively(declaration: IrElement, type: IrType) {
        checkType(declaration, type)
        if (type !is IrSimpleType) return
        for (typeArgument in type.arguments) {
            val argument = typeArgument.typeOrNull ?: continue
            checkTypeRecursively(declaration, argument)
        }
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: Nothing?) {
        super.visitValueParameter(declaration, data)
        checkTypeRecursively(declaration, declaration.type)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: Nothing?) {
        super.visitTypeAlias(declaration, data)
        checkTypeRecursively(declaration, declaration.expandedType)
    }

    override fun visitField(declaration: IrField, data: Nothing?) {
        super.visitField(declaration, data)
        checkTypeRecursively(declaration, declaration.type)
    }

    override fun visitVariable(declaration: IrVariable, data: Nothing?) {
        super.visitVariable(declaration, data)
        checkTypeRecursively(declaration, declaration.type)
    }

    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        super.visitFunction(declaration, data)

        checkTypeRecursively(declaration, declaration.returnType)
        for (parameter in declaration.parameters) {
            checkTypeRecursively(parameter, parameter.type)
        }
    }
}