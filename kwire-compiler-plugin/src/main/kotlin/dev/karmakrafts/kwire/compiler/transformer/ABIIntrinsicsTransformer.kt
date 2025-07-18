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
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

internal class ABIIntrinsicsTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(
    context, setOf(
        KWireIntrinsicType.ABI_GET_MODULE_DATA
    )
) {
    private fun emitModuleData(): IrExpression {
        return context.kwireModuleData.moduleDataClass.symbol.getObjectInstance()
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.ABI_GET_MODULE_DATA -> emitModuleData()
            else -> error("Unsupported intrinsic type $type for ABIIntrinsicsTransformer")
        }
    }
}