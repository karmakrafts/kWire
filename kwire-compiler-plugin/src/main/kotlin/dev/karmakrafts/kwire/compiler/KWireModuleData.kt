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

package dev.karmakrafts.kwire.compiler

import dev.karmakrafts.kwire.compiler.monomorphizer.MonoFunctionSignature
import dev.karmakrafts.kwire.compiler.util.findChild
import dev.karmakrafts.kwire.compiler.util.getCleanSpecialName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class KWireModuleData( // @formatter:off
    private val context: IrPluginContext,
    private val symbols: KWireSymbols,
    private val module: IrModuleFragment
) { // @formatter:on
    val monomorphizedFunctions: HashMap<MonoFunctionSignature, IrSimpleFunction> = HashMap()

    // Allows making a reference to the synthetic class generated in FIR
    val monoFunctionClassName: Name =
        Name.identifier("KWireMonoFunctions\$${module.name.getCleanSpecialName().hashCode().toHexString()}")
    val monoFunctionClassId: ClassId = ClassId.topLevel(FqName.topLevel(monoFunctionClassName))
    val monoFunctionClass: IrClass = module.findChild<IrClass> { it.name == monoFunctionClassName }!!
}