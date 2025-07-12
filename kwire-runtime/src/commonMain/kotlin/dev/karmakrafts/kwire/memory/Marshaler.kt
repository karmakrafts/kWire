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

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.ConstCallable
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.toNUInt

object Marshaler {
    // Utils

    @Suppress("NOTHING_TO_INLINE")
    inline fun lengthUtf8(address: @Const Ptr<Byte>): NUInt = Memory.strlen(address)

    fun lengthUtf16(address: @Const Ptr<Short>): NUInt {
        var index = 0.toNUInt()
        while (Memory.readShort(address + (index * Short.SIZE_BYTES.toNUInt())) != 0.toShort()) {
            ++index
        }
        return index
    }

    // Kotlin -> Native conversions

    fun Allocator.stringUtf8(value: String): Ptr<Byte> {
        val encodedBytes = value.encodeToByteArray()
        val length = encodedBytes.size.toNUInt()
        val address = allocate(length + 1U.toNUInt())
        Memory.writeByte(address + length, 0) // Write null-terminator
        encodedBytes.fixed { srcAddress ->
            Memory.copy(srcAddress, address, length)
        }
        return address.reinterpret()
    }

    fun Allocator.stringsUtf8(vararg values: String): Ptr<Ptr<Byte>> {
        return pointers(*values.map { stringUtf8(it) }.toTypedArray())
    }

    fun Allocator.stringsUtf8(values: List<String>): Ptr<Ptr<Byte>> {
        return pointers(*values.map { stringUtf8(it) }.toTypedArray())
    }

    fun Allocator.stringUtf16(value: String): Ptr<Short> {
        val length = value.length.toNUInt()
        val address = allocate((length + 1U.toNUInt()) * Short.SIZE_BYTES.toNUInt())
        Memory.writeShort(address + length, 0) // Write null-terminator
        value.toCharArray().fixed { srcAddress ->
            Memory.copy(srcAddress, address, length * Short.SIZE_BYTES.toNUInt())
        }
        return address.reinterpret()
    }

    fun Allocator.stringsUtf16(vararg values: String): Ptr<Ptr<Short>> {
        return pointers(*values.map { stringUtf16(it) }.toTypedArray())
    }

    fun Allocator.stringsUtf16(values: List<String>): Ptr<Ptr<Short>> {
        return pointers(*values.map { stringUtf16(it) }.toTypedArray())
    }

    // Native -> Kotlin conversions

    @ConstCallable
    fun Ptr<Byte>.toKStringFromUtf8(): String {
        val length = lengthUtf8(this)
        val data = ByteArray(length.toInt())
        data.fixed { dstAddress ->
            Memory.copy(this, dstAddress, length) // We don't need to copy NT
        }
        return data.decodeToString()
    }

    @ConstCallable
    fun Ptr<Short>.toKStringFromUtf16(): String {
        val length = lengthUtf16(this)
        val data = CharArray(length.toInt())
        data.fixed { dstAddress ->
            Memory.copy(this, dstAddress, length * Short.SIZE_BYTES.toNUInt()) // We don't need to copy NT
        }
        return data.concatToString()
    }
}