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
import kotlin.test.Test

class ConstTypeTest {
    companion object {
        private const val INTERP_BEGIN: String = "\${"
        private const val INTERP_END: String = "}"
    }

    @Test
    fun `Non const value parameter rejects const value`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.*
            fun test(ptr: Ptr<CVoid>) = println(ptr)
            fun main() {
                val ptr: @Const Ptr<CVoid> = nullptr()
                test(ptr)
            }
        """.trimIndent())
        compiler shouldReport {
            error()
            atLine(5)
            inColumn(10)
        }
    }

    @Test
    fun `Const value parameter accepts non const value`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.*
            fun test(ptr: @Const Ptr<CVoid>) = println(ptr)
            fun main() {
                test(nullptr())
            }
        """.trimIndent())
        compiler shouldNotReport { error() }
    }

    @Test
    fun `Const value parameter accepts const value`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.*
            fun test(ptr: @Const Ptr<CVoid>) = println(ptr)
            fun main() {
                val ptr: @Const Ptr<CVoid> = nullptr()
                test(ptr)
            }
        """.trimIndent())
        compiler shouldNotReport { error() }
    }

    @Test
    fun `Const type parameter accepts const type`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.*
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.memory.sizeOf
            @Template
            fun <@ValueType @Const T> test() = println("T is ${INTERP_BEGIN}sizeOf<T>()${INTERP_END} bytes")
            fun main() {
                test<@Const Int>()
            }
        """.trimIndent())
        compiler shouldNotReport { error() }
    }

    @Test
    fun `Const type parameter rejects non const type`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.*
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.memory.sizeOf
            @Template
            fun <@ValueType @Const T> test() = println("T is ${INTERP_BEGIN}sizeOf<T>()${INTERP_END} bytes")
            fun main() {
                test<Int>()
            }
        """.trimIndent())
        compiler shouldReport {
            error()
            atLine(8)
            inColumn(5)
        }
    }
}