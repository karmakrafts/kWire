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

import dev.karmakrafts.iridium.runCompilerTest
import dev.karmakrafts.iridium.setupCompilerTest
import dev.karmakrafts.kwire.compiler.util.BuiltinMemoryLayout
import dev.karmakrafts.kwire.compiler.util.MemoryLayout
import dev.karmakrafts.kwire.compiler.util.StructMemoryLayout
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.defaultType
import kotlin.test.Test

class MemoryLayoutTest {
    private data class PrimitiveType(
        val typeGetter: KWirePluginContext.() -> IrType, val layout: BuiltinMemoryLayout
    )

    private val primitiveTypes: Array<PrimitiveType> = arrayOf(
        PrimitiveType({ irBuiltIns.byteType }, BuiltinMemoryLayout.BYTE),
        PrimitiveType({ irBuiltIns.shortType }, BuiltinMemoryLayout.SHORT),
        PrimitiveType({ irBuiltIns.intType }, BuiltinMemoryLayout.INT),
        PrimitiveType({ irBuiltIns.longType }, BuiltinMemoryLayout.LONG),
        PrimitiveType({ irBuiltIns.floatType }, BuiltinMemoryLayout.FLOAT),
        PrimitiveType({ irBuiltIns.doubleType }, BuiltinMemoryLayout.DOUBLE)
    )

    @Test
    fun `Serialize and deserialize primitive layout`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (type in primitiveTypes) {
            resetAssertions()
            result irMatches {
                val context = KWirePluginContext(pluginContext)
                val layout = context.computeMemoryLayout(type.typeGetter(context))
                val data = layout.serialize()
                data.first() shouldBe type.layout.tag.toByte()
                val deserializedLayout = MemoryLayout.deserialize(data)
                deserializedLayout::class shouldBe StructMemoryLayout::class
                (deserializedLayout as StructMemoryLayout).fields.first() shouldBe type.layout
            }
            evaluate()
        }
    }

    @Test
    fun `Serialize and deserialize simple struct layout`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Struct
            class Foo(
                val x: Byte,
                val y: Short,
                val z: Int
            ) : Struct
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            val context = KWirePluginContext(pluginContext)
            val struct = getChild<IrClass> { it.name.asString() == "Foo" }
            val layout = context.computeMemoryLayout(struct.defaultType)
            val data = layout.serialize()
            data.size shouldBe 3
            val deserializedLayout = MemoryLayout.deserialize(data)
            deserializedLayout::class shouldBe StructMemoryLayout::class
            val fields = (deserializedLayout as StructMemoryLayout).fields
            fields[0] shouldBe BuiltinMemoryLayout.BYTE
            fields[1] shouldBe BuiltinMemoryLayout.SHORT
            fields[2] shouldBe BuiltinMemoryLayout.INT
        }
    }

    @Test
    fun `Serialize and deserialize complex struct layout`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Struct
            class Bar(val x: Int) : Struct
            class Foo(
                val x: Byte,
                val y: Short,
                val z: Bar
            ) : Struct
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            val context = KWirePluginContext(pluginContext)
            val struct = getChild<IrClass> { it.name.asString() == "Foo" }
            val layout = context.computeMemoryLayout(struct.defaultType)
            val data = layout.serialize()
            data.size shouldBe 7
            val deserializedLayout = MemoryLayout.deserialize(data)
            deserializedLayout::class shouldBe StructMemoryLayout::class
            val fields = (deserializedLayout as StructMemoryLayout).fields
            fields[0] shouldBe BuiltinMemoryLayout.BYTE
            fields[1] shouldBe BuiltinMemoryLayout.SHORT
            val nestedField = fields[2]
            nestedField::class shouldBe StructMemoryLayout::class
            val nestedFields = (nestedField as StructMemoryLayout).fields
            nestedFields[0] shouldBe BuiltinMemoryLayout.INT
        }
    }
}