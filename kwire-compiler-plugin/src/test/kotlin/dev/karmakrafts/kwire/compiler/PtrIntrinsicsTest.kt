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
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import kotlin.test.Test

class PtrIntrinsicsTest {
    @Test
    fun `Obtain nullptr to VoidPtr`() = runCompilerTest {
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
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrConstructorCallImpl::class
                }
            }
        }
    }

    @Test
    fun `Obtain nullptr to NumPtr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.NumPtr
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: NumPtr<Byte> = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrConstructorCallImpl::class
                }
            }
        }
    }

    @Test
    fun `Obtain nullptr to Ptr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.NumPtr
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: Ptr<NumPtr<Byte>> = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                getChild<IrField>() matches {
                    val initializer = element.initializer
                    initializer shouldNotBe null
                    val expr = initializer!!.expression
                    expr::class shouldBe IrConstructorCallImpl::class
                }
            }
        }
    }
}