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

private object NativeMemory : Memory {
    override val defaultAlignment: NUInt = (if(Pointer.SIZE_BYTES == Int.SIZE_BYTES) 4U else 16U).toNUInt()

    override fun allocate(size: NUInt, alignment: NUInt): Pointer {
        TODO("Not yet implemented")
    }

    override fun reallocate(address: Pointer, size: NUInt, alignment: NUInt): Pointer {
        TODO("Not yet implemented")
    }

    override fun free(address: Pointer) {
        TODO("Not yet implemented")
    }

    override fun set(address: Pointer, value: Byte, size: NUInt) {
        TODO("Not yet implemented")
    }

    override fun copy(source: Pointer, dest: Pointer, size: NUInt) {
        TODO("Not yet implemented")
    }

    override fun copyOverlapping(source: Pointer, dest: Pointer, size: NUInt) {
        TODO("Not yet implemented")
    }

    override fun compare(first: Pointer, second: Pointer, size: NUInt): Int {
        TODO("Not yet implemented")
    }

    override fun readByte(address: Pointer): Byte {
        TODO("Not yet implemented")
    }

    override fun readShort(address: Pointer): Short {
        TODO("Not yet implemented")
    }

    override fun readInt(address: Pointer): Int {
        TODO("Not yet implemented")
    }

    override fun readLong(address: Pointer): Long {
        TODO("Not yet implemented")
    }

    override fun readNInt(address: Pointer): NInt {
        TODO("Not yet implemented")
    }

    override fun readFloat(address: Pointer): Float {
        TODO("Not yet implemented")
    }

    override fun readDouble(address: Pointer): Double {
        TODO("Not yet implemented")
    }

    override fun readBytes(address: Pointer, size: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun readShorts(address: Pointer, size: Int): ShortArray {
        TODO("Not yet implemented")
    }

    override fun readInts(address: Pointer, size: Int): IntArray {
        TODO("Not yet implemented")
    }

    override fun readLongs(address: Pointer, size: Int): LongArray {
        TODO("Not yet implemented")
    }

    override fun readNInts(address: Pointer, size: Int): NIntArray {
        TODO("Not yet implemented")
    }

    override fun readFloats(address: Pointer, size: Int): FloatArray {
        TODO("Not yet implemented")
    }

    override fun readDoubles(address: Pointer, size: Int): DoubleArray {
        TODO("Not yet implemented")
    }

    override fun writeByte(address: Pointer, value: Byte) {
        TODO("Not yet implemented")
    }

    override fun writeShort(address: Pointer, value: Short) {
        TODO("Not yet implemented")
    }

    override fun writeInt(address: Pointer, value: Int) {
        TODO("Not yet implemented")
    }

    override fun writeLong(address: Pointer, value: Long) {
        TODO("Not yet implemented")
    }

    override fun writeNInt(address: Pointer, value: NInt) {
        TODO("Not yet implemented")
    }

    override fun writeFloat(address: Pointer, value: Float) {
        TODO("Not yet implemented")
    }

    override fun writeDouble(address: Pointer, value: Double) {
        TODO("Not yet implemented")
    }

    override fun writeBytes(address: Pointer, data: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeShorts(address: Pointer, data: ShortArray) {
        TODO("Not yet implemented")
    }

    override fun writeInts(address: Pointer, data: IntArray) {
        TODO("Not yet implemented")
    }

    override fun writeLongs(address: Pointer, data: LongArray) {
        TODO("Not yet implemented")
    }

    override fun writeNInts(address: Pointer, data: NIntArray) {
        TODO("Not yet implemented")
    }

    override fun writeFloats(address: Pointer, data: FloatArray) {
        TODO("Not yet implemented")
    }

    override fun writeDoubles(address: Pointer, data: DoubleArray) {
        TODO("Not yet implemented")
    }
}

@PublishedApi
internal actual fun getPlatformMemory(): Memory = TODO("Not yet implemented")