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

import dev.karmakrafts.kwire.abi.type.isPtr
import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.getIrType
import kotlinx.io.readByteArray
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import dev.karmakrafts.kwire.abi.type.ArrayType as ABIArrayType
import dev.karmakrafts.kwire.abi.type.BuiltinType as ABIBuiltinType
import dev.karmakrafts.kwire.abi.type.ConeType as ABIConeType
import dev.karmakrafts.kwire.abi.type.ReferenceType as ABIReferenceType
import dev.karmakrafts.kwire.abi.type.StructType as ABIStructType
import dev.karmakrafts.kwire.abi.type.NullableType as ABINullableType
import dev.karmakrafts.kwire.abi.type.Type as ABIType

internal sealed interface MemoryLayout {
    companion object {
        fun deserialize(data: ByteArray): MemoryLayout? = ABIType.decompressAndDeserialize(data).getMemoryLayout()
    }

    val abiType: ABIType

    fun emitSize(context: KWirePluginContext): IrExpression
    fun emitAlignment(context: KWirePluginContext): IrExpression
    fun emitOffsetOf(context: KWirePluginContext, index: Int): IrExpression
    fun emitDefault(context: KWirePluginContext): IrExpression

    fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression
    fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression

    fun serialize(): ByteArray = abiType.serializeAndCompress().readByteArray()

    fun getType(context: KWirePluginContext): IrType? = abiType.getIrType(context)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun ABIType.getMemoryLayout(): MemoryLayout? {
    if (isPtr()) return BuiltinMemoryLayout.PTR // No matter the variance, pointer layout is always the same
    return when (this) {
        is ABINullableType -> actualType.getMemoryLayout() // Nullability is irrelevant for memory layout
        is ABIBuiltinType -> getBuiltinMemoryLayout()
        is ABIReferenceType -> ReferenceMemoryLayout(this)
        is ABIStructType -> StructMemoryLayout(this)
        // TODO: Right now we translate arrays into flat structs, because its easy and i'm lazy
        is ABIArrayType -> {
            StructMemoryLayout(ABIStructType(symbolName, ArrayList<ABIType>(dimensions).apply {
                fill(elementType)
            }))
        }

        is ABIConeType -> null
    }
}