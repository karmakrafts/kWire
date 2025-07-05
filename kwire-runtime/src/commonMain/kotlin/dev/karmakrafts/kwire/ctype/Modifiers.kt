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
 * A marker annotation which, when applied to a type
 * which represents a pointer, omits readbacks from unmanaged memory to
 * the source (variable or field) of the pointer reference.
 */
@KWireCompilerApi
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE)
annotation class Const

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