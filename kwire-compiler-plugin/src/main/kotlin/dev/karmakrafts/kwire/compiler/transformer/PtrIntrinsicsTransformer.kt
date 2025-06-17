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
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.isNumber

internal class PtrIntrinsicsTransformer(
    context: KWirePluginContext
) : KWireIntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET
)) {
    private fun emitRef(call: IrCall): IrExpression {
        val type = call.typeArguments.first()!!
        // We need to build a NumPtr
        if(type.isNumber()) {
            return call
        }
        // We need to build a Ptr
        return call
    }

    private fun emitDeref(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    override fun visitIntrinsic(expression: IrCall, data: KWireIntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_REF -> emitRef(expression)
            KWireIntrinsicType.PTR_DEREF -> emitDeref(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}