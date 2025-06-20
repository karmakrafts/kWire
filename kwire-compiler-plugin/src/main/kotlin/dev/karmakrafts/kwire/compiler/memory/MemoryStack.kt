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
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol

internal class MemoryStack(
    override val context: KWirePluginContext
) : Allocator {
    val memoryStackCompanionType: IrClassSymbol = context.referenceClass(KWireNames.MemoryStack.Companion.id)!!
    val memoryStackGet: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.MemoryStack.Companion.get).first()
    val memoryStackPush: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.MemoryStack.push).first()
    val memoryStackPop: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.MemoryStack.pop).first()

    override fun get(): IrCall = memoryStackGet.call(dispatchReceiver = memoryStackCompanionType.getObjectInstance())
    fun push(stack: IrExpression): IrCall = memoryStackPush.call(dispatchReceiver = stack)
    fun pop(stack: IrExpression): IrCall = memoryStackPop.call(dispatchReceiver = stack)
}