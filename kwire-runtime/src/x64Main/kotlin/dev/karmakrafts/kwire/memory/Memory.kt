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

import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NFloatArray
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NIntArray
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.PtrArray
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

    override fun allocate(size: NUInt, alignment: NUInt): Ptr<CVoid> {
        return malloc(Memory.align(size, alignment).value.convert())!!.reinterpret<COpaque>().toPtr().align(alignment)
    }

    override fun reallocate(address: Ptr<*>, size: NUInt, alignment: NUInt): Ptr<CVoid> {
        return realloc(
            address.reinterpret<CVoid>().toCPointer(), Memory.align(size, alignment).value.convert()
        )!!.reinterpret<COpaque>().toPtr().align(alignment)
    }

    override fun free(address: Ptr<*>) {
        posixFree(address.reinterpret<CVoid>().toCPointer())
    }

    override fun set(address: Ptr<*>, value: Byte, size: NUInt) {
        memset(address.reinterpret<CVoid>().toCPointer(), value.toInt(), size.value.convert())
    }

    override fun copy(source: Ptr<*>, dest: Ptr<*>, size: NUInt) {
        memcpy(dest.reinterpret<CVoid>().toCPointer(), source.reinterpret<CVoid>().toCPointer(), size.value.convert())
    }

    override fun copyOverlapping(source: Ptr<*>, dest: Ptr<*>, size: NUInt) {
        memmove(dest.reinterpret<CVoid>().toCPointer(), source.reinterpret<CVoid>().toCPointer(), size.value.convert())
    }

    override fun compare(first: Ptr<*>, second: Ptr<*>, size: NUInt): Int {
        return memcmp(
            first.reinterpret<CVoid>().toCPointer(), second.reinterpret<CVoid>().toCPointer(), size.value.convert()
        )
    }

    override fun strlen(address: Ptr<*>): NUInt {
        return strlen_with_address(address.reinterpret<CVoid>().toCPointer()).toInt().toNUInt()
    }

    override fun strcpy(source: Ptr<*>, dest: Ptr<*>) {
        strcpy_with_address(dest.reinterpret<CVoid>().toCPointer(), source.reinterpret<CVoid>().toCPointer())
    }

    override fun strcmp(first: Ptr<*>, second: Ptr<*>): Int {
        return strcmp_with_address(first.reinterpret<CVoid>().toCPointer(), second.reinterpret<CVoid>().toCPointer())
    }

    override fun readByte(address: Ptr<*>): Byte {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<ByteVar>()[0]
    }

    override fun readShort(address: Ptr<*>): Short {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<ShortVar>()[0]
    }

    override fun readInt(address: Ptr<*>): Int {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<IntVar>()[0]
    }

    override fun readLong(address: Ptr<*>): Long {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<LongVar>()[0]
    }

    override fun readNInt(address: Ptr<*>): NInt {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<LongVar>()[0]
    }

    override fun readPointer(address: Ptr<*>): Ptr<CVoid> {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<COpaquePointerVar>()[0]!!.reinterpret<COpaque>()
            .toPtr()
    }

    override fun readFloat(address: Ptr<*>): Float {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<FloatVar>()[0]
    }

    override fun readDouble(address: Ptr<*>): Double {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<DoubleVar>()[0]
    }

    override fun readNFloat(address: Ptr<*>): NFloat {
        return address.reinterpret<CVoid>().toCPointer().reinterpret<DoubleVar>()[0]
    }

    override fun readBytes(address: Ptr<*>, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun readShorts(address: Ptr<*>, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun readInts(address: Ptr<*>, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun readLongs(address: Ptr<*>, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNInts(address: Ptr<*>, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun readFloats(address: Ptr<*>, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun readDoubles(address: Ptr<*>, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readNFloats(address: Ptr<*>, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun readPointers(address: Ptr<*>, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                pinnedArray.addressOf(dataStart),
                address.reinterpret<CVoid>().toCPointer(),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeByte(address: Ptr<*>, value: Byte) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<ByteVar>()[0] = value
    }

    override fun writeShort(address: Ptr<*>, value: Short) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<ShortVar>()[0] = value
    }

    override fun writeInt(address: Ptr<*>, value: Int) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<IntVar>()[0] = value
    }

    override fun writeLong(address: Ptr<*>, value: Long) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<LongVar>()[0] = value
    }

    override fun writeNInt(address: Ptr<*>, value: NInt) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<LongVar>()[0] = value
    }

    override fun writePointer(address: Ptr<*>, value: Ptr<*>) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<COpaquePointerVar>()[0] =
            value.reinterpret<CVoid>().toCPointer()
    }

    override fun writeFloat(address: Ptr<*>, value: Float) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<FloatVar>()[0] = value
    }

    override fun writeDouble(address: Ptr<*>, value: Double) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<DoubleVar>()[0] = value
    }

    override fun writeNFloat(address: Ptr<*>, value: NFloat) {
        address.reinterpret<CVoid>().toCPointer().reinterpret<DoubleVar>()[0] = value
    }

    override fun writeBytes(address: Ptr<*>, data: ByteArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Byte.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeShorts(address: Ptr<*>, data: ShortArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Short.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeInts(address: Ptr<*>, data: IntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Int.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeLongs(address: Ptr<*>, data: LongArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNInts(address: Ptr<*>, data: NIntArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeFloats(address: Ptr<*>, data: FloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Float.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeDoubles(address: Ptr<*>, data: DoubleArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writeNFloats(address: Ptr<*>, data: NFloatArray, dataStart: Int, dataEnd: Int) {
        data.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Double.SIZE_BYTES).convert()
            )
        }
    }

    override fun writePointers(address: Ptr<*>, data: PtrArray<*>, dataStart: Int, dataEnd: Int) {
        data.value.value.usePinned { pinnedArray ->
            memcpy(
                address.reinterpret<CVoid>().toCPointer(),
                pinnedArray.addressOf(dataStart),
                ((dataEnd - dataStart) * Long.SIZE_BYTES).convert()
            )
        }
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = NativeMemory