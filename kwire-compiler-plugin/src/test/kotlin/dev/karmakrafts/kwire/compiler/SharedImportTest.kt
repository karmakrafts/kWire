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
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.dump
import org.junit.jupiter.api.Test

class SharedImportTest {
    @Test
    fun `Imported function generates library field in file scope`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ffi.SharedImport
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CVoid
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ctype.Const
            @SharedImport(
                libraryNames = ["libc.so.6", "msvcrt.dll", "libSystem.dylib"],
                name = "memcpy"
            )
            external fun copyMemory(dest: Ptr<*>, src: @Const Ptr<*>, count: NUInt): Ptr<CVoid>
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            containsChild<IrField> { it.name.asString().startsWith("__kwire_library_") }
        }
    }

    @Test
    fun `Imported function generates body in file scope`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ffi.SharedImport
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CVoid
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ctype.Const
            @SharedImport(
                libraryNames = ["libc.so.6", "msvcrt.dll", "libSystem.dylib"],
                name = "memcpy"
            )
            external fun copyMemory(dest: Ptr<*>, src: @Const Ptr<*>, count: NUInt): Ptr<CVoid>
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrFunction> { it.name.asString() == "copyMemory" } matches {
                element.isExternal shouldBe false
                val body = element.body
                body shouldNotBe null
            }
        }
    }

    @Test
    fun `Imported function generates library field in class scope`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ffi.SharedImport
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CVoid
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ctype.Const
            class Test {
                @SharedImport(
                    libraryNames = ["libc.so.6", "msvcrt.dll", "libSystem.dylib"],
                    name = "memcpy"
                )
                external fun copyMemory(dest: Ptr<*>, src: @Const Ptr<*>, count: NUInt): Ptr<CVoid>
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrClass> { it.name.asString() == "Test" } matches {
                containsChild<IrField> { it.name.asString().startsWith("__kwire_library_") }
            }
        }
    }

    @Test
    fun `Imported function generates body in class scope`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ffi.SharedImport
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CVoid
            import dev.karmakrafts.kwire.ctype.NUInt
            import dev.karmakrafts.kwire.ctype.Const
            class Test {
                @SharedImport(
                    libraryNames = ["libc.so.6", "msvcrt.dll", "libSystem.dylib"],
                    name = "memcpy"
                )
                external fun copyMemory(dest: Ptr<*>, src: @Const Ptr<*>, count: NUInt): Ptr<CVoid>
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrClass> { it.name.asString() == "Test" } matches {
                getChild<IrFunction> { it.name.asString() == "copyMemory" } matches {
                    println(element.dump())
                    element.isExternal shouldBe false
                    val body = element.body
                    body shouldNotBe null
                }
            }
        }
    }
}