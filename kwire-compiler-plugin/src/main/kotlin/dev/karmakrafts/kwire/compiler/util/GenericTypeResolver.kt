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

import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.isTypeParameter

internal sealed interface ResolvedType {
    object Star : ResolvedType
    data class Concrete(val type: IrType) : ResolvedType
}

internal fun IrTypeArgument.toResolvedType(): ResolvedType = when (this) {
    is IrStarProjection -> ResolvedType.Star
    is IrTypeProjection -> ResolvedType.Concrete(typeOrFail)
}

internal fun IrTypeParametersContainer.computeSubstitutions(
    arguments: List<IrTypeArgument>
): Map<IrTypeParameterSymbol, ResolvedType> {
    return if (typeParameters.size != arguments.size) emptyMap()
    else typeParameters.map { it.symbol }.zip(arguments.map { it.toResolvedType() }).toMap()
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrType.resolveFromReceiver( // @formatter:off
    parent: IrTypeParametersContainer,
    typeArguments: List<IrTypeArgument>,
    receiver: IrExpression?
): ResolvedType? { // @formatter:on
    if (!isTypeParameter()) return ResolvedType.Concrete(this)
    val symbol = classifierOrNull as? IrTypeParameterSymbol ?: return null

    val substitutions = parent.computeSubstitutions(typeArguments)
    val type = substitutions[symbol]

    if (type == null) {
        // Attempt to resolve via receiver
        val receiverType = receiver?.type ?: return null
        if (receiverType !is IrSimpleType) return null
        val receiverClass = receiverType.getClass() ?: return null
        return resolveFromReceiver(
            parent = receiverClass,
            typeArguments = receiverType.arguments,
            receiver = receiverClass.thisReceiver?.symbol?.load()
        )
    }

    return type
}