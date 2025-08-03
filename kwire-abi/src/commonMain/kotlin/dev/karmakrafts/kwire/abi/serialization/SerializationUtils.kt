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

package dev.karmakrafts.kwire.abi.serialization

import kotlinx.io.Buffer

inline fun <T> Buffer.writeOptional(value: T?, writer: (T, Buffer) -> Unit) {
    writeByte(if (value != null) 1 else 0)
    value?.let { writer(it, this) }
}

inline fun <T> Buffer.readOptional(reader: (Buffer) -> T): T? {
    return if (readByte() == 0.toByte()) null
    else reader(this)
}

inline fun <T> Buffer.writeList(values: List<T>, writer: (T, Buffer) -> Unit) {
    writeInt(values.size)
    values.forEach { writer(it, this) }
}

inline fun <T> Buffer.readList(reader: (Buffer) -> T): List<T> {
    return (0..<readInt()).map { reader(this) }
}