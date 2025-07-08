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
import dev.karmakrafts.kwire.compiler.memory.Allocator
import dev.karmakrafts.kwire.compiler.memory.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.reinterpret
import dev.karmakrafts.kwire.compiler.util.times
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.target

internal class AllocatorIntrinsicTransformer( // @formatter:off
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf(
    KWireIntrinsicType.ALLOCATOR_ALLOC,
    KWireIntrinsicType.ALLOCATOR_ALLOC_ARRAY
)) {
    // @formatter:on
    private inline fun emitAllocation(
        call: IrCall, allocator: Allocator.() -> IrExpression
    ): IrExpression {
        val function = call.target
        val extensionParam = function.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        if (extensionParam == null) {
            reportError("Could not find extension parameter for allocator intrinsic", call)
            return call
        }
        val extensionReceiver = call.arguments[extensionParam]
        if (extensionReceiver == null) {
            reportError("Could not retrieve extension receiver for allocator intrinsic", call)
            return call
        }
        val type = call.typeArguments.first()?.typeOrNull
        if (type == null) {
            reportError("Could not retrieve pointed type for allocator intrinsic")
            return call
        }
        return Allocator.from(context, extensionReceiver).allocator()
    }

    private fun emitAlloc(call: IrCall): IrExpression = emitAllocation(call) {
        val pointerType = call.type
        val pointedType = pointerType.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type for allocation intrinsic", call)
            return call
        }
        allocate(pointedType).reinterpret(context, pointerType)
    }

    private fun emitAllocArray(call: IrCall): IrExpression = emitAllocation(call) {
        val function = call.target
        val pointerType = call.type
        val pointedType = pointerType.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type for allocation intrinsic", call)
            return call
        }
        val layout = pointedType.computeMemoryLayout(context)
        val countParam = function.parameters.firstOrNull { it.name.asString() == "count" }
        if (countParam == null) {
            reportError("Could not retrieve count parameter for array allocation intrinsic", call)
            return call
        }
        val count = call.arguments[countParam]
        if (count == null) {
            reportError("Could not retrieve count argument for array allocation intrinsic", call)
            return call
        }
        return allocate(
            size = context.toNUInt(layout.emitSize(context)).times(count),
            alignment = context.toNUInt(layout.emitAlignment(context))
        ).reinterpret(context, function.returnType)
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.ALLOCATOR_ALLOC -> emitAlloc(expression)
            KWireIntrinsicType.ALLOCATOR_ALLOC_ARRAY -> emitAllocArray(expression)
            else -> error("Unsupported intrinsic type $type for AllocatorIntrinsicTransformer")
        }
    }
}