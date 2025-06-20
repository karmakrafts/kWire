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
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.properties

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal enum class FFIType(
    private val typeGetter: KWirePluginContext.() -> IrType
) {
    // @formatter:off
    VOID    ({ irBuiltIns.unitType }),
    BYTE    ({ irBuiltIns.byteType }),
    SHORT   ({ irBuiltIns.shortType }),
    INT     ({ irBuiltIns.intType }),
    LONG    ({ irBuiltIns.longType }),
    NINT    ({ nIntType.owner.expandedType }),
    UBYTE   ({ uByteType.defaultType }),
    USHORT  ({ uShortType.defaultType }),
    UINT    ({ uIntType.defaultType }),
    ULONG   ({ uLongType.defaultType }),
    NUINT   ({ nUIntType.defaultType }),
    FLOAT   ({ irBuiltIns.floatType }),
    DOUBLE  ({ irBuiltIns.doubleType }),
    NFLOAT  ({ nFloatType.owner.expandedType }),
    PTR     ({ addressType.defaultType });
    // @formatter:on

    // @formatter:off
    internal operator fun invoke(context: KWirePluginContext): IrExpression = context.ffi.ffiTypeCompanionType
        .owner.properties
        .first { it.name.asString() == name }
        .getter!!
        .call(dispatchReceiver = context.ffi.ffiTypeCompanionType.getObjectInstance())
    // @formatter:on

    internal fun getType(context: KWirePluginContext): IrType = context.typeGetter()
}

internal fun IrType.getFFIType(context: KWirePluginContext): FFIType? =
    FFIType.entries.find { it.getType(context) == this }