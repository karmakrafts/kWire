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

package dev.karmakrafts.kwire.compiler.checker

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import dev.karmakrafts.kwire.compiler.memory.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.util.MessageCollectorExtensions
import dev.karmakrafts.kwire.compiler.util.isStruct
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.defaultConstructor
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

internal class StructValidationVisitor(
    private val context: KWirePluginContext
) : IrVisitorVoid(), MessageCollectorExtensions by context {
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    private fun isValidStructFieldType(type: IrType): Boolean {
        // At the moment, we accept any field type except kotlin reference objects
        return context.getOrComputeMemoryLayout(type) != ReferenceMemoryLayout
    }

    // Enforce compatible field types and visibility
    override fun visitProperty(declaration: IrProperty) {
        super.visitProperty(declaration)
        val parentClass = declaration.parentClassOrNull ?: return
        if (!parentClass.isStruct(context)) return
        val type = (declaration.getter?.returnType ?: declaration.backingField?.type) ?: return
        if (!isValidStructFieldType(type)) {
            reportError("Unsupported struct field type ${type.render()}", declaration)
        }
        val visibility = declaration.visibility
        if (visibility != DescriptorVisibilities.PUBLIC && visibility != DescriptorVisibilities.INTERNAL) {
            reportError("Unsupported struct field visibility $visibility, must be public or internal", declaration)
        }
    }

    // Enforce default constructor and visibility
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitClass(declaration: IrClass) {
        super.visitClass(declaration)
        if (!declaration.isStruct(context)) return
        val constructor = declaration.defaultConstructor
        if (constructor == null) {
            reportError("Struct requires default constructor", declaration)
            return
        }
        val visibility = constructor.visibility
        if (visibility != DescriptorVisibilities.PUBLIC && visibility != DescriptorVisibilities.INTERNAL) {
            reportError(
                "Unsupported struct constructor visibility $visibility, must be public or internal", declaration
            )
        }
    }
}