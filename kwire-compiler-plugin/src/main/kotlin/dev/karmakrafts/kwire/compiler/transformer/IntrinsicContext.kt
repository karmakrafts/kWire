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
import dev.karmakrafts.kwire.compiler.util.load
import dev.karmakrafts.kwire.compiler.util.toBlock
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrScript
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrGetValue
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
        private val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(AllocationScopeKey)
    }

    private val variableName: Name by lazy {
        Name.identifier("__kwire_stack_frame_${function.hashCode()}__")
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
        ).apply {
            parent = function // Stack frame variable is contained in scope owner function
            initializer = context.memoryStack.push(context.memoryStack.get())
        }
    }

    inline val stackVariable: IrVariable get() = _stack.value
    inline val hasAllocations: Boolean get() = _stack.isInitialized()

    fun getStack(): IrGetValue = stackVariable.load()

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
            tryResult = body.statements.toBlock(function.returnType),
            catches = emptyList(),
            finallyExpression = context.memoryStack.pop(getStack())
        )
        function.body = context.irFactory.createExpressionBody( // @formatter:off
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            expression = listOf(stackVariable, tryExpression).toBlock(function.returnType)
        )
    }
}

internal class IntrinsicContext(  // @formatter:off
    val context: KWirePluginContext
) { // @formatter:on
    private val allocationScopeStack: Stack<AllocationScope> = Stack()
    inline val allocationScope: AllocationScope get() = allocationScopeStack.peek()

    private val parentStack: Stack<IrDeclarationParent> = Stack()
    inline val parent: IrDeclarationParent get() = parentStack.peek()

    fun pushScript(script: IrScript) {
        parentStack.push(script)
    }

    fun popScript() {
        parentStack.pop()
    }

    fun pushFile(file: IrFile) {
        parentStack.push(file)
    }

    fun popFile() {
        parentStack.pop()
    }

    fun pushClass(clazz: IrClass) {
        parentStack.push(clazz)
    }

    fun popClass() {
        parentStack.pop()
    }

    fun pushFunction(function: IrFunction): AllocationScope {
        val scope = AllocationScope(context, function)
        allocationScopeStack.push(scope)
        parentStack.push(function)
        return scope
    }

    fun popFunction() {
        allocationScope.injectIfNeeded()
        allocationScopeStack.pop()
        parentStack.pop()
    }
}