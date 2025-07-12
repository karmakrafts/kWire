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
 * Used as a constraint on generic parameters to denote that the
 * given type has to be one of the following:
 *  - [Byte], [Short], [Int], [Long] or [NInt]
 *  - [UByte], [UShort], [UInt], [ULong] or [NUInt]
 *  - [Float], [Double] or [NFloat]
 *  - [Char]
 *  - [CVoid], [CFn] or [Ptr]
 *  - any subtype of [Struct]
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE_PARAMETER)
annotation class ValueType

/**
 * A marker annotation for pointer types.
 *
 * This annotation may be applied to [Ptr] types
 * to indicate that whatever they are pointing to may not be mutated.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class Const

/**
 * Marker annotation for member functions of value types
 * which permit calls when the aforementioned type is marked with [Const].
 * This also includes extension receivers of that type.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class ConstCallable

/**
 * Marker annotation for types which may be used within
 * value types. This also includes extension receivers
 * of that type.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class InheritsConstness

/**
 * Marker annotation for functions which are or use const discard semantics.
 */
@RequiresOptIn("Discarding constness on a pointer requires explicit opt-in as it may cause undefined behaviour if used wrong")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class DiscardsConstness

// Calling convention modifiers applicable to FunPtr<*> types

/**
 * A type marker annotation to indicate a function pointer
 * uses the cdecl calling convention.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class CDecl

/**
 * A type marker annotation to indicate a function pointer
 * uses the thiscall calling convention.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class ThisCall

/**
 * A type marker annotation to indicate a function pointer
 * uses the stdcall calling convention.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class StdCall

/**
 * A type marker annotation to indicate a function pointer
 * uses the fastcall calling convention.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class FastCall