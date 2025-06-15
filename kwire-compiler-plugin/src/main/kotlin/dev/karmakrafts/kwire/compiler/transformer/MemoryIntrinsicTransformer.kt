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
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.constInt
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

internal class MemoryIntrinsicTransformer(
    private val context: KWirePluginContext
) : KWireIntrinsicTransformer(setOf( // @formatter:off
    KWireIntrinsicType.SIZE_OF,
    KWireIntrinsicType.ALIGN_OF
)) {
    // @formatter:on
    private fun emitSizeOf(call: IrCall): IrExpression {
        val type = call.typeArguments.first() ?: return constInt(context, 0)
        val layout = context.computeMemoryLayout(type)
        return layout.emitSize(context)
    }

    private fun emitAlignOf(call: IrCall): IrExpression {
        val type = call.typeArguments.first() ?: return constInt(context, 0)
        val layout = context.computeMemoryLayout(type)
        return layout.emitAlignment(context)
    }

    override fun visitIntrinsic( // @formatter:off
        expression: IrCall,
        data: KWireIntrinsicContext,
        type: KWireIntrinsicType
    ): IrElement { // @formatter:on
        return when (type) {
            KWireIntrinsicType.SIZE_OF -> emitSizeOf(expression)
            KWireIntrinsicType.ALIGN_OF -> emitAlignOf(expression)
            else -> error("Unsupported intrinsic type $type for MemoryIntrinsicTransformer")
        }
    }
}