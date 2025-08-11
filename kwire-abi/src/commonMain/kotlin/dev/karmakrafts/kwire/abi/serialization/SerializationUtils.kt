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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal fun <T : BinarySerializable> Buffer.writeOptional(value: T?) {
    writeByte(if (value != null) 1 else 0)
    value?.serialize(this)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> Buffer.writeOptional(value: T?, writer: (T, Buffer) -> Unit) {
    contract {
        callsInPlace(writer, InvocationKind.AT_MOST_ONCE)
    }
    writeByte(if (value != null) 1 else 0)
    value?.let { writer(it, this) }
}

internal fun <T> Buffer.readOptional(deserializer: BinaryDeserializer<T>): T? {
    return if (readByte() == 0.toByte()) null
    else deserializer.deserialize(this)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> Buffer.readOptional(reader: (Buffer) -> T): T? {
    contract {
        callsInPlace(reader, InvocationKind.AT_MOST_ONCE)
    }
    return if (readByte() == 0.toByte()) null
    else reader(this)
}

internal fun <T : BinarySerializable> Buffer.writeList(values: List<T>) {
    writeInt(values.size)
    values.forEach { it.serialize(this) }
}

internal inline fun <T> Buffer.writeList(values: List<T>, writer: (T, Buffer) -> Unit) {
    writeInt(values.size)
    values.forEach { writer(it, this) }
}

internal fun <T> Buffer.readList(deserializer: BinaryDeserializer<T>): List<T> {
    val listSize = readInt()
    if (listSize == 0) return emptyList()
    return (0..<listSize).map { deserializer.deserialize(this) }
}

internal inline fun <T> Buffer.readList(reader: (Buffer) -> T): List<T> {
    val listSize = readInt()
    if (listSize == 0) return emptyList()
    return (0..<listSize).map { reader(this) }
}