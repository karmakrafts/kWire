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
import dev.karmakrafts.kwire.compiler.util.call
import dev.karmakrafts.kwire.compiler.util.getObjectInstance
import dev.karmakrafts.kwire.compiler.util.load
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.name.Name
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal data class SharedImportLibrary( // @formatter:off
    val id: Uuid,
    val field: IrField,
    val functions: HashMap<String, IrField> = HashMap()
) // @formatter:on

@OptIn(ExperimentalUuidApi::class)
internal data class SharedImportScope( // @formatter:off
    val context: KWirePluginContext,
    val parent: IrDeclarationContainer
) { // @formatter:on
    companion object : GeneratedDeclarationKey() {
        val declOrigin: IrDeclarationOrigin = IrDeclarationOrigin.GeneratedByPlugin(this)
    }

    // Stores library aliases -> scope-local variable which holds the SharedLibrary instance
    // TODO: Holds a lot of duplicated value refs, optimize this
    private val libraries: HashMap<String, SharedImportLibrary> = HashMap()

    fun getLibrary(names: List<String>): SharedImportLibrary {
        // If we already opened this library in the current scope, return the existing handle field
        for (name in names) {
            if (name !in libraries) continue
            return libraries[name]!!
        }
        // And initialize a new final private field in the current scope with the newly opened library
        val id = Uuid.random()
        val field = context.irFactory.buildField {
            startOffset = SYNTHETIC_OFFSET
            endOffset = SYNTHETIC_OFFSET
            type = context.kwireSymbols.sharedLibraryType.defaultType
            name = Name.identifier("__kwire_library_${id.toHexString()}__")
            isFinal = true
            isStatic = parent is IrFile
            visibility = DescriptorVisibilities.PRIVATE
            origin = declOrigin
        }.apply {
            parent = this@SharedImportScope.parent
            initializer = context.irFactory.createExpressionBody( // @formatter:off
                startOffset = SYNTHETIC_OFFSET,
                endOffset = SYNTHETIC_OFFSET,
                expression = context.kwireSymbols.sharedLibraryOpen.call(
                    dispatchReceiver = context.kwireSymbols.sharedLibraryCompanionType.getObjectInstance(),
                    valueArguments = mapOf(
                        "names" to context.createListOf(
                            type = context.irBuiltIns.stringType,
                            values = names.map { it.toIrConst(context.irBuiltIns.stringType) }
                        )
                    )
                )
            ) // @formatter:on
        }
        // Remember the newly created library for all the given aliases to speed up subsequent resolutions
        val library = SharedImportLibrary(id, field)
        for (name in names) libraries[name] = library
        return library
    }

    fun getFunction(
        libraryNames: List<String>,
        name: String,
        dispatchReceiver: IrExpression? = null
    ): IrField {
        val library = getLibrary(libraryNames)
        val functions = library.functions
        var addressField = functions[name]
        if (addressField == null) {
            addressField = context.irFactory.buildField {
                startOffset = SYNTHETIC_OFFSET
                endOffset = SYNTHETIC_OFFSET
                type = context.constVoidPtr
                this.name = Name.identifier("__kwire_fn_${library.id.toHexString()}_${Uuid.random().toHexString()}__")
                isFinal = true
                isStatic = parent is IrFile
                visibility = DescriptorVisibilities.PRIVATE
                origin = declOrigin
            }.apply {
                parent = this@SharedImportScope.parent
                initializer = context.irFactory.createExpressionBody(
                    startOffset = SYNTHETIC_OFFSET,
                    endOffset = SYNTHETIC_OFFSET,
                    expression = context.kwireSymbols.sharedLibraryGetFunctionAddress.call(
                        dispatchReceiver = library.field.load(receiver = dispatchReceiver),
                        valueArguments = mapOf("name" to name.toIrConst(context.irBuiltIns.stringType))
                    )
                )
            }
            functions[name] = addressField
        }
        return addressField
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun injectIfNeeded() { // @formatter:off
        parent.declarations.addAll(0, libraries.values
            .toSet()
            .flatMap { listOf(it.field) + it.functions.values }
        )
    } // @formatter:on
}

internal class SharedImportContext(
    private val context: KWirePluginContext
) {
    private val scopeStack: Stack<SharedImportScope> = Stack()
    inline val scope: SharedImportScope get() = scopeStack.peek()

    fun pushScope(parent: IrDeclarationContainer) {
        scopeStack.push(SharedImportScope(context, parent))
    }

    fun popScope() {
        scopeStack.pop().injectIfNeeded()
    }
}