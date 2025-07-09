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
import dev.karmakrafts.kwire.compiler.memory.layout.ReferenceMemoryLayout
import dev.karmakrafts.kwire.compiler.memory.layout.computeMemoryLayout
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.isFunPtr
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.render

internal class FunPtrChecker(
    context: KWirePluginContext
) : TypeUsageChecker(context) {
    override fun checkType(declaration: IrDeclaration, type: IrType) {
        if (!type.isFunPtr()) return
        val pointedType = type.getPointedType()
        if (pointedType == null || pointedType !is IrSimpleType) {
            reportError("Could not determine pointed type of FunPtr", declaration)
            return
        }
        val arguments = pointedType.arguments.dropLast(1)
        for (argument in arguments) {
            val type = argument.typeOrFail
            val layout = type.computeMemoryLayout(context)
            if (layout !is ReferenceMemoryLayout) continue
            reportError("Incompatible argument type ${type.render()} for FunPtr signature", declaration)
        }
        val returnType = pointedType.arguments.last().typeOrFail
        val layout = returnType.computeMemoryLayout(context)
        if (layout !is ReferenceMemoryLayout) return
        reportError("Incompatible return type ${returnType.render()} for FunPtr signature", declaration)
    }
}