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

package dev.karmakrafts.kwire.meta

import dev.karmakrafts.kwire.ctype.CFn
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.Struct

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