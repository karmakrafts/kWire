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
import dev.karmakrafts.kwire.compiler.util.call
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType

internal interface Allocator {
    companion object {
        fun from(context: KWirePluginContext, allocator: IrExpression): Allocator {
            return object : Allocator {
                override val context: KWirePluginContext get() = context
                override fun get(): IrExpression = allocator
            }
        }
    }

    val context: KWirePluginContext

    fun get(): IrExpression

    fun allocate(
        size: IrExpression, alignment: IrExpression, dispatchReceiver: IrExpression = get()
    ): IrCall {
        return context.kwireSymbols.allocatorAllocate.call( // @formatter:off
            dispatchReceiver = dispatchReceiver,
            valueArguments = mapOf(
                "size" to size,
                "alignment" to alignment
            )
        ) // @formatter:on
    }

    fun allocate(
        type: IrType, dispatchReceiver: IrExpression = get()
    ): IrCall {
        val layout = type.computeMemoryLayout(context)
        return allocate( // @formatter:off
            size = context.toNUInt(layout.emitSize(context)),
            alignment = context.toNUInt(layout.emitAlignment(context)),
            dispatchReceiver = dispatchReceiver
        ) // @formatter:on
    }

    fun reallocate(
        address: IrExpression, size: IrExpression, alignment: IrExpression, dispatchReceiver: IrExpression = get()
    ): IrCall {
        return context.kwireSymbols.allocatorReallocate.call( // @formatter:off
            dispatchReceiver = dispatchReceiver,
            valueArguments = mapOf(
                "address" to address,
                "size" to size,
                "alignment" to alignment
            )
        ) // @formatter:on
    }

    fun free(
        address: IrExpression, dispatchReceiver: IrExpression = get()
    ): IrCall = context.kwireSymbols.allocatorFree.call( // @formatter:off
        dispatchReceiver = dispatchReceiver,
        valueArguments = mapOf("address" to address)
    ) // @formatter:on
}