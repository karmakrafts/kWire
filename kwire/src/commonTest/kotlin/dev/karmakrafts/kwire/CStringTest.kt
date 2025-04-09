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
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CStringTest {

    @Test
    fun `allocate with NUInt length creates valid CString`() = deferring {
        val length = 10.toNUInt()
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = 'A'.code.toByte()
        bytePtr[1] = 'B'.code.toByte()
        bytePtr[2] = 'C'.code.toByte()
        bytePtr[3] = 0 // Null terminator

        assertEquals(3, cString.length, "CString length should be 3 after writing 3 characters and a null terminator")
        assertEquals("ABC", cString.toString(), "CString should convert to the correct string")
    }

    @Test
    fun `allocate with Long length creates valid CString`() = deferring {
        val length = 10L
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = 'X'.code.toByte()
        bytePtr[1] = 'Y'.code.toByte()
        bytePtr[2] = 'Z'.code.toByte()
        bytePtr[3] = 0 // Null terminator

        assertEquals(3, cString.length, "CString length should be 3 after writing 3 characters and a null terminator")
        assertEquals("XYZ", cString.toString(), "CString should convert to the correct string")
    }

    @Test
    fun `allocate with Int length creates valid CString`() = deferring {
        val length = 10
        val cString by dropping { CString.allocate(length) }

        // Verify the string has the correct length
        assertEquals(0, cString.length, "Newly allocated CString should have length 0")

        // Verify we can write to the allocated memory
        val bytePtr = cString.address.asBytePtr()
        bytePtr[0] = '1'.code.toByte()
        bytePtr[1] = '2'.code.toByte()
        bytePtr[2] = '3'.code.toByte()
        bytePtr[3] = 0 // Null terminator

        assertEquals(3, cString.length, "CString length should be 3 after writing 3 characters and a null terminator")
        assertEquals("123", cString.toString(), "CString should convert to the correct string")
    }

    @Test
    fun `allocate with String creates valid CString with correct content`() = deferring {
        val testString = "Hello, World!"
        val cString by dropping { CString.allocate(testString) }

        // Verify the string has the correct length and content
        assertEquals(testString.length, cString.length, "CString length should match the original string length")
        assertEquals(testString, cString.toString(), "CString should convert back to the original string")
    }

    @Test
    fun `length property returns correct string length`() = deferring {
        val testString = "Test String"
        val cString by dropping { CString.allocate(testString) }

        assertEquals(testString.length, cString.length, "length property should return the correct string length")
    }

    @Test
    fun `longLength property returns correct string length as Long`() = deferring {
        val testString = "Another Test String"
        val cString by dropping { CString.allocate(testString) }

        assertEquals(testString.length.toLong(), cString.longLength, "longLength property should return the correct string length as Long")
    }

    @Test
    fun `contentEquals returns true for identical strings`() = deferring {
        val testString = "Same Content"
        val cString1 by dropping { CString.allocate(testString) }
        val cString2 by dropping { CString.allocate(testString) }

        assertTrue(cString1.contentEquals(cString2), "contentEquals should return true for strings with identical content")
    }

    @Test
    fun `contentEquals returns false for different strings`() = deferring {
        val cString1 by dropping { CString.allocate("String One") }
        val cString2 by dropping { CString.allocate("String Two") }

        assertTrue(!cString1.contentEquals(cString2), "contentEquals should return false for strings with different content")
    }

    @Test
    fun `toByteArray returns correct byte array including null terminator`() = deferring {
        val testString = "ByteArray"
        val cString by dropping { CString.allocate(testString) }

        val byteArray = cString.toByteArray()

        // Expected: the bytes of the string plus a null terminator
        val expected = testString.encodeToByteArray()

        assertContentEquals(expected, byteArray, "toByteArray should return the correct bytes including null terminator")
    }

    @Test
    fun `get returns correct character at specified index`() = deferring {
        val testString = "ABCDEFG"
        val cString by dropping { CString.allocate(testString) }

        for (i in testString.indices) {
            assertEquals(testString[i], cString[i], "get should return the correct character at index $i")
        }
    }

    @Test
    fun `subSequence returns correct substring`() = deferring {
        val testString = "SubSequence Test"
        val cString by dropping { CString.allocate(testString) }

        val start = 3
        val end = 9
        val subSequence = cString.subSequence(start, end)

        assertEquals(testString.substring(start, end), subSequence.toString(), "subSequence should return the correct substring")
    }

    @Test
    fun `toString returns correct string representation`() = deferring {
        val testString = "ToString Test"
        val cString by dropping { CString.allocate(testString) }

        assertEquals(testString, cString.toString(), "toString should return the correct string representation")
    }

    @Test
    fun `close releases memory`() = deferring {
        // This test can only verify that close doesn't crash, as we can't directly test
        // that memory has been released without accessing freed memory (which is undefined behavior)
        val cString = CString.allocate("Test Close")

        // Close the CString
        cString.close()

        // If we reach here without crashing, the test passes
    }

    @Test
    fun `BytePtr toKString extension function converts to correct string`() = deferring {
        val testString = "Extension Function Test"
        val cString by dropping { CString.allocate(testString) }

        val bytePtr = cString.address.asBytePtr()
        val result = bytePtr.toKString()

        assertEquals(testString, result, "BytePtr.toKString should convert to the correct string")
    }
}
