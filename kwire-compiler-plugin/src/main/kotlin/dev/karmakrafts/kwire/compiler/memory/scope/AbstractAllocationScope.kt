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
import dev.karmakrafts.kwire.compiler.memory.layout.getMemoryLayout
import dev.karmakrafts.kwire.compiler.util.getABIType
import dev.karmakrafts.kwire.compiler.util.load
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.name.Name
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal abstract class AbstractAllocationScope( // @formatter:off
    val context: KWirePluginContext,
    val parent: IrDeclarationParent
) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
        val statementOrigin: IrStatementOrigin = IrStatementOriginImpl("kWire-AllocationScope")
    }

    private val _stack: Lazy<IrVariable> = lazy {
        IrVariableImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = declOrigin,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier("__kwire_stack_frame_${Uuid.random().toHexString()}__"),
            type = context.memoryStack.memoryStackType.defaultType,
            isVar = false,
            isConst = false,
            isLateinit = false
        ).apply {
            this.parent = this@AbstractAllocationScope.parent // Stack frame variable is contained in scope parent
            initializer = context.memoryStack.push(context.memoryStack.get())
        }
    }

    inline val stackVariable: IrVariable get() = _stack.value
    inline val hasAllocations: Boolean get() = _stack.isInitialized()
    protected val localRef2Address: HashMap<IrValueDeclaration, IrValueDeclaration> = HashMap()
    protected val address2LocalRef: HashMap<IrValueDeclaration, IrValueDeclaration> = HashMap()

    fun loadStack(): IrGetValue = stackVariable.load()
    fun getLocalAddress(variable: IrValueDeclaration): IrValueDeclaration? = localRef2Address[variable]
    fun getLocalReference(address: IrValueDeclaration): IrValueDeclaration? = address2LocalRef[address]

    fun addLocalAddress(variable: IrValueDeclaration, address: IrVariable) {
        require(variable !in localRef2Address) { "Local reference for ${variable.dump()} already exists" }
        localRef2Address[variable] = address
        address2LocalRef[address] = variable
    }

    fun allocate(size: IrExpression, alignment: IrExpression): IrExpression {
        return context.memoryStack.allocate( // @formatter:off
            size = size,
            alignment = alignment,
            dispatchReceiver = loadStack()
        ) // @formatter:on
    }

    fun allocate(type: IrType): IrExpression {
        val abiType = type.getABIType(context)
        val layout =
            abiType?.getMemoryLayout() ?: error("Could not compute memory layout for ABI type ${abiType?.symbolName}")
        return allocate( // @formatter:off
            size = context.toNUInt(layout.emitSize(context)),
            alignment = context.toNUInt(layout.emitAlignment(context))
        ) // @formatter:on
    }

    abstract fun injectIfNeeded(element: IrElement)
}