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
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstantArray
import org.jetbrains.kotlin.ir.expressions.IrConstantPrimitive
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.IrVarargElement
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.expressions.impl.IrCompositeImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetFieldImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.name.FqName

@OptIn(UnsafeDuringIrConstructionAPI::class)
private fun IrFunctionAccessExpression.putArguments( // @formatter:off
    typeArguments: Map<String, IrType>,
    valueArguments: Map<String, IrExpression>
) { // @formatter:on
    val function = symbol.owner
    for ((name, type) in typeArguments) {
        var parameter = function.typeParameters.find { it.name.asString() == name }
        // For constructor calls, alternatively attempt to resolve type parameter from class
        if (parameter == null && this is IrConstructorCall) {
            parameter = this.type.getClass()?.typeParameters?.find { it.name.asString() == name }
        }
        check(parameter != null) { "No type parameter named $name found in ${function.dump()}" }
        this.typeArguments[parameter.index] = type
    }
    for ((name, value) in valueArguments) {
        val parameter = function.parameters.find { it.name.asString() == name }
        check(parameter != null) { "No value parameter named $name found in ${function.dump()}" }
        arguments[parameter] = value
    }
}


@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrConstructorSymbol.new(
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
    typeArguments: Map<String, IrType> = emptyMap(),
    valueArguments: Map<String, IrExpression> = emptyMap()
): IrConstructorCall = IrConstructorCallImpl(
    startOffset = startOffset,
    endOffset = endOffset,
    type = owner.returnType,
    symbol = this,
    typeArgumentsCount = typeArguments.size,
    constructorTypeArgumentsCount = typeArguments.size
).apply {
    putArguments(typeArguments, valueArguments)
}

internal fun IrConstructor.new(
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
    typeArguments: Map<String, IrType> = emptyMap(),
    valueArguments: Map<String, IrExpression> = emptyMap()
): IrConstructorCall = symbol.new(startOffset, endOffset, typeArguments, valueArguments)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrSimpleFunctionSymbol.call(
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
    typeArguments: Map<String, IrType> = emptyMap(),
    valueArguments: Map<String, IrExpression> = emptyMap(),
    dispatchReceiver: IrExpression? = null,
    extensionReceiver: IrExpression? = null
): IrCall = IrCallImplWithShape(
    startOffset = startOffset,
    endOffset = endOffset,
    type = owner.returnType,
    symbol = this,
    typeArgumentsCount = typeArguments.size,
    valueArgumentsCount = valueArguments.size,
    contextParameterCount = 0,
    hasDispatchReceiver = dispatchReceiver != null,
    hasExtensionReceiver = extensionReceiver != null
).apply {
    this.dispatchReceiver = dispatchReceiver
    owner.parameters.find { it.kind == IrParameterKind.ExtensionReceiver }?.let {
        arguments[it] = extensionReceiver
    }
    putArguments(typeArguments, valueArguments)
}

