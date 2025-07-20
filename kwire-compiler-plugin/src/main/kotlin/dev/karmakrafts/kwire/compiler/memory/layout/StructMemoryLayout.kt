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

package dev.karmakrafts.kwire.compiler.memory.layout

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.constInt
import dev.karmakrafts.kwire.compiler.util.max
import dev.karmakrafts.kwire.compiler.util.plus
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import dev.karmakrafts.kwire.abi.type.StructType as ABIStructType

internal data class StructMemoryLayout(
    override val abiType: ABIStructType
) : MemoryLayout {
    val fields: List<MemoryLayout>
        get() = abiType.fields.map { it.getMemoryLayout() ?: error("Could not get field memory layout") }

    override fun emitSize(context: KWirePluginContext): IrExpression {
        return when (fields.size) {
            0 -> constInt(context, 0)
            1 -> fields.first().emitSize(context)
            else -> {
                val (first, second) = fields
                var expr = first.emitSize(context).plus(second.emitSize(context))
                for (index in 2..<fields.size) {
                    expr = expr.plus(fields[index].emitSize(context))
                }
                expr
            }
        }
    }

    override fun emitAlignment(context: KWirePluginContext): IrExpression {
        return when (fields.size) {
            0 -> constInt(context, 0)
            1 -> fields.first().emitAlignment(context)
            else -> {
                val (first, second) = fields
                var expr = first.emitAlignment(context).max(context, second.emitAlignment(context))
                for (index in 2..<fields.size) {
                    expr = expr.max(context, fields[index].emitAlignment(context))
                }
                expr
            }
        }
    }

    override fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression {
        return when (index) {
            0 -> constInt(context, 0)
            1 -> fields.first().emitSize(context)
            else -> {
                val (first, second) = fields
                var expr = first.emitSize(context).plus(second.emitSize(context))
                for (fieldIndex in 2..<index) {
                    expr = expr.plus(fields[fieldIndex].emitSize(context))
                }
                expr
            }
        }
    }

    override fun emitDefault(context: KWirePluginContext): IrExpression {
        TODO("Finish implementation")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression {
        TODO("Finish implementation")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression {
        TODO("Finish implementation")
    }
}