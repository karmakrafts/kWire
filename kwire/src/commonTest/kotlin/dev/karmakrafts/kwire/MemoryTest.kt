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

@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.karmakrafts.kwire

import dev.karmakrafts.rakii.deferring
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MemoryTest {
    @Test
    fun `allocate returns valid pointer`() = deferring {
        val size = 16.toNUInt()
        val address by dropping { Memory.allocate(size) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Verify we can write to the allocated memory
        val bytePtr = address.asBytePtr()
        bytePtr[0] = 0x42.toByte()
        assertEquals(0x42.toByte(), bytePtr[0], "Should be able to write to and read from allocated memory")
    }

    @Test
    fun `allocate with alignment aligns the pointer`() = deferring {
        val size = 16.toNUInt()
        val alignment = 8.toNUInt()
        val address by dropping { Memory.allocate(size, alignment) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Verify the pointer is aligned
        assertEquals(0.toNUInt(), address.value % alignment, "Pointer should be aligned to the specified boundary")
    }

    @Test
    fun `reallocate preserves data`() = deferring {
        // Allocate initial memory and write data
        val initialSize = 4.toNUInt()
        val address = Memory.allocate(initialSize)
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val bytePtr = address.asBytePtr()
        bytePtr[0] = 0x11.toByte()
        bytePtr[1] = 0x22.toByte()
        bytePtr[2] = 0x33.toByte()
        bytePtr[3] = 0x44.toByte()

        // Reallocate to a larger size
        val newSize = 8.toNUInt()
        val newAddress by dropping { Memory.reallocate(address, newSize) }
        assertNotEquals(nullptr, newAddress, "Memory reallocation should return a valid pointer")

        // Verify original data is preserved
        val newBytePtr = newAddress.asBytePtr()
        assertEquals(0x11.toByte(), newBytePtr[0], "First byte should be preserved after reallocation")
        assertEquals(0x22.toByte(), newBytePtr[1], "Second byte should be preserved after reallocation")
        assertEquals(0x33.toByte(), newBytePtr[2], "Third byte should be preserved after reallocation")
        assertEquals(0x44.toByte(), newBytePtr[3], "Fourth byte should be preserved after reallocation")

        // Verify we can write to the new space
        newBytePtr[4] = 0x55.toByte()
        assertEquals(0x55.toByte(), newBytePtr[4], "Should be able to write to and read from newly allocated memory")
    }

    @Test
    fun `free releases memory`() = deferring {
        // This test can only verify that free doesn't crash, as we can't directly test
        // that memory has been released without accessing freed memory (which is undefined behavior)
        val address = Memory.allocate(16.toNUInt())
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Free the memory
        Memory.free(address)

        // If we reach here without crashing, the test passes
        // We can't verify the memory is actually freed without risking undefined behavior
    }

    @Test
    fun `Write and read byte`() = deferring {
        val address by dropping { Memory.allocate(Byte.SIZE_BYTES.toNUInt()).asBytePtr() }
        assertNotEquals(nullptr.reinterpret(), address, "BytePtr allocation should return a valid pointer")
        address[0] = 0xEE.toByte()
        assertEquals(0xEE.toByte(), address[0], "Read byte value should match the written value")
    }

    @Test
    fun `Write and read short`() = deferring {
        val address by dropping { Memory.allocate(Short.SIZE_BYTES.toNUInt()).asShortPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "ShortPtr allocation should return a valid pointer")
        address[0] = 0x1234.toShort()
        assertEquals(0x1234.toShort(), address[0], "Read short value should match the written value")
    }

    @Test
    fun `Write and read int`() = deferring {
        val address by dropping { Memory.allocate(Int.SIZE_BYTES.toNUInt()).asIntPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "IntPtr allocation should return a valid pointer")
        address[0] = 0x12345678
        assertEquals(0x12345678, address[0], "Read int value should match the written value")
    }

    @Test
    fun `Write and read long`() = deferring {
        val address by dropping { Memory.allocate(Long.SIZE_BYTES.toNUInt()).asLongPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "LongPtr allocation should return a valid pointer")
        address[0] = 0x123456789ABCDEF0L
        assertEquals(0x123456789ABCDEF0L, address[0], "Read long value should match the written value")
    }

    @Test
    fun `Write and read nint`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNIntPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "NIntPtr allocation should return a valid pointer")
        address[0] = 0x12345678.toNInt()
        assertEquals(0x12345678.toNInt(), address[0], "Read nint value should match the written value")
    }

    @Test
    fun `Write and read ubyte`() = deferring {
        val address by dropping { Memory.allocate(UByte.SIZE_BYTES.toNUInt()).asUBytePtr() }
        assertNotEquals(nullptr.reinterpret(), address, "UBytePtr allocation should return a valid pointer")
        address[0] = 0xEEu
        assertEquals(0xEEu, address[0], "Read ubyte value should match the written value")
    }

    @Test
    fun `Write and read ushort`() = deferring {
        val address by dropping { Memory.allocate(UShort.SIZE_BYTES.toNUInt()).asUShortPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "UShortPtr allocation should return a valid pointer")
        address[0] = 0x1234u
        assertEquals(0x1234u, address[0], "Read ushort value should match the written value")
    }

    @Test
    fun `Write and read uint`() = deferring {
        val address by dropping { Memory.allocate(UInt.SIZE_BYTES.toNUInt()).asUIntPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "UIntPtr allocation should return a valid pointer")
        address[0] = 0x12345678u
        assertEquals(0x12345678u, address[0], "Read uint value should match the written value")
    }

    @Test
    fun `Write and read ulong`() = deferring {
        val address by dropping { Memory.allocate(ULong.SIZE_BYTES.toNUInt()).asULongPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "ULongPtr allocation should return a valid pointer")
        address[0] = 0x123456789ABCDEF0uL
        assertEquals(0x123456789ABCDEF0uL, address[0], "Read ulong value should match the written value")
    }

    @Test
    fun `Write and read nuint`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNUIntPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "NUIntPtr allocation should return a valid pointer")
        address[0] = 0x12345678u.toNUInt()
        assertEquals(0x12345678u.toNUInt(), address[0], "Read nuint value should match the written value")
    }

    @Test
    fun `Write and read float`() = deferring {
        val address by dropping { Memory.allocate(Float.SIZE_BYTES.toNUInt()).asFloatPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "FloatPtr allocation should return a valid pointer")
        address[0] = 3.14159f
        assertEquals(3.14159f, address[0], "Read float value should match the written value")
    }

    @Test
    fun `Write and read double`() = deferring {
        val address by dropping { Memory.allocate(Double.SIZE_BYTES.toNUInt()).asDoublePtr() }
        assertNotEquals(nullptr.reinterpret(), address, "DoublePtr allocation should return a valid pointer")
        address[0] = 3.14159265358979
        assertEquals(3.14159265358979, address[0], "Read double value should match the written value")
    }

    @Test
    fun `Write and read pointer`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asPointerPtr() }
        assertNotEquals(nullptr.reinterpret(), address, "PointerPtr allocation should return a valid pointer")
        val testPointer = Memory.allocate(4.toNUInt())
        address[0] = testPointer
        assertEquals(testPointer, address[0], "Read pointer value should match the written value")
        Memory.free(testPointer)
    }

    @Test
    fun `Set fills memory with byte value`() = deferring {
        val size = 10.toNUInt()
        val address by dropping { Memory.allocate(size) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Set memory to a specific byte value
        val fillValue = 0x42.toByte()
        Memory.set(address, fillValue, size)

        // Verify all bytes are set to the fill value
        val bytePtr = address.asBytePtr()
        for (i in 0 until 10) {
            assertEquals(fillValue, bytePtr[i], "Byte at index $i should be set to $fillValue")
        }
    }

    @Test
    fun `set with zero size does nothing`() = deferring {
        val address by dropping { Memory.allocate(10.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Set initial values
        val bytePtr = address.asBytePtr()
        bytePtr[0] = 0x1
        bytePtr[1] = 0x2

        // Call set with zero size
        Memory.set(address, 0x42, 0.toNUInt())

        // Verify values are unchanged
        assertEquals(0x1, bytePtr[0], "Value at index 0 should remain unchanged when set is called with zero size")
        assertEquals(0x2, bytePtr[1], "Value at index 1 should remain unchanged when set is called with zero size")
    }

    @Test
    fun `copy transfers data between non-overlapping regions`() = deferring {
        val size = 10.toNUInt()
        val source by dropping { Memory.allocate(size) }
        val dest by dropping { Memory.allocate(size) }
        assertNotEquals(nullptr, source, "Source memory allocation should return a valid pointer")
        assertNotEquals(nullptr, dest, "Destination memory allocation should return a valid pointer")

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
    }

    @Test
    fun `copy with zero size does nothing`() = deferring {
        val source by dropping { Memory.allocate(10.toNUInt()) }
        val dest by dropping { Memory.allocate(10.toNUInt()) }
        assertNotEquals(nullptr, source, "Source memory allocation should return a valid pointer")
        assertNotEquals(nullptr, dest, "Destination memory allocation should return a valid pointer")

        // Initialize source and destination with different values
        val sourcePtr = source.asBytePtr()
        val destPtr = dest.asBytePtr()
        sourcePtr[0] = 0x1
        destPtr[0] = 0x2

        // Copy with zero size
        Memory.copy(source, dest, 0.toNUInt())

        // Verify destination is unchanged
        assertEquals(0x2, destPtr[0], "Destination value should remain unchanged when copy is called with zero size")
    }

    @Test
    fun `copy overlapping handles overlapping regions source before dest`() = deferring {
        val size = 10.toNUInt()
        val buffer by dropping { Memory.allocate(size + 5.toNUInt()) }
        assertNotEquals(nullptr, buffer, "Buffer memory allocation should return a valid pointer")

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
    }

    @Test
    fun `copy overlapping handles overlapping regions source after dest`() = deferring {
        val size = 10.toNUInt()
        val buffer by dropping { Memory.allocate(size + 5.toNUInt()) }
        assertNotEquals(nullptr, buffer, "Buffer memory allocation should return a valid pointer")

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
    }

    @Test
    fun `compare returns 0 for identical regions`() = deferring {
        val size = 10.toNUInt()
        val first by dropping { Memory.allocate(size) }
        val second by dropping { Memory.allocate(size) }
        assertNotEquals(nullptr, first, "First memory allocation should return a valid pointer")
        assertNotEquals(nullptr, second, "Second memory allocation should return a valid pointer")

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
    }

    @Test
    fun `compare returns non-zero for different regions`() = deferring {
        val size = 10.toNUInt()
        val first by dropping { Memory.allocate(size) }
        val second by dropping { Memory.allocate(size) }
        assertNotEquals(nullptr, first, "First memory allocation should return a valid pointer")
        assertNotEquals(nullptr, second, "Second memory allocation should return a valid pointer")

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
    }

    @Test
    fun `compare returns 0 for empty regions`() = deferring {
        val first by dropping { Memory.allocate(10.toNUInt()) }
        val second by dropping { Memory.allocate(10.toNUInt()) }
        assertNotEquals(nullptr, first, "First memory allocation should return a valid pointer")
        assertNotEquals(nullptr, second, "Second memory allocation should return a valid pointer")

        // Initialize regions with different data
        val firstPtr = first.asBytePtr()
        val secondPtr = second.asBytePtr()
        firstPtr[0] = 0x1
        secondPtr[0] = 0x2

        // Compare with zero size
        val result = Memory.compare(first, second, 0.toNUInt())

        // Verify result is 0 (empty regions are considered identical)
        assertEquals(0, result, "Compare should return 0 for empty memory regions")
    }

    // Direct Memory interface read/write tests

    @Test
    fun `readByte and writeByte`() = deferring {
        val address by dropping { Memory.allocate(Byte.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeByte(address, 0x42.toByte())
        val result = Memory.readByte(address)

        assertEquals(0x42.toByte(), result, "Read byte value should match the written value")
    }

    @Test
    fun `readShort and writeShort`() = deferring {
        val address by dropping { Memory.allocate(Short.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeShort(address, 0x1234.toShort())
        val result = Memory.readShort(address)

        assertEquals(0x1234.toShort(), result, "Read short value should match the written value")
    }

    @Test
    fun `readInt and writeInt`() = deferring {
        val address by dropping { Memory.allocate(Int.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeInt(address, 0x12345678)
        val result = Memory.readInt(address)

        assertEquals(0x12345678, result, "Read int value should match the written value")
    }

    @Test
    fun `readLong and writeLong`() = deferring {
        val address by dropping { Memory.allocate(Long.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeLong(address, 0x123456789ABCDEF0L)
        val result = Memory.readLong(address)

        assertEquals(0x123456789ABCDEF0L, result, "Read long value should match the written value")
    }

    @Test
    fun `readNInt and writeNInt`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeNInt(address, 0x12345678.toNInt())
        val result = Memory.readNInt(address)

        assertEquals(0x12345678.toNInt(), result, "Read nint value should match the written value")
    }

    @Test
    fun `readPointer and writePointer`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testPointer = Memory.allocate(4.toNUInt())
        Memory.writePointer(address, testPointer)
        val result = Memory.readPointer(address)

        assertEquals(testPointer, result, "Read pointer value should match the written value")
        Memory.free(testPointer)
    }

    @Test
    fun `readFloat and writeFloat`() = deferring {
        val address by dropping { Memory.allocate(Float.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeFloat(address, 3.14159f)
        val result = Memory.readFloat(address)

        assertEquals(3.14159f, result, "Read float value should match the written value")
    }

    @Test
    fun `readDouble and writeDouble`() = deferring {
        val address by dropping { Memory.allocate(Double.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeDouble(address, 3.14159265358979)
        val result = Memory.readDouble(address)

        assertEquals(3.14159265358979, result, "Read double value should match the written value")
    }

    @Test
    fun `readUByte and writeUByte`() = deferring {
        val address by dropping { Memory.allocate(UByte.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeUByte(address, 0xEEu)
        val result = Memory.readUByte(address)

        assertEquals(0xEEu, result, "Read ubyte value should match the written value")
    }

    @Test
    fun `readUShort and writeUShort`() = deferring {
        val address by dropping { Memory.allocate(UShort.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeUShort(address, 0x1234u)
        val result = Memory.readUShort(address)

        assertEquals(0x1234u, result, "Read ushort value should match the written value")
    }

    @Test
    fun `readUInt and writeUInt`() = deferring {
        val address by dropping { Memory.allocate(UInt.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeUInt(address, 0x12345678u)
        val result = Memory.readUInt(address)

        assertEquals(0x12345678u, result, "Read uint value should match the written value")
    }

    @Test
    fun `readULong and writeULong`() = deferring {
        val address by dropping { Memory.allocate(ULong.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeULong(address, 0x123456789ABCDEF0uL)
        val result = Memory.readULong(address)

        assertEquals(0x123456789ABCDEF0uL, result, "Read ulong value should match the written value")
    }

    @Test
    fun `readNUInt and writeNUInt`() = deferring {
        val address by dropping { Memory.allocate(Pointer.SIZE_BYTES.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        Memory.writeNUInt(address, 0x12345678u.toNUInt())
        val result = Memory.readNUInt(address)

        assertEquals(0x12345678u.toNUInt(), result, "Read nuint value should match the written value")
    }

    // Array read/write tests

    @Test
    fun `readBytes and writeBytes`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate(size.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = byteArrayOf(0x10, 0x20, 0x30, 0x40, 0x50)
        Memory.writeBytes(address, testData)

        val result = ByteArray(size)
        Memory.readBytes(address, result)

        assertContentEquals(testData, result, "Read byte array should match the written array")
    }

    @Test
    fun `readShorts and writeShorts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Short.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = shortArrayOf(0x1010, 0x2020, 0x3030, 0x4040, 0x5050)
        Memory.writeShorts(address, testData)

        val result = ShortArray(size)
        Memory.readShorts(address, result)

        assertContentEquals(testData, result, "Read short array should match the written array")
    }

    @Test
    fun `readInts and writeInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Int.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = intArrayOf(0x10101010, 0x20202020, 0x30303030, 0x40404040, 0x50505050)
        Memory.writeInts(address, testData)

        val result = IntArray(size)
        Memory.readInts(address, result)

        assertContentEquals(testData, result, "Read int array should match the written array")
    }

    @Test
    fun `readLongs and writeLongs`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Long.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = longArrayOf(
            0x1010101010101010L, 0x2020202020202020L, 0x3030303030303030L, 0x4040404040404040L, 0x5050505050505050L
        )
        Memory.writeLongs(address, testData)

        val result = LongArray(size)
        Memory.readLongs(address, result)

        assertContentEquals(testData, result, "Read long array should match the written array")
    }

    @Test
    fun `readNInts and writeNInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = nIntArray(size).apply {
            this[0] = 0x10101010.toNInt()
            this[1] = 0x20202020.toNInt()
            this[2] = 0x30303030.toNInt()
            this[3] = 0x40404040.toNInt()
            this[4] = 0x50505050.toNInt()
        }
        Memory.writeNInts(address, testData)

        val result = nIntArray(size)
        Memory.readNInts(address, result)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "NInt at index $i should match")
        }
    }

    @Test
    fun `readFloats and writeFloats`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Float.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = floatArrayOf(1.1f, 2.2f, 3.3f, 4.4f, 5.5f)
        Memory.writeFloats(address, testData)

        val result = FloatArray(size)
        Memory.readFloats(address, result)

        assertContentEquals(testData, result, "Read float array should match the written array")
    }

    @Test
    fun `readDoubles and writeDoubles`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Double.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = doubleArrayOf(1.1, 2.2, 3.3, 4.4, 5.5)
        Memory.writeDoubles(address, testData)

        val result = DoubleArray(size)
        Memory.readDoubles(address, result)

        assertContentEquals(testData, result, "Read double array should match the written array")
    }

    @Test
    fun `readPointers and writePointers`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Create test pointers
        val testPointers = Array(size) { Memory.allocate(4.toNUInt()) }
        val testData = pointerArray(size).apply {
            for (i in 0 until size) {
                this[i] = testPointers[i]
            }
        }

        Memory.writePointers(address, testData)

        val result = pointerArray(size)
        Memory.readPointers(address, result)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "Pointer at index $i should match")
        }

        // Free test pointers
        for (ptr in testPointers) {
            Memory.free(ptr)
        }
    }

    @Test
    fun `readUBytes and writeUBytes`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate(size.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ubyteArrayOf(0x10u, 0x20u, 0x30u, 0x40u, 0x50u)
        Memory.writeUBytes(address, testData)

        val result = UByteArray(size)
        Memory.readUBytes(address, result)

        assertContentEquals(testData, result, "Read ubyte array should match the written array")
    }

    @Test
    fun `readUShorts and writeUShorts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * UShort.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ushortArrayOf(0x1010u, 0x2020u, 0x3030u, 0x4040u, 0x5050u)
        Memory.writeUShorts(address, testData)

        val result = UShortArray(size)
        Memory.readUShorts(address, result)

        assertContentEquals(testData, result, "Read ushort array should match the written array")
    }

    @Test
    fun `readUInts and writeUInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * UInt.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = uintArrayOf(0x10101010u, 0x20202020u, 0x30303030u, 0x40404040u, 0x50505050u)
        Memory.writeUInts(address, testData)

        val result = UIntArray(size)
        Memory.readUInts(address, result)

        assertContentEquals(testData, result, "Read uint array should match the written array")
    }

    @Test
    fun `readULongs and writeULongs`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * ULong.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ulongArrayOf(
            0x1010101010101010uL, 0x2020202020202020uL, 0x3030303030303030uL, 0x4040404040404040uL, 0x5050505050505050uL
        )
        Memory.writeULongs(address, testData)

        val result = ULongArray(size)
        Memory.readULongs(address, result)

        assertContentEquals(testData, result, "Read ulong array should match the written array")
    }

    @Test
    fun `readNUInts and writeNUInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = nUIntArray(size).apply {
            this[0] = 0x10101010u.toNUInt()
            this[1] = 0x20202020u.toNUInt()
            this[2] = 0x30303030u.toNUInt()
            this[3] = 0x40404040u.toNUInt()
            this[4] = 0x50505050u.toNUInt()
        }
        Memory.writeNUInts(address, testData)

        val result = nUIntArray(size)
        Memory.readNUInts(address, result)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "NUInt at index $i should match")
        }
    }

    // Allocating read tests

    @Test
    fun `allocating readBytes`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate(size.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = byteArrayOf(0x10, 0x20, 0x30, 0x40, 0x50)
        Memory.writeBytes(address, testData)

        val result = Memory.readBytes(address, size)

        assertContentEquals(testData, result, "Allocating read byte array should match the written array")
    }

    @Test
    fun `allocating readShorts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Short.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = shortArrayOf(0x1010, 0x2020, 0x3030, 0x4040, 0x5050)
        Memory.writeShorts(address, testData)

        val result = Memory.readShorts(address, size)

        assertContentEquals(testData, result, "Allocating read short array should match the written array")
    }

    @Test
    fun `allocating readInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Int.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = intArrayOf(0x10101010, 0x20202020, 0x30303030, 0x40404040, 0x50505050)
        Memory.writeInts(address, testData)

        val result = Memory.readInts(address, size)

        assertContentEquals(testData, result, "Allocating read int array should match the written array")
    }

    @Test
    fun `allocating readLongs`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Long.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = longArrayOf(
            0x1010101010101010L, 0x2020202020202020L, 0x3030303030303030L, 0x4040404040404040L, 0x5050505050505050L
        )
        Memory.writeLongs(address, testData)

        val result = Memory.readLongs(address, size)

        assertContentEquals(testData, result, "Allocating read long array should match the written array")
    }

    @Test
    fun `allocating readNInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = nIntArray(size).apply {
            this[0] = 0x10101010.toNInt()
            this[1] = 0x20202020.toNInt()
            this[2] = 0x30303030.toNInt()
            this[3] = 0x40404040.toNInt()
            this[4] = 0x50505050.toNInt()
        }
        Memory.writeNInts(address, testData)

        val result = Memory.readNInts(address, size)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "NInt at index $i should match")
        }
    }

    @Test
    fun `allocating readFloats`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Float.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = floatArrayOf(1.1f, 2.2f, 3.3f, 4.4f, 5.5f)
        Memory.writeFloats(address, testData)

        val result = Memory.readFloats(address, size)

        assertContentEquals(testData, result, "Allocating read float array should match the written array")
    }

    @Test
    fun `allocating readDoubles`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Double.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = doubleArrayOf(1.1, 2.2, 3.3, 4.4, 5.5)
        Memory.writeDoubles(address, testData)

        val result = Memory.readDoubles(address, size)

        assertContentEquals(testData, result, "Allocating read double array should match the written array")
    }

    @Test
    fun `allocating readPointers`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        // Create test pointers
        val testPointers = Array(size) { Memory.allocate(4.toNUInt()) }
        val testData = pointerArray(size).apply {
            for (i in 0 until size) {
                this[i] = testPointers[i]
            }
        }

        Memory.writePointers(address, testData)

        val result = Memory.readPointers(address, size)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "Pointer at index $i should match")
        }

        // Free test pointers
        for (ptr in testPointers) {
            Memory.free(ptr)
        }
    }

    @Test
    fun `allocating readUBytes`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate(size.toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ubyteArrayOf(0x10u, 0x20u, 0x30u, 0x40u, 0x50u)
        Memory.writeUBytes(address, testData)

        val result = Memory.readUBytes(address, size)

        assertContentEquals(testData, result, "Allocating read ubyte array should match the written array")
    }

    @Test
    fun `allocating readUShorts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * UShort.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ushortArrayOf(0x1010u, 0x2020u, 0x3030u, 0x4040u, 0x5050u)
        Memory.writeUShorts(address, testData)

        val result = Memory.readUShorts(address, size)

        assertContentEquals(testData, result, "Allocating read ushort array should match the written array")
    }

    @Test
    fun `allocating readUInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * UInt.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = uintArrayOf(0x10101010u, 0x20202020u, 0x30303030u, 0x40404040u, 0x50505050u)
        Memory.writeUInts(address, testData)

        val result = Memory.readUInts(address, size)

        assertContentEquals(testData, result, "Allocating read uint array should match the written array")
    }

    @Test
    fun `allocating readULongs`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * ULong.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = ulongArrayOf(
            0x1010101010101010uL, 0x2020202020202020uL, 0x3030303030303030uL, 0x4040404040404040uL, 0x5050505050505050uL
        )
        Memory.writeULongs(address, testData)

        val result = Memory.readULongs(address, size)

        assertContentEquals(testData, result, "Allocating read ulong array should match the written array")
    }

    @Test
    fun `allocating readNUInts`() = deferring {
        val size = 5
        val address by dropping { Memory.allocate((size * Pointer.SIZE_BYTES).toNUInt()) }
        assertNotEquals(nullptr, address, "Memory allocation should return a valid pointer")

        val testData = nUIntArray(size).apply {
            this[0] = 0x10101010u.toNUInt()
            this[1] = 0x20202020u.toNUInt()
            this[2] = 0x30303030u.toNUInt()
            this[3] = 0x40404040u.toNUInt()
            this[4] = 0x50505050u.toNUInt()
        }
        Memory.writeNUInts(address, testData)

        val result = Memory.readNUInts(address, size)

        for (i in 0 until size) {
            assertEquals(testData[i], result[i], "NUInt at index $i should match")
        }
    }
}
