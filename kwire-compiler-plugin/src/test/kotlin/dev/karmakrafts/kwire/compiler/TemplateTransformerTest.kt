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
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.target
import org.junit.jupiter.api.Test

class TemplateTransformerTest {
    @Test
    fun `Monomorphize imported external top level function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.Const
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.toNUInt
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ffi.SharedImport
            @Template
            @SharedImport(name = "memcpy", libraryNames = ["libc.so.6", "msvcrt.dll", "libSystem.dylib"])
            external fun <@ValueType T> memcpy(dest: Ptr<T>, src: @Const Ptr<T>, count: NUInt): Ptr<T>
            fun test() {
                memcpy(nullptr<Int>(), nullptr<Int>(), 0.toNUInt())
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            // TODO: implement this
            println(element.dump())
        }
    }

    @Test
    fun `Monomorphize member function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.Const
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.toNUInt
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ffi.SharedImport
            class Foo(val s: String) {
                @Template
                fun <@ValueType T> foo(): Ptr<T> {
                    println(s)
                    return nullptr()
                }
            }
            fun test() {
                val ptr = Foo("HELLO").foo<Int>()
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            val function = getChild<IrFunction> { it.name.asString().contains("_foo$$") }
            function matches {
                println(element.dump())
            }
            getChild<IrCall> { it.target == function } matches {
                // TODO: implement this
            }
        }
    }

    @Test
    fun `Monomorphize top level function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            @Template
            fun <@ValueType T> foo(address: Ptr<T>): Ptr<T> {
                return address + 2
            }
            fun bar() {
                val ptr = foo<Int>(nullptr())
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            // TODO: implement this
        }
    }

    @Test
    fun `Monomorphize class member function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            class Test {
                @Template
                fun <@ValueType T> foo(address: Ptr<T>): Ptr<T> {
                    return address + 2
                }
                fun bar() {
                    val ptr = foo<Int>(nullptr())
                }
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            // TODO: implement this
        }
    }
}