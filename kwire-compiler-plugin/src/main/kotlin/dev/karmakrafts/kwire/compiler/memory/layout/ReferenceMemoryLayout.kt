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
import dev.karmakrafts.kwire.compiler.util.constInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId

@SerialName("ref")
@Serializable
internal class ReferenceMemoryLayout
@Deprecated( // @formatter:off
    message = "ReferenceMemoryLayout shouldn't be constructed directly",
    replaceWith = ReplaceWith("ReferenceMemoryLayout.of")
) // @formatter:on
constructor(
    override val typeName: String?
) : MemoryLayout {
    companion object {
        @Suppress("DEPRECATION")
        fun of(type: IrType): ReferenceMemoryLayout {
            return ReferenceMemoryLayout(type.getClass()?.classId?.asString())
        }
    }

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