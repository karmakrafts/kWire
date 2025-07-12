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

package dev.karmakrafts.kwire.compiler.memory.layout

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.NativeType
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.constInt
import dev.karmakrafts.kwire.compiler.util.constNFloat
import dev.karmakrafts.kwire.compiler.util.constNInt
import dev.karmakrafts.kwire.compiler.util.constNUInt
import dev.karmakrafts.kwire.compiler.util.getNativeType
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.reinterpret
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.builtins.UnsignedType
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.toIrConst

// This code gets its own object because of initialization order. See KT-74926
@OptIn(UnsafeDuringIrConstructionAPI::class)
private object BuiltinMemoryOps {
    fun read( // @formatter:off
        context: KWirePluginContext,
        address: IrExpression,
        typeSelector: (KWirePluginContext) -> IrType
    ): IrExpression = with(context) { // @formatter:on
        val type = typeSelector(this)
        val function = kwireSymbols.memoryCompanionType.functions.map { it.owner }.first { function ->
            function.name.asString().startsWith("read") && function.returnType == type
        }
        return function.call( // @formatter:off
            dispatchReceiver = kwireSymbols.memoryCompanionType.getObjectInstance(),
            valueArguments = mapOf("address" to address)
        ) // @formatter:on
    }

    fun write( // @formatter:off
        context: KWirePluginContext,
        address: IrExpression,
        value: IrExpression
    ): IrExpression = with(context) { // @formatter:on
        val type = value.type
        val function = kwireSymbols.memoryCompanionType.functions.map { it.owner }.first { function ->
            val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
            function.name.asString().startsWith("write") && params[1].type == type
        }
        return function.call( // @formatter:off
            dispatchReceiver = kwireSymbols.memoryCompanionType.getObjectInstance(),
            valueArguments = mapOf("address" to address, "value" to value)
        ) // @formatter:on
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
@SerialName("builtin")
@Serializable
internal enum class BuiltinMemoryLayout(
    @Transient override val typeName: String,
    @Transient private val typeGetter: KWirePluginContext.() -> IrType,
    @Transient private val sizeEmitter: (KWirePluginContext) -> IrExpression,
    @Transient private val alignmentEmitter: (KWirePluginContext) -> IrExpression,
    @Transient private val readEmitter: (KWirePluginContext, IrExpression) -> IrExpression,
    @Transient private val writeEmitter: (KWirePluginContext, IrExpression, IrExpression) -> IrExpression
) : MemoryLayout {
    // @formatter:off
    VOID("kotlin.Unit",
        { irBuiltIns.unitType },
        { constInt(it, 0) },
        { _, _ -> error("Unit/void type cannot be read from memory") },
        { _, _, _ -> error("Unit/void type cannot be written to memory") }),

    // Signed types

    BYTE("kotlin.Byte",
        { irBuiltIns.byteType },
        { constInt(it, Byte.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.byteType } },
        BuiltinMemoryOps::write),

    SHORT("kotlin.Short",
        { irBuiltIns.shortType },
        { constInt(it, Short.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.shortType } },
        BuiltinMemoryOps::write),

    INT("kotlin.Int",
        { irBuiltIns.intType },
        { constInt(it, Int.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.intType } },
        BuiltinMemoryOps::write),

    LONG("kotlin.Long",
        { irBuiltIns.longType },
        { constInt(it, Long.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.longType } },
        BuiltinMemoryOps::write),

    NINT("dev.karmakrafts.kwire.ctype.NInt",
        { kwireSymbols.nIntType.owner.expandedType },
        { it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.nIntType.owner.expandedType } },
        BuiltinMemoryOps::write),

    // Unsigned types

    UBYTE("kotlin.UByte",
        { kwireSymbols.uByteType.defaultType },
        { constInt(it, UByte.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.uByteType.defaultType } },
        BuiltinMemoryOps::write),

    USHORT("kotlin.UShort",
        { kwireSymbols.uShortType.defaultType },
        { constInt(it, UShort.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.uShortType.defaultType } },
        BuiltinMemoryOps::write),

    UINT("kotlin.UInt",
        { kwireSymbols.uIntType.defaultType },
        { constInt(it, UInt.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.uIntType.defaultType } },
        BuiltinMemoryOps::write),

    ULONG("kotlin.ULong",
        { kwireSymbols.uLongType.defaultType },
        { constInt(it, ULong.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.uLongType.defaultType } },
        BuiltinMemoryOps::write),

    NUINT("dev.karmakrafts.kwire.ctype.NUInt",
        { kwireSymbols.nUIntType.defaultType },
        { it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.nUIntType.defaultType } },
        BuiltinMemoryOps::write),

