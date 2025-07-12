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
import dev.karmakrafts.kwire.compiler.util.KWireNames
import dev.karmakrafts.kwire.compiler.util.isValueType
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.render

internal class ValueTypeChecker( // @formatter:off
    context: KWirePluginContext,
) : TypeUsageChecker(context) { // @formatter:on
    override fun checkType(declaration: IrDeclaration, type: IrType) {
        if (type !is IrSimpleType) return
        val clazz = type.getClass() ?: return
        val parameters = clazz.typeParameters
        for (parameterIndex in parameters.indices) {
            val parameter = parameters[parameterIndex]
            if (!parameter.hasAnnotation(KWireNames.ValueType.id)) continue
            val argument = type.arguments[parameterIndex]
            val argumentType = argument.typeOrNull ?: continue
            if (argumentType.isValueType(context)) continue
            reportError(
                "Type parameter ${parameter.name} of ${type.classFqName} expected value type but got ${argumentType.render()}",
                declaration
            )
        }
    }
}