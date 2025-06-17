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
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.getIntrinsicType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.ir.visitors.IrTransformer

internal abstract class KWireIntrinsicTransformer( // @formatter:off
    protected val context: KWirePluginContext,
    private val types: Set<KWireIntrinsicType>
) : IrTransformer<KWireIntrinsicContext>(), MessageCollectorExtensions by context { // @formatter:on
    abstract fun visitIntrinsic( // @formatter:off
        expression: IrCall,
        data: KWireIntrinsicContext,
        type: KWireIntrinsicType
    ): IrElement // @formatter:on

    override fun visitCall(expression: IrCall, data: KWireIntrinsicContext): IrElement {
        // Transform using depth-first search strategy
        val transformedCall = super.visitCall(expression, data)
        if (transformedCall is IrCall) {
            val function = transformedCall.target
            val type = function.getIntrinsicType() ?: return transformedCall
            if (type !in types) return transformedCall
            return visitIntrinsic(transformedCall, data, type)
        }
        return transformedCall
    }
}