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

fun FFIArgBuffer.toArray(): Array<Any> = Array(types.size) { index ->
    when (val type = types[index]) {
        FFIType.BYTE -> address.asBytePtr()[0]
        FFIType.SHORT -> address.asShortPtr()[0]
        FFIType.INT -> address.asIntPtr()[0]
        FFIType.LONG -> address.asLongPtr()[0]
        FFIType.NINT -> address.asNIntPtr()[0]
        FFIType.FLOAT -> address.asFloatPtr()[0]
        FFIType.DOUBLE -> address.asDoublePtr()[0]
        else -> throw IllegalStateException("Cannot map FFI parameter type $type")
    }
}