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
import dev.karmakrafts.kwire.compiler.util.KWireIntrinsicType
import dev.karmakrafts.kwire.compiler.util.getIntrinsicType
import dev.karmakrafts.kwire.compiler.util.getPointedType
import dev.karmakrafts.kwire.compiler.util.isCFn
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.target

/**
 * Checks for illegal operations on `Ptr<CFn<*>>`.
 */
internal class PtrCFnChecker(
    context: KWirePluginContext
) : AbstractChecker(context) {
    override fun visitCall(expression: IrCall) {
        super.visitCall(expression)

        val function = expression.target
        val intrinsicType = function.getIntrinsicType() ?: return

        val pointerType = expression.dispatchReceiver?.type ?: return
        val pointedType = pointerType.getPointedType() ?: return
        if (!pointedType.isCFn()) return

        when (intrinsicType) {
            KWireIntrinsicType.PTR_DEREF -> reportError("Cannot dereference pointer to C function", expression)
            KWireIntrinsicType.PTR_SET -> reportError("Cannot write to location of C function", expression)
            else -> {}
        }
    }
}