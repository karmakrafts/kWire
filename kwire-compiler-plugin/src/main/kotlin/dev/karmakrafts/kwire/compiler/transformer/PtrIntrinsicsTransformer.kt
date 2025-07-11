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
import dev.karmakrafts.kwire.compiler.ffi.CallingConvention
import dev.karmakrafts.kwire.compiler.ffi.FFI
import dev.karmakrafts.kwire.compiler.memory.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.constNUInt
import dev.karmakrafts.kwire.compiler.util.getFunctionType
import dev.karmakrafts.kwire.compiler.util.getNativeType
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
import dev.karmakrafts.kwire.compiler.util.reinterpret
import dev.karmakrafts.kwire.compiler.util.resolveFromReceiver
import dev.karmakrafts.kwire.compiler.util.times
import dev.karmakrafts.kwire.compiler.util.toBlock
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isNothing
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.isUnsignedType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.isFunctionTypeOrSubtype
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.name.Name

internal object PtrIntrinsicKey : GeneratedDeclarationKey() {
    val origin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
}

internal val ptrIntrinsicOrigin: IrStatementOrigin = IrStatementOriginImpl("kWire-PtrIntrinsicsTransformer")

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
        return when {
            type.isNumPtr() -> {
                val pointedType = requireNotNull(type.getPointedType()) { "Could not retrieve pointed type" }
                context.createNumPtr(value, pointedType)
            }
            type.isFunPtr() -> {
                val pointedType = requireNotNull(type.getPointedType()) { "Could not retrieve pointed type" }
                context.createFunPtr(value, pointedType)
            }
            type.isPtr() -> {
                val pointedType = requireNotNull(type.getPointedType()) { "Could not retrieve pointed type" }
                context.createPtr(value, pointedType)
            }
            type.isVoidPtr() || type.isAddress(context) -> context.createVoidPtr(value)
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

    private fun createUpcallTrampoline(
        reference: IrFunctionReference,
        bufferParam: IrValueParameter,
        parameters: List<IrValueParameter>,
        function: IrSimpleFunction
    ): IrExpression {
        val argumentTypes = parameters.map { it.type }
        val arguments = argumentTypes.map { context.ffi.getArgument(bufferParam.symbol.load(), it) }
        val namedArguments = HashMap<String, IrExpression>()
        for(argumentIndex in arguments.indices) {
            namedArguments[parameters[argumentIndex].name.asString()] = arguments[argumentIndex]
        }
        val returnType = function.returnType
        // If the call doesn't have a reuslt, simply make the call
        if(returnType.isUnit() || returnType.isNothing()) {
            return function.call( // @formatter:off
                dispatchReceiver = reference.dispatchReceiver,
                valueArguments = namedArguments
            ) // @formatter:on
        }
        return context.ffi.putArgument(bufferParam.symbol.load(), function.call( // @formatter:off
            dispatchReceiver = reference.dispatchReceiver,
            valueArguments = namedArguments
        ))
        // @formatter:on
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitFunctionRef(call: IrCall, data: IntrinsicContext): IrExpression {
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
        val function = targetSymbol.owner
        if (function !is IrSimpleFunction) {
            reportError("Function reference target must be a callable function", call)
            return call
        }
        val parameters = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val descriptor = context.ffi.getDescriptor(
            returnType = function.returnType, parameterTypes = parameters.map { it.type })
        val callingConvention = when (call.target.name.asString()) {
            "ref" -> CallingConvention.CDECL
            "refThisCall" -> CallingConvention.THISCALL
            "refStdCall" -> CallingConvention.STDCALL
            "refFastCall" -> CallingConvention.FASTCALL
            else -> {
                reportError("Could not determine function reference calling convention", call)
                return call
            }
        }
        val stubFunction = context.irFactory.buildFun {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            returnType = function.returnType
            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
            visibility = DescriptorVisibilities.LOCAL
            name = Name.special("<anonymous>")
        }.apply {
            parent = data.parent
            val bufferParam = addValueParameter {
                name = Name.identifier("buffer")
                type = context.ffi.ffiArgBufferType.defaultType
                kind = IrParameterKind.Regular
            }
            body = DeclarationIrBuilder(
                generatorContext = context,
                symbol = symbol,
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET
            ).irExprBody(createUpcallTrampoline(reference, bufferParam, parameters, function))
        }
        val stubFunctionExpr = IrFunctionExpressionImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = stubFunction.getFunctionType(context),
            function = stubFunction,
            origin = IrStatementOrigin.LAMBDA
        )
        return context.ffi.createUpcallStub(descriptor, callingConvention, stubFunctionExpr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitLocalRef(call: IrCall, data: IntrinsicContext): IrExpression {
        val function = call.target
        val reference = call.arguments[function.parameters.first { it.kind == IrParameterKind.ExtensionReceiver }]
        if (reference == null) {
            reportError("Could not retrieve extension reciver for NumPtr reference", call)
            return call
        }
        val allocationScope = data.allocationScope

        // Check if there's already a ref to the same local var, and if so, load it instead
        when (reference) {
            is IrGetValue -> {
                val variable = reference.symbol.owner
                val ref = allocationScope.getLocalReference(variable)
                if (ref != null) return ref
            }
        }

        val pointerType = call.type
        val pointedType = pointerType.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type for NumPtr reference", call)
            return call
        }

        val addressVariable = IrVariableImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = PtrIntrinsicKey.origin,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier("__kwire_ref_address_${call.hashCode()}__"),
            type = pointerType,
            isVar = false,
            isConst = false,
            isLateinit = false
        ).apply {
            parent = data.parent
            initializer = allocationScope.allocate(pointedType).reinterpret(context, pointerType)
        }

        // Check if we are ref'ing a local variable, and if so, add a reference to the allocation scope
        var insertAddressInLine = true
        when (reference) {
            is IrGetValue -> {
                val variable = reference.symbol.owner
                allocationScope.addLocalReference(variable, addressVariable)
                insertAddressInLine = false
            }
        }

        val statements = ArrayList<IrStatement>()
        if (insertAddressInLine) statements += addressVariable
        statements += pointedType.computeMemoryLayout(context).emitWrite(context, addressVariable.load(), reference)
        statements += addressVariable.load()

        return statements.toBlock(pointerType, ptrIntrinsicOrigin)
    }

    private fun emitRef(call: IrCall, data: IntrinsicContext): IrExpression {
        val type = call.typeArguments.first()
        if (type == null) {
            reportError("Could not resolve reference type for reference", call)
            return call
        }
        return when { // @formatter:off
            type.isFunctionTypeOrSubtype() -> emitFunctionRef(call, data)
            type.isPrimitiveType(false) || type.isUnsignedType(false) || type.getNativeType() != null ->
                emitLocalRef(call, data)
            type.isPointed(context) -> emitLocalRef(call, data)
            else -> {
                reportError("Incompatible reference type for reference", call)
                call
            }
        } // @formatter:on
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
            if (rawAddress == null) {
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
        if (value == null) {
            reportError("Could not determine value expression for pointer write", call)
            return call
        }
        val indexParam = params.firstOrNull { it.name.asString() == "index" }
        val index = indexParam?.let { call.arguments[it] }

        // Handle subscript-operator memory offsets
        if (index != null) {
            val rawAddress = dispatchReceiver.getRawAddress()
            if (rawAddress == null) {
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
        if (pointerType == null) {
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