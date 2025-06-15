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
        val times: Name = Name.identifier("times")
        val min: Name = Name.identifier("min")
        val max: Name = Name.identifier("max")
    }

    object Kotlin {
        val packageName: FqName = FqName("kotlin")
        val mathPackageName: FqName = FqName("kotlin.math")

        val min: CallableId = CallableId(mathPackageName, Functions.min)
        val max: CallableId = CallableId(mathPackageName, Functions.max)

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
    val memoryPackageName: FqName = FqName("dev.karmakrafts.kwire.memory")
    val ctypePackageName: FqName = FqName("dev.karmakrafts.kwire.ctype")

    val sizeOf: CallableId = CallableId(memoryPackageName, Functions.sizeOf)
    val alignOf: CallableId = CallableId(memoryPackageName, Functions.alignOf)

    object KWireIntrinsic {
        val name: Name = Name.identifier("KWireIntrinsic")
        val id: ClassId = ClassId(packageName, name)
        val fqName: FqName = id.asSingleFqName()

        object Type {
            val name: FqName = FqName("KWireIntrinsic.Type")
            val id: ClassId = ClassId(packageName, name, false)
        }
    }

    object Address {
        val name: Name = Name.identifier("Address")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()

        object Companion {
            val name: FqName = FqName("Address.Companion")
            val id: ClassId = ClassId(ctypePackageName, name, false)
            val fqName: FqName = id.asSingleFqName()
            val SIZE_BYTES: CallableId = CallableId(ctypePackageName, name, Functions.SIZE_BYTES)
        }
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

    object NumPtr {
        val name: Name = Name.identifier("NumPtr")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
    }

    object Ptr {
        val name: Name = Name.identifier("Ptr")
        val id: ClassId = ClassId(ctypePackageName, name)
        val fqName: FqName = id.asSingleFqName()
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

    object Marshal {
        val name: Name = Name.identifier("Marshal")
        val id: ClassId = ClassId(packageName, name)
    }

    object SharedImport {
        val name: Name = Name.identifier("SharedImport")
        val id: ClassId = ClassId(packageName, name)
    }
}