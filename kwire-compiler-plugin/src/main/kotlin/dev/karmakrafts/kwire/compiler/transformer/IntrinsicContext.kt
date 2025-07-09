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
import dev.karmakrafts.kwire.compiler.memory.scope.AbstractAllocationScope
import dev.karmakrafts.kwire.compiler.memory.scope.BlockAllocationScope
import dev.karmakrafts.kwire.compiler.memory.scope.FunctionAllocationScope
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrScript
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import java.util.*

internal class IntrinsicContext(  // @formatter:off
    val context: KWirePluginContext
) { // @formatter:on
    private val allocationScopeStack: Stack<AbstractAllocationScope> = Stack()
    inline val allocationScope: AbstractAllocationScope get() = allocationScopeStack.peek()

    private val parentStack: Stack<IrDeclarationParent> = Stack()
    inline val parent: IrDeclarationParent get() = parentStack.peek()

    fun findLocalReference(variable: IrValueDeclaration): IrVariable? {
        for (scope in allocationScopeStack.reversed()) {
            val ref = scope.getLocalReference(variable) ?: continue
            return ref
        }
        return null
    }

    fun pushScript(script: IrScript) {
        parentStack.push(script)
    }

    fun popScript() {
        parentStack.pop()
    }

    fun pushFile(file: IrFile) {
        parentStack.push(file)
    }

    fun popFile() {
        parentStack.pop()
    }

    fun pushClass(clazz: IrClass) {
        parentStack.push(clazz)
    }

    fun popClass() {
        parentStack.pop()
    }

    fun pushAnonInitializer() {
        allocationScopeStack.push(FunctionAllocationScope(context, parent))
    }

    fun popAnonInitializer(initializer: IrElement) {
        allocationScope.injectIfNeeded(initializer)
        allocationScopeStack.pop()
    }

    fun pushFunction(function: IrFunction) {
        allocationScopeStack.push(FunctionAllocationScope(context, parent))
        parentStack.push(function)
    }

    fun popFunction(function: IrElement) {
        allocationScope.injectIfNeeded(function)
        allocationScopeStack.pop()
        parentStack.pop()
    }

    fun pushBlock() {
        allocationScopeStack.push(BlockAllocationScope(allocationScope))
    }

    fun popBlock(block: IrElement) {
        allocationScope.injectIfNeeded(block)
        allocationScopeStack.pop()
    }
}