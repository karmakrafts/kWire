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
import dev.karmakrafts.kwire.compiler.util.unwrapConstValue
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import kotlin.test.Test

class MemoryIntrinsicsTest {
    val primitiveTypes: Array<String> = arrayOf("Byte", "Short", "Int", "Long", "Float", "Double")
    val primitiveSizes: Array<Int> =
        arrayOf(Byte.SIZE_BYTES, Short.SIZE_BYTES, Int.SIZE_BYTES, Long.SIZE_BYTES, Float.SIZE_BYTES, Double.SIZE_BYTES)
    val primitiveAlignments: Array<Int> =
        arrayOf(Byte.SIZE_BYTES, Short.SIZE_BYTES, Int.SIZE_BYTES, Long.SIZE_BYTES, Float.SIZE_BYTES, Double.SIZE_BYTES)

    @Test
    fun `Obtain size of void`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.memory.sizeOf
            val test: Int = sizeOf<Unit>()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrConstImpl::class
                    val value = expr.unwrapConstValue<Number>()!!.toInt()
                    value shouldBe 0
                }
            }
        }
    }

    @Test
    fun `Obtain size of primitive types`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val expectedSize = primitiveSizes[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.sizeOf
                val test: Int = sizeOf<$type>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrConstImpl::class
                        val value = expr.unwrapConstValue<Number>()!!.toInt()
                        value shouldBe expectedSize
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain size of single field struct`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val expectedSize = primitiveSizes[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.sizeOf
                import dev.karmakrafts.kwire.ctype.Struct
                class Foo(val x: $type) : Struct
                val test: Int = sizeOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrConstImpl::class
                        val value = expr.unwrapConstValue<Number>()!!.toInt()
                        value shouldBe expectedSize
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain size of multi field struct`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val typeSize = primitiveSizes[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.sizeOf
                import dev.karmakrafts.kwire.ctype.Struct
                class Foo(
                    val x: $type,
                    val y: $type,
                    val z: $type
                ) : Struct
                val test: Int = sizeOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
                        val constants = ArrayList<IrConst>()
                        expr.acceptVoid(object : IrVisitorVoid() {
                            override fun visitElement(element: IrElement) {
                                element.acceptChildrenVoid(this)
                            }

                            override fun visitConst(expression: IrConst) {
                                super.visitConst(expression)
                                constants += expression
                            }
                        })
                        constants.size shouldBe 3
                        for (constant in constants) {
                            constant.unwrapConstValue<Number>()!!.toInt() shouldBe typeSize
                        }
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain alignment of void`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.memory.alignOf
            val test: Int = alignOf<Unit>()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrConstImpl::class
                    val value = expr.unwrapConstValue<Number>()!!.toInt()
                    value shouldBe 0
                }
            }
        }
    }

    @Test
    fun `Obtain alignment of primitive types`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val expectedAlignment = primitiveAlignments[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.alignOf
                val test: Int = alignOf<$type>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrConstImpl::class
                        val value = expr.unwrapConstValue<Number>()!!.toInt()
                        value shouldBe expectedAlignment
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain alignment of single field struct`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val expectedAlignment = primitiveAlignments[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.alignOf
                import dev.karmakrafts.kwire.ctype.Struct
                class Foo(val x: $type) : Struct
                val test: Int = alignOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrConstImpl::class
                        val value = expr.unwrapConstValue<Number>()!!.toInt()
                        value shouldBe expectedAlignment
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain alignment of multi field struct`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val typeAlignment = primitiveAlignments[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.memory.alignOf
                import dev.karmakrafts.kwire.ctype.Struct
                class Foo(
                    val x: $type,
                    val y: $type,
                    val z: $type
                ) : Struct
                val test: Int = alignOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
                        val constants = ArrayList<IrConst>()
                        expr.acceptVoid(object : IrVisitorVoid() {
                            override fun visitElement(element: IrElement) {
                                element.acceptChildrenVoid(this)
                            }

                            override fun visitConst(expression: IrConst) {
                                super.visitConst(expression)
                                constants += expression
                            }
                        })
                        constants.size shouldBe 3
                        for (constant in constants) {
                            constant.unwrapConstValue<Number>()!!.toInt() shouldBe typeAlignment
                        }
                    }
                }
            }
            evaluate()
        }
    }
}