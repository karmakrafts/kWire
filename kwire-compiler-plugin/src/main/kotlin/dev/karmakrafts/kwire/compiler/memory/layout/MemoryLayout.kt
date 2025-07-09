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

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.isStruct
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.name.ClassId

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

    val typeName: String?

    fun getType(context: KWirePluginContext): IrType? {
        return typeName?.let { context.referenceClass(ClassId.fromString(it))?.defaultType }
    }

    fun emitSize(context: KWirePluginContext): IrExpression
    fun emitAlignment(context: KWirePluginContext): IrExpression
    fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression

    fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression
    fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression

    fun serialize(): ByteArray = serializer.encodeToByteArray(this)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.computeMemoryLayout(context: KWirePluginContext): MemoryLayout {
    // Handle Unit/void type
    if (type.isUnit()) return BuiltinMemoryLayout.VOID
    // Handle builtin/primitive types
    val builtinLayout = type.getBuiltinMemoryLayout()
    if (builtinLayout != null) return builtinLayout
    // Handle reference objects
    if (!type.isStruct(context)) return ReferenceMemoryLayout.of(type)
    // Handle user defined types
    return type.computeStructMemoryLayout(context) ?: BuiltinMemoryLayout.VOID
}