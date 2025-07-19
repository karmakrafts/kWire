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
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.fields
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import dev.karmakrafts.kwire.abi.symbol.SymbolName as ABISymbolName
import dev.karmakrafts.kwire.abi.type.BuiltinType as ABIBuiltinType
import dev.karmakrafts.kwire.abi.type.ReferenceType as ABIReferenceType
import dev.karmakrafts.kwire.abi.type.StructType as ABIStructType
import dev.karmakrafts.kwire.abi.type.Type as ABIType
import dev.karmakrafts.kwire.abi.type.TypeArgument as ABITypeArgument

internal object ABINames {
    val moduleDataNameName: Name = Name.identifier("name")
    val moduleDataDependenciesName: Name = Name.identifier("dependencies")
    val moduleDataSymbolTableData: Name = Name.identifier("symbolTableData")
}

internal fun FqName.toABISymbolName(): ABISymbolName {
    return ABISymbolName(asString(), shortName().asString())
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
        NativeType.PTR -> ABIBuiltinType.PTR.withArguments(
            getPointedTypeArgument()?.getABITypeArgument(context)
                ?: ABIBuiltinType.VOID // Try to save the conversion by converting to Ptr<CVoid>
        )

        else -> {}
    }
    return null
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.getABIStructType(context: KWirePluginContext): ABIType? {
    if (!isStruct(context)) return null
    val clazz = getClass() ?: return null
    val name = clazz.kotlinFqName
    val fields = clazz.fields.map {
        it.type.getABIType(context) ?: error("Could not determine struct field type")
    }.toList()
    val abiType = ABIStructType(name.toABISymbolName(), fields)
    if (this is IrSimpleType) {
        return abiType.withArguments(arguments.map {
            it.getABITypeArgument(context) ?: error("Could not determine type arguments for struct type")
        }.toList())
    }
    return abiType
}

internal fun IrType.getABIReferenceType(context: KWirePluginContext): ABIType? {
    if (getABIBuiltinType(context) != null || isStruct(context)) return null
    val name = classFqName ?: return null
    val abiType = ABIReferenceType(name.toABISymbolName())
    if (this is IrSimpleType) {
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
        is IrTypeProjection -> type.getABIType(context)
    }
}