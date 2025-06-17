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
import dev.karmakrafts.kwire.compiler.util.ReferenceMemoryLayout
import org.jetbrains.kotlin.backend.common.getCompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

internal class StructValidationVisitor(
    private val context: KWirePluginContext
) : IrVisitorVoid() {
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    private fun isValidStructFieldType(type: IrType): Boolean {
        // At the moment, we accept any field type except kotlin reference objects
        return context.computeMemoryLayout(type) != ReferenceMemoryLayout
    }

    override fun visitProperty(declaration: IrProperty) {
        super.visitProperty(declaration)
        val type = declaration.getter?.returnType ?: declaration.backingField?.type
        if (type == null || isValidStructFieldType(type)) return
        context.messageCollector.report(
            severity = CompilerMessageSeverity.ERROR,
            message = "Unsupported structure field type ${type.render()}",
            location = declaration.getCompilerMessageLocation(context.irFile)
        )
    }
}