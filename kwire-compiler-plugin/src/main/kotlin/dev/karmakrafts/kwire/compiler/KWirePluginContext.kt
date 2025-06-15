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

import dev.karmakrafts.kwire.compiler.util.BuiltinMemoryLayout
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.MemoryLayout
import dev.karmakrafts.kwire.compiler.util.StructMemoryLayout
import dev.karmakrafts.kwire.compiler.util.VoidMemoryLayout
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.getStructLayoutData
import dev.karmakrafts.kwire.compiler.util.hasStructLayoutData
import dev.karmakrafts.kwire.compiler.util.isStruct
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantArrayImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantPrimitiveImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
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
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.toIrConst

internal class KWirePluginContext(
    val pluginContext: IrPluginContext
) : IrPluginContext by pluginContext {
    val sizeOf: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.sizeOf).first()
    val alignOf: IrSimpleFunctionSymbol = referenceFunctions(KWireNames.alignOf).first()

    val addressCompanionType: IrClassSymbol = referenceClass(KWireNames.Address.Companion.id)!!
    val addressSizeBytes: IrPropertySymbol = referenceProperties(KWireNames.Address.Companion.SIZE_BYTES).first()

    val structType: IrClassSymbol = referenceClass(KWireNames.Struct.id)!!
    val structLayoutType: IrClassSymbol = referenceClass(KWireNames.Struct.Layout.id)!!
    val structLayoutConstructor: IrConstructorSymbol = referenceConstructors(KWireNames.Struct.Layout.id).first()

    val typeSystemContext: IrTypeSystemContext = IrTypeSystemContextImpl(irBuiltIns)

    private val memoryLayouts: HashMap<IrType, MemoryLayout> = HashMap()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun attachMemoryLayout(clazz: IrClass, layout: MemoryLayout) {
        val annotation = IrConstructorCallImpl(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = structLayoutType.defaultType,
            symbol = structLayoutConstructor,
            typeArgumentsCount = 0,
            constructorTypeArgumentsCount = 0
        ).apply {
            val function = symbol.owner
            arguments[function.parameters.first { it.name.asString() == "data" }] = IrConstantArrayImpl(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                type = irBuiltIns.byteArray.defaultType,
                initElements = layout.serialize().map { value ->
                    IrConstantPrimitiveImpl(
                        startOffset = SYNTHETIC_OFFSET,
                        endOffset = SYNTHETIC_OFFSET,
                        value = value.toIrConst(irBuiltIns.byteType)
                    )
                })
        }
        metadataDeclarationRegistrar.addMetadataVisibleAnnotationsToElement(clazz, listOf(annotation))
        memoryLayouts[clazz.defaultType] = layout // Add attached layouts to the cache
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun computeMemoryLayout(type: IrType): MemoryLayout = memoryLayouts.getOrPut(type) {
        // Handle Unit/void type
        if (type.isUnit()) return@getOrPut VoidMemoryLayout
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
        if (!type.isStruct(this)) return BuiltinMemoryLayout.ADDRESS // Reference objects use address layout
        // Handle user defined types
        val clazz = type.getClass() ?: return@getOrPut VoidMemoryLayout
        // If the layout of this struct has been computed by an external compilation unit, parse it
        if (clazz.hasStructLayoutData()) {
            return@getOrPut MemoryLayout.deserialize(clazz.getStructLayoutData()!!)
        }
        val fields = ArrayList<MemoryLayout>()
        for (property in clazz.properties) {
            val propertyType = property.backingField?.type
            check(propertyType != null) { "Struct field must have a backing field" }
            fields += computeMemoryLayout(propertyType)
        }
        if (fields.isEmpty()) VoidMemoryLayout else StructMemoryLayout(fields)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun emitPointerSize(): IrCall = IrCallImplWithShape(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        type = irBuiltIns.intType,
        symbol = addressSizeBytes.owner.getter!!.symbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = 0,
        contextParameterCount = 0,
        hasDispatchReceiver = true,
        hasExtensionReceiver = false
    ).apply {
        dispatchReceiver = addressCompanionType.getObjectInstance()
    }
}