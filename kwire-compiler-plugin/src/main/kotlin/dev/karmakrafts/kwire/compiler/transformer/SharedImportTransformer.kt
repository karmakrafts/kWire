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
import dev.karmakrafts.kwire.compiler.util.getAnnotationValue
import dev.karmakrafts.kwire.compiler.util.isSharedImport
import dev.karmakrafts.kwire.compiler.util.load
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.visitors.IrVisitor

internal class SharedImportTransformer(
    private val context: KWirePluginContext
) : IrVisitor<Unit, SharedImportContext>(), MessageCollectorExtensions by context {
    override fun visitElement(element: IrElement, data: SharedImportContext) {
        element.acceptChildren(this, data)
    }

    override fun visitFile(declaration: IrFile, data: SharedImportContext) {
        data.pushScope(declaration)
        super.visitFile(declaration, data)
        data.popScope()
    }

    override fun visitClass(declaration: IrClass, data: SharedImportContext) {
        data.pushScope(declaration)
        super.visitClass(declaration, data)
        data.popScope()
    }

    override fun visitFunction(declaration: IrFunction, data: SharedImportContext) {
        super.visitFunction(declaration, data)

        if (!declaration.isSharedImport()) return
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

        val dispatchReceiver = declaration.dispatchReceiverParameter?.symbol?.load()
        data.scope.getFunction(libraryNames, functionName, dispatchReceiver)

        // Remove external modifier and add an empty body as nop fallback
        declaration.isExternal = false
        declaration.body = context.irFactory.createBlockBody(
            startOffset = SYNTHETIC_OFFSET, endOffset = SYNTHETIC_OFFSET
        )
    }
}