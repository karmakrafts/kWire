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
import dev.karmakrafts.kwire.compiler.util.isTemplate
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName

internal class FunctionNameChecker(context: KWirePluginContext) : AbstractChecker<Nothing?>(context) {
    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        super.visitFunction(declaration, data)
        val name = declaration.kotlinFqName.asString()
        if (!declaration.isTemplate() || "$$" !in name) return
        reportError("$$ is prohibited in the name of template function $name")
    }
}