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
import dev.karmakrafts.kwire.compiler.ffi.CallingConvention
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getAnnotationValue
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.isSharedImport
import dev.karmakrafts.kwire.compiler.util.isTemplate
import dev.karmakrafts.kwire.compiler.util.load
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import java.util.Stack
import kotlin.uuid.ExperimentalUuidApi

internal class SharedImportTransformer(
    private val context: KWirePluginContext
) : IrVisitorVoid(), MessageCollectorExtensions by context {
    private val parentStack: Stack<IrDeclarationParent> = Stack()
    inline val parentOrNull: IrDeclarationParent? get() = parentStack.lastOrNull()

    val isInsideTemplate: Boolean
        get() = (parentOrNull as? IrAnnotationContainer)?.isTemplate() ?: false

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitFile(declaration: IrFile) {
        parentStack.push(declaration)
        super.visitFile(declaration)
        parentStack.pop()
    }

    override fun visitClass(declaration: IrClass) {
        parentStack.push(declaration)
        super.visitClass(declaration)
        parentStack.pop()
    }

    private fun getFunctionAddress(libraryNames: List<String>, functionName: String): IrCall {
        return context.kwireSymbols.sharedLibraryOpenAndGetFunction.call( // @formatter:off
            dispatchReceiver = context.kwireSymbols.sharedLibraryCompanionType.getObjectInstance(),
            valueArguments = mapOf(
                "libraryNames" to context.createListOf(
                    type = context.irBuiltIns.stringType,
                    values = libraryNames.map { it.toIrConst(context.irBuiltIns.stringType) }
                ),
                "functionName" to functionName.toIrConst(context.irBuiltIns.stringType)
            )
        ) // @formatter:on
    }

    private fun createTrampolineBody( // @formatter:off
        libraryNames: List<String>,
        functionName: String,
        callingConvention: CallingConvention,
        function: IrFunction
    ): IrBlockBody { // @formatter:on
        val returnType = function.returnType
        check(!returnType.isTypeParameter()) { "Trampoline function return type must be concrete" }

        val parameterTypes = function.parameters.filter { it.kind == IrParameterKind.Regular }.map { it.type }
        check(parameterTypes.none { it.isTypeParameter() }) { "Trampoline function parameter types must be concrete" }

        val address = getFunctionAddress(libraryNames, functionName)
        val descriptor = context.ffi.getDescriptor(returnType, parameterTypes)

        return context.irFactory.createBlockBody(
            startOffset = SYNTHETIC_OFFSET, endOffset = SYNTHETIC_OFFSET
        ).apply {
            val (_, bufferStatements, bufferVariable) = context.ffi.extractArgumentsIntoBuffer(function)
            if (bufferVariable == null) {
                reportError("Could not build argument buffer for @SharedImport function", function)
                return@apply
            }

            bufferVariable.parent = function
            statements += bufferVariable
            statements += bufferStatements

            fun createTryFinally(): IrTryImpl = IrTryImpl(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                type = returnType,
                tryResult = context.ffi.call(
                    type = returnType,
                    address = address,
                    descriptor = descriptor,
                    argBuffer = bufferVariable.load(),
                    callingConvention = callingConvention
                ),
                catches = emptyList(),
                finallyExpression = context.ffi.releaseArgBuffer(bufferVariable.load())
            )

            // For cases without a result, we can omit the IrReturn element
            if (returnType.isUnit()) {
                statements += createTryFinally()
                return@apply
            }
            // Otherwise we explicitly return the try-finally expression
            statements += IrReturnImpl(
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                type = returnType,
                returnTargetSymbol = function.symbol,
                value = createTryFinally()
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun visitFunction(declaration: IrFunction) {
        parentStack.push(declaration)
        super.visitFunction(declaration)
        // Check before we pop if we are inside a template function
        if (!declaration.isSharedImport() || isInsideTemplate) return
        parentStack.pop()

        if (!declaration.isExternal) {
            reportError("Function marked with @SharedImport must be external", declaration)
            return
        }
        val libraryNames = declaration.getAnnotationValue<List<String>>(KWireNames.SharedImport.fqName, "libraryNames")
        if (libraryNames == null || libraryNames.isEmpty()) {
            reportError("@SharedImport requires at least one library name to be specified", declaration)
            return
        }
        val functionName = declaration.getAnnotationValue<String>(KWireNames.SharedImport.fqName, "name")
        if (functionName == null) {
            reportError("@SharedImport requires function name to be specified", declaration)
            return
        }
        var callingConvention =
            declaration.getAnnotationValue<CallingConvention>(KWireNames.SharedImport.fqName, "callingConvention")
        if (callingConvention == null) {
            // We always default to CDECL, see runtime definition of @SharedImport
            callingConvention = CallingConvention.CDECL
        }

        // Remove external modifier and add an empty body as nop fallback
        declaration.isExternal = false
        declaration.body = createTrampolineBody(
            libraryNames = libraryNames,
            functionName = functionName,
            callingConvention = callingConvention,
            function = declaration
        )
    }
}