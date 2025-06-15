/*
 * Copyright 2025 (C) Karma Krafts & associates
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
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isOverridable
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.filterIsInstanceAnd

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.binaryOp(symbol: IrSimpleFunctionSymbol, other: IrExpression): IrExpression =
    IrCallImplWithShape(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        type = this@binaryOp.type,
        symbol = symbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = 1,
        contextParameterCount = 0,
        hasDispatchReceiver = true,
        hasExtensionReceiver = false
    ).apply {
        val function = symbol.owner
        arguments[function.parameters.first { it.name.asString() == "other" }] = other
        dispatchReceiver = this@binaryOp
    }

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.plus(context: KWirePluginContext, other: IrExpression): IrExpression =
    binaryOp(type.getClass()!!.functions.first { it.name.asString() == "plus" }.symbol, other)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.times(context: KWirePluginContext, other: IrExpression): IrExpression =
    binaryOp(type.getClass()!!.functions.first { it.name.asString() == "times" }.symbol, other)

internal fun constInt(context: KWirePluginContext, value: Int): IrConstImpl = IrConstImpl.int( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.intType,
    value = value
) // @formatter:on

internal fun constLong(context: KWirePluginContext, value: Long): IrConstImpl = IrConstImpl.long( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.longType,
    value = value
) // @formatter:on

internal fun IrAnnotationContainer.getIntrinsicType(): KWireIntrinsicType? =
    getAnnotationValue<KWireIntrinsicType>(KWireNames.KWireIntrinsic.fqName, "type")

internal fun IrAnnotationContainer.hasStructLayoutData(): Boolean = hasAnnotation(KWireNames.StructLayout.fqName)

internal fun IrAnnotationContainer.getStructLayoutData(): ByteArray? =
    getAnnotationValue<List<Byte>>(KWireNames.StructLayout.fqName, "data")?.toByteArray()

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.isStruct(context: KWirePluginContext): Boolean = isSubclassOf(context.structType.owner)

internal fun IrType.isStruct(context: KWirePluginContext): Boolean = getClass()?.isStruct(context) == true

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.findConstructor(context: KWirePluginContext, paramTypes: List<IrType>): IrConstructor? {
    return constructors.find { constructor ->
        val params = constructor.parameters.filter { it.kind == IrParameterKind.Regular }
        if (params.size != paramTypes.size) return@find false
        for (index in params.indices) {
            val rhsType = paramTypes[index]
            val lhsType = params[index].type
            if (lhsType.isAssignableFrom(context, rhsType)) continue
            return@find false
        }
        true
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrElement?.unwrapRawConstValue(): Any? {
    return when (this) {
        is IrErrorExpression -> error("Got IrErrorExpression in getConstType: $description")
        is IrExpressionBody -> expression.unwrapRawConstValue()
        is IrGetField -> symbol.owner.initializer.unwrapRawConstValue()
        is IrGetEnumValue -> symbol.owner.name.asString() // Enum values are unwrapped to their constant names
        is IrClassReference -> classType
        is IrConst -> value
        is IrVararg -> elements.map { element ->
            check(element is IrExpression) { "Annotation vararg element must be an expression" }
            element.unwrapRawConstValue()
        }.toList()

        else -> null
    }
}

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T> IrElement?.unwrapConstValue(): T? {
    val value = unwrapRawConstValue()
    val javaType = T::class.java
    return (if (javaType.isEnum) (javaType.enumConstants as Array<Enum<*>>).find { it.name == value as? String }
    else value) as? T
}

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T> IrElement?.unwrapConstValues(): List<T?> {
    val values = unwrapRawConstValue() as List<Any?>? ?: return emptyList()
    val javaType = T::class.java
    val isEnum = javaType.isEnum
    return values.map { value ->
        (if (isEnum) (javaType.enumConstants as Array<Enum<*>>).find { it.name == value as? String }
        else value) as? T
    }
}

internal fun IrAnnotationContainer.getAnnotation( // @formatter:off
    type: FqName,
    index: Int = 0
): IrConstructorCall? { // @formatter:on
    return annotations.filter { it.type.classFqName == type }.getOrNull(index)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrAnnotationContainer.getRawAnnotationValue(
    type: FqName, name: String, index: Int = 0
): IrExpression? {
    val annotation = getAnnotation(type, index) ?: return null
    val constructor = annotation.symbol.owner
    // @formatter:off
    val parameter = constructor.parameters
        .filter { it.kind == IrParameterKind.Regular }
        .find { it.name.asString() == name }
        ?: return null
    // @formatter:on
    return annotation.getValueArgument(parameter.indexInOldValueParameters)
}

internal inline fun <reified T> IrAnnotationContainer.getAnnotationValue( // @formatter:off
    type: FqName,
    name: String,
    index: Int = 0
): T? = getRawAnnotationValue(type, name, index).unwrapConstValue<T>() // @formatter:on

internal inline fun <reified T> IrAnnotationContainer.getAnnotationValues( // @formatter:off
    type: FqName,
    name: String,
    index: Int = 0
): List<T?> = getRawAnnotationValue(type, name, index).unwrapConstValues<T>() // @formatter:on

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrConstructorCall.getAnnotationValues(): Map<String, Any?> {
    val constructor = symbol.owner
    val parameters = constructor.parameters.filter { it.kind == IrParameterKind.Regular }
    if (parameters.isEmpty()) return emptyMap()
    val parameterNames = parameters.map { it.name.asString() }
    check(parameterNames.size == valueArgumentsCount) { "Missing annotation parameter info" }
    val values = HashMap<String, Any?>()
    val firstParamIndex = parameters.first().indexInOldValueParameters
    val lastParamIndex = firstParamIndex + parameters.size
    var paramIndex = 0
    for (index in firstParamIndex..<lastParamIndex) {
        val value = getValueArgument(index)
        values[parameterNames[paramIndex]] = value.unwrapRawConstValue()
        paramIndex++
    }
    return values
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.getCompanionObjects(): List<IrClass> = declarations.filterIsInstanceAnd<IrClass> { it.isCompanion }

internal fun Visibility.getVisibilityName(): String = when (this) {
    Visibilities.Public -> "PUBLIC"
    Visibilities.Protected -> "PROTECTED"
    Visibilities.Internal -> "INTERNAL"
    else -> "PRIVATE"
}

internal fun Modality.getModalityName(): String = when (this) {
    Modality.OPEN -> "OPEN"
    Modality.SEALED -> "SEALED"
    Modality.ABSTRACT -> "ABSTRACT"
    Modality.FINAL -> "FINAL"
}

internal fun IrFunction.getModality(): Modality = when {
    isOverridable -> Modality.OPEN
    else -> Modality.FINAL
}

internal fun IrClassSymbol.getObjectInstance(): IrGetObjectValueImpl = IrGetObjectValueImpl(
    startOffset = SYNTHETIC_OFFSET, endOffset = SYNTHETIC_OFFSET, type = defaultType, symbol = this@getObjectInstance
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClassSymbol.getEnumConstant(name: String): IrEnumEntrySymbol {
    return requireNotNull(
        defaultType.classOrFail.owner.declarations.filterIsInstance<IrEnumEntry>()
            .find { it.name.asString() == name }) { "No entry $name in $this" }.symbol
}

internal inline fun <T> T.getEnumValue(
    type: IrClassSymbol, mapper: T.() -> String
): IrGetEnumValueImpl = IrGetEnumValueImpl(
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = type.defaultType,
    symbol = type.getEnumConstant(this.mapper())
)

internal inline fun <reified T : IrElement> IrElement.findElement(crossinline predicate: (T) -> Boolean): T? {
    var result: T? = null
    acceptVoid(object : IrVisitorVoid() {
        override fun visitElement(element: IrElement) {
            if (result != null) return
            element.acceptChildrenVoid(this)
            if (element !is T || !predicate(element)) return
            result = element
        }
    })
    return result
}

internal fun IrFunction.createCall(
    origin: IrStatementOrigin? = null
): IrCall {
    check(this is IrSimpleFunction)
    return IrCallImpl(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        type = returnType,
        symbol = symbol,
        typeArgumentsCount = typeParameters.size,
        origin = origin
    )
}