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

@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("Memory$")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmName

@PublishedApi
internal expect fun getPlatformMemory(): Memory

// TODO: document this
interface Memory {
    companion object : Memory by getPlatformMemory()

    val defaultAlignment: NUInt

    fun allocate(size: NUInt, alignment: NUInt = defaultAlignment): Pointer
    fun reallocate(address: Pointer, size: NUInt, alignment: NUInt = defaultAlignment): Pointer
    fun free(address: Pointer)
    fun set(address: Pointer, value: Byte, size: NUInt)
    fun copy(source: Pointer, dest: Pointer, size: NUInt)
    fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt)
    fun compare(first: Pointer, second: Pointer, size: NUInt): Int

    fun readByte(address: Pointer): Byte
    fun readShort(address: Pointer): Short
    fun readInt(address: Pointer): Int
    fun readLong(address: Pointer): Long
    fun readNInt(address: Pointer): NInt
    fun readFloat(address: Pointer): Float
    fun readDouble(address: Pointer): Double
    fun readBytes(address: Pointer, size: Int): ByteArray

    fun writeByte(address: Pointer, value: Byte)
    fun writeShort(address: Pointer, value: Short)
    fun writeInt(address: Pointer, value: Int)
    fun writeLong(address: Pointer, value: Long)
    fun writeNInt(address: Pointer, value: NInt)
    fun writeFloat(address: Pointer, value: Float)
    fun writeDouble(address: Pointer, value: Double)
    fun writeBytes(address: Pointer, data: ByteArray)
}

inline fun Memory.zero(address: Pointer, size: NUInt) = set(address, 0, size)

inline fun Memory.readUByte(address: Pointer): UByte = readByte(address).toUByte()
inline fun Memory.readUShort(address: Pointer): UShort = readShort(address).toUShort()
inline fun Memory.readUInt(address: Pointer): UInt = readInt(address).toUInt()
inline fun Memory.readULong(address: Pointer): ULong = readLong(address).toULong()
inline fun Memory.readNUInt(address: Pointer): NUInt = readNInt(address).toUnsigned()
inline fun Memory.readPointer(address: Pointer): Pointer = Pointer(readNUInt(address))

inline fun Memory.writeUByte(address: Pointer, value: UByte) = writeByte(address, value.toByte())
inline fun Memory.writeUShort(address: Pointer, value: UShort) = writeShort(address, value.toShort())
inline fun Memory.writeUInt(address: Pointer, value: UInt) = writeInt(address, value.toInt())
inline fun Memory.writeULong(address: Pointer, value: ULong) = writeLong(address, value.toLong())
inline fun Memory.writeNUInt(address: Pointer, value: NUInt) = writeNInt(address, value.toSigned())
inline fun Memory.writePointer(address: Pointer, value: Pointer) = writeNUInt(address, value.value)