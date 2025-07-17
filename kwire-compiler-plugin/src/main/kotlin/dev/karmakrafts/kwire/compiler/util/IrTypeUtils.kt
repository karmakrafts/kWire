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
import dev.karmakrafts.kwire.compiler.memory.layout.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.layout.computeMemoryLayout
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImplWithShape
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.SimpleTypeNullability
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.isSubtypeOf
import org.jetbrains.kotlin.ir.util.isTypeParameter

internal enum class ConstCastType {
    NONE, ADD_CONSTNESS, REMOVE_CONSTNESS
}

internal fun IrType.getConstCastTypeFrom(type: IrType): ConstCastType = when {
    !isConst() && type.isConst() -> ConstCastType.REMOVE_CONSTNESS
    isConst() && !type.isConst() -> ConstCastType.ADD_CONSTNESS
    else -> ConstCastType.NONE
}

internal fun IrType.isPointerAssignableFrom(type: IrType, ignoreConstness: Boolean = false): Boolean {
    if (!ignoreConstness && getConstCastTypeFrom(type) == ConstCastType.REMOVE_CONSTNESS) {
        // For normal assignments, we are not allowed to drop constness
        return false
    }
    if (!isPtr() || !type.isPtr()) return false
    val pointedType = getPointedTypeArgument() ?: return false
    if (pointedType is IrStarProjection) return true
    if (pointedType !is IrType) return false
    if (pointedType.isUnit()) {
        // void* is assignable from any pointer type
        return true
    }
    return pointedType.isSameAs(type.getPointedType() ?: return false)
}

internal fun IrType.isAssignableFrom(context: KWirePluginContext, inType: IrType): Boolean {
    return isSameAs(inType) || inType.isSubtypeOf(this, context.typeSystemContext) || isPointerAssignableFrom(inType)
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.isStruct(context: KWirePluginContext): Boolean =
    isSubclassOf(context.kwireSymbols.structType.owner)

internal fun IrType.isStruct(context: KWirePluginContext): Boolean = getClass()?.isStruct(context) == true
internal fun IrType.hasCustomAlignment(): Boolean = getClass()?.hasAnnotation(KWireNames.AlignAs.fqName) == true
internal fun IrType.getCustomAlignment(): Int? = getClass()?.getAnnotationValue<Int>(KWireNames.AlignAs.fqName, "value")

internal fun IrClass.isPtr(): Boolean = isClassWithFqName(KWireNames.Ptr.fqName)
internal fun IrType.isPtr(): Boolean = getClass()?.isPtr() == true

internal fun IrClass.isCFn(): Boolean = isClassWithFqName(KWireNames.CFn.fqName)
internal fun IrType.isCFn(): Boolean = getClass()?.isCFn() == true

internal fun IrType.getPointedType(): IrType? {
    if (this !is IrSimpleType) return null
    return arguments.firstOrNull()?.typeOrNull
}

internal fun IrType.getPointedTypeArgument(): IrTypeArgument? {
    if (this !is IrSimpleType) return null
    return arguments.firstOrNull()
}

internal fun IrType.isCDecl(): Boolean = hasAnnotation(KWireNames.CDecl.id)
internal fun IrType.isStdCall(): Boolean = hasAnnotation(KWireNames.StdCall.id)
internal fun IrType.isThisCall(): Boolean = hasAnnotation(KWireNames.ThisCall.id)
internal fun IrType.isFastCall(): Boolean = hasAnnotation(KWireNames.FastCall.id)

internal fun IrType.isConst(): Boolean = hasAnnotation(KWireNames.Const.id)
internal fun IrTypeParameter.isConst(): Boolean = hasAnnotation(KWireNames.Const.id)
internal fun IrType.isInheritsConstness(): Boolean = hasAnnotation(KWireNames.InheritsConstness.id)

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.isValueType(context: KWirePluginContext): Boolean {
    if (isTypeParameter()) {
        // If the type parameter has the @ValueType annotation, any non value type would fail the checker stage
        return (classifierOrNull as? IrTypeParameterSymbol)?.owner?.hasAnnotation(KWireNames.ValueType.id) == true
    }
    return computeMemoryLayout(context) !is ReferenceMemoryLayout || isCFn()
}

internal fun IrTypeParameter.isValueType(): Boolean {
    return hasAnnotation(KWireNames.ValueType.id)
}

internal fun IrType.markedConst(context: KWirePluginContext): IrType {
    return IrSimpleTypeImpl(
        classifier = classifierOrFail,
        nullability = if (isNullable()) SimpleTypeNullability.MARKED_NULLABLE else SimpleTypeNullability.DEFINITELY_NOT_NULL,
        arguments = if (this is IrSimpleType) arguments.toList() else emptyList(),
        annotations = listOf(
            IrConstructorCallImplWithShape(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                type = context.kwireSymbols.constType.defaultType,
                symbol = context.kwireSymbols.constConstructor,
                typeArgumentsCount = 0,
                constructorTypeArgumentsCount = 0,
                valueArgumentsCount = 0,
                contextParameterCount = 0,
                hasDispatchReceiver = false,
                hasExtensionReceiver = false
            )
        )
    )
}

internal fun IrType.isSameAs(type: IrType): Boolean = this == type