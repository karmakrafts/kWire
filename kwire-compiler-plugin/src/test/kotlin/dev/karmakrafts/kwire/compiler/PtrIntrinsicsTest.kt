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
import dev.karmakrafts.iridium.util.renderIrTree
import dev.karmakrafts.kwire.compiler.util.unwrapConstValue
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.util.target
import kotlin.test.Test

class PtrIntrinsicsTest {
    val primitiveTypes: Array<String> = arrayOf("Byte", "Short", "Int", "Long", "Float", "Double", "NInt", "NFloat")
    val resolvedPrimitiveTypes: Array<String> =
        arrayOf("Byte", "Short", "Int", "Long", "Float", "Double", "Long", "Double")

    @Test
    fun `Invoke function pointer using direct invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.FunPtr
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            fun test() {
                val ptr = nullptr<FunPtr<(Int) -> Unit>>()
                ptr(42)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            println(element.renderIrTree(Int.MAX_VALUE))
        }
    }

    @Test
    fun `Invoke function pointer using dynamic invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.FunPtr
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            fun test() {
                val args = arrayOf<Any>(42)
                val ptr = nullptr<FunPtr<(Int) -> Unit>>()
                ptr(*args)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            println(element.renderIrTree(Int.MAX_VALUE))
        }
    }

    @Test
    fun `Obtain nullptr of void`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.VoidPtr
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: VoidPtr = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                val field = getChild<IrField>()
                val initializer = field.initializer?.expression
                initializer shouldNotBe null
                initializer!!::class shouldBe IrConstructorCallImpl::class

                val arg = (initializer as IrConstructorCall).arguments.first()
                arg shouldNotBe null
                arg!!::class shouldBe IrCallImpl::class

                arg.unwrapConstValue<Number>()!!.toLong() shouldBe 0
            }
        }
    }

    @Test
    fun `Obtain nullptr of primitive type`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (type in primitiveTypes) {
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.NumPtr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                val test: NumPtr<$type> = nullptr()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    val field = getChild<IrField>()
                    val initializer = field.initializer?.expression
                    initializer shouldNotBe null
                    initializer!!::class shouldBe IrConstructorCallImpl::class

                    val arg = (initializer as IrConstructorCall).arguments.first()
                    arg shouldNotBe null
                    arg!!::class shouldBe IrCallImpl::class

                    arg.unwrapConstValue<Number>()!!.toLong() shouldBe 0
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain nullptr of structure type`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.nullptr
            class Foo(val x: Int = 0) : Struct
            val test: Ptr<Foo> = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                val field = getChild<IrField>()
                val initializer = field.initializer?.expression
                initializer shouldNotBe null
                initializer!!::class shouldBe IrConstructorCallImpl::class

                val arg = (initializer as IrConstructorCall).arguments.first()
                arg shouldNotBe null
                arg!!::class shouldBe IrCallImpl::class

                arg.unwrapConstValue<Number>()!!.toLong() shouldBe 0
            }
        }
    }

    @Test
    fun `Obtain nullptr of raw address`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Address
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: Address = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                val field = getChild<IrField>()
                val initializer = field.initializer?.expression
                initializer shouldNotBe null
                initializer!!::class shouldBe IrConstructorCallImpl::class

                val arg = (initializer as IrConstructorCall).arguments.first()
                arg shouldNotBe null
                arg!!::class shouldBe IrCallImpl::class

                arg.unwrapConstValue<Number>()!!.toLong() shouldBe 0
            }
        }
    }

    @Test
    fun `Dereference primitive pointer`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val resolvedType = resolvedPrimitiveTypes[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.NumPtr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val test: $type = nullptr<NumPtr<$type>>().deref()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    val field = getChild<IrField>()
                    val initializer = field.initializer?.expression
                    initializer shouldNotBe null
                    initializer!!::class shouldBe IrCallImpl::class

                    val call = initializer as IrCall
                    val callee = call.target
                    callee.name.asString() shouldBe "read$resolvedType"
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Write to primitive pointer`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (type in primitiveTypes) {
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.NumPtr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val value: NumPtr<$type> = nullptr<NumPtr<$type>>()
                fun test() {
                    value.set(0.to$type())
                }
            """.trimIndent())
            // @formatter:on
            result irMatches {

            }
            evaluate()
        }
    }
}