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

import kotlinx.cinterop.ByteVar
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

    override fun allocate(size: NUInt, alignment: NUInt): Pointer {
        return malloc(Memory.align(size, alignment).value.convert())!!.toPointer().align(alignment)
    }

    override fun reallocate(address: Pointer, size: NUInt, alignment: NUInt): Pointer {
        return realloc(address.toCOpaquePointer(), Memory.align(size, alignment).value.convert())!!.toPointer()
            .align(alignment)
    }

    override fun free(address: Pointer) {
        posixFree(address.toCOpaquePointer())
    }

    override fun set(address: Pointer, value: Byte, size: NUInt) {
        memset(address.toCOpaquePointer(), value.toInt(), size.value.convert())
    }

    override fun copy(source: Pointer, dest: Pointer, size: NUInt) {
        memcpy(dest.toCOpaquePointer(), source.toCOpaquePointer(), size.value.convert())
    }

    override fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt) {
        memmove(dest.toCOpaquePointer(), source.toCOpaquePointer(), size.value.convert())
    }

    override fun compare(first: Pointer, second: Pointer, size: NUInt): Int {
        return memcmp(first.toCOpaquePointer(), second.toCOpaquePointer(), size.value.convert())
    }

    override fun strlen(address: Pointer): NUInt {
        return strlen_with_address(address.toCOpaquePointer()).toInt().toNUInt()
    }

    override fun strcpy(source: Pointer, dest: Pointer) {
        strcpy_with_address(dest.toCOpaquePointer(), source.toCOpaquePointer())
    }

    override fun strcmp(first: Pointer, second: Pointer): Int {
        return strcmp_with_address(first.toCOpaquePointer(), second.toCOpaquePointer())
    }

    override fun readByte(address: Pointer): Byte {
        return address.toCPointer<ByteVar>()?.get(0) ?: 0
    }

    override fun readShort(address: Pointer): Short {
        return address.toCPointer<ShortVar>()?.get(0) ?: 0
    }

    override fun readInt(address: Pointer): Int {
        return address.toCPointer<IntVar>()?.get(0) ?: 0
    }

    override fun readLong(address: Pointer): Long {
        return address.toCPointer<LongVar>()?.get(0) ?: 0
    }

    override fun readNInt(address: Pointer): NInt {
        return address.toCPointer<IntVar>()?.get(0) ?: 0
    }

    override fun readPointer(address: Pointer): Pointer {
        return address.toCPointer<COpaquePointerVar>()?.get(0)?.toPointer() ?: nullptr
    }

    override fun readFloat(address: Pointer): Float {
        return address.toCPointer<FloatVar>()?.get(0) ?: 0F
    }

    override fun readDouble(address: Pointer): Double {
        return address.toCPointer<DoubleVar>()?.get(0) ?: 0.0
    }

    override fun readNFloat(address: Pointer): NFloat {
        return address.toCPointer<FloatVar>()?.get(0) ?: 0F
    }

    override fun readBytes(address: Pointer, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun readShorts(address: Pointer, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun readInts(address: Pointer, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun readLongs(address: Pointer, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNInts(address: Pointer, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readFloats(address: Pointer, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun readDoubles(address: Pointer, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNFloats(address: Pointer, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readPointers(address: Pointer, data: PointerArray, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.toCOpaquePointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeByte(address: Pointer, value: Byte) {
        address.toCPointer<ByteVar>()?.set(0, value)
    }

    override fun writeShort(address: Pointer, value: Short) {
        address.toCPointer<ShortVar>()?.set(0, value)
    }

    override fun writeInt(address: Pointer, value: Int) {
        address.toCPointer<IntVar>()?.set(0, value)
    }

    override fun writeLong(address: Pointer, value: Long) {
        address.toCPointer<LongVar>()?.set(0, value)
    }

    override fun writeNInt(address: Pointer, value: NInt) {
        address.toCPointer<IntVar>()?.set(0, value)
    }

    override fun writePointer(address: Pointer, value: Pointer) {
        address.toCPointer<COpaquePointerVar>()?.set(0, value.toCOpaquePointer())
    }

    override fun writeFloat(address: Pointer, value: Float) {
        address.toCPointer<FloatVar>()?.set(0, value)
    }

    override fun writeDouble(address: Pointer, value: Double) {
        address.toCPointer<DoubleVar>()?.set(0, value)
    }

    override fun writeNFloat(address: Pointer, value: NFloat) {
        address.toCPointer<FloatVar>()?.set(0, value)
    }

    override fun writeBytes(address: Pointer, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeShorts(address: Pointer, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeInts(address: Pointer, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeLongs(address: Pointer, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNInts(address: Pointer, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeFloats(address: Pointer, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeDoubles(address: Pointer, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNFloats(address: Pointer, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writePointers(address: Pointer, data: PointerArray, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                address.toCOpaquePointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = NativeMemory