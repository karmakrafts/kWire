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
import dev.karmakrafts.kwire.compiler.util.constNUInt
import dev.karmakrafts.kwire.compiler.util.isNumPtr
import dev.karmakrafts.kwire.compiler.util.isPtr
import dev.karmakrafts.kwire.compiler.util.isVoidPtr
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.typeOrFail

internal class PtrIntrinsicsTransformer(
    private val context: KWirePluginContext
) : KWireIntrinsicTransformer(setOf( // @formatter:off
    KWireIntrinsicType.PTR_NULL,
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET
)) {
    // @formatter:on
    private fun emitNull(call: IrCall): IrExpression {
        val type = call.typeArguments.first()!!
        return when { // @formatter:off
            type.isNumPtr() -> context.createNumPtr(
                constNUInt(context, 0U),
                (type as IrSimpleType).arguments.first().typeOrFail
            )
            type.isPtr() -> context.createPtr(
                constNUInt(context, 0U),
                (type as IrSimpleType).arguments.first().typeOrFail
            )
            type.isVoidPtr() -> context.createVoidPtr(constNUInt(context, 0U))
            else -> error("Unsupported pointer type for nullptr() intrinsic")
        } // @formatter:on
    }

    override fun visitIntrinsic(expression: IrCall, data: KWireIntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_NULL -> emitNull(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}