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
import dev.karmakrafts.kwire.compiler.memory.layout.BuiltinMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.layout.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.layout.getMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.ResolvedType
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.createBlock
import dev.karmakrafts.kwire.compiler.util.createExpression
import dev.karmakrafts.kwire.compiler.util.declarationBuilder
import dev.karmakrafts.kwire.compiler.util.getABIType
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.getRawAddress
import dev.karmakrafts.kwire.compiler.util.isValueType
import dev.karmakrafts.kwire.compiler.util.load
import dev.karmakrafts.kwire.compiler.util.minus
import dev.karmakrafts.kwire.compiler.util.plus
import dev.karmakrafts.kwire.compiler.util.reinterpret
import dev.karmakrafts.kwire.compiler.util.resolveFromReceiver
import dev.karmakrafts.kwire.compiler.util.times
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isNothing
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isFunctionTypeOrSubtype
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.name.Name
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal object PtrIntrinsicKey : GeneratedDeclarationKey() {
    val origin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
}

internal val ptrIntrinsicOrigin: IrStatementOrigin = IrStatementOriginImpl("kWire-PtrIntrinsicsTransformer")

@OptIn(ExperimentalUuidApi::class)
internal class PtrIntrinsicsTransformer(
    context: KWirePluginContext
) : IntrinsicTransformer(context, setOf( // @formatter:off
    KWireIntrinsicType.PTR_REF,
    KWireIntrinsicType.PTR_DEREF,
    KWireIntrinsicType.PTR_SET,
    KWireIntrinsicType.PTR_ARRAY_GET,
    KWireIntrinsicType.PTR_ARRAY_SET,
    KWireIntrinsicType.PTR_INVOKE,
    KWireIntrinsicType.PTR_PLUS,
    KWireIntrinsicType.PTR_MINUS
)) {
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
        // If the call doesn't have a result, simply make the call
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
            return reportError("Could not retrieve function reference from extension receiver", call)
        }
        val targetSymbol =
            reference.reflectionTarget ?: return reportError("Could not determine target for function reference", call)
        val function = targetSymbol.owner
        if (function !is IrSimpleFunction) {
            return reportError("Function reference target must be a callable function", call)
        }
        val parameters = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val descriptor = context.ffi.getDescriptor(
            returnType = function.returnType, parameterTypes = parameters.map { it.type })
        val callingConvention = when (call.target.name.asString()) {
            "ref" -> CallingConvention.CDECL
            "refThisCall" -> CallingConvention.THISCALL
            "refStdCall" -> CallingConvention.STDCALL
            "refFastCall" -> CallingConvention.FASTCALL
            else -> return reportError("Could not determine function reference calling convention", call)
        }
        val parent =
            data.parentOrNull ?: return reportError("Could not determine parent of function reference intrinsic", call)
        val stubFunction = context.irFactory.buildFun {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            returnType = function.returnType
            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
            visibility = DescriptorVisibilities.LOCAL
            name = Name.special("<anonymous>")
        }.apply {
            this.parent = parent
            val bufferParam = addValueParameter {
                name = Name.identifier("buffer")
                type = context.ffi.ffiArgBufferType.defaultType
                kind = IrParameterKind.Regular
            }
            body = declarationBuilder(context).irExprBody(
                createUpcallTrampoline(
                    reference, bufferParam, parameters, function
                )
            )
        }
        val stubFunctionExpr = stubFunction.createExpression(context)
        return context.ffi.createUpcallStub(descriptor, callingConvention, stubFunctionExpr)
    }

    private fun emitLocalLValueRef(reference: IrGetValue, variable: IrVariable, data: IntrinsicContext): IrExpression {
        return reference
    }

    // L-value references are emitted in-line after transforming the local and are cached by the allocation scope
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitLValueRef(reference: IrGetValue, data: IntrinsicContext): IrExpression {
        return when (val target = reference.symbol.owner) {
            is IrValueParameter -> emitRValueRef(reference, data) // TODO: implement proper handling for parameters
            is IrVariable -> emitLocalLValueRef(reference, target, data)
            is IrField -> reportError(
                "Obtaining the address of a field is currently not possible", reference
            )

            else -> reportError("Unknown target for l-value reference", reference)
        }
    }

    // R-value references are emitted in-line and are not cached by the allocation scope
    private fun emitRValueRef(temporary: IrExpression, data: IntrinsicContext): IrExpression {
        val allocationScope = data.allocationScope

        val pointerType = temporary.type
        val pointedType = pointerType.getPointedType() ?: return reportError(
            "Could not determine pointed type for r-value reference", temporary
        )

        val parent =
            data.parentOrNull ?: return reportError("Could not determine parent for r-value reference", temporary)

        val addressVariable = buildVariable(
            parent = parent,
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = PtrIntrinsicKey.origin,
            name = Name.identifier("__kwire_ref_address_${Uuid.random().toHexString()}__"),
            type = pointerType
        ).apply {
            initializer = allocationScope.allocate(pointedType).reinterpret(context, pointerType)
        }

        return temporary // TODO: implement this
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitValueRef(call: IrCall, data: IntrinsicContext): IrExpression {
        val function = call.target
        val reference = call.arguments[function.parameters.first { it.kind == IrParameterKind.ExtensionReceiver }]
        if (reference == null) {
            return reportError("Could not retrieve extension receiver for NumPtr reference", call)
        }
        return when (reference) {
            is IrGetValue -> emitLValueRef(reference, data)
            else -> emitRValueRef(reference, data) // Everything else is considered an r-value for the time being
        }
    }

    private fun emitRef(call: IrCall, data: IntrinsicContext): IrExpression {
        val type = call.typeArguments.first()
        if (type == null) {
            return reportError("Could not resolve reference type for reference", call)
        }
        return when { // @formatter:off
            type.isFunctionTypeOrSubtype() -> emitFunctionRef(call, data)
            type.isValueType(context) -> emitValueRef(call, data)
            else -> reportError("Incompatible reference type for reference", call)
        } // @formatter:on
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitDeref(call: IrCall): IrExpression {
        val function = call.target
        val address = call.dispatchReceiver ?: return reportError(
            "Could not retrieve dispatch receiver for pointer dereference", call
        )
        val resolvedType = call.type.resolveFromReceiver(function, call.typeArguments.filterNotNull(), address)
            ?: return reportError("Could not resolve reference type for dereference", call)
        if (resolvedType !is ResolvedType.Concrete) {
            return reportError("Dereference requires concrete type", call)
        }
        val pointerType = address.type
        val layout = resolvedType.type.getABIType(context)?.getMemoryLayout()
            ?: return reportError("Could not compute memory layout for dereference", call)

        val indexParam = function.parameters.firstOrNull { it.kind == IrParameterKind.Regular }
        val index = indexParam?.let { call.arguments[it] }

        // Handle subscript-operator memory offsets
        if (index != null) {
            val rawAddress = address.getRawAddress()
                ?: return reportError("Could not retrieve raw address of pointer in dereference", call)
            val offset = context.toNUInt(layout.emitSize(context)).times(context.toNUInt(index))
            return layout.emitRead(context, rawAddress.plus(offset).reinterpret(context, pointerType))
        }

        return layout.emitRead(context, address)
    }

    private fun emitSet(call: IrCall): IrExpression {
        val function = call.target
        val dispatchReceiver = call.dispatchReceiver ?: return reportError(
            "Could not retrieve dispatch receiver for pointer dereference", call
        )

        val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val valueParam = params.single { it.name.asString() == "value" }
        val resolvedType =
            valueParam.type.resolveFromReceiver(function, call.typeArguments.filterNotNull(), dispatchReceiver)
                ?: return reportError("Could not resolve reference type for pointer write", call)
        if (resolvedType !is ResolvedType.Concrete) {
            reportError("Write to address requires concrete type", call)
            return call
        }

        val pointerType = dispatchReceiver.type
        val layout = resolvedType.type.getABIType(context)?.getMemoryLayout()
            ?: return reportError("Could not compute memory layout for pointer write", call)
        val value = call.arguments[valueParam] ?: return reportError(
            "Could not determine value expression for pointer write", call
        )

        val indexParam = params.firstOrNull { it.name.asString() == "index" }
        val index = indexParam?.let { call.arguments[it] }
        // Handle subscript-operator memory offsets
        if (index != null) {
            val rawAddress = dispatchReceiver.getRawAddress()
                ?: return reportError("Could not retrieve raw address of pointer in pointer write", call)
            val offset = context.toNUInt(layout.emitSize(context)).times(context.toNUInt(index))
            return layout.emitWrite(context, rawAddress.plus(offset).reinterpret(context, pointerType), value)
        }

        return layout.emitWrite(context, dispatchReceiver, value)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun getPtrArrayValue(instance: IrExpression): IrExpression? {
        val clazz = instance.type.getClass() ?: return null
        val property = clazz.properties.firstOrNull { it.name.asString() == "value" }
        return property?.getter?.call(dispatchReceiver = instance)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun emitArrayGet(call: IrCall): IrExpression {
        val function = call.target
        val dispatchReceiver = call.dispatchReceiver ?: return reportError(
            "Could not retrieve dispatch receiver for array get intrinsic", call
        )
        val resolvedType = call.type.resolveFromReceiver(function, call.typeArguments.filterNotNull(), dispatchReceiver)
            ?: return reportError("Could not reify type for pointer array get intrinsic", call)
        if (resolvedType !is ResolvedType.Concrete) {
            reportError("Pointer array get requires concrete type", call)
            return call
        }
        val delegate = getPtrArrayValue(dispatchReceiver)
            ?: return reportError("Could not retrieve array delegate for array get intrinsic", call)
        val indexParam = function.parameters.single { it.name.asString() == "index" }
        val index = call.arguments[indexParam]
            ?: return reportError("Could not retrieve index parameter for array get intrinsic", call)
        val delegateType = delegate.type
        val delegateClass = delegateType.getClass()
            ?: return reportError("Could not determine pointer array delegate class for array get intrinsic", call)
        val getOperator = delegateClass.functions.single { it.name.asString() == "get" }
        return getOperator.call(
            dispatchReceiver = delegate, valueArguments = mapOf("index" to index)
        ).reinterpret(context, resolvedType.type)
    }

    private fun emitArraySet(call: IrCall): IrExpression {
        val function = call.target
        val valueParam = function.parameters.single { it.name.asString() == "value" }
        val value =
            call.arguments[valueParam] ?: return reportError("Could not retrieve value for array set intrinsic", call)
        TODO("Implement meeeeee")
    }

    private fun emitInvoke(call: IrCall, data: IntrinsicContext): IrExpression {
        val function = call.target
        val params = function.parameters.filter { it.kind == IrParameterKind.Regular }

        val argsParam = params.single { it.name.asString() == "args" }
        val argsValue = call.arguments[argsParam]!!
        check(argsValue is IrVararg) { "Function pointer invocation requires variadic arguments" }
        val (_, argBufferInit, argBufferVar) = context.ffi.extractArgumentsIntoBuffer(argsValue)
        if (argBufferVar == null) {
            return reportError("Could not extract function pointer invocation arguments", call)
        }

        val (returnTypeArg, functionTypeArg) = call.typeArguments
        val returnType = returnTypeArg!!.typeOrFail
        val functionType = functionTypeArg!!.typeOrFail as? IrSimpleType
            ?: return reportError("Could not determine underlying function type of pointer invocation", call)
        val paramTypes =
            functionType.arguments.dropLast(1) // First n - 1 type args are parameter arguments for FunctionN
        val descriptor = context.ffi.getDescriptor(returnType, paramTypes.map { it.typeOrFail })

        val address = call.arguments[function.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }]
            ?: return reportError("Could not retrieve address for pointer invocation", call)

        val parent = data.parentOrNull ?: return reportError("Could not determine parent for r-value reference", call)

        val resultVariable = buildVariable(
            parent = parent,
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            origin = FFI.declOrigin,
            name = Name.identifier("__kwire_result_${Uuid.random().toHexString()}__"),
            type = returnType
        ).apply {
            initializer = context.ffi.call(returnType, address, descriptor, argBufferVar.load())
        }

        return (argBufferInit + listOf( // @formatter:off
            resultVariable,
            context.ffi.releaseArgBuffer(argBufferVar.load()),
            resultVariable.load()
        )).createBlock(returnType) // @formatter:on
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private inline fun emitPointerArithmeticOp(
        call: IrCall, op: IrExpression.(IrExpression) -> IrExpression
    ): IrExpression {
        val function = call.target
        val dispatchReceiver =
            call.dispatchReceiver ?: return reportError("Pointer arithmetic intrinsic requires dispatch receiver", call)
        // Reify pointer type from class type parameter
        var pointerType: IrType? = call.type
        val resolvedPointedType = pointerType?.getPointedType()?.resolveFromReceiver(
            function, call.typeArguments.filterNotNull(), dispatchReceiver
        ) ?: return reportError("Could not determine pointed type for pointer arithmetic intrinsic", call)
        pointerType = when (resolvedPointedType) {
            is ResolvedType.Star -> pointerType.getClass()?.symbol?.starProjectedType
            is ResolvedType.Concrete -> pointerType.getClass()?.typeWith(resolvedPointedType.type)
        } ?: return reportError("Could not reify pointer type for pointer arithmetic intrinsic", call)

        val layout = when (resolvedPointedType) {
            is ResolvedType.Star -> BuiltinMemoryLayout.BYTE // Assume byte data on wildcard with no type size
            is ResolvedType.Concrete -> resolvedPointedType.type.getABIType(context)?.getMemoryLayout()
        } ?: return reportError("Could not compute memory layout for pointer arithmetic operation", call)
        if (layout is ReferenceMemoryLayout) {
            reportError("Cannot perform pointer arithmetic operation on pointer to reference type", call)
            return call
        }
        val lhs = dispatchReceiver.getRawAddress()
            ?: return reportError("Could not retrieve left hand side expression for pointer arithmetic intrinsic", call)
        val rhs =
            call.arguments[function.parameters.first { it.kind == IrParameterKind.Regular }] ?: return reportError(
                "Could not retrieve right hand side expression for pointer arithmetic intrinsic", call
            )
        return lhs.op(context.toNUInt(rhs).times(context.toNUInt(layout.emitSize(context))))
            .reinterpret(context, pointerType)
    }

    private fun emitPlus(call: IrCall): IrElement {
        return emitPointerArithmeticOp(call) { plus(it) }
    }

    private fun emitMinus(call: IrCall): IrElement {
        return emitPointerArithmeticOp(call) { minus(it) }
    }

    override fun visitIntrinsic(expression: IrCall, data: IntrinsicContext, type: KWireIntrinsicType): IrElement {
        return when (type) {
            KWireIntrinsicType.PTR_REF -> emitRef(expression, data)
            KWireIntrinsicType.PTR_DEREF -> emitDeref(expression)
            KWireIntrinsicType.PTR_SET -> emitSet(expression)
            KWireIntrinsicType.PTR_ARRAY_GET -> emitArrayGet(expression)
            KWireIntrinsicType.PTR_ARRAY_SET -> emitArraySet(expression)
            KWireIntrinsicType.PTR_INVOKE -> emitInvoke(expression, data)
            KWireIntrinsicType.PTR_PLUS -> emitPlus(expression)
            KWireIntrinsicType.PTR_MINUS -> emitMinus(expression)
            else -> error("Unsupported intrinsic type $type for PtrIntrinsicsTransformer")
        }
    }
}