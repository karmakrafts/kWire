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
import org.jetbrains.kotlin.ir.expressions.IrExpression
import java.nio.ByteBuffer
import java.util.*

internal sealed interface MemoryLayout {
    companion object {
        const val MAX_BUFFER_SIZE: Int = 8192

        @OptIn(ExperimentalStdlibApi::class)
        private fun deserialize(buffer: ByteBuffer, fields: ArrayList<MemoryLayout>) {
            while (buffer.hasRemaining()) {
                var tag = buffer.get().toUByte()

                if (tag == VoidMemoryLayout.TAG) {
                    fields += VoidMemoryLayout
                    continue
                }

                val builtinLayout = BuiltinMemoryLayout.entries.find { it.tag == tag }
                if (builtinLayout != null) {
                    fields += builtinLayout
                    continue
                }

                check(tag == StructMemoryLayout.TAG_BEGIN) { "Invalid struct beginning: 0x${tag.toHexString()}" }
                val nestingLevel = buffer.get().toUByte()
                val subData = ByteBuffer.allocate(MAX_BUFFER_SIZE)

                fun peekTag(): UByte = buffer.get(minOf(buffer.capacity(), buffer.position() + 1)).toUByte()
                var nextTag = peekTag()
                while (tag != StructMemoryLayout.TAG_END && nextTag != nestingLevel) {
                    subData.put(tag.toByte())
                    tag = buffer.get().toUByte()
                    nextTag = peekTag()
                }

                tag = buffer.get().toUByte() // Skip over TAG_END_STRUCT
                check(tag == StructMemoryLayout.TAG_END) { "Invalid struct end: 0x${tag.toHexString()}" }

                val structFields = ArrayList<MemoryLayout>()
                deserialize(subData, structFields)
                fields += StructMemoryLayout(structFields)
            }
        }

        fun deserialize(data: ByteArray): MemoryLayout {
            val fields = ArrayList<MemoryLayout>()
            deserialize(ByteBuffer.wrap(data), fields)
            return StructMemoryLayout(fields)
        }
    }

    fun emitSize(context: KWirePluginContext): IrExpression
    fun emitAlignment(context: KWirePluginContext): IrExpression

    fun serialize(buffer: ByteBuffer, stack: Stack<Unit>)

    fun serialize(): ByteArray {
        val buffer = ByteBuffer.allocate(MAX_BUFFER_SIZE)
        serialize(buffer, Stack())
        return ByteArray(buffer.position()).apply {
            buffer.flip()
            buffer.get(this)
        }
    }
}

internal object VoidMemoryLayout : MemoryLayout {
    const val TAG: UByte = 0x00U

    override fun emitSize(context: KWirePluginContext): IrExpression {
        return constInt(context, 0)
    }

    override fun emitAlignment(context: KWirePluginContext): IrExpression {
        return constInt(context, 0)
    }

    override fun serialize(buffer: ByteBuffer, stack: Stack<Unit>) {
        buffer.put(TAG.toByte())
    }
}

internal enum class BuiltinMemoryLayout(
    val tag: UByte,
    private val sizeEmitter: (KWirePluginContext) -> IrExpression,
    private val alignmentEmitter: (KWirePluginContext) -> IrExpression
) : MemoryLayout {
    // @formatter:off
    BYTE    (0x01U, { constInt(it, Byte.SIZE_BYTES) }),
    SHORT   (0x02U, { constInt(it, Short.SIZE_BYTES) }),
    INT     (0x03U, { constInt(it, Int.SIZE_BYTES) }),
    LONG    (0x04U, { constInt(it, Long.SIZE_BYTES) }),
    UBYTE   (0x05U, { constInt(it, UByte.SIZE_BYTES) }),
    USHORT  (0x06U, { constInt(it, UShort.SIZE_BYTES) }),
    UINT    (0x07U, { constInt(it, UInt.SIZE_BYTES) }),
    ULONG   (0x08U, { constInt(it, ULong.SIZE_BYTES) }),
    FLOAT   (0x09U, { constInt(it, Float.SIZE_BYTES) }),
    DOUBLE  (0x0AU, { constInt(it, Double.SIZE_BYTES) }),
    ADDRESS (0x0BU, { it.emitPointerSize() });
    // @formatter:on

    constructor(tag: UByte, sizeEmitter: (KWirePluginContext) -> IrExpression) : this(tag, sizeEmitter, sizeEmitter)

    override fun emitSize(context: KWirePluginContext): IrExpression = sizeEmitter(context)
    override fun emitAlignment(context: KWirePluginContext): IrExpression = alignmentEmitter(context)

    override fun serialize(buffer: ByteBuffer, stack: Stack<Unit>) {
        buffer.put(tag.toByte())
    }
}

@JvmInline
internal value class StructMemoryLayout(
    val fields: List<MemoryLayout>
) : MemoryLayout {
    companion object {
        const val TAG_BEGIN: UByte = 0xFEU
        const val TAG_END: UByte = 0xFFU
    }

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
            var expr = first.emitAlignment(context).plus(context, second.emitAlignment(context))
            for (index in 2..<fields.size) {
                expr = expr.plus(context, fields[index].emitAlignment(context))
            }
            expr
        }
    }

    override fun serialize(buffer: ByteBuffer, stack: Stack<Unit>) {
        for (field in fields) {
            when (field) {
                is BuiltinMemoryLayout, is VoidMemoryLayout -> field.serialize(buffer, stack)
                is StructMemoryLayout -> {
                    check(stack.size < Byte.MAX_VALUE) { "Exceeded max struct nesting level of 255" }
                    buffer.put(TAG_BEGIN.toByte())
                    buffer.put(stack.size.toByte())
                    stack.push(Unit)
                    field.serialize(buffer, stack)
                    stack.pop()
                    buffer.put(TAG_END.toByte())
                    buffer.put(stack.size.toByte())
                }
            }
        }
    }
}