internal fun IrSimpleFunction.call(
    startOffset: Int = SYNTHETIC_OFFSET,
    endOffset: Int = SYNTHETIC_OFFSET,
    typeArguments: Map<String, IrType> = emptyMap(),
    valueArguments: Map<String, IrExpression> = emptyMap(),
    dispatchReceiver: IrExpression? = null,
    extensionReceiver: IrExpression? = null
): IrCall = symbol.call(startOffset, endOffset, typeArguments, valueArguments, dispatchReceiver, extensionReceiver)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.binaryOp(
    symbol: IrSimpleFunctionSymbol, other: IrExpression, isExtension: Boolean = false
): IrExpression = symbol.call(
    dispatchReceiver = if (isExtension) null else this@binaryOp,
    extensionReceiver = if (isExtension) this@binaryOp else null,
    valueArguments = mapOf("other" to other)
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.topLevelBinaryOp(
    lhsName: String = "a", rhsName: String = "b", symbol: IrSimpleFunctionSymbol, other: IrExpression
): IrExpression = symbol.call(
    valueArguments = mapOf(
        lhsName to this@topLevelBinaryOp, rhsName to other
    )
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.plus(
    other: IrExpression, isExtension: Boolean = false
): IrExpression = binaryOp(type.getClass()!!.functions.first { function ->
    function.name.asString() == "plus" && function.parameters.first { it.kind == IrParameterKind.Regular }.type == other.type
}.symbol, other, isExtension)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.times(
    other: IrExpression, isExtension: Boolean = false
): IrExpression = binaryOp(type.getClass()!!.functions.first { function ->
    function.name.asString() == "times" && function.parameters.first { it.kind == IrParameterKind.Regular }.type == other.type
}.symbol, other, isExtension)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.minus(
    other: IrExpression, isExtension: Boolean = false
): IrExpression = binaryOp(type.getClass()!!.functions.first { function ->
    function.name.asString() == "minus" && function.parameters.first { it.kind == IrParameterKind.Regular }.type == other.type
}.symbol, other, isExtension)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.min(context: KWirePluginContext, other: IrExpression): IrExpression = topLevelBinaryOp(
    symbol = context.referenceFunctions(KWireNames.Kotlin.min).first { it.owner.returnType == type }, other = other
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.max(context: KWirePluginContext, other: IrExpression): IrExpression = topLevelBinaryOp(
    symbol = context.referenceFunctions(KWireNames.Kotlin.max).first { it.owner.returnType == type }, other = other
)

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

internal fun constNInt(context: KWirePluginContext, value: Long): IrExpression = context.toNInt(IrConstImpl.long( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.longType,
    value = value
)
) // @formatter:on

internal fun constNUInt(context: KWirePluginContext, value: ULong): IrExpression = context.toNUInt(IrConstImpl.long( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.longType,
    value = value.toLong()
)
) // @formatter:on

internal fun constNFloat(context: KWirePluginContext, value: Double): IrExpression = context.toNFloat(IrConstImpl.double( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.longType,
    value = value
)
) // @formatter:on

// Shared imports

internal fun IrAnnotationContainer.isSharedImport(): Boolean = hasAnnotation(KWireNames.SharedImport.id)

internal fun IrAnnotationContainer.isMarshal(): Boolean = hasAnnotation(KWireNames.Marshal.id)

internal fun IrAnnotationContainer.getMarshalType(): IrType? {
    val annotation = getAnnotation(KWireNames.Marshal.fqName) ?: return null
    return annotation.typeArguments.firstOrNull()
}

// Intrinsics

internal fun IrAnnotationContainer.getIntrinsicType(): KWireIntrinsicType? =
    getAnnotationValue<KWireIntrinsicType>(KWireNames.KWireIntrinsic.fqName, "type")

internal fun IrAnnotationContainer.hasStructLayoutData(): Boolean = hasAnnotation(KWireNames.StructLayout.fqName)

internal fun IrAnnotationContainer.getStructLayoutData(): ByteArray? =
    getAnnotationValue<List<Byte>>(KWireNames.StructLayout.fqName, "data")?.toByteArray()

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
        is IrConstantPrimitive -> value.unwrapRawConstValue()
        is IrConstantArray -> elements.map { it.unwrapRawConstValue() }.toList()

        is IrVararg -> elements.map { element ->
            check(element is IrExpression) { "Annotation vararg element must be an expression" }
            element.unwrapRawConstValue()
        }.toList()

        // Edge case for handling constants wrapped by conversion functions such as toNInt, toNUInt, toNFloat etc.
        is IrCall -> {
            var receiver = dispatchReceiver
            if (receiver != null && receiver is IrConst) {
                return receiver.unwrapRawConstValue()
            }
            receiver = arguments[target.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }]
            if (receiver != null && receiver is IrConst) {
                return receiver.unwrapRawConstValue()
            }
            null
        }

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
    return annotation.arguments[parameter]
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
    val values = HashMap<String, Any?>()
    val firstParamIndex = parameters.first().indexInParameters
    val lastParamIndex = firstParamIndex + parameters.size
    var paramIndex = 0
    for (index in firstParamIndex..<lastParamIndex) {
        val value = arguments[index]
        values[parameterNames[paramIndex]] = value.unwrapRawConstValue()
        paramIndex++
    }
    return values
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

internal fun List<IrStatement>.toComposite(type: IrType): IrComposite = IrCompositeImpl( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = type,
    origin = null,
    statements = this
) // @formatter:on

internal fun List<IrStatement>.toBlock(type: IrType, origin: IrStatementOrigin? = null): IrBlock = IrBlockImpl( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = type,
    origin = origin,
    statements = this
) // @formatter:on

internal fun List<IrVarargElement>.toVararg(context: KWirePluginContext, type: IrType): IrVararg = IrVarargImpl(
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = context.irBuiltIns.arrayClass.typeWith(type),
    varargElementType = type,
    elements = this
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrValueSymbol.load(): IrGetValue = IrGetValueImpl( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    type = owner.type,
    symbol = this
) // @formatter:on

internal fun IrVariable.load(): IrGetValue = symbol.load()

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrFieldSymbol.load(receiver: IrExpression? = null): IrGetField = IrGetFieldImpl( // @formatter:off
    startOffset = SYNTHETIC_OFFSET,
    endOffset = SYNTHETIC_OFFSET,
    symbol = this,
    type = owner.type,
    receiver = receiver
) // @formatter:on

internal fun IrField.load(): IrGetField = symbol.load()

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrExpression.getRawAddress(): IrExpression? { // @formatter:off
    return type.getClass()?.properties
        ?.firstOrNull { it.name.asString() == "rawAddress" }
        ?.getter
        ?.call(dispatchReceiver = this)
} // @formatter:on

internal fun IrFunction.getFunctionType(context: KWirePluginContext): IrType {
    val paramTypes = parameters.filter { it.kind == IrParameterKind.Regular }.map { it.type }
    return context.irBuiltIns.functionN(paramTypes.size).typeWith(paramTypes + returnType)
}

internal fun IrExpression.reinterpret(context: KWirePluginContext, type: IrType): IrExpression {
    if (this.type == type) return this
    val isNumericExpr = this.type.getNativeType() == NativeType.NUINT
    return when {
        type.getNativeType() == NativeType.NUINT -> getRawAddress()!!

        type.isPtr() -> {
            val pointedType = requireNotNull(type.getPointedType()) { "Could not determine pointed type for Ptr" }
            if (isNumericExpr) context.createPtr(this, pointedType)
            else context.createPtr(getRawAddress()!!, pointedType)
        }

        else -> error("Unsupported type for generated reinterpretation: ${type.render()}")
    }
}