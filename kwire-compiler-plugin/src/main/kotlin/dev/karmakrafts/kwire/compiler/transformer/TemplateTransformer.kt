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

package dev.karmakrafts.kwire.compiler.transformer

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.isTemplate
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImplWithShape
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import java.util.*

internal class TemplateTransformer(
    private val context: KWirePluginContext
) : IrTransformer<KWirePluginContext>(), MessageCollectorExtensions by context {
    private var parentStack: Stack<IrDeclarationParent> = Stack()
    inline val parentOrNull: IrDeclarationParent? get() = parentStack.lastOrNull()

    inline val isInsideTemplate: Boolean
        get() = (parentOrNull as? IrAnnotationContainer)?.isTemplate() ?: false

    override fun visitFile(declaration: IrFile, data: KWirePluginContext): IrFile {
        parentStack.push(declaration)
        val transformedFile = super.visitFile(declaration, data)
        parentStack.pop()
        return transformedFile
    }

    override fun visitClass(declaration: IrClass, data: KWirePluginContext): IrStatement {
        if (declaration.classId == context.kwireModuleData.monoFunctionClassId) return declaration
        parentStack.push(declaration)
        val transformedClass = super.visitClass(declaration, data)
        parentStack.pop()
        return transformedClass
    }

    override fun visitFunction(declaration: IrFunction, data: KWirePluginContext): IrStatement {
        parentStack.push(declaration)
        val transformedFunction = super.visitFunction(declaration, data)
        parentStack.pop()
        return transformedFunction
    }

    // Perform DFS transformation of all calls
    override fun visitCall(expression: IrCall, data: KWirePluginContext): IrElement {
        val transformedCall = super.visitCall(expression, data)
        if (transformedCall is IrCall && transformedCall.target.isTemplate() && !isInsideTemplate) {
            return context.functionMonomorphizer.monomorphize(transformedCall) { fn, vaCount, cpCount, hasExtRec ->
                IrCallImplWithShape(
                    startOffset = SYNTHETIC_OFFSET,
                    endOffset = SYNTHETIC_OFFSET,
                    type = fn.returnType,
                    symbol = fn.symbol,
                    typeArgumentsCount = 0,
                    valueArgumentsCount = vaCount,
                    contextParameterCount = cpCount,
                    hasDispatchReceiver = true,
                    hasExtensionReceiver = hasExtRec
                )
            }
        }
        return transformedCall
    }

    // Perform DFS transformation of all function refs
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunctionReference(expression: IrFunctionReference, data: KWirePluginContext): IrElement {
        val transformedRef = super.visitFunctionReference(expression, data)
        if (transformedRef is IrFunctionReference && transformedRef.symbol.owner.isTemplate() && !isInsideTemplate) {
            reportError("Taking references to template functions is currently not supported", expression)
        }
        return transformedRef
    }
}