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

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.name.Name
import dev.karmakrafts.kwire.abi.type.BuiltinType as ABIBuiltinType
import dev.karmakrafts.kwire.abi.type.StructType as ABIStructType
import dev.karmakrafts.kwire.abi.type.ReferenceType as ABIReferenceType
import dev.karmakrafts.kwire.abi.type.Type as ABIType

internal object ABINames {
    val moduleDataNameName: Name = Name.identifier("name")
    val moduleDataDependenciesName: Name = Name.identifier("dependencies")
    val moduleDataSymbolTableData: Name = Name.identifier("symbolTableData")
}

internal fun IrType.getABIBuiltinType(): ABIBuiltinType? {
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
        NativeType.PTR -> return ABIBuiltinType.PTR
        else -> {}
    }
    return null
}

internal fun IrType.getABIStructType(context: KWirePluginContext): ABIStructType? {
    if(!isStruct(context)) return null
    return ABIStructType()
}

internal fun IrType.getABIReferenceType(context: KWirePluginContext): ABIReferenceType? {
    if(!isStruct(context)) return null
    return ABIReferenceType()
}

internal fun IrType.getABIType(context: KWirePluginContext): ABIType? {
    return getABIBuiltinType() ?: getABIStructType(context) ?: getABIReferenceType(context)
}