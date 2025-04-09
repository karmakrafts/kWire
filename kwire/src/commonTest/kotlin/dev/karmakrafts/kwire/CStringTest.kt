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
import kotlin.test.assertTrue

class CStringTest {
    @Test
    fun `allocate with NUInt creates valid CString`() = deferring {
        val length = 10.toNUInt()
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = 'H'.code.toByte()
        bytePtr[1] = 'e'.code.toByte()
        bytePtr[2] = 'l'.code.toByte()
        bytePtr[3] = 'l'.code.toByte()
        bytePtr[4] = 'o'.code.toByte()
        bytePtr[5] = 0 // Null terminator

        assertEquals(5, cString.length, "CString length should be 5 after writing 'Hello'")
        assertEquals("Hello", cString.toString(), "CString should convert to 'Hello'")
    }

    @Test
    fun `allocate with Long creates valid CString`() = deferring {
        val length = 10L
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = 'T'.code.toByte()
        bytePtr[1] = 'e'.code.toByte()
        bytePtr[2] = 's'.code.toByte()
        bytePtr[3] = 't'.code.toByte()
        bytePtr[4] = 0 // Null terminator

        assertEquals(4, cString.length, "CString length should be 4 after writing 'Test'")
        assertEquals("Test", cString.toString(), "CString should convert to 'Test'")
    }

    @Test
    fun `allocate with Int creates valid CString`() = deferring {
        val length = 10
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = 'A'.code.toByte()
        bytePtr[1] = 'B'.code.toByte()
        bytePtr[2] = 'C'.code.toByte()
        bytePtr[3] = 0 // Null terminator

        assertEquals(3, cString.length, "CString length should be 3 after writing 'ABC'")
        assertEquals("ABC", cString.toString(), "CString should convert to 'ABC'")
    }

