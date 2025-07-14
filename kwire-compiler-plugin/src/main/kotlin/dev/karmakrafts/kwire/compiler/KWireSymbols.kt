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

import dev.karmakrafts.kwire.compiler.util.KWireNames
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeAliasSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.isVararg

internal class KWireSymbols(
    context: IrPluginContext
) {
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    val listOf: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.Kotlin.listOf).first { symbol ->
        val parameters = symbol.owner.parameters.filter { it.kind == IrParameterKind.Regular }
        parameters.first().isVararg
    }

    val sizeOf: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.MemoryPkg.sizeOf).first()
    val alignOf: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.MemoryPkg.alignOf).first()

    val memoryType: IrClassSymbol = context.referenceClass(KWireNames.Memory.id)!!
    val memoryCompanionType: IrClassSymbol = context.referenceClass(KWireNames.Memory.Companion.id)!!
    val allocatorType: IrClassSymbol = context.referenceClass(KWireNames.Allocator.id)!!
    val allocatorAllocate: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.Allocator.allocate).first()
    val allocatorReallocate: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.Allocator.reallocate).first()
    val allocatorFree: IrSimpleFunctionSymbol = context.referenceFunctions(KWireNames.Allocator.free).first()

    val structType: IrClassSymbol = context.referenceClass(KWireNames.Struct.id)!!
    val structLayoutType: IrClassSymbol = context.referenceClass(KWireNames.StructLayout.id)!!
    val structLayoutConstructor: IrConstructorSymbol = context.referenceConstructors(KWireNames.StructLayout.id).first()
    val alignAsType: IrClassSymbol = context.referenceClass(KWireNames.AlignAs.id)!!
    val alignAsConstructor: IrConstructorSymbol = context.referenceConstructors(KWireNames.AlignAs.id).first()

    val ptrType: IrClassSymbol = context.referenceClass(KWireNames.Ptr.id)!!
    val ptrCompanionType: IrClassSymbol = context.referenceClass(KWireNames.Ptr.Companion.id)!!
    val ptrSizeBytes: IrPropertySymbol = context.referenceProperties(KWireNames.Ptr.Companion.SIZE_BYTES).first()
    val ptrConstructor: IrConstructorSymbol = context.referenceConstructors(KWireNames.Ptr.id).first()
    val ptrPlus: Collection<IrSimpleFunctionSymbol> = context.referenceFunctions(KWireNames.Ptr.plus)
    val ptrMinus: Collection<IrSimpleFunctionSymbol> = context.referenceFunctions(KWireNames.Ptr.minus)

    val nIntType: IrTypeAliasSymbol = context.referenceTypeAlias(KWireNames.NInt.id)!!
    val nUIntType: IrClassSymbol = context.referenceClass(KWireNames.NUInt.id)!!
    val nFloatType: IrTypeAliasSymbol = context.referenceTypeAlias(KWireNames.NFloat.id)!!

    val uByteType: IrClassSymbol = context.referenceClass(KWireNames.Kotlin.UByte.id)!!
    val uShortType: IrClassSymbol = context.referenceClass(KWireNames.Kotlin.UShort.id)!!
    val uIntType: IrClassSymbol = context.referenceClass(KWireNames.Kotlin.UInt.id)!!
    val uLongType: IrClassSymbol = context.referenceClass(KWireNames.Kotlin.ULong.id)!!

    val sharedLibraryType: IrClassSymbol = context.referenceClass(KWireNames.SharedLibrary.id)!!
    val sharedLibraryCompanionType: IrClassSymbol = context.referenceClass(KWireNames.SharedLibrary.Companion.id)!!
    val sharedLibraryOpen: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.SharedLibrary.Companion.open).first()
    val sharedLibraryGetFunctionAddress: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.SharedLibrary.getFunctionAddress).first()
    val sharedLibraryCloseOnExit: IrSimpleFunctionSymbol =
        context.referenceFunctions(KWireNames.SharedLibrary.closeOnExit).first()

    val constType: IrClassSymbol = context.referenceClass(KWireNames.Const.id)!!
    val constConstructor: IrConstructorSymbol = context.referenceConstructors(KWireNames.Const.id).first()
}