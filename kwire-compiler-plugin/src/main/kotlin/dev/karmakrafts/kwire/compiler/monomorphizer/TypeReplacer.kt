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

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.typeWithArguments
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.visitors.IrTransformer

private class TypeReplacer(
    private val substitutions: Map<IrTypeParameterSymbol, IrType>
) : IrTransformer<Nothing?>() {
    private fun resolveType(type: IrType): IrType {
        if (type.isTypeParameter()) {
            val symbol = type.classifierOrNull as? IrTypeParameterSymbol ?: return type
            return substitutions[symbol] ?: type
        }
        if (type is IrSimpleType && type.arguments.isNotEmpty()) {
            val clazz = type.classifierOrNull ?: return type
            return clazz.typeWithArguments(type.arguments.map {
                if (it !is IrType) it
                else resolveType(it)
            })
        }
        return type
    }

    override fun visitExpression(expression: IrExpression, data: Nothing?): IrExpression {
        expression.type = resolveType(expression.type)
        return super.visitExpression(expression, data)
    }

    override fun visitCall(expression: IrCall, data: Nothing?): IrElement {
        expression.type = resolveType(expression.type)
        val oldTypeArgs = expression.typeArguments.toList()
        expression.typeArguments.clear()
        expression.typeArguments += oldTypeArgs.map {
            if (it == null) null
            else resolveType(it)
        }
        return super.visitCall(expression, data)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: Nothing?): IrStatement {
        declaration.expandedType = resolveType(declaration.expandedType)
        return super.visitTypeAlias(declaration, data)
    }

    override fun visitVariable(declaration: IrVariable, data: Nothing?): IrStatement {
        declaration.type = resolveType(declaration.type)
        return super.visitVariable(declaration, data)
    }

    override fun visitFunction(declaration: IrFunction, data: Nothing?): IrStatement {
        declaration.returnType = resolveType(declaration.returnType)
        for (parameter in declaration.parameters) {
            parameter.type = resolveType(parameter.type)
        }
        return super.visitFunction(declaration, data)
    }

    override fun visitField(declaration: IrField, data: Nothing?): IrStatement {
        declaration.type = resolveType(declaration.type)
        return super.visitField(declaration, data)
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: Nothing?): IrStatement {
        declaration.type = resolveType(declaration.type)
        return super.visitValueParameter(declaration, data)
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: Nothing?): IrStatement {
        declaration.superTypes = declaration.superTypes.map { resolveType(it) }
        return super.visitTypeParameter(declaration, data)
    }
}

fun IrElement.replaceTypes(substitutions: Map<IrTypeParameterSymbol, IrType>) {
    transform(TypeReplacer(substitutions), null)
}