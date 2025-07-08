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
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.getIntrinsicType
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrScript
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.target
import org.jetbrains.kotlin.ir.visitors.IrTransformer

internal abstract class IntrinsicTransformer( // @formatter:off
    protected val context: KWirePluginContext,
    private val types: Set<KWireIntrinsicType>
) : IrTransformer<IntrinsicContext>(), MessageCollectorExtensions by context { // @formatter:on
    abstract fun visitIntrinsic( // @formatter:off
        expression: IrCall,
        data: IntrinsicContext,
        type: KWireIntrinsicType
    ): IrElement // @formatter:on

    override fun visitScript(declaration: IrScript, data: IntrinsicContext): IrStatement {
        data.pushScript(declaration)
        val transformedScript = super.visitScript(declaration, data)
        data.popScript()
        return transformedScript
    }

    override fun visitFile(declaration: IrFile, data: IntrinsicContext): IrFile {
        data.pushFile(declaration)
        val transformedFile = super.visitFile(declaration, data)
        data.popFile()
        return transformedFile
    }

    override fun visitClass(declaration: IrClass, data: IntrinsicContext): IrStatement {
        data.pushClass(declaration)
        val transformedClass = super.visitClass(declaration, data)
        data.popClass()
        return transformedClass
    }

    override fun visitFunction(declaration: IrFunction, data: IntrinsicContext): IrStatement {
        data.pushFunction(declaration)
        val transformedFunction = super.visitFunction(declaration, data)
        data.popFunction()
        return transformedFunction
    }

    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: IntrinsicContext): IrStatement {
        data.pushAnonInitializer(declaration)
        val transformedInitializer = super.visitAnonymousInitializer(declaration, data)
        data.popAnonInitializer()
        return transformedInitializer
    }

    override fun visitCall(expression: IrCall, data: IntrinsicContext): IrElement {
        // Transform using depth-first search strategy
        val transformedCall = super.visitCall(expression, data)
        if (transformedCall is IrCall) {
            val function = transformedCall.target
            val type = function.getIntrinsicType() ?: return transformedCall
            if (type !in types) return transformedCall
            return visitIntrinsic(transformedCall, data, type)
        }
        return transformedCall
    }
}