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
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import kotlin.test.Test

class MemoryIntrinsicsTest {
    val primitiveTypes: Array<String> = arrayOf("Byte", "Short", "Int", "Long", "Float", "Double", "NInt", "NFloat")
    val primitiveSizes: Array<Int> = arrayOf(
        Byte.SIZE_BYTES,
        Short.SIZE_BYTES,
        Int.SIZE_BYTES,
        Long.SIZE_BYTES,
        Float.SIZE_BYTES,
        Double.SIZE_BYTES,
        NInt.SIZE_BYTES,
        NFloat.SIZE_BYTES
    )
    val primitiveAlignments: Array<Int> = arrayOf(
        Byte.SIZE_BYTES,
        Short.SIZE_BYTES,
        Int.SIZE_BYTES,
        Long.SIZE_BYTES,
        Float.SIZE_BYTES,
        Double.SIZE_BYTES,
        NInt.SIZE_BYTES,
        NFloat.SIZE_BYTES
    )

    @Test
    fun `Obtain offset of field in single field struct`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.memory.offsetOf
            class Foo(val x: Int = 0) : Struct
            val test: NUInt = offsetOf(Foo::x)
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrCallImpl::class
                    val value = expr.unwrapConstValue<Number>()!!.toInt()
                    value shouldBe 0
                }
            }
        }
    }

    @Test
    fun `Obtain offset of field in multi field struct`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.memory.offsetOf
            class Foo(
                val x: Int = 0,
                val y: Int = 0,
                val z: Int = 0
            ) : Struct
            val test: NUInt = offsetOf(Foo::z)
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
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
                    constants.size shouldBe 2
                    for (constant in constants) {
                        constant.unwrapConstValue<Number>()!!.toInt() shouldBe 4
                    }
                }
            }
        }
    }

    @Test
    fun `Obtain size of void`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.memory.sizeOf
            import dev.karmakrafts.kwire.ctype.NUInt
            val test: NUInt = sizeOf<Unit>()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                val test: NUInt = sizeOf<$type>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
                        val value = expr.unwrapConstValue<Number>()!!.toInt()
                        value shouldBe expectedSize
                    }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain size of zero-size struct`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
                import dev.karmakrafts.kwire.memory.sizeOf
                import dev.karmakrafts.kwire.ctype.Struct
                import dev.karmakrafts.kwire.ctype.NUInt
                class Foo : Struct
                val test: NUInt = sizeOf<Foo>()
            """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrCallImpl::class
                    val value = expr.unwrapConstValue<Number>()!!.toInt()
                    value shouldBe 0
                }
            }
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                class Foo(val x: $type = 0.to$type()) : Struct
                val test: NUInt = sizeOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                class Foo(
                    val x: $type = 0.to$type(),
                    val y: $type = 0.to$type(),
                    val z: $type = 0.to$type()
                ) : Struct
                val test: NUInt = sizeOf<Foo>()
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
            import dev.karmakrafts.kwire.ctype.NUInt
            val test: NUInt = alignOf<Unit>()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                val test: NUInt = alignOf<$type>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                class Foo(val x: $type = 0.to$type()) : Struct
                val test: NUInt = alignOf<Foo>()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    getChild<IrField>() matches {
                        val initializer = element.initializer
                        initializer shouldNotBe null
                        val expr = initializer!!.expression
                        expr::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.NUInt
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                class Foo(
                    val x: $type = 0.to$type(),
                    val y: $type = 0.to$type(),
                    val z: $type = 0.to$type()
                ) : Struct
                val test: NUInt = alignOf<Foo>()
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