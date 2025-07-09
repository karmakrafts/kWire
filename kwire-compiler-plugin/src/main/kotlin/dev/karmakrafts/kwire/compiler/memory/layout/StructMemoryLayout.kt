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
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.constInt
import dev.karmakrafts.kwire.compiler.util.getStructLayoutData
import dev.karmakrafts.kwire.compiler.util.hasStructLayoutData
import dev.karmakrafts.kwire.compiler.util.max
import dev.karmakrafts.kwire.compiler.util.new
import dev.karmakrafts.kwire.compiler.util.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties

@SerialName("struct")
@Serializable
internal data class StructMemoryLayout
@Deprecated( // @formatter:off
    message = "StructMemoryLayout shouldn't be constructed directly",
    replaceWith = ReplaceWith("StructMemoryLayout.of")
) // @formatter:on
constructor(
    override val typeName: String?, // FQN of IrClass for serialization
    val fields: List<MemoryLayout>
) : MemoryLayout {
    companion object {
        @Suppress("DEPRECATION")
        val zeroSize: StructMemoryLayout = StructMemoryLayout(null, emptyList())

        @Suppress("DEPRECATION")
        fun of(type: IrType, fields: List<MemoryLayout>): StructMemoryLayout {
            return if (fields.isEmpty()) zeroSize
            else StructMemoryLayout(type.getClass()?.classId?.asString(), fields)
        }
    }

    override fun emitSize(context: KWirePluginContext): IrExpression = when (fields.size) {
        0 -> constInt(context, 0)
        1 -> fields.first().emitSize(context)
        else -> {
            val (first, second) = fields
            var expr = first.emitSize(context).plus(second.emitSize(context))
            for (index in 2..<fields.size) {
                expr = expr.plus(fields[index].emitSize(context))
            }
            expr
        }
    }

    override fun emitAlignment(context: KWirePluginContext): IrExpression = when (fields.size) {
        0 -> constInt(context, 0)
        1 -> fields.first().emitAlignment(context)
        else -> {
            val (first, second) = fields
            var expr = first.emitAlignment(context).max(context, second.emitAlignment(context))
            for (index in 2..<fields.size) {
                expr = expr.max(context, fields[index].emitAlignment(context))
            }
            expr
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun emitRead(context: KWirePluginContext, address: IrExpression): IrExpression {
        val type = getType(context) ?: error("Could not retrieve type for struct $typeName")
        val clazz = type.getClass() ?: error("Could not retrieve class for struct type $typeName")
        val constructor =
            clazz.primaryConstructor ?: error("Could not retrieve struct primary constructor for $typeName")

        val properties = clazz.properties
        val parameters = constructor.parameters.filter { it.kind == IrParameterKind.Regular }
        check(parameters.all { param -> properties.any { param.name.asString() == it.name.asString() } }) {
            "Primary struct constructor must only declare properties"
        }
        val arguments = HashMap<String, IrExpression>()

        // Load all primary constructor arguments from memory using their respective memory layout
        for (parameterIndex in parameters.indices) {
            val parameter = parameters[parameterIndex]
            val fieldLayout = fields[parameterIndex]
            val fieldOffset = emitOffsetOf(context, parameterIndex)
            arguments[parameter.name.asString()] = fieldLayout.emitRead(context, address.plus(fieldOffset))
        }

        val instance = constructor.new(valueArguments = arguments)

        // Handle struct fields which are not declared as part of the primary constructor
        val memberProperties = properties.filter { it.parent is IrClass }.toList()
        for (propertyIndex in memberProperties.indices) {
            val property = memberProperties[propertyIndex]
            val fieldIndex = parameters.size + propertyIndex
            val fieldLayout = fields[fieldIndex] // Skip the constructor-defined field layouts
            val fieldOffset = emitOffsetOf(context, fieldIndex)
            val setter = property.setter ?: error("Could not retrieve setter for $typeName.${property.name.asString()}")
            setter.call(
                dispatchReceiver = instance,
                valueArguments = mapOf("value" to fieldLayout.emitRead(context, address.plus(fieldOffset))),
            )
        }

        return instance
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun emitWrite(context: KWirePluginContext, address: IrExpression, value: IrExpression): IrExpression {
        val type = getType(context) ?: error("Could not retrieve type for struct $typeName")
        val clazz = type.getClass() ?: error("Could not retrieve class for struct type $typeName")
        val properties = clazz.properties.toList()

        for (propertyIndex in properties.indices) {
            properties[propertyIndex]
            fields[propertyIndex]
        }

        TODO("Finish implementation")
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

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.computeStructMemoryLayout(context: KWirePluginContext): StructMemoryLayout? {
    val clazz = type.getClass() ?: return null
    if (clazz.hasStructLayoutData()) {
        // If this struct already has layout data attached, deserialize it
        return MemoryLayout.deserialize(clazz.getStructLayoutData()!!) as? StructMemoryLayout
    }
    val fields = ArrayList<MemoryLayout>()
    // Use .properties so we get constructor props + member props
    for (property in clazz.properties) {
        val propertyType = property.backingField?.type
        check(propertyType != null) { "Struct field must have a backing field" }
        fields += propertyType.computeMemoryLayout(context)
    }
    return StructMemoryLayout.of(this, fields)
}