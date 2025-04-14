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

import dev.karmakrafts.rakii.deferring
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class StringSliceTest {
    @Test
    fun `constructor creates valid StringSlice`() = deferring {
        val cString by dropping { CString.allocate("Hello") }
        val slice = cString.toStringSlice()

        assertEquals(5, slice.length, "StringSlice length should be 5")
        assertEquals(5L, slice.longLength, "StringSlice longLength should be 5L")
        assertEquals(5U.toNUInt(), slice.nativeLength, "StringSlice nativeLength should be 5U")
        assertEquals("Hello", slice.toString(), "StringSlice should convert to 'Hello'")
    }

    @Test
    fun `fromCString creates StringSlice from CString`() = deferring {
        val cString by dropping { CString.allocate("Test String") }
        val slice = cString.toStringSlice()

        assertEquals(cString.length, slice.length, "StringSlice length should match CString length")
        assertEquals(cString.toString(), slice.toString(), "StringSlice content should match CString content")
    }

    @Test
    fun `toByteArray returns correct byte array`() = deferring {
        val value = "Test"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val byteArray = slice.toByteArray()
        assertContentEquals(value.encodeToByteArray(), byteArray, "Byte array should match the encoded string")
    }

    @Test
    fun `contentEquals compares string content correctly with StringSlice`() = deferring {
        val value1 = "Same Content"
        val value2 = "Same Content"
        val value3 = "Different"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        val slice1 = StringSlice(cString1.address, value1.length.toUInt().toNUInt())
        val slice2 = StringSlice(cString2.address, value2.length.toUInt().toNUInt())
        val slice3 = StringSlice(cString3.address, value3.length.toUInt().toNUInt())

        assertTrue(slice1.contentEquals(slice2), "StringSlices with same content should be equal")
        assertFalse(slice1.contentEquals(slice3), "StringSlices with different content should not be equal")
    }

    @Test
    fun `contentEquals compares string content correctly with CString`() = deferring {
        val value = "Test String"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        assertTrue(slice.contentEquals(cString), "StringSlice should equal CString with same content")

        val differentCString by dropping { CString.allocate("Different") }
        assertFalse(
            slice.contentEquals(differentCString),
            "StringSlice should not equal CString with different content"
        )
    }

    @Test
    fun `compare returns correct comparison result with StringSlice`() = deferring {
        val value1 = "ABC"
        val value2 = "ABC"
        val value3 = "DEF"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        val slice1 = cString1.toStringSlice()
        val slice2 = cString2.toStringSlice()
        val slice3 = cString3.toStringSlice()

        assertEquals(0, slice1.compare(slice2), "Equal StringSlices should return 0")
        assertTrue(slice1.compare(slice3) < 0, "ABC should be less than DEF")
        assertTrue(slice3.compare(slice1) > 0, "DEF should be greater than ABC")
    }

    @Test
    fun `compare returns correct comparison result with CString`() = deferring {
        val value1 = "ABC"
        val value3 = "DEF"

        val cString1 by dropping { CString.allocate(value1) }
        val cString3 by dropping { CString.allocate(value3) }

        val slice1 = cString1.toStringSlice()

        assertEquals(0, slice1.compare(cString1), "Equal content should return 0")
        assertTrue(slice1.compare(cString3) < 0, "ABC should be less than DEF")
    }

    @Test
    fun `get with Int index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        assertEquals('H', slice[0], "Character at index 0 should be 'H'")
        assertEquals('e', slice[1], "Character at index 1 should be 'e'")
        assertEquals('l', slice[2], "Character at index 2 should be 'l'")
        assertEquals('l', slice[3], "Character at index 3 should be 'l'")
        assertEquals('o', slice[4], "Character at index 4 should be 'o'")
    }

    @Test
    fun `get with Long index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        assertEquals('H', slice[0L], "Character at index 0 should be 'H'")
        assertEquals('e', slice[1L], "Character at index 1 should be 'e'")
        assertEquals('l', slice[2L], "Character at index 2 should be 'l'")
        assertEquals('l', slice[3L], "Character at index 3 should be 'l'")
        assertEquals('o', slice[4L], "Character at index 4 should be 'o'")
    }

    @Test
    fun `get with NUInt index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        assertEquals('H', slice[0U.toNUInt()], "Character at index 0 should be 'H'")
        assertEquals('e', slice[1U.toNUInt()], "Character at index 1 should be 'e'")
        assertEquals('l', slice[2U.toNUInt()], "Character at index 2 should be 'l'")
        assertEquals('l', slice[3U.toNUInt()], "Character at index 3 should be 'l'")
        assertEquals('o', slice[4U.toNUInt()], "Character at index 4 should be 'o'")
    }

    @Test
    fun `subSequence returns correct substring`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val subSeq = slice.subSequence(7, 12)
        assertEquals("World", subSeq.toString(), "Subsequence should be 'World'")
    }

    @Test
    fun `subSlice returns correct substring as StringSlice`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val subSlice = slice.subSlice(7, 12)
        assertEquals("World", subSlice.toString(), "Subslice should be 'World'")
        assertEquals(5, subSlice.length, "Subslice length should be 5")
    }

    @Test
    fun `subSlice with IntRange returns correct substring`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val subSlice = slice.subSlice(0..4)
        assertEquals("Hello", subSlice.toString(), "Subslice should be 'Hello'")
    }

    @Test
    fun `get with IntRange returns correct substring`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val subSlice = slice[0..4]
        assertEquals("Hello", subSlice.toString(), "Range indexing should return 'Hello'")
    }

    @Test
    fun `intoCString creates a CString with the same content`() = deferring {
        val value = "Test String"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        val newCString by dropping { slice.intoCString() }

        assertEquals(slice.length, newCString.length, "CString should have the same length")
        assertEquals(slice.toString(), newCString.toString(), "CString should have the same content")
    }

    @Test
    fun `equals operator compares content correctly`() = deferring {
        val value1 = "Same Content"
        val value2 = "Same Content"
        val value3 = "Different"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        val slice1 = cString1.toStringSlice()
        val slice2 = cString2.toStringSlice()
        val slice3 = cString3.toStringSlice()

        assertEquals(slice1, slice2, "StringSlices with same content should be equal")
        assertNotEquals(slice1, slice3, "StringSlices with different content should not be equal")
        assertTrue(slice1.equals(cString1), "StringSlice should equal CString with same content")
        assertFalse(slice1.equals("Not a StringSlice"), "StringSlice should not equal a non-StringSlice object")
    }

    @Test
    fun `toString converts to correct Kotlin String`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }
        val slice = cString.toStringSlice()

        assertEquals(value, slice.toString(), "toString should return the correct string content")
    }

    @Test
    fun `hashCode returns consistent hash for same content`() = deferring {
        val value = "Test String"
        val cString1 by dropping { CString.allocate(value) }
        val cString2 by dropping { CString.allocate(value) }

        val slice1 = cString1.toStringSlice()
        val slice2 = cString2.toStringSlice()

        assertEquals(slice1.hashCode(), slice2.hashCode(), "Hash codes should be equal for equal content")
    }
}
