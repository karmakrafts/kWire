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

package dev.karmakrafts.kwire.compiler.ffi

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getEnumValue
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.isAssignableFrom
import dev.karmakrafts.kwire.compiler.util.isPointerAssignableFrom
import dev.karmakrafts.kwire.compiler.util.isPtr
import dev.karmakrafts.kwire.compiler.util.isSameAs
import dev.karmakrafts.kwire.compiler.util.load
import dev.karmakrafts.kwire.compiler.util.reinterpret
import dev.karmakrafts.kwire.compiler.util.toVararg
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrSpreadElement
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.Name
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal data object FFIGenerationKey : GeneratedDeclarationKey()

internal data class ExtractedFFIArgBuffer(
    val isDirectCall: Boolean = false,
    val statements: List<IrStatement> = emptyList(),
    val bufferVariable: IrVariable? = null
) {
    companion object {
        val empty: ExtractedFFIArgBuffer = ExtractedFFIArgBuffer(true, emptyList(), null)
    }
}

@OptIn(ExperimentalUuidApi::class)
internal class FFI(
    private val context: KWirePluginContext
) {
    companion object {
        internal val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(FFIGenerationKey)
    }

    val ffiType: IrClassSymbol = context.referenceClass(KWireNames.FFI.id)!!
    val ffiCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFI.Companion.id)!!
    val ffiCreateUpcallStub: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.FFI.createUpcallStub).first()

    val ffiTypeType: IrClassSymbol = context.referenceClass(KWireNames.FFIType.id)!!

    val ffiDescriptorType: IrClassSymbol = context.referenceClass(KWireNames.FFIDescriptor.id)!!
    val ffiDescriptorCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFIDescriptor.Companion.id)!!

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val ffiDescriptorOf: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.FFIDescriptor.Companion.of).first { symbol ->
            val params = symbol.owner.parameters.filter { it.kind == IrParameterKind.Regular }
            params.last().isVararg
        }

    val ffiArgBufferType: IrClassSymbol = context.referenceClass(KWireNames.FFIArgBuffer.id)!!
    val ffiArgBufferPutAll: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.FFIArgBuffer.putAll).first()
    val ffiArgBufferCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFIArgBuffer.Companion.id)!!
    val ffiArgBufferAcquire: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.FFIArgBuffer.Companion.acquire).first()
    val ffiArgBufferRelease: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.FFIArgBuffer.release).first()

    val callingConventionType: IrClassSymbol = context.referenceClass(KWireNames.CallingConvention.id)!!

    fun getDescriptor(returnType: IrExpression, parameterTypes: List<IrExpression>): IrExpression {
        return ffiDescriptorOf.call( // @formatter:off
            dispatchReceiver = ffiDescriptorCompanionType.getObjectInstance(),
            valueArguments = mapOf(
                "returnType" to returnType,
                "parameterTypes" to parameterTypes.toVararg(context, ffiTypeType.defaultType)
            )
        ) // @formatter:on
    }

    fun getDescriptor(returnType: IrType, parameterTypes: List<IrType>): IrExpression {
        return getDescriptor(
            returnType = returnType.getFFIType(context)!!(context),
            parameterTypes = parameterTypes.map { it.getFFIType(context)!!(context) })
    }

    fun createUpcallStub(
        descriptor: IrExpression, callingConvention: CallingConvention, function: IrExpression
    ): IrCall = ffiCreateUpcallStub.call( // @formatter:off
        dispatchReceiver = ffiCompanionType.getObjectInstance(),
        valueArguments = mapOf(
            "descriptor" to descriptor,
            "callingConvention" to callingConvention.getEnumValue(callingConventionType) { name },
            "function" to function
        )
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun call(
        type: IrType,
        address: IrExpression,
        descriptor: IrExpression,
        argBuffer: IrExpression,
        callingConvention: CallingConvention? = null
    ): IrCall {
        return ffiType.owner.functions.first { function ->
            if (!function.name.asString().startsWith("call") || !function.returnType.isSameAs(type)) return@first false
            val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
            params.last().type == ffiArgBufferType.defaultType
        }.call( // @formatter:off
            dispatchReceiver = ffiCompanionType.getObjectInstance(),
            valueArguments = mapOf(
                "address" to address,
                "descriptor" to descriptor,
                "callingConvention" to (address.type
                    .getCallingConvention()
                    ?: callingConvention
                    ?: CallingConvention.CDECL)
                    .getEnumValue(callingConventionType) { name },
                "args" to argBuffer
            )
        ) // @formatter:on
    }

    fun acquireArgBuffer(): IrCall = ffiArgBufferAcquire.call(
        dispatchReceiver = ffiArgBufferCompanionType.getObjectInstance()
    )

    fun releaseArgBuffer(buffer: IrExpression): IrCall = ffiArgBufferRelease.call(
        dispatchReceiver = buffer
    )

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun getArgument(buffer: IrExpression, type: IrType): IrExpression {
        val function = ffiArgBufferType.owner.functions.first { function ->
            if (!function.name.asString().startsWith("get")) return@first false
            val returnType = function.returnType
            type.isAssignableFrom(context, returnType)
        }
        val result = function.call(dispatchReceiver = buffer)
        if (result.type.isPtr()) {
            return result.reinterpret(context, type)
        }
        return result
    }

    fun putArguments(buffer: IrExpression, argumentArray: IrExpression): IrCall {
        return ffiArgBufferPutAll.call( // @formatter:off
            dispatchReceiver = buffer,
            valueArguments = mapOf("arguments" to argumentArray)
        ) // @formatter:on
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun putArgument(buffer: IrExpression, argument: IrExpression): IrCall {
        val type = argument.type
        val function = ffiArgBufferType.owner.functions.first { function ->
            if (!function.name.asString().startsWith("put")) return@first false
            val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
            val paramType = params.first().type
            paramType == type || (paramType.isPtr() && type.isPtr())
        }
        return function.call( // @formatter:off
            dispatchReceiver = buffer,
            valueArguments = mapOf("value" to argument)
        ) // @formatter:on
    }

    private fun createBufferVar(): IrVariable = IrVariableImpl(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        origin = declOrigin,
        symbol = IrVariableSymbolImpl(),
        name = Name.identifier("__kwire_arg_buffer_${Uuid.random().toHexString()}__"),
        type = context.ffi.ffiArgBufferType.defaultType,
        isVar = false,
        isConst = false,
        isLateinit = false
    ).apply {
        initializer = acquireArgBuffer()
    }

    fun extractArgumentsIntoBuffer(function: IrFunction): ExtractedFFIArgBuffer {
        val bufferVariable = createBufferVar()
        val parameters = function.parameters.filter { it.kind == IrParameterKind.Regular }
        if (parameters.isEmpty()) return ExtractedFFIArgBuffer.empty
        val statements = ArrayList<IrStatement>()
        for (parameter in parameters) {
            statements += putArgument(bufferVariable.load(), parameter.load())
        }
        // Wrapped function invocations are always considered as direct call
        return ExtractedFFIArgBuffer(true, statements, bufferVariable)
    }

    fun extractArgumentsIntoBuffer(args: IrVararg): ExtractedFFIArgBuffer {
        val bufferVariable = createBufferVar()
        val argElements = args.elements
        // For dynamic invocations, we need to unbox all args at runtime first, go through putAll
        val statements = ArrayList<IrStatement>()
        statements += bufferVariable
        if (argElements.any { it is IrSpreadElement }) {
            for (argElement in argElements) {
                statements += when (argElement) {
                    is IrSpreadElement -> putArguments(bufferVariable.load(), argElement.expression)
                    is IrExpression -> putArgument(bufferVariable.load(), argElement)
                    else -> return ExtractedFFIArgBuffer.empty
                }
            }
            return ExtractedFFIArgBuffer(statements = statements, bufferVariable = bufferVariable)
        }
        // For direct invocations we can expand all arguments at compile time
        for (argElement in argElements) {
            if (argElement !is IrExpression) return ExtractedFFIArgBuffer.empty
            statements += putArgument(bufferVariable.load(), argElement)
        }
        return ExtractedFFIArgBuffer(true, statements, bufferVariable)
    }
}