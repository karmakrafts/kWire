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
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.types.IrType

internal abstract class TypeUsageChecker(
    context: KWirePluginContext
) : AbstractChecker(context) {
    abstract fun checkType(declaration: IrDeclaration, type: IrType)

    override fun visitTypeAlias(declaration: IrTypeAlias) {
        super.visitTypeAlias(declaration)
        checkType(declaration, declaration.expandedType)
    }

    override fun visitField(declaration: IrField) {
        super.visitField(declaration)
        checkType(declaration, declaration.type)
    }

    override fun visitVariable(declaration: IrVariable) {
        super.visitVariable(declaration)
        checkType(declaration, declaration.type)
    }

    override fun visitFunction(declaration: IrFunction) {
        super.visitFunction(declaration)
        checkType(declaration, declaration.returnType)
        for (parameter in declaration.parameters) {
            checkType(parameter, parameter.type)
        }
    }
}