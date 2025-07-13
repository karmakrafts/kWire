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
import dev.karmakrafts.kwire.compiler.memory.layout.MemoryLayout
import dev.karmakrafts.kwire.compiler.memory.layout.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.getEnumValue
import dev.karmakrafts.kwire.compiler.util.isPtr
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isUnit

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal enum class FFIType(
    private val typeGetter: KWirePluginContext.() -> IrType,
    private val typePredicate: KWirePluginContext.(IrType) -> Boolean
) {
    // @formatter:off
    VOID    ({ irBuiltIns.unitType }, { it.isUnit() }),
    BYTE    ({ irBuiltIns.byteType }),
    SHORT   ({ irBuiltIns.shortType }),
    INT     ({ irBuiltIns.intType }),
    LONG    ({ irBuiltIns.longType }),
    NINT    ({ kwireSymbols.nIntType.owner.expandedType }),
    UBYTE   ({ kwireSymbols.uByteType.defaultType }),
    USHORT  ({ kwireSymbols.uShortType.defaultType }),
    UINT    ({ kwireSymbols.uIntType.defaultType }),
    ULONG   ({ kwireSymbols.uLongType.defaultType }),
    NUINT   ({ kwireSymbols.nUIntType.defaultType }),
    FLOAT   ({ irBuiltIns.floatType }),
    DOUBLE  ({ irBuiltIns.doubleType }),
    NFLOAT  ({ kwireSymbols.nFloatType.owner.expandedType }),
    PTR     ({ anyPtr }, { it.isPtr() });
    // @formatter:on

    constructor(typeGetter: KWirePluginContext.() -> IrType) : this(
        typeGetter, { it == typeGetter() })

    internal operator fun invoke(context: KWirePluginContext): IrExpression =
        getEnumValue(context.ffi.ffiTypeType) { name }

    internal fun isType(context: KWirePluginContext, type: IrType): Boolean = context.typePredicate(type)

    internal fun getType(context: KWirePluginContext): IrType = context.typeGetter()

    internal fun getMemoryLayout(context: KWirePluginContext): MemoryLayout =
        getType(context).computeMemoryLayout(context)
}

internal fun IrType.getFFIType(context: KWirePluginContext): FFIType? =
    FFIType.entries.find { it.isType(context, this) }