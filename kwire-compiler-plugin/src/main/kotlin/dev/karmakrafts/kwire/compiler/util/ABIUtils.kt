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

package dev.karmakrafts.kwire.compiler.util

import dev.karmakrafts.kwire.abi.type.withArguments
import dev.karmakrafts.kwire.compiler.KWirePluginContext
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.impl.IrStarProjectionImpl
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeWithArguments
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import dev.karmakrafts.kwire.abi.symbol.SymbolName as ABISymbolName
import dev.karmakrafts.kwire.abi.type.BuiltinType as ABIBuiltinType
import dev.karmakrafts.kwire.abi.type.ReferenceType as ABIReferenceType
import dev.karmakrafts.kwire.abi.type.StructType as ABIStructType
import dev.karmakrafts.kwire.abi.type.Type as ABIType
import dev.karmakrafts.kwire.abi.type.TypeArgument as ABITypeArgument
import dev.karmakrafts.kwire.abi.type.ConeType as ABIConeType

internal object ABINames {
    val moduleDataNameName: Name = Name.identifier("name")
    val moduleDataDependenciesName: Name = Name.identifier("dependencies")
    val moduleDataSymbolTableData: Name = Name.identifier("symbolTableData")
}

internal fun ABITypeArgument.toIrTypeArgument(context: KWirePluginContext): IrTypeArgument? {
    return when (this) {
        is ABITypeArgument.Star -> IrStarProjectionImpl
        is ABITypeArgument.Concrete -> type.getIrType(context)
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun ABIType.getIrType(context: KWirePluginContext): IrType? {
    return when (this) {
        ABIBuiltinType.VOID -> context.irBuiltIns.unitType

        ABIBuiltinType.BYTE -> context.irBuiltIns.byteType
        ABIBuiltinType.SHORT -> context.irBuiltIns.shortType
        ABIBuiltinType.INT -> context.irBuiltIns.intType
        ABIBuiltinType.LONG -> context.irBuiltIns.longType
        ABIBuiltinType.NINT -> context.kwireSymbols.nIntType.owner.expandedType

        ABIBuiltinType.UBYTE -> context.irBuiltIns.ubyteType
        ABIBuiltinType.USHORT -> context.irBuiltIns.ushortType
        ABIBuiltinType.UINT -> context.irBuiltIns.uintType
        ABIBuiltinType.ULONG -> context.irBuiltIns.ulongType
        ABIBuiltinType.NUINT -> context.kwireSymbols.nUIntType.defaultType

        ABIBuiltinType.FLOAT -> context.irBuiltIns.floatType
        ABIBuiltinType.DOUBLE -> context.irBuiltIns.doubleType
        ABIBuiltinType.NFLOAT -> context.kwireSymbols.nFloatType.owner.expandedType

        ABIBuiltinType.BOOL -> context.irBuiltIns.booleanType
        ABIBuiltinType.CHAR -> context.irBuiltIns.charType
        ABIBuiltinType.PTR -> context.anyPtr

        else -> {
            val baseType = context.referenceClass(symbolName.toClassId()) ?: return null
            if (this is ABIConeType) {
                return baseType.typeWithArguments(typeArguments.map {
                    it.toIrTypeArgument(context)
                        ?: error("Could not convert type arguments during ABI type conversion to Kotlin IR")
                }.toList())
            }
            baseType.defaultType
        }
    }
}

internal fun ABISymbolName.toClassId(): ClassId {
    return if(packageName.isEmpty()) ClassId.topLevel(FqName.topLevel(Name.identifier(shortName)))
    else ClassId(FqName(packageName), FqName(shortName), false)
}

internal fun ABISymbolName.toCallableId(): CallableId {
    val nameSegments = nameSegments()
    val className = FqName("$packageName.${nameSegments.dropLast(1).joinToString(".")}")
    val name = Name.identifier(nameSegments.last())
    return CallableId(FqName(packageName), className, name)
}

internal fun ClassId.toABISymbolName(): ABISymbolName {
    val shortName = relativeClassName.asString()
    val packageName = packageFqName.asString()
    return if (packageName.isEmpty()) ABISymbolName(shortName, shortName)
    else ABISymbolName("$packageName.$shortName", shortName)
}

internal fun CallableId.toABISymbolName(): ABISymbolName {
    val shortName = "${className?.asString()?.let { "$it." } ?: ""}.${callableName.asString()}"
    val packageName = packageName.asString()
    return if (packageName.isEmpty()) ABISymbolName(shortName, shortName)
    else ABISymbolName("$packageName.$shortName", shortName)
}

internal fun IrType.getABIBuiltinType(context: KWirePluginContext): ABIBuiltinType? {
    if (isUnit()) return ABIBuiltinType.VOID
    when (getPrimitiveType()) {
        PrimitiveType.BYTE -> return ABIBuiltinType.BYTE
        PrimitiveType.SHORT -> return ABIBuiltinType.SHORT
        PrimitiveType.INT -> return ABIBuiltinType.INT
        PrimitiveType.LONG -> return ABIBuiltinType.LONG
        PrimitiveType.FLOAT -> return ABIBuiltinType.FLOAT
        PrimitiveType.DOUBLE -> return ABIBuiltinType.DOUBLE
        PrimitiveType.CHAR -> return ABIBuiltinType.CHAR
        PrimitiveType.BOOLEAN -> return ABIBuiltinType.BOOL
        else -> {}
    }
    when (getUnsignedType()) {
        UnsignedType.UBYTE -> return ABIBuiltinType.UBYTE
        UnsignedType.USHORT -> return ABIBuiltinType.USHORT
        UnsignedType.UINT -> return ABIBuiltinType.UINT
        UnsignedType.ULONG -> return ABIBuiltinType.ULONG
        else -> {}
    }
    when (getNativeType()) {
        NativeType.NINT -> return ABIBuiltinType.NINT
        NativeType.NUINT -> return ABIBuiltinType.NUINT
        NativeType.NFLOAT -> return ABIBuiltinType.NFLOAT
        NativeType.PTR -> { // @formatter:off
            val typeArg = getPointedTypeArgument()?.getABITypeArgument(context)
                ?: ABITypeArgument.Concrete(ABIBuiltinType.VOID)
            ABIBuiltinType.PTR.withArguments(typeArg)
        } // @formatter:on

        else -> {}
    }
    return null
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.getABIStructType(context: KWirePluginContext): ABIType? {
    if (!isStruct(context)) return null
    val clazz = getClass() ?: return null
    val id = clazz.classIdOrFail
    val fields = clazz.properties.mapNotNull { it.backingField?.type?.getABIType(context) }.toList()
    val abiType = ABIStructType(id.toABISymbolName(), fields)
    if (this is IrSimpleType && arguments.isNotEmpty()) {
        return abiType.withArguments(arguments.map {
            it.getABITypeArgument(context) ?: error("Could not determine type arguments for struct type")
        }.toList())
    }
    return abiType
}

internal fun IrType.getABIReferenceType(context: KWirePluginContext): ABIType? {
    if (getABIBuiltinType(context) != null || isStruct(context)) return null
    val id = getClass()?.classIdOrFail ?: return null
    val abiType = ABIReferenceType(id.toABISymbolName())
    if (this is IrSimpleType && arguments.isNotEmpty()) {
        return abiType.withArguments(arguments.map {
            it.getABITypeArgument(context) ?: error("Could not determine type arguments for reference type")
        }.toList())
    }
    return abiType
}

internal fun IrType.getABIType(context: KWirePluginContext): ABIType? {
    return getABIBuiltinType(context) ?: getABIStructType(context) ?: getABIReferenceType(context)
}

internal fun IrTypeArgument.getABITypeArgument(context: KWirePluginContext): ABITypeArgument? {
    return when (this) {
        is IrStarProjection -> ABITypeArgument.Star
        is IrTypeProjection -> ABITypeArgument.Concrete(
            type.getABIType(context) ?: error("Could not resolve IrTypeProjection to ABITypeArgument")
        )
    }
}