    // IEEE-754 types

    FLOAT("kotlin.Float",
        { irBuiltIns.floatType },
        { constInt(it, Float.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.floatType } },
        BuiltinMemoryOps::write),

    DOUBLE("kotlin.Double",
        { irBuiltIns.doubleType },
        { constInt(it, Double.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.doubleType } },
        BuiltinMemoryOps::write),

    NFLOAT("dev.karmakrafts.kwire.ctype.NFloat",
        { kwireSymbols.nFloatType.owner.expandedType },
        { it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.nFloatType.owner.expandedType } },
        BuiltinMemoryOps::write),

    // Pointer types

    PTR("dev.karmakrafts.kwire.ctype.Ptr",
        { kwireSymbols.ptrType.starProjectedType },
        { it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.kwireSymbols.ptrType.typeWith(it.irBuiltIns.unitType) } },
        BuiltinMemoryOps::write);
    // @formatter:on

    constructor(
        typeName: String,
        typeGetter: KWirePluginContext.() -> IrType,
        sizeEmitter: (KWirePluginContext) -> IrExpression,
        readEmitter: (KWirePluginContext, IrExpression) -> IrExpression,
        writeEmitter: (KWirePluginContext, IrExpression, IrExpression) -> IrExpression
    ) : this(typeName, typeGetter, sizeEmitter, sizeEmitter, readEmitter, writeEmitter)

    override fun getType(context: KWirePluginContext): IrType? = typeGetter(context)
    override fun emitSize(context: KWirePluginContext): IrExpression = sizeEmitter(context)
    override fun emitAlignment(context: KWirePluginContext): IrExpression = alignmentEmitter(context)

    override fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression {
        return constInt(context, 0) // Offset for scalars is always 0
    }

    override fun emitDefault(context: KWirePluginContext): IrExpression {
        return when (this) {
            VOID -> context.irBuiltIns.unitClass.getObjectInstance()
            BYTE -> 0.toByte().toIrConst(context.irBuiltIns.byteType)
            SHORT -> 0.toShort().toIrConst(context.irBuiltIns.shortType)
            INT -> 0.toIrConst(context.irBuiltIns.intType)
            LONG -> 0L.toIrConst(context.irBuiltIns.longType)
            NINT -> constNInt(context, 0L)
            UBYTE -> 0U.toUByte().toIrConst(context.irBuiltIns.ubyteType)
            USHORT -> 0U.toUShort().toIrConst(context.irBuiltIns.ushortType)
            UINT -> 0U.toIrConst(context.irBuiltIns.uintType)
            ULONG -> 0UL.toIrConst(context.irBuiltIns.ulongType)
            NUINT -> constNUInt(context, 0UL)
            FLOAT -> 0F.toIrConst(context.irBuiltIns.floatType)
            DOUBLE -> 0.0.toIrConst(context.irBuiltIns.doubleType)
            NFLOAT -> constNFloat(context, 0.0)
            PTR -> constNUInt(context, 0UL).reinterpret(
                context, context.kwireSymbols.ptrType.typeWith(context.irBuiltIns.unitType)
            )
        }
    }

    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression =
        readEmitter(context, address)

    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression =
        writeEmitter(context, address, value)
}

internal fun IrType.getBuiltinMemoryLayout(): BuiltinMemoryLayout? {
    if (isUnit()) return BuiltinMemoryLayout.VOID
    // Handle signed integer types and IEEE-754 types
    val primitiveType = type.getPrimitiveType()
    if (primitiveType != null) return when (primitiveType) {
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
    if (unsignedType != null) return when (unsignedType) {
        UnsignedType.UBYTE -> BuiltinMemoryLayout.UBYTE
        UnsignedType.USHORT -> BuiltinMemoryLayout.USHORT
        UnsignedType.UINT -> BuiltinMemoryLayout.UINT
        UnsignedType.ULONG -> BuiltinMemoryLayout.ULONG
    }
    // Handle native builtin types
    val nativeType = type.getNativeType()
    if (nativeType != null) return when (nativeType) {
        NativeType.NINT -> BuiltinMemoryLayout.NINT
        NativeType.NUINT -> BuiltinMemoryLayout.NUINT
        NativeType.NFLOAT -> BuiltinMemoryLayout.NFLOAT
        NativeType.PTR -> BuiltinMemoryLayout.PTR
    }
    return null
}