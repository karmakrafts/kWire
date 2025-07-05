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
import dev.karmakrafts.kwire.compiler.util.getNativeType
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.isNumPtr
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getPrimitiveType
import org.jetbrains.kotlin.ir.types.getUnsignedType
import org.jetbrains.kotlin.ir.util.render

internal class NumPtrChecker(
    context: KWirePluginContext
) : TypeUsageChecker(context) {
    override fun checkType(declaration: IrDeclaration, type: IrType) {
        if (!type.isNumPtr()) return
        val pointedType = type.getPointedType()
        if (pointedType == null) {
            reportError("Could not determine pointed type of NumPtr", declaration)
            return
        }
        when {
            pointedType.getPrimitiveType() != null -> return
            pointedType.getUnsignedType() != null -> return
            pointedType.getNativeType() != null -> return
        }
        reportError("Incompatible type ${pointedType.render()} for NumPtr", declaration)
    }
}