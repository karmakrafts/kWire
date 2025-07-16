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
import dev.karmakrafts.kwire.compiler.util.findContainingParent
import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
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
import org.jetbrains.kotlin.ir.util.copyValueArgumentsFrom
import org.jetbrains.kotlin.ir.util.deepCopyWithoutPatchingParents
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.ir.util.target

private data class MonoFunctionSignature( // @formatter:off
    val symbol: IrFunctionSymbol,
    val substitutions: LinkedHashMap<IrTypeParameterSymbol, IrType>
) // @formatter:on

internal class FunctionMonomorphizer(
    private val context: KWirePluginContext
) {
    companion object : GeneratedDeclarationKey() {
        val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
    }

    private val functions: HashMap<MonoFunctionSignature, IrSimpleFunction> = HashMap()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun injectIntoParent(originalFunction: IrFunction, monoFunction: IrSimpleFunction) {
        val container = originalFunction.findContainingParent() ?: return
        container.declarations += monoFunction
    }

    fun monomorphize(
        function: IrFunction, substitutions: LinkedHashMap<IrTypeParameterSymbol, IrType>
    ): IrSimpleFunction {
        return functions.getOrPut(MonoFunctionSignature(function.symbol, substitutions)) {
            context.irFactory.buildFun {
                updateFrom(function) // Copy base properties like visibility and modality
                name = function.name // Copy original name as it is mangled after construction
                origin = declOrigin
                returnType = function.returnType
            }.apply functionScope@{
                with(context.mangler) { mangleName(substitutions.values.toList()) }
                // Only copy value parameters, as type parameters are eliminated
                parameters = function.parameters.map { it.deepCopyWithoutPatchingParents() }
                // Copy all annotations except @Template
                annotations = function.annotations.filterNot {
                    it.type.getClass()?.isClassWithFqName(KWireNames.Template.fqName) == true
                }.map { it.deepCopyWithoutPatchingParents() }
                body = when (val originalBody = function.body) {
                    is IrExpressionBody -> context.irFactory.createExpressionBody(
                        startOffset = SYNTHETIC_OFFSET,
                        endOffset = SYNTHETIC_OFFSET,
                        expression = originalBody.expression.deepCopyWithoutPatchingParents()
                    )

                    is IrBlockBody -> context.irFactory.createBlockBody( // @formatter:off
                        startOffset = SYNTHETIC_OFFSET,
                        endOffset = SYNTHETIC_OFFSET
                    ).apply { // @formatter:on
                        statements += originalBody.statements.map { it.deepCopyWithoutPatchingParents() }
                    }

                    else -> null
                }
                // Patch all parameters to reference the newly deep-copied mono-function params
                replaceParameterRefs(function.parameters.map { it.symbol }.zip(parameters.map { it.symbol }).toMap())
                replaceReturnTargets(function.symbol) // Patch all return targets
                replaceTypes(substitutions) // Substitute all used types recursively
                patchDeclarationParents(function.parent)
                // Recursively transform all templates for this newly monomorphized function
                transform(TemplateTransformer(context), context)
            }
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
        injectIntoParent(function, monoFunction)
        val extensionReceiverParam = function.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        val hasExtensionReceiver = extensionReceiverParam != null
        val valueArgumentsCount = function.parameters.count { it.kind == IrParameterKind.Regular }
        // Create an entirely new call from the original one so we don't mutate the original
        return IrCallImplWithShape(
            startOffset = call.startOffset,
            endOffset = call.endOffset,
            type = call.type,
            symbol = monoFunction.symbol,
            typeArgumentsCount = 0,
            valueArgumentsCount = valueArgumentsCount,
            contextParameterCount = 0,
            hasDispatchReceiver = call.dispatchReceiver != null,
            hasExtensionReceiver = hasExtensionReceiver
        ).apply {
            copyValueArgumentsFrom(call, monoFunction)
        }
    }
}