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

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.asPtr
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.memory.Memory
import dev.karmakrafts.kwire.memory.MemoryStack
import dev.karmakrafts.kwire.memory.byte
import dev.karmakrafts.kwire.memory.double
import dev.karmakrafts.kwire.memory.float
import dev.karmakrafts.kwire.memory.int
import dev.karmakrafts.kwire.memory.long
import dev.karmakrafts.kwire.memory.nFloat
import dev.karmakrafts.kwire.memory.nInt
import dev.karmakrafts.kwire.memory.nUInt
import dev.karmakrafts.kwire.memory.pointer
import dev.karmakrafts.kwire.memory.short
import dev.karmakrafts.kwire.memory.uByte
import dev.karmakrafts.kwire.memory.uInt
import dev.karmakrafts.kwire.memory.uLong
import dev.karmakrafts.kwire.memory.uShort

/**
 * A first-in-last-out style buffer for storing arguments (and results) for interop calls.
 * The buffer is a facade built around [MemoryStack] and relies on creating new stack frames.
 */
@KWireCompilerApi
interface FFIArgBuffer {
    @KWireCompilerApi
    companion object {
        @KWireCompilerApi
        inline fun acquire(): FFIArgBuffer = FFIArgBufferImpl(MemoryStack.get().push())
    }

    /**
     * The base memory address of the buffer.
     */
    val address: Ptr<CVoid>

    /**
     * The current address within the stack frame of this argument buffer.
     * This is incremented/decremented with every write/read to/from the buffer.
     */
    val currentAddress: Ptr<CVoid>

    /**
     * List of types of arguments stored in the buffer.
     */
    val types: List<FFIType>

    fun rewindToLast()

    @KWireCompilerApi
    fun release()

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
    fun putPointer(value: Ptr<CVoid>)

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
    fun getNFloat(): NFloat

    @KWireCompilerApi
    fun getPointer(): Ptr<CVoid>
}

@PublishedApi
internal class FFIArgBufferImpl @PublishedApi internal constructor(
    private val stackFrame: MemoryStack
) : FFIArgBuffer {
    override val address: Ptr<CVoid> = stackFrame.frameAddress // Capture base pointer of argument frame
    override val types: ArrayList<FFIType> = ArrayList()
    private var index: Int = 0

    override val currentAddress: Ptr<CVoid>
        get() = (address.asNUInt() + types.take(index).sumOf { it.size }.toNUInt()).asPtr()

    override fun rewindToLast() {
        index = types.lastIndex
    }

    override fun release() {
        stackFrame.pop()
    }

    override fun putAll(arguments: Array<Any>) {
        for (argument in arguments) when (argument) {
            is Byte -> putByte(argument)
            is Short -> putShort(argument)
            is Int -> putInt(argument)
            is Long -> putLong(argument)
            is UByte -> putUByte(argument)
            is UShort -> putUShort(argument)
            is UInt -> putUInt(argument)
            is ULong -> putULong(argument)
            is NUInt -> putNUInt(argument)
            is Float -> putFloat(argument)
            is Double -> putDouble(argument)
            is Ptr<*> -> putPointer(argument.reinterpret())
        }
    }

    override fun putByte(value: Byte) {
        stackFrame.byte(value)
        types += FFIType.BYTE
    }

    override fun putShort(value: Short) {
        stackFrame.short(value)
        types += FFIType.SHORT
    }

    override fun putInt(value: Int) {
        stackFrame.int(value)
        types += FFIType.INT
    }

    override fun putLong(value: Long) {
        stackFrame.long(value)
        types += FFIType.LONG
    }

    override fun putNInt(value: NInt) {
        stackFrame.nInt(value)
        types += FFIType.NINT
    }

    override fun putPointer(value: Ptr<CVoid>) {
        stackFrame.pointer(value)
        types += FFIType.PTR
    }

    override fun putFloat(value: Float) {
        stackFrame.float(value)
        types += FFIType.FLOAT
    }

    override fun putDouble(value: Double) {
        stackFrame.double(value)
        types += FFIType.DOUBLE
    }

    override fun putNFloat(value: NFloat) {
        stackFrame.nFloat(value)
        types += FFIType.NFLOAT
    }

    override fun putUByte(value: UByte) {
        stackFrame.uByte(value)
        types += FFIType.UBYTE
    }

    override fun putUShort(value: UShort) {
        stackFrame.uShort(value)
        types += FFIType.USHORT
    }

    override fun putUInt(value: UInt) {
        stackFrame.uInt(value)
        types += FFIType.UINT
    }

    override fun putULong(value: ULong) {
        stackFrame.uLong(value)
        types += FFIType.ULONG
    }

    override fun putNUInt(value: NUInt) {
        stackFrame.nUInt(value)
        types += FFIType.NUINT
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun getNextReadAddress(): Ptr<CVoid> =
        (address.asNUInt() + types.take(index++).sumOf { it.size }.toNUInt()).asPtr()

    override fun getByte(): Byte = Memory.readByte(getNextReadAddress())
    override fun getShort(): Short = Memory.readShort(getNextReadAddress())
    override fun getInt(): Int = Memory.readInt(getNextReadAddress())
    override fun getLong(): Long = Memory.readLong(getNextReadAddress())
    override fun getNInt(): NInt = Memory.readNInt(getNextReadAddress())

    override fun getUByte(): UByte = Memory.readUByte(getNextReadAddress())
    override fun getUShort(): UShort = Memory.readUShort(getNextReadAddress())
    override fun getUInt(): UInt = Memory.readUInt(getNextReadAddress())
    override fun getULong(): ULong = Memory.readULong(getNextReadAddress())
    override fun getNUInt(): NUInt = Memory.readNUInt(getNextReadAddress())

    override fun getFloat(): Float = Memory.readFloat(getNextReadAddress())
    override fun getDouble(): Double = Memory.readDouble(getNextReadAddress())
    override fun getNFloat(): NFloat = Memory.readNFloat(getNextReadAddress())

    override fun getPointer(): Ptr<CVoid> = Memory.readPointer(getNextReadAddress())
}