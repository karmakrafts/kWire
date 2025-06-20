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

// @formatter:off
@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalUnsignedTypes::class)
// @formatter:on

package dev.karmakrafts.kwire.ffi

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ShutdownHandler
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NFloatArray
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NIntArray
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.NUIntArray
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.size
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.memory.Memory

@PublishedApi
internal val _argBuffer: ThreadLocalRef<FFIArgBufferImpl> = ThreadLocalRef()

@PublishedApi
internal inline val argBuffer: FFIArgBufferImpl
    get() {
        var buffer = _argBuffer.value
        if (buffer == null) {
            buffer = FFIArgBufferImpl()
            _argBuffer.value = buffer
        }
        return buffer
    }

/**
 * Interface for a buffer that stores arguments to be passed to native functions.
 *
 * The buffer manages memory for storing arguments of various types and tracks the
 * types of arguments stored. This is used by the FFI system to pass arguments to
 * native functions.
 */
@KWireCompilerApi
interface FFIArgBuffer {
    @KWireCompilerApi
    companion object {
        /**
         * Default size of the argument buffer in bytes.
         */
        const val DEFAULT_SIZE: Int = 4096

        /**
         * Gets the thread-local argument buffer instance, clearing it first.
         *
         * @return The cleared argument buffer
         */
        @KWireCompilerApi
        inline fun get(): FFIArgBuffer = argBuffer.clear()
    }

    /**
     * The memory address of the buffer.
     */
    val address: VoidPtr

    /**
     * List of types of arguments stored in the buffer.
     */
    val types: List<FFIType>

    fun rewind()

    /**
     * Get the address of an argument at the given index.
     *
     * @param index The index of the argument to retrieve the address of
     * @return The memory address where the argument is stored
     */
    fun getAddress(index: Int): VoidPtr

    /**
     * Unboxes all values in the given array and
     * puts them into the buffer accordingly.
     */
    @KWireCompilerApi
    fun putAll(arguments: Array<Any>)

    /**
     * Puts a byte value into the buffer.
     *
     * @param value The byte value to store
     */
    @KWireCompilerApi
    fun putByte(value: Byte)

    /**
     * Puts a short value into the buffer.
     *
     * @param value The short value to store
     */
    @KWireCompilerApi
    fun putShort(value: Short)

    /**
     * Puts an int value into the buffer.
     *
     * @param value The int value to store
     */
    @KWireCompilerApi
    fun putInt(value: Int)

    /**
     * Puts a long value into the buffer.
     *
     * @param value The long value to store
     */
    @KWireCompilerApi
    fun putLong(value: Long)

    /**
     * Puts a native integer value into the buffer.
     *
     * @param value The native integer value to store
     */
    @KWireCompilerApi
    fun putNInt(value: NInt)

    /**
     * Puts a pointer value into the buffer.
     *
     * @param value The pointer value to store
     */
    @KWireCompilerApi
    fun putPointer(value: Address)

    /**
     * Puts a float value into the buffer.
     *
     * @param value The float value to store
     */
    @KWireCompilerApi
    fun putFloat(value: Float)

    /**
     * Puts a double value into the buffer.
     *
     * @param value The double value to store
     */
    @KWireCompilerApi
    fun putDouble(value: Double)

    /**
     * Puts a native float value into the buffer.
     *
     * @param value The native float value to store
     */
    @KWireCompilerApi
    fun putNFloat(value: NFloat)

    /**
     * Puts a byte array into the buffer.
     *
     * @param values The byte array to store
     */
    @KWireCompilerApi
    fun putBytes(values: ByteArray)

    /**
     * Puts a short array into the buffer.
     *
     * @param values The short array to store
     */
    @KWireCompilerApi
    fun putShorts(values: ShortArray)

    /**
     * Puts an int array into the buffer.
     *
     * @param values The int array to store
     */
    @KWireCompilerApi
    fun putInts(values: IntArray)

    /**
     * Puts a long array into the buffer.
     *
     * @param values The long array to store
     */
    @KWireCompilerApi
    fun putLongs(values: LongArray)

    /**
     * Puts a native integer array into the buffer.
     *
     * @param values The native integer array to store
     */
    @KWireCompilerApi
    fun putNInts(values: NIntArray)

    /**
     * Puts an unsigned byte value into the buffer.
     *
     * @param value The unsigned byte value to store
     */
    @KWireCompilerApi
    fun putUByte(value: UByte)

    /**
     * Puts an unsigned short value into the buffer.
     *
     * @param value The unsigned short value to store
     */
    @KWireCompilerApi
    fun putUShort(value: UShort)

    /**
     * Puts an unsigned int value into the buffer.
     *
     * @param value The unsigned int value to store
     */
    @KWireCompilerApi
    fun putUInt(value: UInt)

    /**
     * Puts an unsigned long value into the buffer.
     *
     * @param value The unsigned long value to store
     */
    @KWireCompilerApi
    fun putULong(value: ULong)

    /**
     * Puts a native unsigned integer value into the buffer.
     *
     * @param value The native unsigned integer value to store
     */
    @KWireCompilerApi
    fun putNUInt(value: NUInt)

