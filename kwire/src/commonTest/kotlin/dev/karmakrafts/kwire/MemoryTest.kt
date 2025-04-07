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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MemoryTest {
    companion object {
        private val testSize = (Int.SIZE_BYTES * 4).toNUInt()
    }

    @Test
    fun `Allocate and free`() {
        val address = Memory.allocate(testSize)
        assertNotEquals(nullptr, address)
        Memory.free(address)
    }

    @Test
    fun `Write and read byte`() {
        val address = Memory.allocate(Byte.SIZE_BYTES.toNUInt()).asBytePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0xEE.toByte()
        assertEquals(0xEE.toByte(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read short`() {
        val address = Memory.allocate(Short.SIZE_BYTES.toNUInt()).asShortPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x1234.toShort()
        assertEquals(0x1234.toShort(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read int`() {
        val address = Memory.allocate(Int.SIZE_BYTES.toNUInt()).asIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678
        assertEquals(0x12345678, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read long`() {
        val address = Memory.allocate(Long.SIZE_BYTES.toNUInt()).asLongPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x123456789ABCDEF0L
        assertEquals(0x123456789ABCDEF0L, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read nint`() {
        val address = Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678.toNInt()
        assertEquals(0x12345678.toNInt(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ubyte`() {
        val address = Memory.allocate(UByte.SIZE_BYTES.toNUInt()).asUBytePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0xEEu
        assertEquals(0xEEu, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ushort`() {
        val address = Memory.allocate(UShort.SIZE_BYTES.toNUInt()).asUShortPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x1234u
        assertEquals(0x1234u, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read uint`() {
        val address = Memory.allocate(UInt.SIZE_BYTES.toNUInt()).asUIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678u
        assertEquals(0x12345678u, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ulong`() {
        val address = Memory.allocate(ULong.SIZE_BYTES.toNUInt()).asULongPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x123456789ABCDEF0uL
        assertEquals(0x123456789ABCDEF0uL, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read nuint`() {
        val address = Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNUIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678u.toNUInt()
        assertEquals(0x12345678u.toNUInt(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read float`() {
        val address = Memory.allocate(Float.SIZE_BYTES.toNUInt()).asFloatPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 3.14159f
        assertEquals(3.14159f, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read double`() {
        val address = Memory.allocate(Double.SIZE_BYTES.toNUInt()).asDoublePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 3.14159265358979
        assertEquals(3.14159265358979, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Set fills memory with byte value`() {
        val size = 10.toNUInt()
        val address = Memory.allocate(size)
        assertNotEquals(nullptr, address)

        // Set memory to a specific byte value
        val fillValue = 0x42.toByte()
        Memory.set(address, fillValue, size)

        // Verify all bytes are set to the fill value
        val bytePtr = address.asBytePtr()
        for (i in 0 until 10) {
            assertEquals(fillValue, bytePtr[i], "Byte at index $i should be set to $fillValue")
        }

        Memory.free(address)
    }

    @Test
    fun `Set with zero size does nothing`() {
        val address = Memory.allocate(10.toNUInt())
        assertNotEquals(nullptr, address)

        // Set initial values
        val bytePtr = address.asBytePtr()
        bytePtr[0] = 0x1
        bytePtr[1] = 0x2

        // Call set with zero size
        Memory.set(address, 0x42, 0.toNUInt())

        // Verify values are unchanged
        assertEquals(0x1, bytePtr[0])
        assertEquals(0x2, bytePtr[1])

        Memory.free(address)
    }

    @Test
    fun `Copy transfers data between non-overlapping regions`() {
        val size = 10.toNUInt()
        val source = Memory.allocate(size)
        val dest = Memory.allocate(size)
        assertNotEquals(nullptr, source)
        assertNotEquals(nullptr, dest)

        // Initialize source with test data
        val sourcePtr = source.asBytePtr()
        for (i in 0 until 10) {
            sourcePtr[i] = (i + 1).toByte()
        }

        // Copy from source to destination
        Memory.copy(source, dest, size)

        // Verify destination has the same data as source
        val destPtr = dest.asBytePtr()
        for (i in 0 until 10) {
            assertEquals(sourcePtr[i], destPtr[i], "Byte at index $i should match source")
        }

        Memory.free(source)
        Memory.free(dest)
    }

    @Test
    fun `Copy with zero size does nothing`() {
        val source = Memory.allocate(10.toNUInt())
        val dest = Memory.allocate(10.toNUInt())
        assertNotEquals(nullptr, source)
        assertNotEquals(nullptr, dest)

        // Initialize source and destination with different values
        val sourcePtr = source.asBytePtr()
        val destPtr = dest.asBytePtr()
        sourcePtr[0] = 0x1
        destPtr[0] = 0x2

        // Copy with zero size
        Memory.copy(source, dest, 0.toNUInt())

        // Verify destination is unchanged
        assertEquals(0x2, destPtr[0])

        Memory.free(source)
        Memory.free(dest)
    }

    @Test
    fun `Copy overlapping handles overlapping regions source before dest`() {
        val size = 10.toNUInt()
        val buffer = Memory.allocate(size + 5.toNUInt())
        assertNotEquals(nullptr, buffer)

        // Initialize buffer with test data
        val bufferPtr = buffer.asBytePtr()
        for (i in 0 until 10) {
            bufferPtr[i] = (i + 1).toByte()
        }

        // Create overlapping source and destination (source before destination)
        val source = buffer
        val dest = buffer + 5.toNUInt()

        // Copy from source to destination (overlapping)
        Memory.copyOverlapping(source, dest, 5.toNUInt())

        // Verify destination has correct data
        for (i in 0 until 5) {
            assertEquals(bufferPtr[i], bufferPtr[i + 5], "Byte at index ${i + 5} should match source at index $i")
        }

        Memory.free(buffer)
    }

    @Test
    fun `Copy overlapping handles overlapping regions source after dest`() {
        val size = 10.toNUInt()
        val buffer = Memory.allocate(size + 5.toNUInt())
        assertNotEquals(nullptr, buffer)

        // Initialize buffer with test data
        val bufferPtr = buffer.asBytePtr()
        for (i in 0 until 15) {
            bufferPtr[i] = (i + 1).toByte()
        }

        // Create overlapping source and destination (source after destination)
        val source = buffer + 5.toNUInt()
        val dest = buffer

        // Copy from source to destination (overlapping)
        Memory.copyOverlapping(source, dest, 5.toNUInt())

        // Verify destination has correct data
        for (i in 0 until 5) {
            assertEquals(bufferPtr[i + 5], bufferPtr[i], "Byte at index $i should match source at index ${i + 5}")
        }

        Memory.free(buffer)
    }

    @Test
    fun `Compare returns 0 for identical regions`() {
        val size = 10.toNUInt()
        val first = Memory.allocate(size)
        val second = Memory.allocate(size)
        assertNotEquals(nullptr, first)
        assertNotEquals(nullptr, second)

        // Initialize both regions with the same data
        val firstPtr = first.asBytePtr()
        val secondPtr = second.asBytePtr()
        for (i in 0 until 10) {
            val value = (i + 1).toByte()
            firstPtr[i] = value
            secondPtr[i] = value
        }

        // Compare the regions
        val result = Memory.compare(first, second, size)

        // Verify result is 0 (identical)
        assertEquals(0, result, "Compare should return 0 for identical memory regions")

        Memory.free(first)
        Memory.free(second)
    }

    @Test
    fun `Compare returns non-zero for different regions`() {
        val size = 10.toNUInt()
        val first = Memory.allocate(size)
        val second = Memory.allocate(size)
        assertNotEquals(nullptr, first)
        assertNotEquals(nullptr, second)

        // Initialize regions with different data
        val firstPtr = first.asBytePtr()
        val secondPtr = second.asBytePtr()
        for (i in 0 until 10) {
            firstPtr[i] = (i + 1).toByte()
            secondPtr[i] = (i + 2).toByte()
        }

        // Compare the regions
        val result = Memory.compare(first, second, size)

        // Verify result is not 0 (different)
        assertNotEquals(0, result, "Compare should return non-zero for different memory regions")

        Memory.free(first)
        Memory.free(second)
    }

    @Test
    fun `Compare returns 0 for empty regions`() {
        val first = Memory.allocate(10.toNUInt())
        val second = Memory.allocate(10.toNUInt())
        assertNotEquals(nullptr, first)
        assertNotEquals(nullptr, second)

        // Initialize regions with different data
        val firstPtr = first.asBytePtr()
        val secondPtr = second.asBytePtr()
        firstPtr[0] = 0x1
        secondPtr[0] = 0x2

        // Compare with zero size
        val result = Memory.compare(first, second, 0.toNUInt())

        // Verify result is 0 (empty regions are considered identical)
        assertEquals(0, result, "Compare should return 0 for empty memory regions")

        Memory.free(first)
        Memory.free(second)
    }
}
