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

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import dev.karmakrafts.kwire.compiler.KWirePluginContext
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.kotlin.ir.expressions.IrExpression

private val serializer: MsgPack = MsgPack(serializersModule = SerializersModule {
    polymorphic(MemoryLayout::class) {
        subclass(BuiltinMemoryLayout::class)
        subclass(StructMemoryLayout::class)
        subclass(ReferenceMemoryLayout::class)
    }
})

@Serializable
@Polymorphic
internal sealed interface MemoryLayout {
    companion object {
        fun deserialize(data: ByteArray): MemoryLayout = serializer.decodeFromByteArray(data)
    }

    fun emitSize(context: KWirePluginContext): IrExpression
    fun emitAlignment(context: KWirePluginContext): IrExpression
    fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression

    fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression
    fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression

    fun serialize(): ByteArray = serializer.encodeToByteArray(this)
}