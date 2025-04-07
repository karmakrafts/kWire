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

@ConsistentCopyVisibility
data class StructField internal constructor(
    val type: FFIType
) {
    var offset: NUInt = 0U.toNUInt()
        internal set
}

@OptIn(ExperimentalUnsignedTypes::class)
@Suppress("NOTHING_TO_INLINE", "WRONG_MODIFIER_TARGET")
class Struct private constructor(
    val address: Pointer, val fields: List<StructField>
) : AutoCloseable {
    constructor(fieldTypes: List<FFIType>) : this(
        Memory.allocate(fieldTypes.sumOf { it.size }.toNUInt()),
        fieldTypes.map { StructField(it) })

    inline constructor(vararg fieldTypes: FFIType) : this(fieldTypes.toList())

    init {
        // Pre-compute all field offsets
        for (field in fields) {
            val index = fields.indexOf(field)
            field.offset = fields.asSequence().take(index).sumOf { it.type.size }.toNUInt()
        }
    }

    fun getFieldOffset(index: Int): NUInt {
        return requireNotNull(fields.getOrNull(index)) {
            "Struct field $index does not exist"
        }.offset
    }

    inline fun getFieldAddress(index: Int): Pointer = address + getFieldOffset(index)

    inline fun getByte(index: Int): Byte = Memory.readByte(getFieldAddress(index))
    inline fun getShort(index: Int): Short = Memory.readShort(getFieldAddress(index))
    inline fun getInt(index: Int): Int = Memory.readInt(getFieldAddress(index))
    inline fun getLong(index: Int): Long = Memory.readLong(getFieldAddress(index))
    inline fun getNInt(index: Int): NInt = Memory.readNInt(getFieldAddress(index))
    inline fun getUByte(index: Int): UByte = Memory.readUByte(getFieldAddress(index))
    inline fun getUShort(index: Int): UShort = Memory.readUShort(getFieldAddress(index))
    inline fun getUInt(index: Int): UInt = Memory.readUInt(getFieldAddress(index))
    inline fun getULong(index: Int): ULong = Memory.readULong(getFieldAddress(index))
    inline fun getNUInt(index: Int): NUInt = Memory.readNUInt(getFieldAddress(index))
    inline fun getFloat(index: Int): Float = Memory.readFloat(getFieldAddress(index))
    inline fun getDouble(index: Int): Double = Memory.readDouble(getFieldAddress(index))
    inline fun getPointer(index: Int): Pointer = Memory.readPointer(getFieldAddress(index))

    inline fun getBytes(index: Int, size: Int): ByteArray = Memory.readBytes(getFieldAddress(index), size)
    inline fun getShorts(index: Int, size: Int): ShortArray = Memory.readShorts(getFieldAddress(index), size)
    inline fun getInts(index: Int, size: Int): IntArray = Memory.readInts(getFieldAddress(index), size)
    inline fun getLongs(index: Int, size: Int): LongArray = Memory.readLongs(getFieldAddress(index), size)
    inline fun getNInts(index: Int, size: Int): NIntArray = Memory.readNInts(getFieldAddress(index), size)
    inline fun getUBytes(index: Int, size: Int): UByteArray = Memory.readUBytes(getFieldAddress(index), size)
    inline fun getUShorts(index: Int, size: Int): UShortArray = Memory.readUShorts(getFieldAddress(index), size)
    inline fun getUInts(index: Int, size: Int): UIntArray = Memory.readUInts(getFieldAddress(index), size)
    inline fun getULongs(index: Int, size: Int): ULongArray = Memory.readULongs(getFieldAddress(index), size)
    inline fun getNUInts(index: Int, size: Int): NUIntArray = Memory.readNUInts(getFieldAddress(index), size)
    inline fun getFloats(index: Int, size: Int): FloatArray = Memory.readFloats(getFieldAddress(index), size)
    inline fun getDoubles(index: Int, size: Int): DoubleArray = Memory.readDoubles(getFieldAddress(index), size)

    inline fun setByte(index: Int, value: Byte) = Memory.writeByte(getFieldAddress(index), value)
    inline fun setShort(index: Int, value: Short) = Memory.writeShort(getFieldAddress(index), value)
    inline fun setInt(index: Int, value: Int) = Memory.writeInt(getFieldAddress(index), value)
    inline fun setLong(index: Int, value: Long) = Memory.writeLong(getFieldAddress(index), value)
    inline fun setNInt(index: Int, value: NInt) = Memory.writeNInt(getFieldAddress(index), value)
    inline fun setUByte(index: Int, value: UByte) = Memory.writeUByte(getFieldAddress(index), value)
    inline fun setUShort(index: Int, value: UShort) = Memory.writeUShort(getFieldAddress(index), value)
    inline fun setUInt(index: Int, value: UInt) = Memory.writeUInt(getFieldAddress(index), value)
    inline fun setULong(index: Int, value: ULong) = Memory.writeULong(getFieldAddress(index), value)
    inline fun setNUInt(index: Int, value: NUInt) = Memory.writeNUInt(getFieldAddress(index), value)
    inline fun setFloat(index: Int, value: Float) = Memory.writeFloat(getFieldAddress(index), value)
    inline fun setDouble(index: Int, value: Double) = Memory.writeDouble(getFieldAddress(index), value)
    inline fun setPointer(index: Int, value: Pointer) = Memory.writePointer(getFieldAddress(index), value)

    inline fun setBytes(index: Int, data: ByteArray) = Memory.writeBytes(getFieldAddress(index), data)
    inline fun setShorts(index: Int, data: ShortArray) = Memory.writeShorts(getFieldAddress(index), data)
    inline fun setInts(index: Int, data: IntArray) = Memory.writeInts(getFieldAddress(index), data)
    inline fun setLongs(index: Int, data: LongArray) = Memory.writeLongs(getFieldAddress(index), data)
    inline fun setNInts(index: Int, data: NIntArray) = Memory.writeNInts(getFieldAddress(index), data)
    inline fun setUBytes(index: Int, data: UByteArray) = Memory.writeUBytes(getFieldAddress(index), data)
    inline fun setUShorts(index: Int, data: UShortArray) = Memory.writeUShorts(getFieldAddress(index), data)
    inline fun setUInts(index: Int, data: UIntArray) = Memory.writeUInts(getFieldAddress(index), data)
    inline fun setULongs(index: Int, data: ULongArray) = Memory.writeULongs(getFieldAddress(index), data)
    inline fun setNUInts(index: Int, data: NUIntArray) = Memory.writeNUInts(getFieldAddress(index), data)
    inline fun setFloats(index: Int, data: FloatArray) = Memory.writeFloats(getFieldAddress(index), data)
    inline fun setDoubles(index: Int, data: DoubleArray) = Memory.writeDoubles(getFieldAddress(index), data)

    override fun close() {
        Memory.free(address)
    }
}
