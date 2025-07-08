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

package dev.karmakrafts.kwire.compiler.memory

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.load
import dev.karmakrafts.kwire.compiler.util.toBlock
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name

internal data class AllocationScope( // @formatter:off
    val context: KWirePluginContext,
    val function: IrDeclaration,
    val parent: IrDeclarationParent
) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        private val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
        val statementOrigin: IrStatementOrigin = IrStatementOriginImpl("kWire-AllocationScope")
    }

    private val _stack: Lazy<IrVariable> = lazy {
        IrVariableImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = declOrigin,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier("__kwire_stack_frame_${parent.hashCode()}__"),
            type = context.memoryStack.memoryStackType.defaultType,
            isVar = false,
            isConst = false,
            isLateinit = false
        ).apply {
            this.parent = this@AllocationScope.parent // Stack frame variable is contained in scope owner function
            initializer = context.memoryStack.push(context.memoryStack.get())
        }
    }

    inline val stackVariable: IrVariable get() = _stack.value
    inline val hasAllocations: Boolean get() = _stack.isInitialized()

    // Store receiver symbol of .ref() call -> address accessor so we can re-use the existing refs
    private val localReferences: HashMap<IrValueDeclaration, IrVariable> = HashMap()

    fun getLocalReference(variable: IrValueDeclaration): IrExpression? {
        return localReferences[variable]?.load()
    }

    fun addLocalReference(variable: IrValueDeclaration, address: IrVariable) {
        require(variable !in localReferences) { "Local reference for ${variable.dump()} already exists" }
        localReferences[variable] = address
    }

    fun getStack(): IrGetValue = stackVariable.load()

    fun allocate(size: IrExpression, alignment: IrExpression): IrExpression {
        return context.memoryStack.allocate( // @formatter:off
            size = size,
            alignment = alignment,
            dispatchReceiver = getStack()
        ) // @formatter:on
    }

    fun allocate(type: IrType): IrExpression {
        val layout = type.computeMemoryLayout(context)
        return allocate( // @formatter:off
            size = context.toNUInt(layout.emitSize(context)),
            alignment = context.toNUInt(layout.emitAlignment(context))
        ) // @formatter:on
    }

    /**
     * If the memory stack was used in the current scope,
     * we need to emit the generated stack instance variable
     * and wrap everything in a try-finally so we can safely pop it.
     */
    fun injectIfNeeded() {
        if (!hasAllocations) return // We don't need to inject if this scope doesn't have any allocations
        val body = when (function) {
            is IrFunction -> function.body
            is IrAnonymousInitializer -> function.body
            else -> return
        } ?: return
        val returnType = when (function) {
            is IrFunction -> function.returnType
            is IrAnonymousInitializer -> context.irBuiltIns.unitType
            else -> return
        }
        val tryExpression = IrTryImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = returnType,
            tryResult = (localReferences.values + body.statements).toBlock(returnType, statementOrigin),
            catches = emptyList(),
            finallyExpression = context.memoryStack.pop(getStack())
        )
        when (function) {
            is IrFunction -> function.body = context.irFactory.createExpressionBody( // @formatter:off
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                expression = listOf(stackVariable, tryExpression).toBlock(returnType)
            )
            is IrAnonymousInitializer -> {
                val statements = function.body.statements
                statements.clear()
                statements += stackVariable
                statements += tryExpression
            }
        }
    }
}