    /**
     * Puts an unsigned byte array into the buffer.
     *
     * @param values The unsigned byte array to store
     */
    @KWireCompilerApi
    fun putUBytes(values: UByteArray)

    /**
     * Puts an unsigned short array into the buffer.
     *
     * @param values The unsigned short array to store
     */
    @KWireCompilerApi
    fun putUShorts(values: UShortArray)

    /**
     * Puts an unsigned int array into the buffer.
     *
     * @param values The unsigned int array to store
     */
    @KWireCompilerApi
    fun putUInts(values: UIntArray)

    /**
     * Puts an unsigned long array into the buffer.
     *
     * @param values The unsigned long array to store
     */
    @KWireCompilerApi
    fun putULongs(values: ULongArray)

    /**
     * Puts a native unsigned integer array into the buffer.
     *
     * @param values The native unsigned integer array to store
     */
    @KWireCompilerApi
    fun putNUInts(values: NUIntArray)

    /**
     * Puts a float array into the buffer.
     *
     * @param values The float array to store
     */
    @KWireCompilerApi
    fun putFloats(values: FloatArray)

    /**
     * Puts a double array into the buffer.
     *
     * @param values The double array to store
     */
    @KWireCompilerApi
    fun putDoubles(values: DoubleArray)

    /**
     * Puts a native float array into the buffer.
     *
     * @param values The native float array to store
     */
    @KWireCompilerApi
    fun putNFloats(values: NFloatArray)

    /**
     * Puts an array of pointers into the buffer.
     *
     * @param values The pointer array to store
     */
    @KWireCompilerApi
    fun putPointers(values: PtrArray<*>)

    @KWireCompilerApi
    fun getByte(): Byte

    @KWireCompilerApi
    fun getShort(): Short

    @KWireCompilerApi
    fun getInt(): Int

    @KWireCompilerApi
    fun getLong(): Long

    @KWireCompilerApi
    fun getNInt(): NInt

    @KWireCompilerApi
    fun getUByte(): UByte

    @KWireCompilerApi
    fun getUShort(): UShort

    @KWireCompilerApi
    fun getUInt(): UInt

    @KWireCompilerApi
    fun getULong(): ULong

    @KWireCompilerApi
    fun getNUInt(): NUInt

    @KWireCompilerApi
    fun getFloat(): Float

    @KWireCompilerApi
    fun getDouble(): Double

    @KWireCompilerApi
    fun getPointer(): VoidPtr
}

@PublishedApi
internal class FFIArgBufferImpl @PublishedApi internal constructor() : FFIArgBuffer {
    override val address: VoidPtr = Memory.allocate(FFIArgBuffer.DEFAULT_SIZE.toNUInt())
    private var offset: NUInt = 0U.toNUInt()
    private var index: Int = 0
    override val types: ArrayList<FFIType> = ArrayList()

    init {
        // Register for cleanup on shutdown
        ShutdownHandler.register(AutoCloseable(::free))
    }

    private fun nextOffset(type: FFIType, count: Int = 1): NUInt {
        val newOffset = offset + (type.size * count).toNUInt()
        check(newOffset < FFIArgBuffer.DEFAULT_SIZE.toNUInt()) { "Exceeded argument buffer limit" }
        val oldOffset = offset
        offset = newOffset
        types += type
        ++index
        return oldOffset
    }

    private inline fun nextWriteAddress(type: FFIType, count: Int = 1): VoidPtr = address + nextOffset(type, count)
    private inline fun nextReadAddress(): VoidPtr = getAddress(index++)

    override fun rewind() {
        index = 0
    }

    override fun getAddress(index: Int): VoidPtr {
        return address + types.take(index).sumOf { it.size }
    }

    override fun putAll(arguments: Array<Any>) {
        // We only need to handle NUInt here because it's got its own type identity;
        // NInt and NFloat are expanded into their respective types, so Int/Long and Float/Double handles them.
        for (argument in arguments) when (argument) {
            is Byte -> putByte(argument)
            is Short -> putShort(argument)
            is Int -> putInt(argument)
            is Long -> putLong(argument)
            is UByte -> putUByte(argument)
            is UShort -> putUShort(argument)
            is UInt -> putUInt(argument)
            is ULong -> putULong(argument)
            is Float -> putFloat(argument)
            is Double -> putDouble(argument)
            is NUInt -> putNUInt(argument)
            is ByteArray -> putBytes(argument)
            is ShortArray -> putShorts(argument)
            is IntArray -> putInts(argument)
            is LongArray -> putLongs(argument)
            is UByteArray -> putUBytes(argument)
            is UShortArray -> putUShorts(argument)
            is UIntArray -> putUInts(argument)
            is ULongArray -> putULongs(argument)
            is FloatArray -> putFloats(argument)
            is NUIntArray -> putNUInts(argument)
            is Address -> putPointer(argument)
            is PtrArray<*> -> putPointers(argument)
            else -> error("Unsupported FFI argument type ${argument::class}")
        }
    }

