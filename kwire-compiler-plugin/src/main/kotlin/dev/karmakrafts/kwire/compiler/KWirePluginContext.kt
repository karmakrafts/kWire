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

package dev.karmakrafts.kwire.compiler

import dev.karmakrafts.kwire.compiler.ffi.FFI
import dev.karmakrafts.kwire.compiler.memory.Memory
import dev.karmakrafts.kwire.compiler.memory.MemoryStack
import dev.karmakrafts.kwire.compiler.memory.layout.MemoryLayout
import dev.karmakrafts.kwire.compiler.monomorphizer.ClassMonomorphizer
import dev.karmakrafts.kwire.compiler.monomorphizer.FunctionMonomorphizer
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.markedConst
import dev.karmakrafts.kwire.compiler.util.new
import dev.karmakrafts.kwire.compiler.util.toVararg
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.IrTypeSystemContextImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.toIrConst

internal class KWirePluginContext( // @formatter:off
    val pluginContext: IrPluginContext,
    val irModule: IrModuleFragment,
    override val irFile: IrFile,
    val kwireSymbols: KWireSymbols,
    val kwireModuleData: KWireModuleData
) : IrPluginContext by pluginContext, MessageCollectorExtensions { // @formatter:on
    var checkerFailed: Boolean = false
    val typeSystemContext: IrTypeSystemContext = IrTypeSystemContextImpl(irBuiltIns)
    val ffi: FFI = FFI(this)
    val memory: Memory = Memory(this)
    val memoryStack: MemoryStack = MemoryStack(this)

    val functionMonomorphizer: FunctionMonomorphizer = FunctionMonomorphizer(this)
    val classMonomorphizer: ClassMonomorphizer = ClassMonomorphizer(this)

    val voidPtr: IrType = kwireSymbols.ptrType.typeWith(irBuiltIns.unitType)
    val constVoidPtr: IrType = voidPtr.markedConst(this)
    val anyPtr: IrType = kwireSymbols.ptrType.starProjectedType
    val constAnyPtr: IrType = anyPtr.markedConst(this)

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNInt(expr: IrExpression): IrExpression {
        if (expr.type == kwireSymbols.nIntType.owner.expandedType) return expr
        val symbol = referenceFunctions(KWireNames.CTypePkg.toNInt).single {
            it.owner.parameters.single { param ->
                param.kind == IrParameterKind.ExtensionReceiver
            }.type == expr.type
        }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNUInt(expr: IrExpression): IrExpression {
        if (expr.type == kwireSymbols.nUIntType.defaultType) return expr
        val symbol = referenceFunctions(KWireNames.CTypePkg.toNUInt).single {
            it.owner.parameters.single { param ->
                param.kind == IrParameterKind.ExtensionReceiver
            }.type == expr.type
        }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNFloat(expr: IrExpression): IrExpression {
        if (expr.type == kwireSymbols.nFloatType.owner.expandedType) return expr
        val symbol = referenceFunctions(KWireNames.CTypePkg.toNFloat).single {
            it.owner.parameters.single { param ->
                param.kind == IrParameterKind.ExtensionReceiver
            }.type == expr.type
        }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun createPtr(address: IrExpression, pointedType: IrType): IrConstructorCall = kwireSymbols.ptrConstructor.new( // @formatter:off
        typeArguments = mapOf("T" to pointedType),
        valueArguments = mapOf("rawAddress" to address)
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun attachMemoryLayout(type: IrClass, layout: MemoryLayout) {
        val data = layout.serialize().map { it.toIrConst(irBuiltIns.byteType) }
        val dataArray = IrVarargImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = irBuiltIns.arrayClass.typeWith(irBuiltIns.byteType),
            varargElementType = irBuiltIns.byteType,
            elements = data
        )
        val annotation = kwireSymbols.structLayoutConstructor.new(valueArguments = mapOf("data" to dataArray))
        metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(type, listOf(annotation))
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun emitPointerSize(): IrExpression = kwireSymbols.ptrSizeBytes.owner.getter!!.call(
        dispatchReceiver = kwireSymbols.ptrCompanionType.getObjectInstance()
    )

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    internal fun createListOf( // @formatter:off
        type: IrType,
        values: List<IrExpression>
    ): IrCall = kwireSymbols.listOf.call(
        typeArguments = mapOf("T" to type),
        valueArguments = mapOf("elements" to values.toVararg(this, type))
    ) // @formatter:on
}