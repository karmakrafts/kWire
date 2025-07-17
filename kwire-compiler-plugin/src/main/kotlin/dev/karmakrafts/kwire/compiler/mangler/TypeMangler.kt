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
import dev.karmakrafts.kwire.compiler.util.NativeType
import dev.karmakrafts.kwire.compiler.util.getNativeType
import dev.karmakrafts.kwire.compiler.util.getPointedType
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isNothing
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.isUnsignedType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.getArrayElementType
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.isPrimitiveArray
import org.jetbrains.kotlin.ir.util.kotlinFqName

internal class TypeMangler(
    val context: KWirePluginContext
) {
    internal val builtinMappings: Map<IrType, String> = mapOf(
        builtIns.byteType to ManglingConstants.BYTE_NAME,
        builtIns.shortType to ManglingConstants.SHORT_NAME,
        builtIns.intType to ManglingConstants.INT_NAME,
        builtIns.longType to ManglingConstants.LONG_NAME,
        context.irBuiltIns.ubyteType to ManglingConstants.UBYTE_NAME,
        context.irBuiltIns.ushortType to ManglingConstants.USHORT_NAME,
        context.irBuiltIns.uintType to ManglingConstants.UINT_NAME,
        context.irBuiltIns.ulongType to ManglingConstants.ULONG_NAME,
        builtIns.floatType to ManglingConstants.FLOAT_NAME,
        builtIns.doubleType to ManglingConstants.DOUBLE_NAME,
        builtIns.charType to ManglingConstants.CHAR_NAME,
        builtIns.booleanType to ManglingConstants.BOOLEAN_NAME,
        builtIns.unitType to ManglingConstants.UNIT_NAME,
        builtIns.nothingType to ManglingConstants.NOTHING_NAME,
        builtIns.stringType to ManglingConstants.STRING_NAME
    )

    val reverseBuiltinMappings: Map<String, IrType> = builtinMappings.map { (key, value) -> value to key }.toMap()

    internal inline val builtIns: IrBuiltIns
        get() = context.irBuiltIns

    fun List<IrTypeArgument>.mangleTypeParameters(): String {
        if (isEmpty()) return ""
        var result = ManglingConstants.TYPE_LIST_BEGIN
        for (argument in this) {
            result += when (argument) {
                is IrStarProjection -> ManglingConstants.STAR_PROJECTION
                is IrTypeProjection -> argument.typeOrFail.mangle()
            }
        }
        return "$result${ManglingConstants.TYPE_LIST_END}"
    }

    fun IrType.mangleClass(): String {
        val clazz = requireNotNull(getClass()) { "Could not obtain IrClass for mangling class type" }
        val classFqName = clazz.kotlinFqName
        var result = ManglingConstants.CLASS_BEGIN
        for (packageSegment in classFqName.pathSegments().dropLast(1)) {
            result += "${packageSegment.asString()}${ManglingConstants.PACKAGE_DELIMITER}"
        }
        result += "${classFqName.shortName().asString()}${ManglingConstants.CLASS_END}"
        if (this is IrSimpleType) result += arguments.mangleTypeParameters()
        return result
    }

    fun IrType.manglePointer(): String {
        var result = ManglingConstants.PTR_NAME
        if (this is IrSimpleType) result += arguments.mangleTypeParameters()
        return result
    }

    fun IrType.mangle(): String {
        val nativeType = getNativeType()
        when (nativeType) {
            NativeType.NINT -> return ManglingConstants.NINT_NAME
            NativeType.NUINT -> return ManglingConstants.NUINT_NAME
            NativeType.NFLOAT -> return ManglingConstants.NFLOAT_NAME
            NativeType.PTR -> {
                val typeArgument = listOf<IrTypeArgument>(getPointedType()!!).mangleTypeParameters()
                return "${ManglingConstants.PTR_NAME}$typeArgument"
            }
            else -> { /* fallthrough */
            }
        }
        var result = when {
            // @formatter:off
            isUnit()
                || isNothing()
                || isString()
                || isPrimitiveType(false)
                || isPrimitiveType(true)
                || isUnsignedType(false)
                || isUnsignedType(true) -> builtinMappings[this]!!
            // @formatter:on
            isArray() || isPrimitiveArray() -> "${ManglingConstants.ARRAY_BEGIN}${getArrayElementType(builtIns).mangle()}${ManglingConstants.ARRAY_END}"
            else -> mangleClass()
        }
        if (isNullable()) result += ManglingConstants.NULLABLE_SUFFIX
        return result
    }

    fun Collection<IrType>.mangle(): String = joinToString("") { it.mangle() }

    //fun String.demangleTypes(): List<IrType> {
    //    val charStream = CharStreams.fromString(this)
    //    val lexer = DemanglerLexer(charStream)
    //    val tokenStream = BufferedTokenStream(lexer)
    //    val parser = DemanglerParser(tokenStream)
    //    val signature = parser.signature()
    //    val visitor = Demangler(context, reverseBuiltinMappings)
    //    signature.enterRule(visitor)
    //    return emptyList()
    //}

    //fun String.demangleType(): IrType = demangleTypes().first()
}