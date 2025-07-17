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
import org.jetbrains.kotlin.ir.util.dump
import org.junit.jupiter.api.Test

class TemplateTransformerTest {
    @Test
    fun `Monomorphize top level function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.meta.Template
            import dev.karmakrafts.kwire.meta.ValueType
            import dev.karmakrafts.kwire.memory.sizeOf
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            @Template
            fun <@ValueType T> test(): Ptr<T> {
                return nullptr()
            }
            @Template
            fun <@ValueType T> foo() {
                val ptr = test<T>()
            }
            fun bar() {
                foo<Int>()
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            println(element.dump())
            getChild<IrFunction> { it.name.asString() == "foo" } matches {
            }
        }
    }
}