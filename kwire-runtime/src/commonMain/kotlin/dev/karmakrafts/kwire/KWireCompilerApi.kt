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

package dev.karmakrafts.kwire

/**
 * Marker annotation to indicate runtime types and functions used by
 * the KWire compiler in generated code.
 * This is to warn about changing a runtime function without
 * adjusting its compiler counterpart, possibly causing code generation
 * errors otherwise.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPEALIAS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
internal annotation class KWireCompilerApi
