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

package dev.karmakrafts.kwire.abi.demangler

import dev.karmakrafts.kwire.abi.symbol.SymbolName
import dev.karmakrafts.kwire.abi.type.BuiltinType
import dev.karmakrafts.kwire.abi.type.ReferenceType
import dev.karmakrafts.kwire.abi.type.StructType
import dev.karmakrafts.kwire.abi.type.Type
import dev.karmakrafts.kwire.abi.type.TypeArgument
import dev.karmakrafts.kwire.abi.type.asArray
import dev.karmakrafts.kwire.abi.type.asNullable
import dev.karmakrafts.kwire.abi.type.withArguments
import org.antlr.v4.kotlinruntime.BufferedTokenStream
import org.antlr.v4.kotlinruntime.CharStreams

typealias StructResolver = (name: SymbolName) -> List<Type>

private class LazyStructType(
    symbolName: SymbolName, resolver: StructResolver
) : StructType(symbolName, emptyList()) {
    override val fields: List<Type> by lazy { resolver(symbolName) }
}

private class TypeConversionVisitor(
    private val structResolver: StructResolver
) : DemanglerParserBaseVisitor<List<Type>>() {
    private var isNullable: Boolean = false

    override fun defaultResult(): List<Type> = emptyList()

    override fun aggregateResult(aggregate: List<Type>, nextResult: List<Type>): List<Type> = aggregate + nextResult

    override fun visitSignature(ctx: DemanglerParser.SignatureContext): List<Type> {
        return ctx.type().flatMap(::visitType)
    }

    override fun visitType(ctx: DemanglerParser.TypeContext): List<Type> {
        isNullable = ctx.NULLABLE_SUFFIX() != null
        val types = super.visitType(ctx)
        isNullable = false
        return types
    }

    override fun visitBuiltin(ctx: DemanglerParser.BuiltinContext): List<Type> {
        val baseType = BuiltinType.entries.first {
            it.mangledName == ctx.BUILTIN().text
        }
        val typeListNode = ctx.typeList()
        if (typeListNode != null) {
            return listOf(adjustNullability(baseType.withArguments(convertTypeList(typeListNode))))
        }
        return listOf(adjustNullability(baseType))
    }

    override fun visitClassType(ctx: DemanglerParser.ClassTypeContext): List<Type> {
        val baseType = ReferenceType(SymbolName.demangle(ctx.CLASS_NAME().text))
        val typeListNode = ctx.typeList()
        if (typeListNode != null) {
            return listOf(adjustNullability(baseType.withArguments(convertTypeList(typeListNode))))
        }
        return listOf(adjustNullability(baseType))
    }

    override fun visitStructType(ctx: DemanglerParser.StructTypeContext): List<Type> {
        val symbolName = SymbolName.demangle(ctx.STRUCT_NAME().text)
        val baseType = LazyStructType(symbolName, structResolver)
        val typeListNode = ctx.typeList()
        if (typeListNode != null) {
            return listOf(adjustNullability(baseType.withArguments(convertTypeList(typeListNode))))
        }
        return listOf(adjustNullability(baseType))
    }

    override fun visitArrayType(ctx: DemanglerParser.ArrayTypeContext): List<Type> {
        val dimensions = ctx.ARRAY_BEGIN().text.length // Number of A's in the begin-marker
        return listOf(adjustNullability(visitType(ctx.type()).first().asArray(dimensions)))
    }

    private fun adjustNullability(type: Type): Type {
        return if (isNullable) type.asNullable()
        else type
    }

    private fun convertTypeList(ctx: DemanglerParser.TypeListContext): List<TypeArgument> {
        val argumentNodes = ctx.typeArgument()
        val arguments = ArrayList<TypeArgument>()
        for (argumentNode in argumentNodes) {
            arguments += when {
                argumentNode.WILDCARD() != null -> TypeArgument.Star
                else -> TypeArgument.Concrete(visitType(argumentNode.type()!!).first())
            }
        }
        return arguments
    }
}

object Demangler {
    fun demangle(value: String, structResolver: StructResolver): List<Type> {
        val charStream = CharStreams.fromString(value)
        val lexer = DemanglerLexer(charStream)
        val tokenStream = BufferedTokenStream(lexer)
        val parser = DemanglerParser(tokenStream)
        return parser.signature().accept(TypeConversionVisitor(structResolver))
    }

    fun demangleFirst(value: String, structResolver: StructResolver): Type = demangle(value, structResolver).first()
}