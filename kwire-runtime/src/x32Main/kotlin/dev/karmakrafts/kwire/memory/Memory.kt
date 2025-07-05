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

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NFloatArray
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NIntArray
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.PtrArray
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.toCPointer
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.ctype.toPtr
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import platform.posix.malloc
import platform.posix.memcmp
import platform.posix.memcpy
import platform.posix.memmove
import platform.posix.memset
import platform.posix.realloc
import platform.posix.strcmp_with_address
import platform.posix.strcpy_with_address
import platform.posix.strlen_with_address
import platform.posix.free as posixFree

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private object NativeMemory : Memory {
    override val defaultAlignment: NUInt = 16U.toNUInt()

    override fun allocate(size: NUInt, alignment: NUInt): VoidPtr {
        return malloc(Memory.align(size, alignment).value.convert())!!.reinterpret<COpaque>().toPtr().align(alignment)
    }

    override fun reallocate(address: Address, size: NUInt, alignment: NUInt): VoidPtr {
        return realloc(address.toCPointer(), Memory.align(size, alignment).value.convert())!!.reinterpret<COpaque>()
            .toPtr()
            .align(alignment)
    }

    override fun free(address: Address) {
        posixFree(address.toCPointer())
    }

    override fun set(address: Address, value: Byte, size: NUInt) {
        memset(address.toCPointer(), value.toInt(), size.value.convert())
    }

    override fun copy(source: Address, dest: Address, size: NUInt) {
        memcpy(dest.toCPointer(), source.toCPointer(), size.value.convert())
    }

    override fun copyOverlapping(source: Address, dest: Address, size: NUInt) {
        memmove(dest.toCPointer(), source.toCPointer(), size.value.convert())
    }

    override fun compare(first: Address, second: Address, size: NUInt): Int {
        return memcmp(first.toCPointer(), second.toCPointer(), size.value.convert())
    }

    override fun strlen(address: Address): NUInt {
        return strlen_with_address(address.toCPointer()).toNUInt()
    }

    override fun strcpy(source: Address, dest: Address) {
        strcpy_with_address(dest.toCPointer(), source.toCPointer())
    }

    override fun strcmp(first: Address, second: Address): Int {
        return strcmp_with_address(first.toCPointer(), second.toCPointer())
    }

    override fun readByte(address: Address): Byte {
        return address.toCPointer().reinterpret<ByteVar>()[0]
    }

    override fun readShort(address: Address): Short {
        return address.toCPointer().reinterpret<ShortVar>()[0]
    }

    override fun readInt(address: Address): Int {
        return address.toCPointer().reinterpret<IntVar>()[0]
    }

    override fun readLong(address: Address): Long {
        return address.toCPointer().reinterpret<LongVar>()[0]
    }

    override fun readNInt(address: Address): NInt {
        return address.toCPointer().reinterpret<IntVar>()[0]
    }

    override fun readPointer(address: Address): VoidPtr {
        return address.toCPointer().reinterpret<COpaquePointerVar>()[0]!!.reinterpret<COpaque>().toPtr()
    }

    override fun readFloat(address: Address): Float {
        return address.toCPointer().reinterpret<FloatVar>()[0]
    }

    override fun readDouble(address: Address): Double {
        return address.toCPointer().reinterpret<DoubleVar>()[0]
    }

    override fun readNFloat(address: Address): NFloat {
        return address.toCPointer().reinterpret<FloatVar>()[0]
    }

    override fun readBytes(address: Address, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun readShorts(address: Address, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun readInts(address: Address, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun readLongs(address: Address, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNInts(address: Address, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readFloats(address: Address, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun readDoubles(address: Address, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNFloats(address: Address, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readPointers(address: Address, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeByte(address: Address, value: Byte) {
        address.toCPointer().reinterpret<ByteVar>()[0] = value
    }

    override fun writeShort(address: Address, value: Short) {
        address.toCPointer().reinterpret<ShortVar>()[0] = value
    }

    override fun writeInt(address: Address, value: Int) {
        address.toCPointer().reinterpret<IntVar>()[0] = value
    }

    override fun writeLong(address: Address, value: Long) {
        address.toCPointer().reinterpret<LongVar>()[0] = value
    }

    override fun writeNInt(address: Address, value: NInt) {
        address.toCPointer().reinterpret<IntVar>()[0] = value
    }

    override fun writePointer(address: Address, value: Address) {
        address.toCPointer().reinterpret<COpaquePointerVar>()[0] = value.toCPointer()
    }

    override fun writeFloat(address: Address, value: Float) {
        address.toCPointer().reinterpret<FloatVar>()[0] = value
    }

    override fun writeDouble(address: Address, value: Double) {
        address.toCPointer().reinterpret<DoubleVar>()[0] = value
    }

    override fun writeNFloat(address: Address, value: NFloat) {
        address.toCPointer().reinterpret<FloatVar>()[0] = value
    }

    override fun writeBytes(address: Address, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeShorts(address: Address, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeInts(address: Address, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeLongs(address: Address, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNInts(address: Address, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeFloats(address: Address, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeDoubles(address: Address, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNFloats(address: Address, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writePointers(address: Address, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                address.toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = NativeMemory