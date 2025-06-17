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
import dev.karmakrafts.kwire.compiler.util.isNumPtr
import dev.karmakrafts.kwire.compiler.util.isPtr
import dev.karmakrafts.kwire.compiler.util.isVoidPtr
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.target

internal class PtrIntrinsicsTransformer(
    context: KWirePluginContext
) : KWireIntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.PTR_NULL,
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_SET,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET
)) {
    private fun emitNull(call: IrCall): IrExpression {
        val type = call.typeArguments.first()
        if(type == null) {
            reportError("Could not determine pointer type for nullptr", call)
            return call
        }
        return when {
            type.isNumPtr() -> {
                val pointedType = type.getPointedType()
                if(pointedType == null) {
                    reportError("Could not determine pointed type for NumPtr", call)
                    return call
                }
                context.createNumPtr(constNUInt(context, 0UL), pointedType)
            }
            type.isPtr() -> {
                val pointedType = type.getPointedType()
                if(pointedType == null) {
                    reportError("Could not determine pointed type for Ptr", call)
                    return call
                }
                context.createPtr(constNUInt(context, 0UL), pointedType)
            }
            // Void pointers and raw addresses are handled the same since VoidPtr is polymorphic over Address
            type.isVoidPtr() || type.isAddress(context) -> context.createVoidPtr(constNUInt(context, 0UL))
            else -> {
                reportError("Could not determine pointer type for nullptr", call)
                call
            }
        }
    }

    private fun emitRef(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitDeref(call: IrCall): IrExpression {
        val function = call.target

        // Determine the outgoing type (parameter)
        val type = function.returnType // This references the N type for NumPtr and the T type for Ptr
        if (!type.isTypeParameter()) {
            reportError("Could not determine type of dereference", call)
            return call
        }
        val typeParam = (type.classifierOrNull as? IrTypeParameterSymbol)?.owner
        if(typeParam == null) {
            reportError("Could not determine type parameter symbol of dereference", call)
            return call
        }

        // Determine the concrete pointed type of the given pointer dispatch receiver
        val dispatchType = call.dispatchReceiver?.type
        if(dispatchType == null || dispatchType !is IrSimpleType) {
            reportError("Could not determine dispatch receiver type of dereference", call)
            return call
        }
        val dispatchClass = dispatchType.getClass()
        if(dispatchClass == null) {
            reportError("Could not determine dispatch receiver class of dereference", call)
            return call
        }
        val classTypeParam = dispatchClass.typeParameters.find { it.name == typeParam.name }
        if(classTypeParam == null) {
            reportError("Could not determine parent type parameter of dereference", call)
            return call
        }
        val actualType = dispatchType.arguments[classTypeParam.index].typeOrNull
        if(actualType == null) {
            reportError("Could not determine concrete type of dereference", call)
            return call
        }

        // Determine the memory layout of the concrete type and emit a read
        val layout = context.computeMemoryLayout(actualType)
        return layout.emitRead(context, call.dispatchReceiver!!)
    }

    private fun emitSet(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    private fun emitArrayGet(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    private fun emitArraySet(call: IrCall): IrExpression {
        TODO("Implement this")
    }

    override fun visitIntrinsic(expression: IrCall, data: KWireIntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_NULL -> emitNull(expression)
            KWireIntrinsicType.PTR_REF -> emitRef(expression)
            KWireIntrinsicType.PTR_DEREF -> emitDeref(expression)
            KWireIntrinsicType.PTR_SET -> emitSet(expression)
            KWireIntrinsicType.PTR_ARRAY_GET -> emitArrayGet(expression)
            KWireIntrinsicType.PTR_ARRAY_SET -> emitArraySet(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}