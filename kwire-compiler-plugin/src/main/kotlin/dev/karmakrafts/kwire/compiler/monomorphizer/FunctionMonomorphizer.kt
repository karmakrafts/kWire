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

package dev.karmakrafts.kwire.compiler.monomorphizer

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.transformer.TemplateTransformer
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.parameterAccessor
import dev.karmakrafts.kwire.compiler.util.remapSyntheticSourceRanges
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.deepCopyWithoutPatchingParents
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

internal data class MonoFunctionSignature( // @formatter:off
    val symbol: IrFunctionSymbol,
    val substitutions: LinkedHashMap<IrTypeParameterSymbol, IrType>
) // @formatter:on

internal class FunctionMonomorphizer(
    private val context: KWirePluginContext
) {
    companion object : GeneratedDeclarationKey() {
        val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
    }

    private fun IrFunction.remapReceiverParameters(monoFunctionClass: IrClass) {
        val oldDispatchReceiver = parameters.firstOrNull { it.kind == IrParameterKind.DispatchReceiver }
        val dispatchReceiverParam = buildReceiverParameter {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            name = Name.special("<this>")
            type = monoFunctionClass.defaultType
            kind = IrParameterKind.DispatchReceiver
        }
        if (oldDispatchReceiver == null) {
            // If we didn't originally have a dispatch receiver, we can just slip in our newly created one at index 0
            parameterAccessor.dispatchReceiverParameter = dispatchReceiverParam
        }
        else {
            val remappedDispatchReceiverParam = buildReceiverParameter {
                startOffset = SYNTHETIC_OFFSET
                endOffset = SYNTHETIC_OFFSET
                name = Name.special("<reloc-this>")
                type = oldDispatchReceiver.type
                kind = IrParameterKind.Context
            }
            parameterAccessor.contextParameters =
                listOf(remappedDispatchReceiverParam) + parameterAccessor.contextParameters
            // Remap old dispatch receiver accesses to new context parameter
            replaceValueAccesses(mapOf(oldDispatchReceiver.symbol to remappedDispatchReceiverParam.symbol))
        }
        // Remap old extension receiver if present
        val oldExtensionReceiver = parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        if (oldExtensionReceiver != null) {
            val newExtensionReceiver = parameters.single { it.kind == IrParameterKind.ExtensionReceiver }
            replaceValueAccesses(mapOf(oldExtensionReceiver.symbol to newExtensionReceiver.symbol))
        }
    }

    private fun IrFunction.copyBody(): IrBody? {
        return when (val originalBody = body) {
            is IrExpressionBody -> context.irFactory.createExpressionBody(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                expression = originalBody.expression.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges()
            )

            is IrBlockBody -> context.irFactory.createBlockBody( // @formatter:off
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET
            ).apply { // @formatter:on
                statements += originalBody.statements.map {
                    it.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges()
                }
            }

            else -> null
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun monomorphize( // @formatter:off
        originalFunction: IrFunction,
        substitutions: LinkedHashMap<IrTypeParameterSymbol, IrType>
    ): IrSimpleFunction { // @formatter:on
        val signature = MonoFunctionSignature(originalFunction.symbol, substitutions)
        return context.kwireModuleData.monomorphizedFunctions.getOrPut(signature) {
            val monoFunctionClass = context.kwireModuleData.monoFunctionClass
            val function = context.irFactory.buildFun {
                updateFrom(originalFunction) // Copy base properties like visibility and modality
                startOffset = SYNTHETIC_OFFSET
                endOffset = SYNTHETIC_OFFSET
                name = originalFunction.name // Copy original name as it is mangled after construction
                origin = declOrigin
                returnType = originalFunction.returnType
                visibility =
                    DescriptorVisibilities.PUBLIC // Monomorphized functions are relocated, so they're always public
                originalDeclaration = originalFunction
            }.apply functionScope@{
                // @formatter:off
                // Copy all annotations except @Template
                annotations = originalFunction.annotations
                    .filterNot { it.type.getClass()?.isClassWithFqName(KWireNames.Template.fqName) == true }
                    .map { it.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges() }
                body = originalFunction.copyBody()
                parameters = originalFunction.parameters.map { it.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges() }
                replaceValueAccesses(originalFunction.parameters.map { it.symbol }.zip(parameters.map { it.symbol }).toMap())
                patchDeclarationParents(monoFunctionClass)
                // @formatter:on
                // Patch all parameters to reference the newly deep-copied mono-function params
                replaceReturnTargets(originalFunction.symbol) // Patch all return targets
                replaceTypes(substitutions) // Substitute all used types recursively
                remapValueAccesses(originalFunction) // Remap all local references accordingly
                remapReceiverParameters(monoFunctionClass)
                // Mangle function in-place after it is constructed
                with(context.mangler) {
                    mangleNameInPlace(
                        substitutions.values.toList(), originalFunction.kotlinFqName
                    )
                }
                // Recursively transform all templates for this newly monomorphized function
                transform(TemplateTransformer(context), context)
            }
            // Register the function as visible and inject it
            monoFunctionClass.declarations += function
            function
        }
    }

    private fun <S : IrSymbol> IrMemberAccessExpression<S>.remapRegularArguments(
        sourceExpr: IrMemberAccessExpression<S>, function: IrFunction, monoFunction: IrFunction
    ) {
        // Copy and remap regular value arguments
        val originalParams = function.parameters.filter { it.kind == IrParameterKind.Regular }
        val monoParams = monoFunction.parameters.filter { it.kind == IrParameterKind.Regular }
        check(originalParams.size == monoParams.size) { "Parameter count mismatch while monomorphizing function" }
        for ((originalParam, monoParam) in originalParams.zip(monoParams)) {
            arguments[monoParam] = sourceExpr.arguments[originalParam]
        }
    }

    private fun <S : IrSymbol> IrMemberAccessExpression<S>.remapContextArguments(
        sourceExpr: IrMemberAccessExpression<S>,
        function: IrFunction,
        monoFunction: IrFunction,
        alreadyHasDispatchReceiver: Boolean
    ) {
        // Remap original context parameters
        val originalContextParams = function.parameters.filter { it.kind == IrParameterKind.Context }
        var monoContextParams = monoFunction.parameters.filter { it.kind == IrParameterKind.Context }
        // If we have a relocated dispatch receiver, drop the first parameter as it is synthetic
        if (alreadyHasDispatchReceiver) monoContextParams = monoContextParams.drop(1)
        check(originalContextParams.size == monoContextParams.size) { "Context parameter count mismatch while monomorphizing function" }
        for ((originalParam, monoParam) in originalContextParams.zip(monoContextParams)) {
            arguments[monoParam] = sourceExpr.arguments[originalParam]
        }
    }

    private fun <S : IrSymbol> IrMemberAccessExpression<S>.remapDispatchReceiverArgument(
        sourceExpr: IrMemberAccessExpression<S>,
        function: IrFunction,
        monoFunction: IrFunction,
        alreadyHasDispatchReceiver: Boolean
    ) {
        // If the original caller already had a dispatch receiver, add the value as the first context parameter
        if (alreadyHasDispatchReceiver) {
            val newDispatchReceiverParam = monoFunction.parameters.first { it.kind == IrParameterKind.Context }
            val oldDispatchReceiverParam = function.parameters.single { it.kind == IrParameterKind.DispatchReceiver }
            arguments[newDispatchReceiverParam] = sourceExpr.arguments[oldDispatchReceiverParam]
        }
    }

    private fun <S : IrSymbol> IrMemberAccessExpression<S>.remapExtensionReceiverArgument(
        sourceExpr: IrMemberAccessExpression<S>,
        function: IrFunction,
        monoFunction: IrFunction,
        hasExtensionReceiver: Boolean
    ) {
        // If the original caller had an extension receiver, populate it
        if (hasExtensionReceiver) {
            val oldExtensionReceiverParam = function.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }
            val newExtensionReceiverParam =
                monoFunction.parameters.single { it.kind == IrParameterKind.ExtensionReceiver }
            arguments[newExtensionReceiverParam] = sourceExpr.arguments[oldExtensionReceiverParam]
        }
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    @Suppress("UNCHECKED_CAST")
    inline fun <E : IrMemberAccessExpression<S>, S : IrFunctionSymbol> monomorphize( // @formatter:off
        expression: E,
        factory: (
            monoFunction: IrSimpleFunction,
            valueArgumentsCount: Int,
            contextParameterCount: Int,
            hasExtensionReceiver: Boolean
        ) -> E
    ): E { // @formatter:on
        val typeArguments = expression.typeArguments
        if (typeArguments.any { it == null }) return expression
        typeArguments as List<IrType>
        val function = expression.symbol.owner

        val parameterAccessor = function.parameterAccessor
        val hasDispatchReceiver = parameterAccessor.dispatchReceiverParameter != null
        val hasExtensionReceiver = parameterAccessor.extensionReceiverParameter != null
        var valueArgumentsCount = parameterAccessor.regularParameters.size
        var contextParameterCount = parameterAccessor.contextParameters.size

        if (hasDispatchReceiver) {
            // We add our own context parameter to pass the original dispatch receiver
            valueArgumentsCount++
            contextParameterCount++
        }

        val substitutions = function.typeParameters.map { it.symbol }.zip(typeArguments).toMap(LinkedHashMap())
        val monoFunction = monomorphize(function, substitutions)

        return factory(monoFunction, valueArgumentsCount, contextParameterCount, hasExtensionReceiver).apply {
            origin = expression.origin // Keep the original origin
            dispatchReceiver = context.kwireModuleData.monoFunctionClass.symbol.getObjectInstance()
            remapRegularArguments(expression, function, monoFunction)
            remapContextArguments(expression, function, monoFunction, hasDispatchReceiver)
            remapDispatchReceiverArgument(expression, function, monoFunction, hasDispatchReceiver)
            remapExtensionReceiverArgument(expression, function, monoFunction, hasExtensionReceiver)
        }
    }
}