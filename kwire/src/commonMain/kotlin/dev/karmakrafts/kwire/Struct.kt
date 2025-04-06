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

data class StructField(
    val type: FFIType
) {
    var offset: NUInt = 0U.toNUInt()
        internal set
}

@Suppress("NOTHING_TO_INLINE")
class Struct private constructor(
    val address: Pointer,
    val fields: LinkedHashMap<String, StructField>
) : AutoCloseable {
    companion object {
        fun allocate(fields: LinkedHashMap<String, FFIType>): Struct {
            return Struct(
                Memory.allocate(fields.values.sumOf { it.size }.toNUInt()),
                fields.map { it.key to StructField(it.value) }.toMap(LinkedHashMap())
            )
        }
    }

    init {
        // Pre-compute all field offsets
        val fieldList = fields.values
        for (field in fieldList) {
            val index = fieldList.indexOf(field)
            field.offset = fieldList.take(index).sumOf { it.type.size }.toNUInt()
        }
    }

    fun getFieldOffset(name: String): NUInt {
        return requireNotNull(fields[name]) {
            "Struct field '$name' does not exist"
        }.offset
    }

    inline fun getFieldAddress(name: String): Pointer = address + getFieldOffset(name)

    inline fun getByte(name: String): Byte = Memory.readByte(getFieldAddress(name))
    inline fun getShort(name: String): Short = Memory.readShort(getFieldAddress(name))
    inline fun getInt(name: String): Int = Memory.readInt(getFieldAddress(name))
    inline fun getLong(name: String): Long = Memory.readLong(getFieldAddress(name))
    inline fun getNInt(name: String): NInt = Memory.readNInt(getFieldAddress(name))
    inline fun getUByte(name: String): UByte = Memory.readUByte(getFieldAddress(name))
    inline fun getUShort(name: String): UShort = Memory.readUShort(getFieldAddress(name))
    inline fun getUInt(name: String): UInt = Memory.readUInt(getFieldAddress(name))
    inline fun getULong(name: String): ULong = Memory.readULong(getFieldAddress(name))
    inline fun getNUInt(name: String): NUInt = Memory.readNUInt(getFieldAddress(name))
    inline fun getFloat(name: String): Float = Memory.readFloat(getFieldAddress(name))
    inline fun getDouble(name: String): Double = Memory.readDouble(getFieldAddress(name))
    inline fun getPointer(name: String): Pointer = Memory.readPointer(getFieldAddress(name))

    inline fun setByte(name: String, value: Byte) = Memory.writeByte(getFieldAddress(name), value)
    inline fun setShort(name: String, value: Short) = Memory.writeShort(getFieldAddress(name), value)
    inline fun setInt(name: String, value: Int) = Memory.writeInt(getFieldAddress(name), value)
    inline fun setLong(name: String, value: Long) = Memory.writeLong(getFieldAddress(name), value)
    inline fun setNInt(name: String, value: NInt) = Memory.writeNInt(getFieldAddress(name), value)
    inline fun setUByte(name: String, value: UByte) = Memory.writeUByte(getFieldAddress(name), value)
    inline fun setUShort(name: String, value: UShort) = Memory.writeUShort(getFieldAddress(name), value)
    inline fun setUInt(name: String, value: UInt) = Memory.writeUInt(getFieldAddress(name), value)
    inline fun setULong(name: String, value: ULong) = Memory.writeULong(getFieldAddress(name), value)
    inline fun setNUInt(name: String, value: NUInt) = Memory.writeNUInt(getFieldAddress(name), value)
    inline fun setFloat(name: String, value: Float) = Memory.writeFloat(getFieldAddress(name), value)
    inline fun setDouble(name: String, value: Double) = Memory.writeDouble(getFieldAddress(name), value)
    inline fun setPointer(name: String, value: Pointer) = Memory.writePointer(getFieldAddress(name), value)

    override fun close() {
        Memory.free(address)
    }
}