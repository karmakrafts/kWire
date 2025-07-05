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
import dev.karmakrafts.kwire.compiler.ffi.FFI
import dev.karmakrafts.kwire.compiler.memory.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.constNUInt
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.getRawAddress
import dev.karmakrafts.kwire.compiler.util.isAddress
import dev.karmakrafts.kwire.compiler.util.isFunPtr
import dev.karmakrafts.kwire.compiler.util.isNumPtr
import dev.karmakrafts.kwire.compiler.util.isPointed
import dev.karmakrafts.kwire.compiler.util.isPtr
import dev.karmakrafts.kwire.compiler.util.isVoidPtr
import dev.karmakrafts.kwire.compiler.util.load
import dev.karmakrafts.kwire.compiler.util.minus
import dev.karmakrafts.kwire.compiler.util.plus
import dev.karmakrafts.kwire.compiler.util.resolveFromReceiver
import dev.karmakrafts.kwire.compiler.util.times
import dev.karmakrafts.kwire.compiler.util.toBlock
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isNumber
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.isFunctionTypeOrSubtype
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.name.Name

internal class PtrIntrinsicsTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.PTR_NULL,
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_SET,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET,
    KWireIntrinsicType.PTR_INVOKE,
    KWireIntrinsicType.PTR_PLUS,
    KWireIntrinsicType.PTR_MINUS
)) {
    private fun emitTypedPointer(
        errorExpr: IrExpression,
        type: IrType,
        value: IrExpression
    ): IrExpression {
        val isOpaque = type.isVoidPtr()
        val pointedType = if(isOpaque) context.irBuiltIns.unitType else type.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type for pointer", errorExpr)
            return errorExpr
        }
        return when {
            type.isNumPtr() -> context.createNumPtr(value, pointedType)
            type.isFunPtr() -> context.createFunPtr(value, pointedType)
            type.isPtr() -> context.createPtr(value, pointedType)
            isOpaque -> context.createVoidPtr(value)
            else -> {
                reportError("Unrecognized pointer type ${type.render()}", errorExpr)
                return errorExpr
            }
        }
    }

    private fun emitTypedNull(
        call: IrCall,
        type: IrType
    ): IrExpression = emitTypedPointer(call, type, constNUInt(context, 0UL))

    private fun emitNull(call: IrCall): IrExpression {
        val type = call.typeArguments.first()
        if (type == null) {
            reportError("Could not determine pointer type for nullptr", call)
            return call
        }
        return emitTypedNull(call, type)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitFunctionRef(call: IrCall): IrExpression {
        val reference = call.arguments[call.target.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }]
        if (reference == null || reference !is IrFunctionReference) {
            reportError("Could not retrieve function reference from extension receiver", call)
            return call
        }
        val targetSymbol = reference.reflectionTarget
        if (targetSymbol == null) {
            reportError("Could not determine target for function reference", call)
            return call
        }
        // TODO: finish me
        return call
    }

    private fun emitRef(call: IrCall, data: IntrinsicContext): IrExpression {
        val type = call.typeArguments.first()
        if (type == null) {
            reportError("Could not resolve reference type for reference", call)
            return call
        }
        return when {
            type.isFunctionTypeOrSubtype() -> emitFunctionRef(call)
            type.isNumber() -> call
            type.isPointed(context) -> call
            else -> {
                reportError("Could not determine reference type for reference", call)
                call
            }
        }
    }

    private fun emitDeref(call: IrCall): IrExpression {
        val type = call.type.resolveFromReceiver(call)
        if (type == null) {
            reportError("Could not resolve reference type for dereference", call)
            return call
        }
        val dispatchReceiver = call.dispatchReceiver
        if (dispatchReceiver == null) {
            reportError("Could not retrieve dispatch receiver for pointer dereference", call)
            return call
        }
        val pointerType = dispatchReceiver.type
        val layout = type.computeMemoryLayout(context)

        val function = call.target
        val indexParam = function.parameters.firstOrNull { it.kind == IrParameterKind.Regular }
        val index = indexParam?.let { call.arguments[it] }

        // Handle subscript-operator memory offsets
        if (index != null) {
            val rawAddress = dispatchReceiver.getRawAddress()
            if(rawAddress == null) {
                reportError("Could not retrieve raw address of pointer in dereference", call)
                return call
            }
            val offset = context.toNUInt(layout.emitSize(context)).times(context.toNUInt(index))
            return layout.emitRead(context, emitTypedPointer(call, pointerType, rawAddress.plus(offset)))
        }

        return layout.emitRead(context, dispatchReceiver)
    }

    private fun emitSet(call: IrCall): IrExpression {
        val function = call.target
        val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val valueParam = params.single { it.name.asString() == "value" }
        val type = valueParam.type.resolveFromReceiver(call)
        if (type == null) {
            reportError("Could not resolve reference type for pointer write", call)
            return call
        }

        val dispatchReceiver = call.dispatchReceiver
        if (dispatchReceiver == null) {
            reportError("Could not retrieve dispatch receiver for pointer dereference", call)
            return call
        }
        val pointerType = dispatchReceiver.type

        val layout = type.computeMemoryLayout(context)
        val value = call.arguments[valueParam]
        if(value == null) {
            reportError("Could not determine value expression for pointer write", call)
            return call
        }
        val indexParam = params.firstOrNull { it.name.asString() == "index" }
        val index = indexParam?.let { call.arguments[it] }

        // Handle subscript-operator memory offsets
        if(index != null) {
            val rawAddress = dispatchReceiver.getRawAddress()
            if(rawAddress == null) {
                reportError("Could not retrieve raw address of pointer in pointer write", call)
                return call
            }
            val offset = context.toNUInt(layout.emitSize(context)).times(context.toNUInt(index))
            return layout.emitWrite(context, emitTypedPointer(call, pointerType, rawAddress.plus(offset)), value)
        }

        return layout.emitWrite(context, dispatchReceiver, value)
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

        val argsParam = params.single { it.name.asString() == "args" }
        val argsValue = call.arguments[argsParam]!!
        check(argsValue is IrVararg) { "Function pointer invocation requires variadic arguments" }
        val (_, argBufferInit, argBufferVar) = context.ffi.extractArgumentsIntoBuffer(argsValue)
        if (argBufferVar == null) {
            reportError("Could not extract function pointer invocation arguments", call)
            return call
        }

        val (returnTypeArg, functionTypeArg) = call.typeArguments
        val returnType = returnTypeArg!!.typeOrFail
        val functionType = functionTypeArg!!.typeOrFail as? IrSimpleType
        if (functionType == null) {
            reportError("Could not determine underlying function type of pointer invocation", call)
            return call
        }
        val paramTypes =
            functionType.arguments.dropLast(1) // First n - 1 type args are parameter arguments for FunctionN
        val descriptor = context.ffi.getDescriptor(returnType, paramTypes.map { it.typeOrFail })

        val address = call.arguments[function.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }]
        if (address == null) {
            reportError("Could not retrieve address for pointer invocation", call)
            return call
        }

        val resultVariable = IrVariableImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = FFI.declOrigin,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier("__kwire_result_${call.hashCode()}__"),
            type = returnType,
            isVar = false,
            isConst = false,
            isLateinit = false
        ).apply {
            initializer = context.ffi.call(returnType, address, descriptor, argBufferVar.load())
        }

        return (argBufferInit + listOf( // @formatter:off
            resultVariable,
            context.ffi.releaseArgBuffer(argBufferVar.load()),
            resultVariable.load()
        )).toBlock(returnType) // @formatter:on
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private inline fun emitPointerArithmeticOp(
        call: IrCall, op: IrExpression.(IrExpression) -> IrExpression
    ): IrExpression {
        // Reify pointer type from class type parameter
        var pointerType: IrType? = call.type
        val pointedType = pointerType?.getPointedType()?.resolveFromReceiver(call)
        if (pointedType == null) {
            reportError("Could not determine pointed type for pointer arithmetic intrinsic", call)
            return call
        }
        pointerType = pointerType.getClass()?.typeWith(pointedType)
        if(pointerType == null) {
            reportError("Could not reify pointer type for pointer arithmetic intrinsic", call)
            return call
        }

        val layout = pointedType.computeMemoryLayout(context)
        if (layout is ReferenceMemoryLayout) {
            reportError("Cannot perform pointer arithmetic operation on pointer to reference type", call)
            return call
        }
        val function = call.target
        val dispatchReceiver = call.dispatchReceiver
        if (dispatchReceiver == null) {
            reportError("Pointer arithmetic intrinsic requires dispatch receiver", call)
            return call
        }
        val lhs = dispatchReceiver.getRawAddress()
        if (lhs == null) {
            reportError("Could not retrieve left hand side expression for pointer arithmetic intrinsic", call)
            return call
        }
        val rhs = call.arguments[function.parameters.first { it.kind == IrParameterKind.Regular }]
        if (rhs == null) {
            reportError("Could not retrieve right hand side expression for pointer arithmetic intrinsic", call)
            return call
        }
        return emitTypedPointer(
            call, pointerType, lhs.op(context.toNUInt(rhs).times(context.toNUInt(layout.emitSize(context))))
        )
    }

    private fun emitPlus(call: IrCall): IrElement {
        return emitPointerArithmeticOp(call) { plus(it) }
    }

    private fun emitMinus(call: IrCall): IrElement {
        return emitPointerArithmeticOp(call) { minus(it) }
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_NULL -> emitNull(expression)
            KWireIntrinsicType.PTR_REF -> emitRef(expression, data)
            KWireIntrinsicType.PTR_DEREF -> emitDeref(expression)
            KWireIntrinsicType.PTR_SET -> emitSet(expression)
            KWireIntrinsicType.PTR_ARRAY_GET -> emitArrayGet(expression)
            KWireIntrinsicType.PTR_ARRAY_SET -> emitArraySet(expression)
            KWireIntrinsicType.PTR_INVOKE -> emitInvoke(expression)
            KWireIntrinsicType.PTR_PLUS -> emitPlus(expression)
            KWireIntrinsicType.PTR_MINUS -> emitMinus(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}