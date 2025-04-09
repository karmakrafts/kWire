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

/**
 * Represents a field in a C-style struct.
 *
 * This class holds information about a field in a struct, including its type and
 * memory offset within the struct. The offset is calculated based on the field's
 * position in the struct and the sizes of preceding fields.
 *
 * @property type The FFI type of this field
 * @property offset The memory offset of this field within the struct, initialized to 0
 */
@ConsistentCopyVisibility
data class StructField internal constructor(
    val type: FFIType
) {
    var offset: NUInt = 0U.toNUInt()
        internal set
}

/**
 * Represents a C-style struct in memory.
 *
 * This class provides a way to work with C-style structs in Kotlin. It allocates memory
 * for the struct, manages field offsets, and provides methods for reading and writing
 * various data types at specific field indices. The struct's memory is automatically
 * freed when the instance is closed.
 *
 * @property address The memory address of the struct
 * @property fields The list of fields in the struct
 */
@OptIn(ExperimentalUnsignedTypes::class)
@Suppress("NOTHING_TO_INLINE")
class Struct private constructor( // @formatter:off
    val address: Pointer,
    val fields: List<StructField>
) : AutoCloseable { // @formatter:on
    /**
     * Constructs a struct with the specified field types.
     *
     * This constructor allocates memory for the struct based on the total size of all fields
     * and creates a StructField for each type in the list.
     *
     * @param fieldTypes A list of FFI types representing the fields of the struct
     */
    constructor(fieldTypes: List<FFIType>) : this(
        Memory.allocate(fieldTypes.sumOf { it.size }.toNUInt()),
        fieldTypes.map { StructField(it) })

    /**
     * Constructs a struct with the specified field types.
     *
     * This is a convenience constructor that converts the vararg parameter to a list
     * and calls the list-based constructor.
     *
     * @param fieldTypes Variable number of FFI types representing the fields of the struct
     */
    constructor(vararg fieldTypes: FFIType) : this(fieldTypes.toList())

    init {
        // Pre-compute all field offsets
        for (field in fields) {
            val index = fields.indexOf(field)
            field.offset = fields.asSequence().take(index).sumOf { it.type.size }.toNUInt()
        }
    }

    /**
     * Gets the memory offset of a field in the struct.
     *
     * @param index The index of the field
     * @return The memory offset of the field
     * @throws IllegalArgumentException if the field index does not exist
     */
    fun getFieldOffset(index: Int): NUInt {
        return requireNotNull(fields.getOrNull(index)) {
            "Struct field $index does not exist"
        }.offset
    }

    /**
     * Gets the memory address of a field in the struct.
     *
     * @param index The index of the field
     * @return The memory address of the field
     */
    inline fun getFieldAddress(index: Int): Pointer = address + getFieldOffset(index)

    /**
     * Gets the byte value of a field in the struct.
     *
     * @param index The index of the field
     * @return The byte value of the field
     */
    inline fun getByte(index: Int): Byte = Memory.readByte(getFieldAddress(index))

    /**
     * Gets the short value of a field in the struct.
     *
     * @param index The index of the field
     * @return The short value of the field
     */
    inline fun getShort(index: Int): Short = Memory.readShort(getFieldAddress(index))

    /**
     * Gets the int value of a field in the struct.
     *
     * @param index The index of the field
     * @return The int value of the field
     */
    inline fun getInt(index: Int): Int = Memory.readInt(getFieldAddress(index))

    /**
     * Gets the long value of a field in the struct.
     *
     * @param index The index of the field
     * @return The long value of the field
     */
    inline fun getLong(index: Int): Long = Memory.readLong(getFieldAddress(index))

    /**
     * Gets the native integer value of a field in the struct.
     *
     * @param index The index of the field
     * @return The native integer value of the field
     */
    inline fun getNInt(index: Int): NInt = Memory.readNInt(getFieldAddress(index))

    /**
     * Gets the unsigned byte value of a field in the struct.
     *
     * @param index The index of the field
     * @return The unsigned byte value of the field
     */
    inline fun getUByte(index: Int): UByte = Memory.readUByte(getFieldAddress(index))

    /**
     * Gets the unsigned short value of a field in the struct.
     *
     * @param index The index of the field
     * @return The unsigned short value of the field
     */
    inline fun getUShort(index: Int): UShort = Memory.readUShort(getFieldAddress(index))

    /**
     * Gets the unsigned int value of a field in the struct.
     *
     * @param index The index of the field
     * @return The unsigned int value of the field
     */
    inline fun getUInt(index: Int): UInt = Memory.readUInt(getFieldAddress(index))

    /**
     * Gets the unsigned long value of a field in the struct.
     *
     * @param index The index of the field
     * @return The unsigned long value of the field
     */
    inline fun getULong(index: Int): ULong = Memory.readULong(getFieldAddress(index))

    /**
     * Gets the native unsigned integer value of a field in the struct.
     *
     * @param index The index of the field
     * @return The native unsigned integer value of the field
     */
    inline fun getNUInt(index: Int): NUInt = Memory.readNUInt(getFieldAddress(index))

    /**
     * Gets the float value of a field in the struct.
     *
     * @param index The index of the field
     * @return The float value of the field
     */
    inline fun getFloat(index: Int): Float = Memory.readFloat(getFieldAddress(index))

    /**
     * Gets the double value of a field in the struct.
     *
     * @param index The index of the field
     * @return The double value of the field
     */
    inline fun getDouble(index: Int): Double = Memory.readDouble(getFieldAddress(index))

    /**
     * Gets the pointer value of a field in the struct.
     *
     * @param index The index of the field
     * @return The pointer value of the field
     */
    inline fun getPointer(index: Int): Pointer = Memory.readPointer(getFieldAddress(index))

    /**
     * Gets an array of bytes from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of bytes to read
     * @return The array of bytes read from the field
     */
    inline fun getBytes(index: Int, size: Int): ByteArray = Memory.readBytes(getFieldAddress(index), size)

    /**
     * Gets an array of shorts from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of shorts to read
     * @return The array of shorts read from the field
     */
    inline fun getShorts(index: Int, size: Int): ShortArray = Memory.readShorts(getFieldAddress(index), size)

    /**
     * Gets an array of ints from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of ints to read
     * @return The array of ints read from the field
     */
    inline fun getInts(index: Int, size: Int): IntArray = Memory.readInts(getFieldAddress(index), size)

    /**
     * Gets an array of longs from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of longs to read
     * @return The array of longs read from the field
     */
    inline fun getLongs(index: Int, size: Int): LongArray = Memory.readLongs(getFieldAddress(index), size)

    /**
     * Gets an array of native integers from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of native integers to read
     * @return The array of native integers read from the field
     */
    inline fun getNInts(index: Int, size: Int): NIntArray = Memory.readNInts(getFieldAddress(index), size)

    /**
     * Gets an array of unsigned bytes from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of unsigned bytes to read
     * @return The array of unsigned bytes read from the field
     */
    inline fun getUBytes(index: Int, size: Int): UByteArray = Memory.readUBytes(getFieldAddress(index), size)

    /**
     * Gets an array of unsigned shorts from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of unsigned shorts to read
     * @return The array of unsigned shorts read from the field
     */
    inline fun getUShorts(index: Int, size: Int): UShortArray = Memory.readUShorts(getFieldAddress(index), size)

    /**
     * Gets an array of unsigned ints from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of unsigned ints to read
     * @return The array of unsigned ints read from the field
     */
    inline fun getUInts(index: Int, size: Int): UIntArray = Memory.readUInts(getFieldAddress(index), size)

    /**
     * Gets an array of unsigned longs from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of unsigned longs to read
     * @return The array of unsigned longs read from the field
     */
    inline fun getULongs(index: Int, size: Int): ULongArray = Memory.readULongs(getFieldAddress(index), size)

    /**
     * Gets an array of native unsigned integers from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of native unsigned integers to read
     * @return The array of native unsigned integers read from the field
     */
    inline fun getNUInts(index: Int, size: Int): NUIntArray = Memory.readNUInts(getFieldAddress(index), size)

    /**
     * Gets an array of floats from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of floats to read
     * @return The array of floats read from the field
     */
    inline fun getFloats(index: Int, size: Int): FloatArray = Memory.readFloats(getFieldAddress(index), size)

    /**
     * Gets an array of doubles from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of doubles to read
     * @return The array of doubles read from the field
     */
    inline fun getDoubles(index: Int, size: Int): DoubleArray = Memory.readDoubles(getFieldAddress(index), size)

    /**
     * Gets an array of pointers from a field in the struct.
     *
     * @param index The index of the field
     * @param size The number of pointers to read
     * @return The array of pointers read from the field
     */
    inline fun getPointers(index: Int, size: Int): PointerArray = Memory.readPointers(getFieldAddress(index), size)

    /**
     * Sets the byte value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The byte value to set
     */
    inline fun setByte(index: Int, value: Byte) = Memory.writeByte(getFieldAddress(index), value)

    /**
     * Sets the short value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The short value to set
     */
    inline fun setShort(index: Int, value: Short) = Memory.writeShort(getFieldAddress(index), value)

    /**
     * Sets the int value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The int value to set
     */
    inline fun setInt(index: Int, value: Int) = Memory.writeInt(getFieldAddress(index), value)

    /**
     * Sets the long value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The long value to set
     */
    inline fun setLong(index: Int, value: Long) = Memory.writeLong(getFieldAddress(index), value)

    /**
     * Sets the native integer value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The native integer value to set
     */
    inline fun setNInt(index: Int, value: NInt) = Memory.writeNInt(getFieldAddress(index), value)

    /**
     * Sets the unsigned byte value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The unsigned byte value to set
     */
    inline fun setUByte(index: Int, value: UByte) = Memory.writeUByte(getFieldAddress(index), value)

    /**
     * Sets the unsigned short value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The unsigned short value to set
     */
    inline fun setUShort(index: Int, value: UShort) = Memory.writeUShort(getFieldAddress(index), value)

    /**
     * Sets the unsigned int value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The unsigned int value to set
     */
    inline fun setUInt(index: Int, value: UInt) = Memory.writeUInt(getFieldAddress(index), value)

    /**
     * Sets the unsigned long value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The unsigned long value to set
     */
    inline fun setULong(index: Int, value: ULong) = Memory.writeULong(getFieldAddress(index), value)

    /**
     * Sets the native unsigned integer value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The native unsigned integer value to set
     */
    inline fun setNUInt(index: Int, value: NUInt) = Memory.writeNUInt(getFieldAddress(index), value)

    /**
     * Sets the float value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The float value to set
     */
    inline fun setFloat(index: Int, value: Float) = Memory.writeFloat(getFieldAddress(index), value)

    /**
     * Sets the double value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The double value to set
     */
    inline fun setDouble(index: Int, value: Double) = Memory.writeDouble(getFieldAddress(index), value)

    /**
     * Sets the pointer value of a field in the struct.
     *
     * @param index The index of the field
     * @param value The pointer value to set
     */
    inline fun setPointer(index: Int, value: Pointer) = Memory.writePointer(getFieldAddress(index), value)

    /**
     * Sets an array of bytes in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of bytes to set
     */
    inline fun setBytes(index: Int, data: ByteArray) = Memory.writeBytes(getFieldAddress(index), data)

    /**
     * Sets an array of shorts in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of shorts to set
     */
    inline fun setShorts(index: Int, data: ShortArray) = Memory.writeShorts(getFieldAddress(index), data)

    /**
     * Sets an array of ints in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of ints to set
     */
    inline fun setInts(index: Int, data: IntArray) = Memory.writeInts(getFieldAddress(index), data)

    /**
     * Sets an array of longs in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of longs to set
     */
    inline fun setLongs(index: Int, data: LongArray) = Memory.writeLongs(getFieldAddress(index), data)

    /**
     * Sets an array of native integers in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of native integers to set
     */
    inline fun setNInts(index: Int, data: NIntArray) = Memory.writeNInts(getFieldAddress(index), data)

    /**
     * Sets an array of unsigned bytes in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of unsigned bytes to set
     */
    inline fun setUBytes(index: Int, data: UByteArray) = Memory.writeUBytes(getFieldAddress(index), data)

    /**
     * Sets an array of unsigned shorts in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of unsigned shorts to set
     */
    inline fun setUShorts(index: Int, data: UShortArray) = Memory.writeUShorts(getFieldAddress(index), data)

    /**
     * Sets an array of unsigned ints in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of unsigned ints to set
     */
    inline fun setUInts(index: Int, data: UIntArray) = Memory.writeUInts(getFieldAddress(index), data)

    /**
     * Sets an array of unsigned longs in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of unsigned longs to set
     */
    inline fun setULongs(index: Int, data: ULongArray) = Memory.writeULongs(getFieldAddress(index), data)

    /**
     * Sets an array of native unsigned integers in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of native unsigned integers to set
     */
    inline fun setNUInts(index: Int, data: NUIntArray) = Memory.writeNUInts(getFieldAddress(index), data)

    /**
     * Sets an array of floats in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of floats to set
     */
    inline fun setFloats(index: Int, data: FloatArray) = Memory.writeFloats(getFieldAddress(index), data)

    /**
     * Sets an array of doubles in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of doubles to set
     */
    inline fun setDoubles(index: Int, data: DoubleArray) = Memory.writeDoubles(getFieldAddress(index), data)

    /**
     * Sets an array of pointers in a field in the struct.
     *
     * @param index The index of the field
     * @param data The array of pointers to set
     */
    inline fun setPointers(index: Int, data: PointerArray) = Memory.writePointers(getFieldAddress(index), data)

    /**
     * Frees the memory allocated for this struct.
     *
     * This method is called automatically when the struct is no longer needed,
     * either explicitly by calling close() or implicitly through a use() block.
     */
    override fun close() {
        Memory.free(address)
    }
}
