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
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.target
import kotlin.test.Test

@OptIn(UnsafeDuringIrConstructionAPI::class)
class PtrIntrinsicsTest {
    val primitiveTypes: Array<String> = arrayOf("Byte", "Short", "Int", "Long", "Float", "Double", "NInt", "NFloat")
    val resolvedPrimitiveTypes: Array<String> =
        arrayOf("Byte", "Short", "Int", "Long", "Float", "Double", "Long", "Double")

    @Test
    fun `Dereference of local in same scope is optimized away`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.ref
            fun test() {
                var x = 100
                val xPtr = x.ref()
                println(xPtr.deref())
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            // TODO: implement actual test logic
        }
    }

    @Test
    fun `Perform plus operation on NumPtr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: Ptr<Byte> = nullptr<Byte>() + 42
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            containsChild<IrCall> { it.target.name.asString() == "plus" }
        }
    }

    @Test
    fun `Perform plus operation on Ptr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.nullptr
            class Test(val x: Int = 0) : Struct
            val test: Ptr<Test> = nullptr<Test>() + 42
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            containsChild<IrCall> { it.target.name.asString() == "plus" }
        }
    }

    @Test
    fun `Perform minus operation on NumPtr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: Ptr<Byte> = nullptr<Byte>() - 42
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            containsChild<IrCall> { it.target.name.asString() == "minus" }
        }
    }

    @Test
    fun `Perform minus operation on Ptr`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.nullptr
            class Test(val x: Int = 0) : Struct
            val test: Ptr<Test> = nullptr<Test>() - 42
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            containsChild<IrCall> { it.target.name.asString() == "minus" }
        }
    }

    @Test
    fun `Obtain pointer to local primitive variable`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (type in primitiveTypes) {
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.ref
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNFloat
                fun test() {
                    val x: $type = 100.to$type()
                    val ptr = x.ref()
                }
            """.trimIndent())
            // @formatter:on
            result irMatches {
                // TODO: implement me
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain pointer to local primitive variable in constructor`() = setupCompilerTest {
        kwireTransformerPipeline()
        default {
            compiler shouldNotReport { error() }
        }
        for (type in primitiveTypes) {
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.ref
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNFloat
                class Test {
                    init {
                        val x: $type = 100.to$type()
                        val ptr = x.ref()
                    }
                }
            """.trimIndent())
            // @formatter:on
            result irMatches {
                // TODO: implement this
            }
            evaluate()
        }
    }

    @Test
    fun `Obtain pointer to top level function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.ref
            fun foo(x: Float, y: Float): Int = 42
            val test: Ptr<CFn<(Float, Float) -> Int>> = ::foo.ref()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                containsChild<IrCall> { it.target.name.asString() == "getFloat" }
                containsChild<IrCall> { it.target.name.asString() == "putInt" }
                containsChild<IrCall> { it.target.name.asString() == "createUpcallStub" }
            }
        }
    }

    @Test
    fun `Obtain pointer to member function`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.Struct
            import dev.karmakrafts.kwire.ctype.ref
            class Foo(val x: Int = 0) : Struct {
                fun test(y: Float) = println("Hello, World!")
            }
            val foo: Foo = Foo()
            val test: Ptr<CFn<(Float) -> Unit>> = foo::test.ref()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                containsChild<IrCall> { it.target.name.asString() == "getFloat" }
                containsChild<IrCall> { it.target.name.asString() == "createUpcallStub" }
            }
        }
    }

    @Test
    fun `Invoke function pointer using direct invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            fun test() {
                val ptr = nullptr<CFn<(Int, Float) -> Double>>()
                val foo = ptr(42, 2F)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrVariable> { it.name.asString() == "foo" } matches {
                containsChild<IrCall> { it.target.name.asString() == "callDouble" }
                containsChild<IrCall> { it.target.name.asString() == "putInt" }
                containsChild<IrCall> { it.target.name.asString() == "putFloat" }
                containsChild<IrCall> { it.target.name.asString() == "acquire" }
                containsChild<IrCall> { it.target.name.asString() == "release" }
                containsChild<IrGetEnumValue> { it.symbol.owner.name.asString() == "CDECL" }
            }
        }
    }

    @Test
    fun `Invoke stdcall function pointer using direct invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            import dev.karmakrafts.kwire.ctype.StdCall
            typealias MyFuncPtr = @StdCall Ptr<CFn<(Int, Float) -> Double>>
            fun test() {          
                val ptr: MyFuncPtr = nullptr()
                val foo = ptr(42, 2F)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrVariable> { it.name.asString() == "foo" } matches {
                containsChild<IrCall> { it.target.name.asString() == "callDouble" }
                containsChild<IrCall> { it.target.name.asString() == "putInt" }
                containsChild<IrCall> { it.target.name.asString() == "putFloat" }
                containsChild<IrCall> { it.target.name.asString() == "acquire" }
                containsChild<IrCall> { it.target.name.asString() == "release" }
                containsChild<IrGetEnumValue> { it.symbol.owner.name.asString() == "STDCALL" }
            }
        }
    }

    @Test
    fun `Invoke function pointer using dynamic invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            fun test() {
                val args = arrayOf<Any>(42, 2F)
                val ptr = nullptr<CFn<(Int, Float, Int) -> Double>>()
                val foo = ptr(*args, 10)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrVariable> { it.name.asString() == "foo" } matches {
                containsChild<IrCall> { it.target.name.asString() == "callDouble" }
                containsChild<IrCall> { it.target.name.asString() == "putAll" }
                containsChild<IrCall> { it.target.name.asString() == "putInt" }
                containsChild<IrCall> { it.target.name.asString() == "acquire" }
                containsChild<IrCall> { it.target.name.asString() == "release" }
                containsChild<IrGetEnumValue> { it.symbol.owner.name.asString() == "CDECL" }
            }
        }
    }

    @Test
    fun `Invoke stdcall function pointer using dynamic invocation`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CFn
            import dev.karmakrafts.kwire.ctype.nullptr
            import dev.karmakrafts.kwire.ctype.invoke
            import dev.karmakrafts.kwire.ctype.StdCall
            typealias MyFuncPtr = @StdCall Ptr<CFn<(Int, Float, Int) -> Double>>
            fun test() {
                val args = arrayOf<Any>(42, 2F)
                val ptr: MyFuncPtr = nullptr()
                val foo = ptr(*args, 10)
            }
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrVariable> { it.name.asString() == "foo" } matches {
                containsChild<IrCall> { it.target.name.asString() == "callDouble" }
                containsChild<IrCall> { it.target.name.asString() == "putAll" }
                containsChild<IrCall> { it.target.name.asString() == "putInt" }
                containsChild<IrCall> { it.target.name.asString() == "acquire" }
                containsChild<IrCall> { it.target.name.asString() == "release" }
                containsChild<IrGetEnumValue> { it.symbol.owner.name.asString() == "STDCALL" }
            }
        }
    }

    @Test
    fun `Obtain nullptr of void`() = runCompilerTest {
        kwireTransformerPipeline()
        // @formatter:off
        source("""
            import dev.karmakrafts.kwire.ctype.Ptr
            import dev.karmakrafts.kwire.ctype.CVoid
            import dev.karmakrafts.kwire.ctype.nullptr
            val test: Ptr<CVoid> = nullptr()
        """.trimIndent())
        // @formatter:on
        compiler shouldNotReport { error() }
        result irMatches {
            getChild<IrProperty> { it.name.asString() == "test" } matches {
                val field = getChild<IrField>()
                val initializer = field.initializer?.expression
                initializer shouldNotBe null
                initializer!!::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.Ptr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                val test: Ptr<$type> = nullptr()
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrProperty> { it.name.asString() == "test" } matches {
                    val field = getChild<IrField>()
                    val initializer = field.initializer?.expression
                    initializer shouldNotBe null
                    initializer!!::class shouldBe IrCallImpl::class
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
                initializer!!::class shouldBe IrCallImpl::class
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
                import dev.karmakrafts.kwire.ctype.Ptr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val test: $type = nullptr<$type>().deref()
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
    fun `Dereference primitive pointer with index`() = setupCompilerTest {
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
                import dev.karmakrafts.kwire.ctype.Ptr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val test: $type = nullptr<$type>()[2]
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
        for (typeIndex in primitiveTypes.indices) {
            val type = primitiveTypes[typeIndex]
            val resolvedType = resolvedPrimitiveTypes[typeIndex]
            resetAssertions()
            // @formatter:off
            source("""
                import dev.karmakrafts.kwire.ctype.Ptr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val value: Ptr<$type> = nullptr<$type>()
                fun test() {
                    value.set(0.to$type())
                }
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrFunction> { it.name.asString() == "test" } matches {
                    containsChild<IrCall> { it.target.name.asString() == "write$resolvedType" }
                }
            }
            evaluate()
        }
    }

    @Test
    fun `Write to primitive pointer with index`() = setupCompilerTest {
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
                import dev.karmakrafts.kwire.ctype.Ptr
                import dev.karmakrafts.kwire.ctype.nullptr
                import dev.karmakrafts.kwire.ctype.NInt
                import dev.karmakrafts.kwire.ctype.NFloat
                import dev.karmakrafts.kwire.ctype.toNInt
                import dev.karmakrafts.kwire.ctype.toNFloat
                val value: Ptr<$type> = nullptr<$type>()
                fun test() {
                    value[2] = 0.to$type()
                }
            """.trimIndent())
            // @formatter:on
            result irMatches {
                getChild<IrFunction> { it.name.asString() == "test" } matches {
                    containsChild<IrCall> { it.target.name.asString() == "write$resolvedType" }
                }
            }
            evaluate()
        }
    }
}