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

package dev.karmakrafts.kwire.compiler.memory.scope

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.toBlock
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.statements

internal class FunctionAllocationScope( // @formatter:off
    context: KWirePluginContext,
    parent: IrDeclarationParent
) : AbstractAllocationScope(context, parent) { // @formatter:on
    override fun injectIfNeeded(element: IrElement) {
        if (!hasAllocations) return // We don't need to inject if this scope doesn't have any allocations
        val body = when (element) {
            is IrFunction -> element.body
            is IrAnonymousInitializer -> element.body
            else -> return
        } ?: return
        val returnType = when (element) {
            is IrFunction -> element.returnType
            is IrAnonymousInitializer -> context.irBuiltIns.unitType
            else -> return
        }
        val tryExpression = IrTryImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = returnType,
            tryResult = (localReferences.values + body.statements).toBlock(returnType, statementOrigin),
            catches = emptyList(),
            finallyExpression = context.memoryStack.pop(loadStack())
        )
        when (element) {
            is IrFunction -> element.body = context.irFactory.createExpressionBody( // @formatter:off
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                expression = listOf(stackVariable, tryExpression).toBlock(returnType)
            )
            is IrAnonymousInitializer -> {
                val statements = element.body.statements
                statements.clear()
                statements += stackVariable
                statements += tryExpression
            }
        }
    }
}