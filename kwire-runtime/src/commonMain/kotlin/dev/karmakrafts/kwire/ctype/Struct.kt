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

package dev.karmakrafts.kwire.ctype

import dev.karmakrafts.kwire.KWireCompilerApi

/**
 * Represents a C struct in the KWire FFI system.
 * 
 * This interface serves as a marker for classes that represent C struct types.
 * Structs are composite data types that can contain multiple fields of different types.
 * They extend [Pointed], meaning they can be referenced by pointers in the C type system.
 */
@KWireCompilerApi
interface Struct : Pointed

/**
 * Specifies a custom alignment for a struct type.
 * 
 * This annotation can be applied to classes implementing [Struct] to override
 * the default memory alignment that would be calculated by the compiler.
 * 
 * @property alignment The alignment value in bytes. Must be a power of 2.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class AlignAs(val alignment: Int)

@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class StructLayout(val data: ByteArray)
