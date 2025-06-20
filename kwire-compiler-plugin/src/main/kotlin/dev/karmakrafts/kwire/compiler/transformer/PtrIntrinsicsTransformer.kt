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
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.isAddress
import dev.karmakrafts.kwire.compiler.util.isFunPtr
import dev.karmakrafts.kwire.compiler.util.isNumPtr
import dev.karmakrafts.kwire.compiler.util.isPtr
import dev.karmakrafts.kwire.compiler.util.isVoidPtr
import dev.karmakrafts.kwire.compiler.util.resolveFromReceiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.target

internal class PtrIntrinsicsTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.PTR_NULL,
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_SET,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET,
    KWireIntrinsicType.PTR_INVOKE
)) {
    private inline fun emitTypedNull( // @formatter:off
        call: IrCall,
        type: IrType,
        emitter: (IrExpression, IrType) -> IrExpression
    ): IrExpression { // @formatter:on
        val pointedType = type.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type for NumPtr", call)
            return call
        }
        return emitter(constNUInt(context, 0UL), pointedType)
    }

    private fun emitNull(call: IrCall): IrExpression {
        val type = call.typeArguments.first()
        if (type == null) {
            reportError("Could not determine pointer type for nullptr", call)
            return call
        }
        return when {
            type.isNumPtr() -> emitTypedNull(call, type, context::createNumPtr)
            type.isFunPtr() -> emitTypedNull(call, type, context::createFunPtr)
            type.isPtr() -> emitTypedNull(call, type, context::createPtr)
            type.isVoidPtr() || type.isAddress(context) -> context.createVoidPtr(constNUInt(context, 0UL))
            else -> {
                reportError("Could not determine pointer type for nullptr", call)
                call
            }
        }
    }

    private fun emitRef(call: IrCall): IrExpression {
        // TODO: implement allocation scopes
        TODO("Implement this")
    }

    private fun emitDeref(call: IrCall): IrExpression {
        val type = call.type.resolveFromReceiver(call)
        if (type == null) {
            reportError("Could not resolve reference type for dereference", call)
            return call
        }
        val layout = context.getOrComputeMemoryLayout(type)
        return layout.emitRead(context, call.dispatchReceiver!!)
    }

    private fun emitSet(call: IrCall): IrExpression {
        val function = call.target
        val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val valueParam = params.first { it.name.asString() == "value" }
        val type = valueParam.type.resolveFromReceiver(call)
        if (type == null) {
            reportError("Could not resolve reference type for pointer write", call)
            return call
        }
        val layout = context.getOrComputeMemoryLayout(type)
        val value = call.arguments[valueParam]!!
        return layout.emitWrite(context, call.dispatchReceiver!!, value)
    }

    private fun emitArrayGet(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    private fun emitArraySet(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    private fun emitInvoke(call: IrCall): IrExpression {
        val function = call.target
        val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val argsParam = params.first { it.name.asString() == "args" }
        val argsValue = call.arguments[argsParam]!!
        check(argsValue is IrVararg) { "Function pointer invocation requires variadic arguments" }
        val argBuffer = context.ffi.extractArgumentsIntoBuffer(argsValue)
        if (argBuffer == null) {
            reportError("Could not extract pointer invocation arguments", call)
            return call
        }
        return call
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_NULL -> emitNull(expression)
            KWireIntrinsicType.PTR_REF -> emitRef(expression)
            KWireIntrinsicType.PTR_DEREF -> emitDeref(expression)
            KWireIntrinsicType.PTR_SET -> emitSet(expression)
            KWireIntrinsicType.PTR_ARRAY_GET -> emitArrayGet(expression)
            KWireIntrinsicType.PTR_ARRAY_SET -> emitArraySet(expression)
            KWireIntrinsicType.PTR_INVOKE -> emitInvoke(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}