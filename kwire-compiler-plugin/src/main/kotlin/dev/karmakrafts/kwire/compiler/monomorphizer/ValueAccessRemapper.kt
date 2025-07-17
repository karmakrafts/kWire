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

import dev.karmakrafts.kwire.compiler.util.valuesToKeys
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid

private class ValueAccessRemapper(
    private val localMappings: Map<IrValueDeclaration, IrValueDeclaration>,
    accessMappings: Map<IrValueAccessExpression, IrValueAccessExpression>
) : IrVisitorVoid() {
    private val revAccessMappings: Map<IrValueAccessExpression, IrValueAccessExpression> =
        accessMappings.valuesToKeys(HashMap())

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitValueAccess(expression: IrValueAccessExpression) {
        super.visitValueAccess(expression)
        val oldExpression = revAccessMappings[expression] ?: return
        val newVariable = localMappings[oldExpression.symbol.owner]
        expression.symbol = newVariable?.symbol ?: return // Update the targeted symbol
    }
}

private class ValueAccessFinder : IrVisitorVoid() {
    val locals: ArrayList<IrValueDeclaration> = ArrayList()
    val accesses: ArrayList<IrValueAccessExpression> = ArrayList()

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitVariable(declaration: IrVariable) {
        locals += declaration
        super.visitVariable(declaration)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitValueAccess(expression: IrValueAccessExpression) {
        if (expression.symbol.owner is IrVariable) {
            accesses += expression
        }
        super.visitValueAccess(expression)
    }
}

internal fun IrFunction.remapValueAccesses(oldFunction: IrFunction) {
    val oldFinder = ValueAccessFinder()
    oldFunction.acceptVoid(oldFinder)
    val finder = ValueAccessFinder()
    acceptVoid(finder)
    // Make sure our list sizes match
    check(oldFinder.locals.size == finder.locals.size) { "Mismatched locals count for value access remapping" }
    check(oldFinder.accesses.size == finder.accesses.size) { "Mismatched access count for value access remapping" }
    // Create the old->new mappings
    val localMappings = oldFinder.locals.zip(finder.locals).toMap()
    val accessMappings = oldFinder.accesses.zip(finder.accesses).toMap()
    // Peform the actual remapping
    acceptVoid(ValueAccessRemapper(localMappings, accessMappings))
}