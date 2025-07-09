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
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET

internal class BlockAllocationScope(
    context: KWirePluginContext, parent: IrDeclarationParent
) : AbstractAllocationScope(context, parent) {
    constructor(parentScope: AbstractAllocationScope) : this(parentScope.context, parentScope.parent)

    override fun injectIfNeeded(element: IrElement) {
        if (element !is IrBlock || !hasAllocations) return
        val statements = element.statements
        val oldStatements = statements.toList()
        val type = element.type
        val tryExpression = IrTryImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = type,
            tryResult = (localReferences.values + oldStatements).toBlock(type, statementOrigin),
            catches = emptyList(),
            finallyExpression = context.memoryStack.pop(loadStack())
        )
        statements.clear()
        statements += stackVariable
        statements += tryExpression
    }
}