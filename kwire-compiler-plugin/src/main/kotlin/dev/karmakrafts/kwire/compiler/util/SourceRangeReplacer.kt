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

package dev.karmakrafts.kwire.compiler.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid

internal class SourceRangeReplacer(
    private val startGetter: (IrElement) -> Int, private val endGetter: (IrElement) -> Int
) : IrVisitorVoid() {
    override fun visitElement(element: IrElement) {
        element.startOffset = startGetter(element)
        element.endOffset = endGetter(element)
        element.acceptChildrenVoid(this)
    }
}

internal fun <E : IrElement> E.remapSourceRanges(
    startMapper: (IrElement) -> Int,
    endMapper: (IrElement) -> Int
): E {
    acceptVoid(SourceRangeReplacer(startMapper, endMapper))
    return this
}

internal fun <E : IrElement> E.remapSyntheticSourceRanges(): E {
    return remapSourceRanges({ SYNTHETIC_OFFSET }) { SYNTHETIC_OFFSET }
}