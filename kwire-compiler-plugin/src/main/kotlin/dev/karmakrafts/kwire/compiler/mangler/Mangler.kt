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

package dev.karmakrafts.kwire.compiler.mangler

import dev.karmakrafts.kwire.compiler.KWirePluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.name.Name

/**
 * The mangler transforms the name of a given IR element
 * based on the type information available at the instantiation site.
 * This is done to prevent name clashes during monomorphization
 * of possibly many different versions of the same element.
 *
 * Functions are mangled as follows when monomorphized:
 *  - fun <T> test(value: T)       ->  test<Int>()     ->  test_c__c_mono()
 *  - fun <T> test(value: Int)     ->  test<Float>()   ->  test_c__k_mono()
 *  - fun <T> T.test()             ->  10.test()       ->  test__c_mono
 *
 * The original function name is followed by **two underscores** when
 * the function has a dispatch- or extension receiver.
 * Otherwise, it is always followed by **a single underscore**.
 *
 * Classes are mangled as follows when monomorphized:
 *  - class Test<T>                ->  Test<Int>()     -> Test$c$mono()
 */
internal class Mangler(
    private val context: KWirePluginContext
) {
    companion object {
        const val MARKER_SUFFIX: String = "mono"
    }

    private fun IrFunction.computeMangledSignature(): String {
        val parameterTypes = parameters.filter { it.kind == IrParameterKind.Regular }.map { it.type }
        val types = listOf(returnType) + parameterTypes
        return with(context.typeMangler) { types.mangle() }
    }

    fun IrFunction.mangleName(typeArguments: List<IrType>) {
        // Gather receiver parameters
        val hasDispatchReceiver = dispatchReceiverParameter != null
        val extensionReceiverParameter = parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        val hasExtensionReceiver = extensionReceiverParameter != null
        val hasReceiver = hasDispatchReceiver || hasExtensionReceiver

        // Construct receiver signature
        val receiverTypes = ArrayList<IrType>()
        if (hasDispatchReceiver) receiverTypes += dispatchReceiverParameter!!.type
        if (hasExtensionReceiver) receiverTypes += extensionReceiverParameter.type
        val receiverSignature = with(context.typeMangler) { receiverTypes.mangle() }

        var newName = name.asString()
        newName += if (hasReceiver) "__${receiverSignature}_"
        else "_"
        newName += computeMangledSignature()

        if (typeArguments.isNotEmpty()) {
            newName += "_${with(context.typeMangler) { typeArguments.mangle() }}"
        }

        newName += "_$MARKER_SUFFIX"
        name = Name.identifier(newName)
    }

    fun IrClass.mangleName(typeArguments: List<IrType>) {
        val suffix = with(context.typeMangler) { typeWith(typeArguments).mangle() }
        name = Name.identifier("${name.asString()}\$${suffix}\$$MARKER_SUFFIX")
    }
}