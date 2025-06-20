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
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrSpreadElement
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties

internal class FFI(
    private val context: KWirePluginContext
) {
    val ffiType: IrClassSymbol = context.referenceClass(KWireNames.FFI.id)!!
    val ffiCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFI.Companion.id)!!

    val ffiTypeType: IrClassSymbol = context.referenceClass(KWireNames.FFIType.id)!!
    val ffiTypeCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFIType.Companion.id)!!

    val ffiDescriptorType: IrClassSymbol = context.referenceClass(KWireNames.FFIDescriptor.id)!!
    val ffiDescriptorConstructor: IrConstructorSymbol =
        context.referenceConstructors(KWireNames.FFIDescriptor.id).first()

    val ffiArgBufferType: IrClassSymbol = context.referenceClass(KWireNames.FFIArgBuffer.id)!!
    val ffiArgBufferPutAll: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.FFIArgBuffer.putAll).first()
    val ffiArgBufferCompanionType: IrClassSymbol = context.referenceClass(KWireNames.FFIArgBuffer.Companion.id)!!
    val ffiArgBufferGet: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.FFIArgBuffer.Companion.get).first()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun call(type: IrType, address: IrExpression, descriptor: IrExpression, argBuffer: IrExpression): IrCall {
        return ffiType.owner.functions.first { function ->
            if (!function.name.asString().startsWith("call") || function.returnType != type) return@first false
            val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
            params.last().type == ffiArgBufferType.defaultType
        }.call( // @formatter:off
            dispatchReceiver = ffiCompanionType.getObjectInstance(),
            valueArguments = mapOf(
                "address" to address,
                "descriptor" to descriptor,
                "args" to argBuffer
            )
        ) // @formatter:on
    }

    fun getArgBuffer(): IrCall = ffiArgBufferGet.call(
        dispatchReceiver = ffiArgBufferCompanionType.getObjectInstance()
    )

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
            params.first().type == type
        }
        return function.call( // @formatter:off
            dispatchReceiver = buffer,
            valueArguments = mapOf("value" to argument)
        ) // @formatter:on
    }

    fun extractArgumentsIntoBuffer(args: IrVararg): IrExpression? {
        val buffer = getArgBuffer()
        val argElements = args.elements
        // For dynamic invocations, we need to unbox all args at runtime first, go through putAll
        if (argElements.any { it is IrSpreadElement }) {
            for (argElement in argElements) when (argElement) {
                is IrSpreadElement -> putArguments(buffer, argElement.expression)
                is IrExpression -> putArgument(buffer, argElement)
                else -> return null
            }
            return buffer
        }
        // For direct invocations we can expand all arguments at compile time
        for (argElement in argElements) {
            if (argElement !is IrExpression) return null
            putArgument(buffer, argElement)
        }
        return buffer
    }


}