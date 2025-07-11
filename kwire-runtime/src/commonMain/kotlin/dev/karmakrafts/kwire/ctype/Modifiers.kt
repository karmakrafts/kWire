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
 * A marker annotation for pointer types.
 *
 * This annotation may be applied to [FunPtr], [NumPtr] or [Ptr] types
 * to indicate that whatever they are pointing to may not be mutated.
 *  - For [FunPtr], constness only exists for static analysis purposes and to allow smooth interop
 *  - For [NumPtr], constness restrictions apply to any mutating operation like [NumPtr.set] or
 *    other member functions of [NumPtr] which are not annotated with [Const]
 *  - For [Ptr], constness restrictions apply to any mutating operation like [Ptr.set] or
 *    other member functions of [Ptr] which are not annotated with [Const]
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class Const

/**
 * Marker annotation for member functions of subtypes of [Pointed]
 * which permit calls when the aforementioned type is marked with [Const].
 * This also includes extension receivers of that type.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class ConstCallable

/**
 * Marker annotation for types which may be used within
 * subtypes [Pointed]. This also includes extension receivers
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