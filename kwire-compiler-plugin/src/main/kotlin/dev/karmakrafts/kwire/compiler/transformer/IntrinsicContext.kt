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

package dev.karmakrafts.kwire.compiler.transformer

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.toComposite
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name
import java.util.*

internal data object AllocationScopeKey : GeneratedDeclarationKey()

internal data class AllocationScope( // @formatter:off
    val context: KWirePluginContext,
    val function: IrFunction
) { // @formatter:on
    companion object {
        private val variableName: Name = Name.special("<__stack_frame__>")
        private val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(AllocationScopeKey)
        private val statementOrigin: IrStatementOrigin = IrStatementOriginImpl("AllocationScope")
    }

    private val _stack: Lazy<IrVariable> = lazy {
        IrVariableImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = declOrigin,
            symbol = IrVariableSymbolImpl(),
            name = variableName,
            type = context.memoryStack.memoryStackType.defaultType,
            isVar = false,
            isConst = false,
            isLateinit = false
        )
    }

    inline val stackVariable: IrVariable get() = _stack.value
    inline val hasAllocations: Boolean get() = _stack.isInitialized()

    fun getStack(): IrGetValue = IrGetValueImpl(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        type = context.memoryStack.memoryStackType.defaultType,
        symbol = stackVariable.symbol
    )

    /**
     * If the memory stack was used in the current scope,
     * we need to emit the generated stack instance variable
     * and wrap everything in a try-finally so we can safely pop it.
     */
    fun injectIfNeeded() {
        if (!hasAllocations) return // We don't need to inject if this scope doesn't have any allocations
        val body = function.body ?: return
        val tryExpression = IrTryImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = function.returnType,
            tryResult = body.statements.toComposite(function.returnType),
            catches = emptyList(),
            finallyExpression = context.memoryStack.pop(getStack())
        )
        function.body = context.irFactory.createExpressionBody( // @formatter:off
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            expression = listOf(stackVariable, tryExpression).toComposite(function.returnType)
        )
    }
}

internal class IntrinsicContext(  // @formatter:off
    val context: KWirePluginContext
) { // @formatter:on
    private val allocationScopeStack: Stack<AllocationScope> = Stack()
    inline val allocationScope: AllocationScope get() = allocationScopeStack.peek()

    fun pushAllocationScope(function: IrFunction): AllocationScope {
        val scope = AllocationScope(context, function)
        allocationScopeStack.push(scope)
        return scope
    }

    fun popAllocationScope(scope: AllocationScope) {
        scope.injectIfNeeded()
        allocationScopeStack.remove(scope)
    }
}