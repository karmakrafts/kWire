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

@file:Suppress("NOTHING_TO_INLINE") @file:OptIn(ExperimentalUnsignedTypes::class)

package dev.karmakrafts.kwire

import kotlin.native.concurrent.ThreadLocal

@PublishedApi
@ThreadLocal
internal val argBuffer: FFIArgBufferImpl = FFIArgBufferImpl()

// TODO: document this
interface FFIArgBuffer {
    companion object {
        const val DEFAULT_SIZE: Int = 4096

        inline fun get(): FFIArgBuffer = argBuffer.clear()
    }

    val address: Pointer
    val types: List<FFIType>

    fun putByte(value: Byte)
    fun putShort(value: Short)
    fun putInt(value: Int)
    fun putLong(value: Long)
    fun putNInt(value: NInt)
    fun putFloat(value: Float)
    fun putDouble(value: Double)

    fun putBytes(value: ByteArray)
    fun putShorts(value: ShortArray)
    fun putInts(value: IntArray)
    fun putLongs(value: LongArray)
    fun putNInts(value: NIntArray)
    fun putFloats(value: FloatArray)
    fun putDoubles(value: DoubleArray)
}

inline fun FFIArgBuffer.putUByte(value: UByte) = putByte(value.toByte())
inline fun FFIArgBuffer.putUShort(value: UShort) = putShort(value.toShort())
inline fun FFIArgBuffer.putUInt(value: UInt) = putInt(value.toInt())
inline fun FFIArgBuffer.putULong(value: ULong) = putLong(value.toLong())
inline fun FFIArgBuffer.putNUInt(value: NUInt) = putNInt(value.toSigned())

inline fun FFIArgBuffer.putPointer(value: Pointer) = putNUInt(value.value)

inline fun FFIArgBuffer.putUBytes(value: UByteArray) = putBytes(value.asByteArray())
inline fun FFIArgBuffer.putUShorts(value: UShortArray) = putShorts(value.asShortArray())
inline fun FFIArgBuffer.putUInts(value: UIntArray) = putInts(value.asIntArray())
inline fun FFIArgBuffer.putULongs(value: ULongArray) = putLongs(value.asLongArray())
inline fun FFIArgBuffer.putNUInts(value: NUIntArray) = putNInts(value.asNIntArray())

@PublishedApi
internal class FFIArgBufferImpl internal constructor() : FFIArgBuffer, AutoCloseable {
    override val address: Pointer = Memory.allocate(FFIArgBuffer.DEFAULT_SIZE.toNUInt())
    private var offset: NUInt = 0U.toNUInt()
    override val types: ArrayList<FFIType> = ArrayList()

    init {
        ShutdownHandler.register(this)
    }

    private fun nextOffset(type: FFIType, count: Int = 1): NUInt {
        val newOffset = offset + (type.size * count).toNUInt()
        check(newOffset < FFIArgBuffer.DEFAULT_SIZE.toNUInt()) { "Exceeded argument buffer limit" }
        val oldOffset = offset
        offset = newOffset
        types += type
        return oldOffset
    }

    private inline fun nextAddress(type: FFIType, count: Int = 1): Pointer = address + nextOffset(type, count)

    override fun putByte(value: Byte) = Memory.writeByte(nextAddress(FFIType.BYTE), value)
    override fun putShort(value: Short) = Memory.writeShort(nextAddress(FFIType.SHORT), value)
    override fun putInt(value: Int) = Memory.writeInt(nextAddress(FFIType.INT), value)
    override fun putLong(value: Long) = Memory.writeLong(nextAddress(FFIType.LONG), value)
    override fun putNInt(value: NInt) = Memory.writeNInt(nextAddress(FFIType.NINT), value)
    override fun putFloat(value: Float) = Memory.writeFloat(nextAddress(FFIType.FLOAT), value)
    override fun putDouble(value: Double) = Memory.writeDouble(nextAddress(FFIType.DOUBLE), value)

    override fun putBytes(value: ByteArray) = Memory.writeBytes(nextAddress(FFIType.BYTE, value.size), value)
    override fun putShorts(value: ShortArray) = Memory.writeShorts(nextAddress(FFIType.SHORT, value.size), value)
    override fun putInts(value: IntArray) = Memory.writeInts(nextAddress(FFIType.INT, value.size), value)
    override fun putLongs(value: LongArray) = Memory.writeLongs(nextAddress(FFIType.LONG, value.size), value)
    override fun putNInts(value: NIntArray) = Memory.writeNInts(nextAddress(FFIType.NINT, value.size), value)
    override fun putFloats(value: FloatArray) = Memory.writeFloats(nextAddress(FFIType.FLOAT, value.size), value)
    override fun putDoubles(value: DoubleArray) = Memory.writeDoubles(nextAddress(FFIType.DOUBLE, value.size), value)

    @PublishedApi
    internal fun clear(): FFIArgBufferImpl {
        if (offset == 0U.toNUInt()) return this
        offset = 0U.toNUInt()
        types.clear()
        return this
    }

    override fun close() {
        Memory.free(address)
        types.clear()
    }
}

// TODO: document this
typealias FFIArgSpec = FFIArgBuffer.() -> Unit