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
import org.jetbrains.kotlin.ir.expressions.IrExpression

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
}

internal fun MemoryLayout.serialize(): ByteArray = msgPack.encodeToByteArray(this)

@SerialName("builtin")
@Serializable
internal enum class BuiltinMemoryLayout(
    @Transient private val sizeEmitter: (KWirePluginContext) -> IrExpression,
    @Transient private val alignmentEmitter: (KWirePluginContext) -> IrExpression
) : MemoryLayout {
    // @formatter:off
    VOID    ({ constInt(it, 0) }),
    BYTE    ({ constInt(it, Byte.SIZE_BYTES) }),
    SHORT   ({ constInt(it, Short.SIZE_BYTES) }),
    INT     ({ constInt(it, Int.SIZE_BYTES) }),
    LONG    ({ constInt(it, Long.SIZE_BYTES) }),
    NINT    ({ it.emitPointerSize() }),
    UBYTE   ({ constInt(it, UByte.SIZE_BYTES) }),
    USHORT  ({ constInt(it, UShort.SIZE_BYTES) }),
    UINT    ({ constInt(it, UInt.SIZE_BYTES) }),
    ULONG   ({ constInt(it, ULong.SIZE_BYTES) }),
    NUINT   ({ it.emitPointerSize() }),
    FLOAT   ({ constInt(it, Float.SIZE_BYTES) }),
    DOUBLE  ({ constInt(it, Double.SIZE_BYTES) }),
    NFLOAT  ({ it.emitPointerSize() }),
    ADDRESS ({ it.emitPointerSize() });
    // @formatter:on

    constructor(sizeEmitter: (KWirePluginContext) -> IrExpression) : this(sizeEmitter, sizeEmitter)

    override fun emitSize(context: KWirePluginContext): IrExpression = sizeEmitter(context)
    override fun emitAlignment(context: KWirePluginContext): IrExpression = alignmentEmitter(context)
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
            var expr = first.emitSize(context).plus(context, second.emitSize(context))
            for (index in 2..<fields.size) {
                expr = expr.plus(context, fields[index].emitSize(context))
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
}