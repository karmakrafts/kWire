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

import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.name.Name
import dev.karmakrafts.kwire.abi.type.BuiltinType as BuiltinABIType

object ABINames {
    val moduleDataNameName: Name = Name.identifier("name")
    val moduleDataDependenciesName: Name = Name.identifier("dependencies")
    val moduleDataSymbolTableData: Name = Name.identifier("symbolTableData")
}

fun IrType.getBuiltinABIType(): BuiltinABIType? {
    if (isUnit()) return null
    when (getPrimitiveType()) {
        PrimitiveType.BYTE -> return BuiltinABIType.BYTE
        PrimitiveType.SHORT -> return BuiltinABIType.SHORT
        PrimitiveType.INT -> return BuiltinABIType.INT
        PrimitiveType.LONG -> return BuiltinABIType.LONG
        PrimitiveType.FLOAT -> return BuiltinABIType.FLOAT
        PrimitiveType.DOUBLE -> return BuiltinABIType.DOUBLE
        PrimitiveType.CHAR -> return BuiltinABIType.CHAR
        PrimitiveType.BOOLEAN -> return BuiltinABIType.BOOL
        else -> {}
    }
    when (getUnsignedType()) {
        UnsignedType.UBYTE -> return BuiltinABIType.UBYTE
        UnsignedType.USHORT -> return BuiltinABIType.USHORT
        UnsignedType.UINT -> return BuiltinABIType.UINT
        UnsignedType.ULONG -> return BuiltinABIType.ULONG
        else -> {}
    }
    when (getNativeType()) {
        NativeType.NINT -> return BuiltinABIType.NINT
        NativeType.NUINT -> return BuiltinABIType.NUINT
        NativeType.NFLOAT -> return BuiltinABIType.NFLOAT
        NativeType.PTR -> return BuiltinABIType.PTR
        else -> {}
    }
    return null
}