    @Test
    fun `allocate with String creates valid CString`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }

        // Verify the string has the correct length and content
        assertEquals(value.length, cString.length, "CString length should match the original string")
        assertEquals(value, cString.toString(), "CString should convert back to the original string")
    }

    @Test
    fun `length properties return correct values`() = deferring {
        val value = "Test String"
        val cString by dropping { CString.allocate(value) }

        assertEquals(value.length, cString.length, "length should match the string length")
        assertEquals(value.length.toLong(), cString.longLength, "longLength should match the string length as Long")
        assertEquals(
            value.length.toUInt().toNUInt(),
            cString.nativeLength,
            "nativeLength should match the string length as NUInt"
        )
    }

    @Test
    fun `contentEquals compares string content correctly`() = deferring {
        val value1 = "Same Content"
        val value2 = "Same Content"
        val value3 = "Different"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        assertTrue(cString1.contentEquals(cString2), "Strings with same content should be equal")
        assertFalse(cString1.contentEquals(cString3), "Strings with different content should not be equal")
    }

    @Test
    fun `compare returns correct comparison result`() = deferring {
        val value1 = "ABC"
        val value2 = "ABC"
        val value3 = "DEF"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        assertEquals(0, cString1.compare(cString2), "Equal strings should return 0")
        assertTrue(cString1.compare(cString3) < 0, "ABC should be less than DEF")
        assertTrue(cString3.compare(cString1) > 0, "DEF should be greater than ABC")
    }

    @Test
    fun `toByteArray returns correct byte array`() = deferring {
        val value = "Test"
        val cString by dropping { CString.allocate(value) }

        val byteArray = cString.toByteArray()
        assertContentEquals(value.encodeToByteArray(), byteArray, "Byte array should match the encoded string")
    }

    @Test
    fun `get with Int index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }

        assertEquals('H', cString[0], "Character at index 0 should be 'H'")
        assertEquals('e', cString[1], "Character at index 1 should be 'e'")
        assertEquals('l', cString[2], "Character at index 2 should be 'l'")
        assertEquals('l', cString[3], "Character at index 3 should be 'l'")
        assertEquals('o', cString[4], "Character at index 4 should be 'o'")
    }

    @Test
    fun `get with Long index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }

        assertEquals('H', cString[0L], "Character at index 0 should be 'H'")
        assertEquals('e', cString[1L], "Character at index 1 should be 'e'")
        assertEquals('l', cString[2L], "Character at index 2 should be 'l'")
        assertEquals('l', cString[3L], "Character at index 3 should be 'l'")
        assertEquals('o', cString[4L], "Character at index 4 should be 'o'")
    }

    @Test
    fun `get with NUInt index returns correct character`() = deferring {
        val value = "Hello"
        val cString by dropping { CString.allocate(value) }

        assertEquals('H', cString[0U.toNUInt()], "Character at index 0 should be 'H'")
        assertEquals('e', cString[1U.toNUInt()], "Character at index 1 should be 'e'")
        assertEquals('l', cString[2U.toNUInt()], "Character at index 2 should be 'l'")
        assertEquals('l', cString[3U.toNUInt()], "Character at index 3 should be 'l'")
        assertEquals('o', cString[4U.toNUInt()], "Character at index 4 should be 'o'")
    }

    @Test
    fun `copy creates a deep copy`() = deferring {
        val value = "Original"
        val original by dropping { CString.allocate(value) }
        val copy by dropping { original.copy() }

        // Verify the copy has the same content
        assertEquals(original.toString(), copy.toString(), "Copy should have the same content as original")

        // Modify the original and verify the copy is unchanged
        val originalPtr = original.address.asBytePtr()
        originalPtr[0] = 'M'.code.toByte() // Change "Original" to "Mriginal"

        assertEquals("Mriginal", original.toString(), "Original should be modified")
        assertEquals("Original", copy.toString(), "Copy should remain unchanged")
    }

    @Test
    fun `asSlice creates a StringSlice with the same content`() = deferring {
        val value = "Test String"
        val cString by dropping { CString.allocate(value) }

        val slice = cString.toStringSlice()

        assertEquals(cString.length, slice.length, "StringSlice should have the same length")
        assertEquals(cString.toString(), slice.toString(), "StringSlice should have the same content")
    }

    @Test
    fun `subSequence returns correct substring`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }

        val subSeq = cString.subSequence(7, 12)
        assertEquals("World", subSeq.toString(), "Subsequence should be 'World'")
    }

    @Test
    fun `equals operator compares content correctly`() = deferring {
        val value1 = "Same Content"
        val value2 = "Same Content"
        val value3 = "Different"

        val cString1 by dropping { CString.allocate(value1) }
        val cString2 by dropping { CString.allocate(value2) }
        val cString3 by dropping { CString.allocate(value3) }

        assertTrue(cString1.equals(cString2), "Strings with same content should be equal")
        assertFalse(cString1.equals(cString3), "Strings with different content should not be equal")
        assertFalse(cString1.equals("Not a CString"), "CString should not equal a non-CString object")
    }

    @Test
    fun `toString converts to correct Kotlin String`() = deferring {
        val value = "Hello, World!"
        val cString by dropping { CString.allocate(value) }

        assertEquals(value, cString.toString(), "toString should return the correct string content")
    }

    @Test
    fun `hashCode returns consistent hash for same content`() = deferring {
        val value = "Test String"
        val cString1 by dropping { CString.allocate(value) }
        val cString2 by dropping { CString.allocate(value) }

        assertEquals(cString1.hashCode(), cString2.hashCode(), "Hash codes should be equal for equal content")
    }

    @Test
    fun `reinterpret converts to correct pointer types`() = deferring {
        val value = "Test"
        val cString by dropping { CString.allocate(value) }

        val pointer = cString.reinterpret<Pointer>()
        assertEquals(cString.address, pointer, "Should reinterpret to the same Pointer")

        val bytePtr = cString.reinterpret<BytePtr>()
        assertEquals(cString.address.asBytePtr().value, bytePtr.value, "Should reinterpret to the correct BytePtr")

        val sameCString = cString.reinterpret<CString>()
        assertEquals(cString.address, sameCString.address, "Should reinterpret to the same CString")
    }

    @Test
    fun `BytePtr toKString extension function works correctly`() = deferring {
        val value = "Test String"
        val cString by dropping { CString.allocate(value) }

        val bytePtr = cString.address.asBytePtr()
        assertEquals(value, bytePtr.toKString(), "BytePtr.toKString should return the correct string")
    }
}
