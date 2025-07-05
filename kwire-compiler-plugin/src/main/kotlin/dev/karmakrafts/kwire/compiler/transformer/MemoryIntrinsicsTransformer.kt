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
import dev.karmakrafts.kwire.compiler.memory.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.constNUInt
import dev.karmakrafts.kwire.compiler.util.getCustomAlignment
import dev.karmakrafts.kwire.compiler.util.hasCustomAlignment
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.target

internal class MemoryIntrinsicsTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.SIZE_OF,
    KWireIntrinsicType.ALIGN_OF,
    KWireIntrinsicType.OFFSET_OF
)) {
    // @formatter:on
    private fun emitSizeOf(call: IrCall): IrExpression {
        val type = call.typeArguments.first() ?: return constNUInt(context, 0UL)
        val layout = type.computeMemoryLayout(context)
        return context.toNUInt(layout.emitSize(context))
    }

    private fun emitAlignOf(call: IrCall): IrExpression {
        val type = call.typeArguments.first() ?: return constNUInt(context, 0UL)
        if (type.hasCustomAlignment()) {
            return constNUInt(context, type.getCustomAlignment()!!.toULong())
        }
        val layout = type.computeMemoryLayout(context)
        return context.toNUInt(layout.emitAlignment(context))
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitOffsetOf(call: IrCall): IrExpression {
        val function = call.target
        val ref = call.arguments[function.parameters.first { it.name.asString() == "field" }]
        if (ref !is IrPropertyReference) {
            reportError("offsetOf parameter needs to be a property reference", call)
            return call
        }
        val property = ref.symbol.owner
        val clazz = property.parentAsClass
        val layout = clazz.defaultType.computeMemoryLayout(context)
        val index = clazz.properties.indexOf(property)
        if (index == -1) {
            reportError("Could not determine field index for offsetOf", call)
            return call
        }
        return context.toNUInt(layout.emitOffsetOf(context, index))
    }

    override fun visitIntrinsic( // @formatter:off
        expression: IrCall,
        data: IntrinsicContext,
        type: KWireIntrinsicType
    ): IrElement { // @formatter:on
        return when (type) {
            KWireIntrinsicType.SIZE_OF -> emitSizeOf(expression)
            KWireIntrinsicType.ALIGN_OF -> emitAlignOf(expression)
            KWireIntrinsicType.OFFSET_OF -> emitOffsetOf(expression)
            else -> error("Unsupported intrinsic type $type for MemoryIntrinsicTransformer")
        }
    }
}