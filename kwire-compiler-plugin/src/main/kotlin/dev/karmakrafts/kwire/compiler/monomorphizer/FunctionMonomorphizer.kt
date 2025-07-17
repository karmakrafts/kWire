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
import dev.karmakrafts.kwire.compiler.util.remapSyntheticSourceRanges
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
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
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.deepCopyWithoutPatchingParents
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.ir.util.target
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

    private fun IrFunction.remapReceivers(oldFunction: IrFunction, monoFunctionClass: IrClass) {
        val oldDispatchReceiver = oldFunction.parameters.firstOrNull { it.kind == IrParameterKind.DispatchReceiver }
        val dispatchReceiverParam = buildReceiverParameter {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            name = Name.special("<this>")
            type = monoFunctionClass.defaultType
            kind = IrParameterKind.DispatchReceiver
        }
        if (oldDispatchReceiver == null) {
            // If we didn't originally have a dispatch receiver, we can just slip in our newly created one at index 0
            parameters = listOf(dispatchReceiverParam) + parameters
        }
        else {
            val remappedDispatchReceiverParam = buildReceiverParameter {
                startOffset = SYNTHETIC_OFFSET
                endOffset = SYNTHETIC_OFFSET
                name = Name.special("<reloc-this>")
                type = oldDispatchReceiver.type
                kind = IrParameterKind.Context
            }
            // If we originally had a dispatch receiver, we need to turn it into a context parameter
            // DISPATCH_RECEIVER (this), CONTEXT (reloc-this), original params - DISPATCH_RECEIVER
            parameters = listOf(
                dispatchReceiverParam, remappedDispatchReceiverParam
            ) + parameters.filterNot { it.kind == IrParameterKind.DispatchReceiver }
            // Remap old dispatch receiver accesses to new context parameter
            replaceValueAccesses(mapOf(oldDispatchReceiver.symbol to remappedDispatchReceiverParam.symbol))
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
        function: IrFunction,
        substitutions: LinkedHashMap<IrTypeParameterSymbol, IrType>
    ): IrSimpleFunction { // @formatter:on
        val signature = MonoFunctionSignature(function.symbol, substitutions)
        return context.kwireModuleData.monomorphizedFunctions.getOrPut(signature) {
            val monoFunctionClass = context.kwireModuleData.monoFunctionClass
            val function = context.irFactory.buildFun {
                updateFrom(function) // Copy base properties like visibility and modality
                startOffset = SYNTHETIC_OFFSET
                endOffset = SYNTHETIC_OFFSET
                name = function.name // Copy original name as it is mangled after construction
                origin = declOrigin
                returnType = function.returnType
                visibility =
                    DescriptorVisibilities.PUBLIC // Monomorphized functions are relocated, so they're always public
            }.apply functionScope@{
                // Only copy value parameters, as type parameters are eliminated
                // @formatter:off
                // Copy all annotations except @Template
                annotations = function.annotations
                    .filterNot { it.type.getClass()?.isClassWithFqName(KWireNames.Template.fqName) == true }
                    .map { it.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges() }
                body = function.copyBody()
                parameters = function.parameters.map { it.deepCopyWithoutPatchingParents().remapSyntheticSourceRanges() }
                patchDeclarationParents(monoFunctionClass)
                // @formatter:on
                // Patch all parameters to reference the newly deep-copied mono-function params
                replaceValueAccesses(function.parameters.map { it.symbol }.zip(parameters.map { it.symbol }).toMap())
                replaceReturnTargets(function.symbol) // Patch all return targets
                replaceTypes(substitutions) // Substitute all used types recursively
                remapReceivers(function, monoFunctionClass)
                remapValueAccesses(function) // Remap all local references accordingly
                // Mangle function in-place after it is constructed
                with(context.mangler) { mangleNameInPlace(substitutions.values.toList()) }
                // Recursively transform all templates for this newly monomorphized function
                transform(TemplateTransformer(context), context)
            }
            // Register the function as visible and inject it
            context.metadataDeclarationRegistrar.registerFunctionAsMetadataVisible(function)
            monoFunctionClass.declarations += function
            function
        }
    }

    @OptIn(DeprecatedForRemovalCompilerApi::class)
    @Suppress("UNCHECKED_CAST")
    fun monomorphize(call: IrCall): IrCall {
        val typeArguments = call.typeArguments
        if (typeArguments.any { it == null }) return call
        typeArguments as List<IrType>
        val function = call.target

        val substitutions = function.typeParameters.map { it.symbol }.zip(typeArguments).toMap(LinkedHashMap())
        val monoFunction = monomorphize(function, substitutions)

        val regularParams = function.parameters.filter { it.kind == IrParameterKind.Regular }
        var valueArgumentsCount = regularParams.size
        var contextParameterCount = function.parameters.count { it.kind == IrParameterKind.Context }
        val hasExtensionReceiver = function.parameters.any { it.kind == IrParameterKind.ExtensionReceiver }

        val alreadyHasDispatchReceiver = function.dispatchReceiverParameter != null

        if (alreadyHasDispatchReceiver) {
            // We add our own context parameter to pass the original dispatch receiver
            valueArgumentsCount++
            contextParameterCount++
        }

        return IrCallImplWithShape(
            startOffset = SYNTHETIC_OFFSET,
            endOffset = SYNTHETIC_OFFSET,
            type = monoFunction.returnType,
            symbol = monoFunction.symbol,
            typeArgumentsCount = 0,
            valueArgumentsCount = valueArgumentsCount,
            contextParameterCount = contextParameterCount,
            hasDispatchReceiver = true, // This is always true due to relocation
            hasExtensionReceiver = hasExtensionReceiver
        ).apply {
            dispatchReceiver = context.kwireModuleData.monoFunctionClass.symbol.getObjectInstance()
            // Copy and remap regular value arguments
            for (parameter in function.parameters) {
                if (parameter.kind == IrParameterKind.DispatchReceiver) continue
                val newParameter = monoFunction.parameters.find { it.name == parameter.name } ?: continue
                arguments[newParameter] = call.arguments[parameter]
            }
            // If the original caller already had a dispatch receiver, add the value as the first context parameter
            if (alreadyHasDispatchReceiver) {
                val contextParameter = monoFunction.parameters.first { it.kind == IrParameterKind.Context }
                val oldDispatchReceiverParam =
                    function.parameters.single { it.kind == IrParameterKind.DispatchReceiver }
                arguments[contextParameter] = call.arguments[oldDispatchReceiverParam]
            }
        }
    }
}