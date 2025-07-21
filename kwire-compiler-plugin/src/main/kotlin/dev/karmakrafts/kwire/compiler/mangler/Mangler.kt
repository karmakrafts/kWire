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
import dev.karmakrafts.kwire.compiler.util.getABIType
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * The mangler transforms the name of a given IR element
 * based on the type information available at the instantiation site.
 * This is done to prevent name clashes during monomorphization
 * of possibly many different versions of the same element.
 *
 * Functions are mangled as follows when monomorphized:
 *  - fun <T> test(value: T)       ->  test<Int>()     ->  test_XXXXXXXX_c__c()
 *  - fun <T> test(value: Int)     ->  test<Float>()   ->  test_XXXXXXXX_c__k()
 *  - fun <T> T.test()             ->  10.test()       ->  test_XXXXXXXX__c
 *
 * The original function name is followed by **two underscores** when
 * the function has a dispatch- or extension receiver.
 * Otherwise, it is always followed by **a single underscore**.
 *
 * Classes are mangled as follows when monomorphized:
 *  - class Test<T>                ->  Test<Int>()     -> Test$XXXXXXXX$c()
 *
 * Where `XXXXXXXX` is a placeholder of the hash code of the original IR element
 * before monomorphization.
 */
internal class Mangler(
    private val context: KWirePluginContext
) {
    private fun IrFunction.computeMangledSignature(): String {
        val parameterTypes = parameters.filter { it.kind == IrParameterKind.Regular }.map { it.type }
        val types = listOf(returnType) + parameterTypes
        return types.joinToString("") { it.getABIType(context)!!.mangledName }
    }

    fun IrFunction.mangleName(
        typeArguments: List<IrType>, originalName: FqName = kotlinFqName
    ): Name {
        // Gather receiver parameters
        val extensionReceiverParameter = parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver }
        val hasReceiver = extensionReceiverParameter != null

        // Construct receiver signature
        val receiverSignature = extensionReceiverParameter?.type?.let {
            it.getABIType(context)?.mangledName
        }

        var newName = originalName.asString().replace('.', '_')
        newName += if (hasReceiver) "__${receiverSignature}_"
        else "_"
        newName += computeMangledSignature()

        if (typeArguments.isNotEmpty()) {
            newName += "_${typeArguments.joinToString("") { it.getABIType(context)!!.mangledName }}"
        }

        return Name.identifier(newName)
    }

    fun IrFunction.mangleNameInPlace(
        typeArguments: List<IrType>, originalName: FqName = kotlinFqName
    ) {
        name = mangleName(typeArguments, originalName)
    }

    fun IrClass.mangleName(typeArguments: List<IrType>): Name {
        val suffix = typeWith(typeArguments).getABIType(context)!!.mangledName
        return Name.identifier("${name.asString()}\$${hashCode()}\$${suffix}")
    }

    fun IrClass.mangleNameInPlace(typeArguments: List<IrType>) {
        name = mangleName(typeArguments)
    }
}