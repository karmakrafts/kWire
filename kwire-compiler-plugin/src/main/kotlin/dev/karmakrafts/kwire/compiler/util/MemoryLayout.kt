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

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import dev.karmakrafts.kwire.compiler.KWirePluginContext
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.functions

private val msgPack: MsgPack = MsgPack(serializersModule = SerializersModule {
    polymorphic(MemoryLayout::class) {
        subclass(BuiltinMemoryLayout::class)
        subclass(StructMemoryLayout::class)
    }
})

@Serializable
@Polymorphic
internal sealed interface MemoryLayout {
    companion object {
        fun deserialize(data: ByteArray): MemoryLayout = msgPack.decodeFromByteArray(data)
    }

    fun emitSize(context: KWirePluginContext): IrExpression
    fun emitAlignment(context: KWirePluginContext): IrExpression
    fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression

    fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression
    fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression
}

internal fun MemoryLayout.serialize(): ByteArray = msgPack.encodeToByteArray(this)

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
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.byteType } },
        ::write),

    SHORT({ constInt(it, Short.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.shortType } },
        ::write),

    INT({ constInt(it, Int.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.intType } },
        ::write),

    LONG({ constInt(it, Long.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.longType } },
        ::write),

    NINT({ it.emitPointerSize() },
        { ctx, addr -> read(ctx, addr) { it.nIntType.owner.expandedType } },
        ::write),

    // Unsigned types

    UBYTE({ constInt(it, UByte.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.uByteType.defaultType } },
        ::write),

    USHORT({ constInt(it, UShort.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.uShortType.defaultType } },
        ::write),

    UINT({ constInt(it, UInt.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.uIntType.defaultType } },
        ::write),

    ULONG({ constInt(it, ULong.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.uLongType.defaultType } },
        ::write),

    NUINT({ it.emitPointerSize() },
        { ctx, addr -> read(ctx, addr) { it.nUIntType.defaultType } },
        ::write),

    // IEEE-754 types

    FLOAT({ constInt(it, Float.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.floatType } },
        ::write),

    DOUBLE({ constInt(it, Double.SIZE_BYTES) },
        { ctx, addr -> read(ctx, addr) { it.irBuiltIns.doubleType } },
        ::write),

    NFLOAT({ it.emitPointerSize() },
        { ctx, addr -> read(ctx, addr) { it.nFloatType.owner.expandedType } },
        ::write),

    // Pointer types

    ADDRESS ({ it.emitPointerSize() },
        { ctx, addr -> read(ctx, addr) { it.voidPtrType.defaultType } },
        ::write);
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

    companion object {
        private fun read( // @formatter:off
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

        private fun write( // @formatter:off
            context: KWirePluginContext,
            address: IrExpression,
            value: IrExpression
        ): IrExpression { // @formatter:on
            val type = value.type
            val function = context.memoryCompanionType.functions.map { it.owner }.first { function ->
                val params = function.parameters.filter { it.kind == IrParameterKind.Regular }
                function.name.asString().startsWith("write") && params.last().type == type
            }
            return function.call( // @formatter:off
                dispatchReceiver = context.memoryCompanionType.getObjectInstance(),
                valueArguments = mapOf("address" to address, "value" to value)
            ) // @formatter:on
        }
    }
}

@SerialName("struct")
@Serializable
internal data class StructMemoryLayout(
    val fields: List<MemoryLayout>
) : MemoryLayout {
    override fun emitSize(context: KWirePluginContext): IrExpression = when {
        fields.isEmpty() -> constInt(context, 0)
        fields.size == 1 -> fields.first().emitSize(context)
        else -> {
            val (first, second) = fields
            var expr = first.emitSize(context).plus(second.emitSize(context))
            for (index in 2..<fields.size) {
                expr = expr.plus(fields[index].emitSize(context))
            }
            expr
        }
    }

    override fun emitAlignment(context: KWirePluginContext): IrExpression = when {
        fields.isEmpty() -> constInt(context, 0)
        fields.size == 1 -> fields.first().emitAlignment(context)
        else -> {
            val (first, second) = fields
            var expr = first.emitAlignment(context).max(context, second.emitAlignment(context))
            for (index in 2..<fields.size) {
                expr = expr.max(context, fields[index].emitAlignment(context))
            }
            expr
        }
    }

    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression {
        TODO("Not yet implemented")
    }

    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression {
        TODO("Not yet implemented")
    }

    override fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression = when (index) {
        0 -> constInt(context, 0)
        1 -> fields.first().emitSize(context)
        else -> {
            val (first, second) = fields
            var expr = first.emitSize(context).plus(second.emitSize(context))
            for (fieldIndex in 2..<index) {
                expr = expr.plus(fields[fieldIndex].emitSize(context))
            }
            expr
        }
    }
}

@SerialName("ref")
@Serializable
internal object ReferenceMemoryLayout : MemoryLayout {
    override fun emitSize(context: KWirePluginContext): IrExpression {
        return context.emitPointerSize()
    }

    override fun emitAlignment(context: KWirePluginContext): IrExpression {
        return context.emitPointerSize()
    }

    override fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression {
        return constInt(context, 0) // Offset for references is always 0
    }

    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression {
        error("Reading references from memory is not supported")
    }

    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression {
        error("Writing references to memory is not supported")
    }
}