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
 * A marker interface for types that can be pointed to in the C type system.
 *
 * This interface serves as the base for all types that can be referenced by pointers
 * in the KWire FFI system. It is extended by [Address], which is the base interface
 * for all pointer types, and is used as a type parameter constraint in [Ptr].
 *
 * Implementations of this interface represent C-compatible types that can be
 * allocated in memory and accessed via pointers.
 */
@KWireCompilerApi
sealed interface Pointed
