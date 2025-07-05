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
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isSubclassOf
import org.jetbrains.kotlin.ir.util.isSubtypeOf
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.target

internal fun IrType.isAssignableFrom(context: KWirePluginContext, type: IrType): Boolean {
    val inType = type.type
    return this == inType || inType.isSubtypeOf(type, context.typeSystemContext)
}

internal fun IrType.toClassReference(context: IrPluginContext): IrClassReferenceImpl = with(context) {
    IrClassReferenceImpl(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        type = irBuiltIns.kClassClass.starProjectedType,
        symbol = classOrFail,
        classType = this@toClassReference
    )
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.isStruct(context: KWirePluginContext): Boolean = isSubclassOf(context.kwireSymbols.structType.owner)

internal fun IrType.isStruct(context: KWirePluginContext): Boolean = getClass()?.isStruct(context) == true
internal fun IrType.hasCustomAlignment(): Boolean = getClass()?.hasAnnotation(KWireNames.AlignAs.fqName) == true
internal fun IrType.getCustomAlignment(): Int? = getClass()?.getAnnotationValue<Int>(KWireNames.AlignAs.fqName, "value")

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.isAddress(context: KWirePluginContext): Boolean = isSubclassOf(context.kwireSymbols.addressType.owner)
internal fun IrType.isAddress(context: KWirePluginContext): Boolean = getClass()?.isAddress(context) == true

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrClass.isPointed(context: KWirePluginContext): Boolean = isSubclassOf(context.kwireSymbols.pointedType.owner)
internal fun IrType.isPointed(context: KWirePluginContext): Boolean = getClass()?.isPointed(context) == true

internal fun IrClass.isNumPtr(): Boolean = isClassWithFqName(KWireNames.NumPtr.fqName)
internal fun IrType.isNumPtr(): Boolean = getClass()?.isNumPtr() == true

internal fun IrClass.isPtr(): Boolean = isClassWithFqName(KWireNames.Ptr.fqName)
internal fun IrType.isPtr(): Boolean = getClass()?.isPtr() == true

internal fun IrClass.isFunPtr(): Boolean = isClassWithFqName(KWireNames.FunPtr.fqName)
internal fun IrType.isFunPtr(): Boolean = getClass()?.isFunPtr() == true

internal fun IrClass.isVoidPtr(): Boolean = isClassWithFqName(KWireNames.VoidPtr.fqName)
internal fun IrType.isVoidPtr(): Boolean = getClass()?.isVoidPtr() == true

internal fun IrType.getPointedType(): IrType? {
    if (this !is IrSimpleType) return null
    return arguments.first().typeOrNull
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.resolveFromReceiver(call: IrCall): IrType? {
    if (!isTypeParameter()) return this
    val typeParam = (classifierOrNull as? IrTypeParameterSymbol)?.owner ?: return null

    fun tryResolve(parentType: IrType): IrType? {
        if (parentType !is IrSimpleType) return null
        val dispatchClass = parentType.getClass() ?: return null
        val classTypeParam = dispatchClass.typeParameters.find { it == typeParam } ?: return null
        return parentType.arguments[classTypeParam.index].typeOrNull
    }
    // First attempt to resolve via dispatcher reseiver, then extension receiver
    return call.dispatchReceiver?.type?.let(::tryResolve)
        ?: call.arguments[call.target.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }]?.type?.let(::tryResolve)
}

internal fun IrType.isCDecl(): Boolean = hasAnnotation(KWireNames.CDecl.id)
internal fun IrType.isStdCall(): Boolean = hasAnnotation(KWireNames.StdCall.id)
internal fun IrType.isThisCall(): Boolean = hasAnnotation(KWireNames.ThisCall.id)
internal fun IrType.isFastCall(): Boolean = hasAnnotation(KWireNames.FastCall.id)