    override fun putByte(value: Byte) = Memory.writeByte(nextWriteAddress(FFIType.BYTE), value)
    override fun putShort(value: Short) = Memory.writeShort(nextWriteAddress(FFIType.SHORT), value)
    override fun putInt(value: Int) = Memory.writeInt(nextWriteAddress(FFIType.INT), value)
    override fun putLong(value: Long) = Memory.writeLong(nextWriteAddress(FFIType.LONG), value)
    override fun putNInt(value: NInt) = Memory.writeNInt(nextWriteAddress(FFIType.NINT), value)

    override fun putUByte(value: UByte) = Memory.writeUByte(nextWriteAddress(FFIType.UBYTE), value)
    override fun putUShort(value: UShort) = Memory.writeUShort(nextWriteAddress(FFIType.USHORT), value)
    override fun putUInt(value: UInt) = Memory.writeUInt(nextWriteAddress(FFIType.UINT), value)
    override fun putULong(value: ULong) = Memory.writeULong(nextWriteAddress(FFIType.ULONG), value)
    override fun putNUInt(value: NUInt) = Memory.writeNUInt(nextWriteAddress(FFIType.NUINT), value)

    override fun putPointer(value: Address) = Memory.writePointer(nextWriteAddress(FFIType.PTR), value)

    override fun putFloat(value: Float) = Memory.writeFloat(nextWriteAddress(FFIType.FLOAT), value)
    override fun putDouble(value: Double) = Memory.writeDouble(nextWriteAddress(FFIType.DOUBLE), value)
    override fun putNFloat(value: NFloat) = Memory.writeNFloat(nextWriteAddress(FFIType.NFLOAT), value)

    override fun putBytes(values: ByteArray) = Memory.writeBytes(nextWriteAddress(FFIType.BYTE, values.size), values)
    override fun putShorts(values: ShortArray) =
        Memory.writeShorts(nextWriteAddress(FFIType.SHORT, values.size), values)

    override fun putInts(values: IntArray) = Memory.writeInts(nextWriteAddress(FFIType.INT, values.size), values)
    override fun putLongs(values: LongArray) = Memory.writeLongs(nextWriteAddress(FFIType.LONG, values.size), values)
    override fun putNInts(values: NIntArray) = Memory.writeNInts(nextWriteAddress(FFIType.NINT, values.size), values)

    override fun putUBytes(values: UByteArray) =
        Memory.writeUBytes(nextWriteAddress(FFIType.UBYTE, values.size), values)

    override fun putUShorts(values: UShortArray) =
        Memory.writeUShorts(nextWriteAddress(FFIType.USHORT, values.size), values)

    override fun putUInts(values: UIntArray) = Memory.writeUInts(nextWriteAddress(FFIType.UINT, values.size), values)
    override fun putULongs(values: ULongArray) =
        Memory.writeULongs(nextWriteAddress(FFIType.ULONG, values.size), values)

    override fun putNUInts(values: NUIntArray) =
        Memory.writeNUInts(nextWriteAddress(FFIType.NUINT, values.size), values)

    override fun putFloats(values: FloatArray) =
        Memory.writeFloats(nextWriteAddress(FFIType.FLOAT, values.size), values)

    override fun putDoubles(values: DoubleArray) =
        Memory.writeDoubles(nextWriteAddress(FFIType.DOUBLE, values.size), values)

    override fun putNFloats(values: NFloatArray) =
        Memory.writeNFloats(nextWriteAddress(FFIType.NFLOAT, values.size), values)

    override fun putPointers(values: PtrArray<*>) =
        Memory.writePointers(nextWriteAddress(FFIType.PTR, values.size), values)

    override fun getByte(): Byte = Memory.readByte(nextReadAddress())
    override fun getShort(): Short = Memory.readShort(nextReadAddress())
    override fun getInt(): Int = Memory.readInt(nextReadAddress())
    override fun getLong(): Long = Memory.readLong(nextReadAddress())
    override fun getNInt(): NInt = Memory.readNInt(nextReadAddress())

    override fun getUByte(): UByte = Memory.readUByte(nextReadAddress())
    override fun getUShort(): UShort = Memory.readUShort(nextReadAddress())
    override fun getUInt(): UInt = Memory.readUInt(nextReadAddress())
    override fun getULong(): ULong = Memory.readULong(nextReadAddress())
    override fun getNUInt(): NUInt = Memory.readNUInt(nextReadAddress())

    override fun getFloat(): Float = Memory.readFloat(nextReadAddress())
    override fun getDouble(): Double = Memory.readDouble(nextReadAddress())

    override fun getPointer(): VoidPtr = Memory.readPointer(nextReadAddress())

    @PublishedApi
    internal fun clear(): FFIArgBufferImpl {
        if (offset == 0U.toNUInt()) return this
        offset = 0U.toNUInt()
        types.clear()
        return this
    }

    private fun free() {
        Memory.free(address)
        types.clear()
    }
}

/**
 * Type alias for a function with [FFIArgBuffer] as its receiver.
 *
 * This is used to specify arguments to native function calls in a type-safe and convenient way.
 * For example:
 * ```
 * FFI.call(address, descriptor) {
 *     putInt(42)
 *     putFloat(3.14f)
 * }
 * ```
 */
typealias FFIArgSpec = FFIArgBuffer.() -> Unit
