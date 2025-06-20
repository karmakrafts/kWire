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
import dev.karmakrafts.kwire.compiler.memory.BuiltinMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.MemoryLayout
import dev.karmakrafts.kwire.compiler.memory.MemoryStack
import dev.karmakrafts.kwire.compiler.memory.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.StructMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.NativeType
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getNativeType
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.getStructLayoutData
import dev.karmakrafts.kwire.compiler.util.hasStructLayoutData
import dev.karmakrafts.kwire.compiler.util.isStruct
import dev.karmakrafts.kwire.compiler.util.new
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantArrayImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantPrimitiveImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeAliasSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.IrTypeSystemContextImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.toIrConst

internal class KWirePluginContext(
    val pluginContext: IrPluginContext, val irModule: IrModuleFragment, override val irFile: IrFile
) : IrPluginContext by pluginContext, MessageCollectorExtensions {
    val sizeOf: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.MemoryPkg.sizeOf).first()
    val alignOf: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.MemoryPkg.alignOf).first()

    val pointedType: IrClassSymbol = referenceClass(KWireNames.Pointed.id)!!

    val addressType: IrClassSymbol = referenceClass(KWireNames.Address.id)!!
    val addressCompanionType: IrClassSymbol = referenceClass(KWireNames.Address.Companion.id)!!
    val addressSizeBytes: IrPropertySymbol = referenceProperties(KWireNames.Address.Companion.SIZE_BYTES).first()

    val memoryType: IrClassSymbol = referenceClass(KWireNames.Memory.id)!!
    val memoryCompanionType: IrClassSymbol = referenceClass(KWireNames.Memory.Companion.id)!!
    val allocatorType: IrClassSymbol = referenceClass(KWireNames.Allocator.id)!!
    val allocatorAllocate: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.Allocator.allocate).first()
    val allocatorReallocate: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.Allocator.reallocate).first()
    val allocatorFree: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.Allocator.free).first()

    val structType: IrClassSymbol = referenceClass(KWireNames.Struct.id)!!
    val structLayoutType: IrClassSymbol = referenceClass(KWireNames.StructLayout.id)!!
    val structLayoutConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.StructLayout.id).first()
    val alignAsType: IrClassSymbol = referenceClass(KWireNames.AlignAs.id)!!
    val alignAsConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.AlignAs.id).first()

    val numPtrType: IrClassSymbol = referenceClass(KWireNames.NumPtr.id)!!
    val numPtrConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.NumPtr.id).first()
    val ptrType: IrClassSymbol = referenceClass(KWireNames.Ptr.id)!!
    val ptrConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.Ptr.id).first()
    val funPtrType: IrClassSymbol = referenceClass(KWireNames.FunPtr.id)!!
    val funPtrConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.FunPtr.id).first()
    val voidPtrType: IrClassSymbol = referenceClass(KWireNames.VoidPtr.id)!!
    val voidPtrConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.VoidPtr.id).first()

    val nIntType: IrTypeAliasSymbol = referenceTypeAlias(KWireNames.NInt.id)!!
    val nUIntType: IrClassSymbol = referenceClass(KWireNames.NUInt.id)!!
    val nFloatType: IrTypeAliasSymbol = referenceTypeAlias(KWireNames.NFloat.id)!!

    val uByteType: IrClassSymbol = referenceClass(KWireNames.Kotlin.UByte.id)!!
    val uShortType: IrClassSymbol = referenceClass(KWireNames.Kotlin.UShort.id)!!
    val uIntType: IrClassSymbol = referenceClass(KWireNames.Kotlin.UInt.id)!!
    val uLongType: IrClassSymbol = referenceClass(KWireNames.Kotlin.ULong.id)!!

    val typeSystemContext: IrTypeSystemContext = IrTypeSystemContextImpl(irBuiltIns)
    val ffi: FFI = FFI(this)
    val memoryStack: MemoryStack = MemoryStack(this)
    private val memoryLayoutCache: HashMap<IrType, MemoryLayout> = HashMap()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNInt(expr: IrExpression): IrExpression {
        val symbol =
            referenceFunctions(KWireNames.CTypePkg.toNInt).first { it.owner.extensionReceiverParameter!!.type == expr.type }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNUInt(expr: IrExpression): IrExpression {
        val symbol =
            referenceFunctions(KWireNames.CTypePkg.toNUInt).first { it.owner.extensionReceiverParameter!!.type == expr.type }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun toNFloat(expr: IrExpression): IrExpression {
        val symbol =
            referenceFunctions(KWireNames.CTypePkg.toNFloat).first { it.owner.extensionReceiverParameter!!.type == expr.type }
        return symbol.call(extensionReceiver = expr)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun createNumPtr(address: IrExpression, pointedType: IrType): IrConstructorCall = numPtrConstructor.new( // @formatter:off
        typeArguments = mapOf("N" to pointedType),
        valueArguments = mapOf("rawAddress" to address)
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun createPtr(address: IrExpression, pointedType: IrType): IrConstructorCall = ptrConstructor.new( // @formatter:off
        typeArguments = mapOf("T" to pointedType),
        valueArguments = mapOf("rawAddress" to address)
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun createFunPtr(address: IrExpression, pointedType: IrType): IrConstructorCall = funPtrConstructor.new( // @formatter:off
        typeArguments = mapOf("F" to pointedType),
        valueArguments = mapOf("rawAddress" to address)
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun createVoidPtr(address: IrExpression): IrConstructorCall = voidPtrConstructor.new( // @formatter:off
        valueArguments = mapOf("rawAddress" to address)
    ) // @formatter:on

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun attachMemoryLayout(type: IrClass, layout: MemoryLayout) {
        val data = layout.serialize().map { value ->
            IrConstantPrimitiveImpl(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                value = value.toIrConst(irBuiltIns.byteType)
            )
        }
        val dataArray = IrConstantArrayImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = irBuiltIns.byteArray.defaultType,
            initElements = data
        )
        val annotation = structLayoutConstructor.new(valueArguments = mapOf("data" to dataArray))
        metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(type, listOf(annotation))
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun getOrComputeMemoryLayout(type: IrType): MemoryLayout = memoryLayoutCache.getOrPut(type) {
        // Handle Unit/void type
        if (type.isUnit()) return@getOrPut BuiltinMemoryLayout.VOID
        // Handle signed integer types and IEEE-754 types
        val primitiveType = type.getPrimitiveType()
        if (primitiveType != null) return@getOrPut when (primitiveType) {
            PrimitiveType.BYTE -> BuiltinMemoryLayout.BYTE
            PrimitiveType.SHORT -> BuiltinMemoryLayout.SHORT
            PrimitiveType.INT -> BuiltinMemoryLayout.INT
            PrimitiveType.LONG -> BuiltinMemoryLayout.LONG
            PrimitiveType.FLOAT -> BuiltinMemoryLayout.FLOAT
            PrimitiveType.DOUBLE -> BuiltinMemoryLayout.DOUBLE
            else -> error("Unsupported primitive type $primitiveType")
        }
        // Handle unsigned integer types
        val unsignedType = type.getUnsignedType()
        if (unsignedType != null) return@getOrPut when (unsignedType) {
            UnsignedType.UBYTE -> BuiltinMemoryLayout.UBYTE
            UnsignedType.USHORT -> BuiltinMemoryLayout.USHORT
            UnsignedType.UINT -> BuiltinMemoryLayout.UINT
            UnsignedType.ULONG -> BuiltinMemoryLayout.ULONG
        }
        // Handle native builtin types
        val nativeType = type.getNativeType()
        if (nativeType != null) return@getOrPut when (nativeType) {
            NativeType.NINT -> BuiltinMemoryLayout.NINT
            NativeType.NUINT -> BuiltinMemoryLayout.NUINT
            NativeType.NFLOAT -> BuiltinMemoryLayout.NFLOAT
            NativeType.PTR -> BuiltinMemoryLayout.ADDRESS
        }
        // Handle reference objects
        if (!type.isStruct(this)) return ReferenceMemoryLayout
        // Handle user defined types
        val clazz = type.getClass() ?: return@getOrPut BuiltinMemoryLayout.VOID
        if (clazz.hasStructLayoutData()) {
            // If this struct already has layout data attached, deserialize it
            return@getOrPut MemoryLayout.deserialize(clazz.getStructLayoutData()!!)
        }
        val fields = ArrayList<MemoryLayout>()
        for (property in clazz.properties) {
            val propertyType = property.backingField?.type
            check(propertyType != null) { "Struct field must have a backing field" }
            fields += getOrComputeMemoryLayout(propertyType)
        }
        if (fields.isEmpty()) BuiltinMemoryLayout.VOID else StructMemoryLayout(fields)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun emitPointerSize(): IrExpression = addressSizeBytes.owner.getter!!.call(
        dispatchReceiver = addressCompanionType.getObjectInstance()
    )
}