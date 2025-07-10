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

package dev.karmakrafts.kwire.compiler.optimizer

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.getIntrinsicType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.ir.visitors.IrTransformer

internal class PtrOptimizer(
    context: KWirePluginContext
) : IrTransformer<KWirePluginContext>(), MessageCollectorExtensions by context {
    private fun isRef(call: IrCall): Boolean = when (call.target.getIntrinsicType()) {
        KWireIntrinsicType.PTR_REF -> true
        else -> false
    }

    private fun isDeref(call: IrCall): Boolean = when (call.target.getIntrinsicType()) {
        KWireIntrinsicType.PTR_DEREF -> true
        else -> false
    }

    override fun visitCall(expression: IrCall, data: KWirePluginContext): IrElement {
        val transformedCall = super.visitCall(expression, data)
        if (transformedCall !is IrCall) return transformedCall
        return when {
            // x.ref().deref().ref() -> x.ref()
            isRef(transformedCall) -> {
                val refFunction = transformedCall.target
                // Unwrap reference to dereference
                val refReceiverParam =
                    refFunction.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
                        ?: return transformedCall
                val refReceiver = transformedCall.arguments[refReceiverParam] ?: return transformedCall
                if (refReceiver !is IrCall || !isDeref(refReceiver)) return transformedCall
                // Unwrap dereference to address receiver
                val derefReceiver = refReceiver.dispatchReceiver
                if (derefReceiver == null) {
                    reportWarn("Could not optimize double-reference", transformedCall)
                    transformedCall
                }
                else derefReceiver
            }

            else -> transformedCall
        }
    }
}