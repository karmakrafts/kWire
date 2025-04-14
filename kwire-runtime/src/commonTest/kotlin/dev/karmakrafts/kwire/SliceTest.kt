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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliceTest {
    @Test
    fun `constructor creates valid Slice`() = deferring {
        val memory by dropping { Memory.allocate(10.toNUInt()) }
        val slice = Slice(memory, 10.toNUInt())

        assertEquals(memory, slice.address, "Slice address should match the memory address")
        assertEquals(10.toNUInt(), slice.size, "Slice size should be 10")
    }

    @Test
    fun `toStringSlice converts to StringSlice`() = deferring {
        val text = "Hello"
        val cString by dropping { CString.allocate(text) }
        val slice = Slice(cString.address, text.length.toUInt().toNUInt())

        val stringSlice = slice.toStringSlice()

        assertEquals(slice.address, stringSlice.address, "StringSlice address should match Slice address")
        assertEquals(slice.size, stringSlice.nativeLength, "StringSlice nativeLength should match Slice size")
        assertEquals(text, stringSlice.toString(), "StringSlice content should match original text")
    }

    @Test
    fun `copyTo copies content to another slice`() = deferring {
        val source by dropping { Memory.allocate(5.toNUInt()) }
        val dest by dropping { Memory.allocate(5.toNUInt()) }

        // Initialize source with some data
        for (i in 0 until 5) {
            Memory.writeByte(source + i.toNUInt(), (i + 1).toByte())
        }

        val sourceSlice = Slice(source, 5.toNUInt())
        val destSlice = Slice(dest, 5.toNUInt())

        sourceSlice.copyTo(destSlice)

        // Verify the content was copied correctly
        for (i in 0 until 5) {
            assertEquals(
                Memory.readByte(source + i.toNUInt()),
                Memory.readByte(dest + i.toNUInt()),
                "Byte at index $i should be copied correctly"
            )
        }
    }

    @Test
    fun `copyToOverlapping handles overlapping memory regions`() = deferring {
        val memory by dropping { Memory.allocate(10.toNUInt()) }

        // Initialize memory with sequential values
        for (i in 0 until 10) {
            Memory.writeByte(memory + i.toNUInt(), (i + 1).toByte())
        }

        // Create overlapping slices
        val sourceSlice = Slice(memory, 5.toNUInt())
        val originalMemory by dropping { Memory.allocate(10.toNUInt()) }
        Memory.copy(memory, originalMemory, 10.toNUInt())
        val destSlice = Slice(memory + 3.toNUInt(), 5.toNUInt())

        sourceSlice.copyToOverlapping(destSlice)

        // Verify the content was copied correctly despite overlap
        for (i in 0 until 5) {
            assertEquals(
                Memory.readByte(originalMemory + i.toNUInt()),
                Memory.readByte(memory + (i + 3).toNUInt()),
                "Byte at index $i should be copied correctly to index ${i + 3}"
            )
        }
    }

    @Test
    fun `compareTo compares slices lexicographically`() = deferring {
        val memory1 by dropping { Memory.allocate(5.toNUInt()) }
        val memory2 by dropping { Memory.allocate(5.toNUInt()) }
        val memory3 by dropping { Memory.allocate(5.toNUInt()) }

        // Initialize memory1 with "ABCDE"
        for (i in 0 until 5) {
            Memory.writeByte(memory1 + i.toNUInt(), ('A'.code + i).toByte())
        }

        // Initialize memory2 with "ABCDE" (same as memory1)
        for (i in 0 until 5) {
            Memory.writeByte(memory2 + i.toNUInt(), ('A'.code + i).toByte())
        }

        // Initialize memory3 with "FGHIJ" (greater than memory1)
        for (i in 0 until 5) {
            Memory.writeByte(memory3 + i.toNUInt(), ('F'.code + i).toByte())
        }

        val slice1 = Slice(memory1, 5.toNUInt())
        val slice2 = Slice(memory2, 5.toNUInt())
        val slice3 = Slice(memory3, 5.toNUInt())

        assertEquals(0, slice1.compareTo(slice2), "Equal slices should return 0")
        assertTrue(slice1.compareTo(slice3) < 0, "ABCDE should be less than FGHIJ")
        assertTrue(slice3.compareTo(slice1) > 0, "FGHIJ should be greater than ABCDE")
    }

    @Test
    fun `subSlice creates correct sub-slice with indices`() = deferring {
        val memory by dropping { Memory.allocate(10.toNUInt()) }

        // Initialize memory with sequential values
        for (i in 0 until 10) {
            Memory.writeByte(memory + i.toNUInt(), (i + 1).toByte())
        }

        val slice = Slice(memory, 10.toNUInt())
        val subSlice = slice.subSlice(2, 7)

        assertEquals(memory + 2.toNUInt(), subSlice.address, "Subslice address should be offset by 2")
        assertEquals(5.toNUInt(), subSlice.size, "Subslice size should be 5")

        // Verify the content of the subslice
        for (i in 0 until 5) {
            assertEquals(
                Memory.readByte(memory + (i + 2).toNUInt()),
                Memory.readByte(subSlice.address + i.toNUInt()),
                "Byte at subslice index $i should match original memory at index ${i + 2}"
            )
        }
    }

    @Test
    fun `subSlice creates correct sub-slice with IntRange`() = deferring {
        val memory by dropping { Memory.allocate(10.toNUInt()) }

        // Initialize memory with sequential values
        for (i in 0 until 10) {
            Memory.writeByte(memory + i.toNUInt(), (i + 1).toByte())
        }

        val slice = Slice(memory, 10.toNUInt())
        val subSlice = slice.subSlice(2..6)

        assertEquals(memory + 2.toNUInt(), subSlice.address, "Subslice address should be offset by 2")
        assertEquals(5.toNUInt(), subSlice.size, "Subslice size should be 5")
    }

    @Test
    fun `get with IntRange returns correct sub-slice`() = deferring {
        val memory by dropping { Memory.allocate(10.toNUInt()) }

        // Initialize memory with sequential values
        for (i in 0 until 10) {
            Memory.writeByte(memory + i.toNUInt(), (i + 1).toByte())
        }

        val slice = Slice(memory, 10.toNUInt())
        val subSlice = slice[2..6]

        assertEquals(memory + 2.toNUInt(), subSlice.address, "Subslice address should be offset by 2")
        assertEquals(5.toNUInt(), subSlice.size, "Subslice size should be 5")
    }

    @Test
    fun `contentEquals compares content correctly`() = deferring {
        val memory1 by dropping { Memory.allocate(5.toNUInt()) }
        val memory2 by dropping { Memory.allocate(5.toNUInt()) }
        val memory3 by dropping { Memory.allocate(5.toNUInt()) }

        // Initialize memory1 and memory2 with the same content
        for (i in 0 until 5) {
            val value = (i + 1).toByte()
            Memory.writeByte(memory1 + i.toNUInt(), value)
            Memory.writeByte(memory2 + i.toNUInt(), value)
        }

        // Initialize memory3 with different content
        for (i in 0 until 5) {
            Memory.writeByte(memory3 + i.toNUInt(), (i + 10).toByte())
        }

        val slice1 = Slice(memory1, 5.toNUInt())
        val slice2 = Slice(memory2, 5.toNUInt())
        val slice3 = Slice(memory3, 5.toNUInt())

        assertTrue(slice1.contentEquals(slice2), "Slices with same content should be equal")
        assertFalse(slice1.contentEquals(slice3), "Slices with different content should not be equal")
    }

    @Test
    fun `addressEquals compares addresses correctly`() = deferring {
        val memory1 by dropping { Memory.allocate(5.toNUInt()) }
        val memory2 by dropping { Memory.allocate(5.toNUInt()) }

        val slice1 = Slice(memory1, 5.toNUInt())
        val slice2 = Slice(memory1, 3.toNUInt()) // Same address but different size
        val slice3 = Slice(memory2, 5.toNUInt()) // Different address

        assertTrue(slice1.addressEquals(slice2), "Slices with same address should be equal")
        assertFalse(slice1.addressEquals(slice3), "Slices with different addresses should not be equal")
    }

    @Test
    fun `toString returns correct string representation`() = deferring {
        val memory by dropping { Memory.allocate(5.toNUInt()) }

        // Initialize memory with bytes 1, 2, 3, 4, 5
        for (i in 0 until 5) {
            Memory.writeByte(memory + i.toNUInt(), (i + 1).toByte())
        }

        val slice = Slice(memory, 5.toNUInt())
        val stringRepresentation = slice.toString()

        // The toString method should include the address, size, and a preview of the content
        assertTrue(stringRepresentation.contains(memory.toString()), "String representation should include the address")
        assertTrue(stringRepresentation.contains("5"), "String representation should include the size")
        assertTrue(stringRepresentation.contains("0x01"), "String representation should include the content preview")
        assertTrue(stringRepresentation.contains("0x05"), "String representation should include the content preview")
    }
}
