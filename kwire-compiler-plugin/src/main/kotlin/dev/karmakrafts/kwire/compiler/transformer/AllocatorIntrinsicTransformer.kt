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
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.target

internal class AllocatorIntrinsicTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf(
    KWireIntrinsicType.ALLOCATOR_ALLOC,
    KWireIntrinsicType.ALLOCATOR_ALLOC_ARRAY
)) {
    private fun emitAlloc(call: IrCall): IrExpression {
        val function = call.target
        val extensionParam = function.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        if(extensionParam == null) {
            reportError("Could not find extension parameter for allocator intrinsic", call)
            return call
        }
        val allocator = call.arguments[extensionParam]
        if(allocator == null) {
            reportError("Could not retrieve instance for allocator intrinsic", call)
            return call
        }
        return call
    }

    private fun emitAllocArray(call: IrCall): IrExpression {
        return call
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when(type) {
            KWireIntrinsicType.ALLOCATOR_ALLOC -> emitAlloc(expression)
            KWireIntrinsicType.ALLOCATOR_ALLOC_ARRAY -> emitAllocArray(expression)
            else -> error("Unsupported intrinsic type $type for AllocatorIntrinsicTransformer")
        }
    }
}