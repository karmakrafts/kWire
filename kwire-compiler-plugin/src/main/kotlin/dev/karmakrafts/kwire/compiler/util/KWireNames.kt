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

package dev.karmakrafts.kwire.compiler.util

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal object KWireNames {
    object Functions {
        val SIZE_BYTES: Name = Name.identifier("SIZE_BYTES")
        val sizeOf: Name = Name.identifier("sizeOf")
        val alignOf: Name = Name.identifier("alignOf")
        val plus: Name = Name.identifier("plus")
        val minus: Name = Name.identifier("minus")
        val times: Name = Name.identifier("times")
        val min: Name = Name.identifier("min")
        val max: Name = Name.identifier("max")
        val toNInt: Name = Name.identifier("toNInt")
        val toNUInt: Name = Name.identifier("toNUInt")
        val toNFloat: Name = Name.identifier("toNFloat")
        val get: Name = Name.identifier("get")
        val putAll: Name = Name.identifier("putAll")
        val push: Name = Name.identifier("push")
        val pop: Name = Name.identifier("pop")
        val allocate: Name = Name.identifier("allocate")
        val reallocate: Name = Name.identifier("reallocate")
        val free: Name = Name.identifier("free")
        val of: Name = Name.identifier("of")
        val listOf: Name = Name.identifier("listOf")
        val acquire: Name = Name.identifier("acquire")
        val release: Name = Name.identifier("release")
        val createUpcallStub: Name = Name.identifier("createUpcallStub")
        val open: Name = Name.identifier("open")
        val getFunctionAddress: Name = Name.identifier("getFunctionAddress")
    }

    object Kotlin {
        val packageName: FqName = FqName("kotlin")
        val mathPackageName: FqName = FqName("kotlin.math")
        val collectionsPackageName: FqName = FqName("kotlin.collections")

        val min: CallableId = CallableId(mathPackageName, Functions.min)
        val max: CallableId = CallableId(mathPackageName, Functions.max)
        val listOf: CallableId = CallableId(collectionsPackageName, Functions.listOf)

        object Byte {
            val name: Name = Name.identifier("Byte")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object Short {
            val name: Name = Name.identifier("Short")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object Int {
            val name: Name = Name.identifier("Int")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object Long {
            val name: Name = Name.identifier("Long")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object UByte {
            val name: Name = Name.identifier("UByte")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object UShort {
            val name: Name = Name.identifier("UShort")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object UInt {
            val name: Name = Name.identifier("UInt")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object ULong {
            val name: Name = Name.identifier("ULong")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object Float {
            val name: Name = Name.identifier("Float")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }

        object Double {
            val name: Name = Name.identifier("Double")
            val id: ClassId = ClassId(packageName, name)
            val fqName: FqName = id.asSingleFqName()
        }
    }

    val packageName: FqName = FqName("dev.karmakrafts.kwire")
    val memoryPackageName: FqName = FqName("${packageName.asString()}.memory")
    val ctypePackageName: FqName = FqName("${packageName.asString()}.ctype")
    val ffiPackageName: FqName = FqName("${packageName.asString()}.ffi")

    // ------------------------------ dev.karmakrafts.kwire

    object KWireIntrinsic {
        val name: Name = Name.identifier("KWireIntrinsic")
        val id: ClassId = ClassId(packageName, name)
        val fqName: FqName = id.asSingleFqName()

        object Type {
            val name: FqName = FqName("KWireIntrinsic.Type")
            val id: ClassId = ClassId(packageName, name, false)
        }
    }

    // ------------------------------ dev.karmakrafts.kwire.ctype

    object CTypePkg {
        val toNInt: CallableId = CallableId(ctypePackageName, Functions.toNInt)
        val toNUInt: CallableId = CallableId(ctypePackageName, Functions.toNUInt)
        val toNFloat: CallableId = CallableId(ctypePackageName, Functions.toNFloat)
    }

    object ValueType {
        val name: Name = Name.identifier("ValueType")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object CFn {
        val name: Name = Name.identifier("CFn")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object Const {
        val name: Name = Name.identifier("Const")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object InheritsConstness {
        val name: Name = Name.identifier("InheritsConstness")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object CDecl {
        val name: Name = Name.identifier("CDecl")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object ThisCall {
        val name: Name = Name.identifier("ThisCall")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object StdCall {
        val name: Name = Name.identifier("StdCall")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object FastCall {
        val name: Name = Name.identifier("FastCall")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object NInt {
        val name: Name = Name.identifier("NInt")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object NUInt {
        val name: Name = Name.identifier("NUInt")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object NFloat {
        val name: Name = Name.identifier("NFloat")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object Ptr {
        val name: Name = Name.identifier("Ptr")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val plus: CallableId = CallableId(id, Functions.plus)
        val minus: CallableId = CallableId(id, Functions.minus)

        object Companion {
            val name: FqName = FqName("Ptr.Companion")
            val id: ClassId = ClassId(ctypePackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val SIZE_BYTES: CallableId = CallableId(ctypePackageName, name, Functions.SIZE_BYTES)
        }
    }

    object Struct {
        val name: Name = Name.identifier("Struct")
        val id: ClassId = ClassId(ctypePackageName, name)
    }

    object StructLayout {
        val name: FqName = FqName("StructLayout")
        val id: ClassId = ClassId(ctypePackageName, name, false)
        val fqName: FqName = id.asSingleFqName()
    }

    object AlignAs {
        val name: FqName = FqName("AlignAs")
        val id: ClassId = ClassId(ctypePackageName, name, false)
        val fqName: FqName = id.asSingleFqName()
    }

    // ------------------------------ dev.karmakrafts.kwire.memory

    object MemoryPkg {
        val sizeOf: CallableId = CallableId(memoryPackageName, Functions.sizeOf)
        val alignOf: CallableId = CallableId(memoryPackageName, Functions.alignOf)
    }

    object Allocator {
        val name: Name = Name.identifier("Allocator")
        val id: ClassId = ClassId(memoryPackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val allocate: CallableId = CallableId(id, Functions.allocate)
        val reallocate: CallableId = CallableId(id, Functions.reallocate)
        val free: CallableId = CallableId(id, Functions.free)
    }

    object Memory {
        val name: Name = Name.identifier("Memory")
        val id: ClassId = ClassId(memoryPackageName, name)
        val fqName: FqName = id.asSingleFqName()

        object Companion {
            val name: FqName = FqName("Memory.Companion")
            val id: ClassId = ClassId(memoryPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
        }
    }

    object MemoryStack {
        val name: Name = Name.identifier("MemoryStack")
        val id: ClassId = ClassId(memoryPackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val push: CallableId = CallableId(id, Functions.push)
        val pop: CallableId = CallableId(id, Functions.pop)

        object Companion {
            val name: FqName = FqName("MemoryStack.Companion")
            val id: ClassId = ClassId(memoryPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val get: CallableId = CallableId(memoryPackageName, name, Functions.get)
        }
    }

    // ------------------------------ dev.karmakrafts.kwire.ffi

    object Marshal {
        val name: Name = Name.identifier("Marshal")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object SharedImport {
        val name: Name = Name.identifier("SharedImport")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object SharedLibrary {
        val name: Name = Name.identifier("SharedLibrary")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val getFunctionAddress: CallableId = CallableId(id, Functions.getFunctionAddress)

        object Companion {
            val name: FqName = FqName("SharedLibrary.Companion")
            val id: ClassId = ClassId(ffiPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val open: CallableId = CallableId(ffiPackageName, name, Functions.open)
        }
    }

    object CallingConvention {
        val name: Name = Name.identifier("CallingConvention")
        val id: ClassId = ClassId(ffiPackageName, name)
    }

    object FFI {
        val name: Name = Name.identifier("FFI")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val createUpcallStub: CallableId = CallableId(id, Functions.createUpcallStub)

        object Companion {
            val name: FqName = FqName("FFI.Companion")
            val id: ClassId = ClassId(ffiPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
        }
    }

    object FFIType {
        val name: Name = Name.identifier("FFIType")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object FFIDescriptor {
        val name: Name = Name.identifier("FFIDescriptor")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()

        object Companion {
            val name: FqName = FqName("FFIDescriptor.Companion")
            val id: ClassId = ClassId(ffiPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val of: CallableId = CallableId(ffiPackageName, name, Functions.of)
        }
    }

    object FFIArgBuffer {
        val name: Name = Name.identifier("FFIArgBuffer")
        val id: ClassId = ClassId(ffiPackageName, name)
        val fqName: FqName = id.asSingleFqName()
        val putAll: CallableId = CallableId(id, Functions.putAll)
        val release: CallableId = CallableId(id, Functions.release)

        object Companion {
            val name: FqName = FqName("FFIArgBuffer.Companion")
            val id: ClassId = ClassId(ffiPackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val acquire: CallableId = CallableId(ffiPackageName, name, Functions.acquire)
        }
    }
}