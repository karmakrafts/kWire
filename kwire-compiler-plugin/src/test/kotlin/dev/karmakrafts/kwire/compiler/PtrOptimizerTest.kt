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
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.util.dump
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class PtrOptimizerTest {
    @Test
    fun `Optimize ref-deref-ref`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.ref
            fun test() {
                val x = 100
                val xPtr = x.ref().deref().ref()
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrFunction> { it.name.asString() == "test" } matches {
                getChild<IrVariable> { it.name.asString() == "xPtr" } matches {

                }
            }
        }
    }

    @Ignore // TODO: re-implement this optimization
    @Test
    fun `Optimize deref-ref-deref`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.ref
            fun test() {
                val x = 100
                val x2 = x.ref().deref()
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrFunction> { it.name.asString() == "test" } matches {
                getChild<IrVariable> { it.name.asString() == "x2" } matches {
                    println(element.dump())
                }
            }
        }
    }
}