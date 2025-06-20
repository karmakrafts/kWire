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

package dev.karmakrafts.kwire.compiler.memory

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.constInt
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.functions

// This code gets its own object because of initialization order. See KT-74926
@OptIn(UnsafeDuringIrConstructionAPI::class)
private object BuiltinMemoryOps {
    fun read( // @formatter:off
        context: KWirePluginContext,
        address: IrExpression,
        typeSelector: (KWirePluginContext) -> IrType
    ): IrExpression { // @formatter:on
        val type = typeSelector(context)
        val function = context.memoryCompanionType.functions.map { it.owner }.first { function ->
            function.name.asString().startsWith("read") && function.returnType == type
        }
        return function.call( // @formatter:off
            dispatchReceiver = context.memoryCompanionType.getObjectInstance(),
            valueArguments = mapOf("address" to address)
        ) // @formatter:on
    }

    fun write( // @formatter:off
        context: KWirePluginContext,
        address: IrExpression,
        value: IrExpression
    ): IrExpression { // @formatter:on
        val type = value.type
        val function = context.memoryCompanionType.functions.map { it.owner }.first { function ->
            val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
            function.name.asString().startsWith("write") && params[1].type == type
        }
        return function.call( // @formatter:off
            dispatchReceiver = context.memoryCompanionType.getObjectInstance(),
            valueArguments = mapOf("address" to address, "value" to value)
        ) // @formatter:on
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
@SerialName("builtin")
@Serializable
internal enum class BuiltinMemoryLayout(
    @Transient private val sizeEmitter: (KWirePluginContext) -> IrExpression,
    @Transient private val alignmentEmitter: (KWirePluginContext) -> IrExpression,
    @Transient private val readEmitter: (KWirePluginContext, IrExpression) -> IrExpression,
    @Transient private val writeEmitter: (KWirePluginContext, IrExpression, IrExpression) -> IrExpression
) : MemoryLayout {
    // @formatter:off
    VOID({ constInt(it, 0) },
        { _, _ -> error("Unit/void type cannot be read from memory") },
        { _, _, _ -> error("Unit/void type cannot be written to memory") }),

    // Signed types

    BYTE({ constInt(it, Byte.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.byteType } },
        BuiltinMemoryOps::write),

    SHORT({ constInt(it, Short.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.shortType } },
        BuiltinMemoryOps::write),

    INT({ constInt(it, Int.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.intType } },
        BuiltinMemoryOps::write),

    LONG({ constInt(it, Long.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.longType } },
        BuiltinMemoryOps::write),

    NINT({ it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.nIntType.owner.expandedType } },
        BuiltinMemoryOps::write),

    // Unsigned types

    UBYTE({ constInt(it, UByte.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.uByteType.defaultType } },
        BuiltinMemoryOps::write),

    USHORT({ constInt(it, UShort.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.uShortType.defaultType } },
        BuiltinMemoryOps::write),

    UINT({ constInt(it, UInt.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.uIntType.defaultType } },
        BuiltinMemoryOps::write),

    ULONG({ constInt(it, ULong.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.uLongType.defaultType } },
        BuiltinMemoryOps::write),

    NUINT({ it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.nUIntType.defaultType } },
        BuiltinMemoryOps::write),

    // IEEE-754 types

    FLOAT({ constInt(it, Float.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.floatType } },
        BuiltinMemoryOps::write),

    DOUBLE({ constInt(it, Double.SIZE_BYTES) },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.irBuiltIns.doubleType } },
        BuiltinMemoryOps::write),

    NFLOAT({ it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.nFloatType.owner.expandedType } },
        BuiltinMemoryOps::write),

    // Pointer types

    ADDRESS({ it.emitPointerSize() },
        { ctx, addr -> BuiltinMemoryOps.read(ctx, addr) { it.voidPtrType.defaultType } },
        BuiltinMemoryOps::write);
    // @formatter:on

    constructor(
        sizeEmitter: (KWirePluginContext) -> IrExpression,
        readEmitter: (KWirePluginContext, IrExpression) -> IrExpression,
        writeEmitter: (KWirePluginContext, IrExpression, IrExpression) -> IrExpression
    ) : this(sizeEmitter, sizeEmitter, readEmitter, writeEmitter)

    override fun emitSize(context: KWirePluginContext): IrExpression = sizeEmitter(context)
    override fun emitAlignment(context: KWirePluginContext): IrExpression = alignmentEmitter(context)

    override fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression {
        return constInt(context, 0) // Offset for scalars is always 0
    }

    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression =
        readEmitter(context, address)

    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression =
        writeEmitter(context, address, value)
}