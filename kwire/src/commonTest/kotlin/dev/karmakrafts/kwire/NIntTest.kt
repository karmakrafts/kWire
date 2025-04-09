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
import kotlin.test.assertTrue

class NIntTest {

    // Conversion tests

    @Test
    fun `Int to NInt conversion`() {
        val intValue = 42
        val nIntValue = intValue.toNInt()
        assertEquals(
            intValue.toLong(),
            nIntValue.longValue,
            "NInt longValue should match the original Int value converted to Long"
        )
        assertEquals(intValue, nIntValue.intValue, "NInt intValue should match the original Int value")
    }

    @Test
    fun `UInt to NInt conversion`() {
        val uIntValue = 42u
        val nIntValue = uIntValue.toNInt()
        assertEquals(
            uIntValue.toLong(),
            nIntValue.longValue,
            "NInt longValue should match the original UInt value converted to Long"
        )
        assertEquals(
            uIntValue.toInt(),
            nIntValue.intValue,
            "NInt intValue should match the original UInt value converted to Int"
        )
    }

    @Test
    fun `Long to NInt conversion`() {
        val longValue = 42L
        val nIntValue = longValue.toNInt()
        assertEquals(longValue, nIntValue.longValue, "NInt longValue should match the original Long value")
        assertEquals(
            longValue.toInt(),
            nIntValue.intValue,
            "NInt intValue should match the original Long value converted to Int"
        )
    }

    @Test
    fun `ULong to NInt conversion`() {
        val uLongValue = 42uL
        val nIntValue = uLongValue.toNInt()
        assertEquals(
            uLongValue.toLong(),
            nIntValue.longValue,
            "NInt longValue should match the original ULong value converted to Long"
        )
        assertEquals(
            uLongValue.toInt(),
            nIntValue.intValue,
            "NInt intValue should match the original ULong value converted to Int"
        )
    }

    @Test
    fun `NFloat to NInt conversion`() {
        val nFloatValue = 42.5.toNFloat()
        val nIntValue = nFloatValue.toNInt()
        // NFloat to NInt conversion truncates the decimal part
        assertEquals(
            42.toLong(),
            nIntValue.longValue,
            "NInt longValue should be the truncated value of NFloat (decimal part removed)"
        )
        assertEquals(
            42,
            nIntValue.intValue,
            "NInt intValue should be the truncated value of NFloat (decimal part removed)"
        )
    }

    // Property tests

    @Test
    fun `intValue property returns correct Int value`() {
        val nIntValue = 42.toNInt()
        assertEquals(42, nIntValue.intValue, "intValue property should return the correct Int representation")
    }

    @Test
    fun `longValue property returns correct Long value`() {
        val nIntValue = 42.toNInt()
        assertEquals(42L, nIntValue.longValue, "longValue property should return the correct Long representation")
    }

    // Arithmetic operator tests

    @Test
    fun `plus operator adds two NInt values correctly`() {
        val a = 40.toNInt()
        val b = 2.toNInt()
        val result = a + b
        assertEquals(42.toNInt().longValue, result.longValue, "Addition of 40 + 2 should equal 42")
    }

    @Test
    fun `minus operator subtracts two NInt values correctly`() {
        val a = 44.toNInt()
        val b = 2.toNInt()
        val result = a - b
        assertEquals(42.toNInt().longValue, result.longValue, "Subtraction of 44 - 2 should equal 42")
    }

    @Test
    fun `times operator multiplies two NInt values correctly`() {
        val a = 21.toNInt()
        val b = 2.toNInt()
        val result = a * b
        assertEquals(42.toNInt().longValue, result.longValue, "Multiplication of 21 * 2 should equal 42")
    }

    @Test
    fun `div operator divides two NInt values correctly`() {
        val a = 84.toNInt()
        val b = 2.toNInt()
        val result = a / b
        assertEquals(42.toNInt().longValue, result.longValue, "Division of 84 / 2 should equal 42")
    }

    @Test
    fun `rem operator calculates remainder correctly`() {
        val a = 44.toNInt()
        val b = 10.toNInt()
        val result = a % b
        assertEquals(4.toNInt().longValue, result.longValue, "Remainder of 44 % 10 should equal 4")
    }

    // Bitwise operation tests

    @Test
    fun `and operator performs bitwise AND correctly`() {
        val a = 0b1010.toNInt()
        val b = 0b1100.toNInt()
        val result = a and b
        assertEquals(
            0b1000.toNInt().longValue,
            result.longValue,
            "Bitwise AND of 0b1010 and 0b1100 should equal 0b1000"
        )
    }

    @Test
    fun `or operator performs bitwise OR correctly`() {
        val a = 0b1010.toNInt()
        val b = 0b1100.toNInt()
        val result = a or b
        assertEquals(0b1110.toNInt().longValue, result.longValue, "Bitwise OR of 0b1010 and 0b1100 should equal 0b1110")
    }

    @Test
    fun `xor operator performs bitwise XOR correctly`() {
        val a = 0b1010.toNInt()
        val b = 0b1100.toNInt()
        val result = a xor b
        assertEquals(
            0b0110.toNInt().longValue,
            result.longValue,
            "Bitwise XOR of 0b1010 and 0b1100 should equal 0b0110"
        )
    }

    @Test
    fun `shl operator performs left shift correctly`() {
        val a = 0b0001.toNInt()
        val result = a shl 2
        assertEquals(0b0100.toNInt().longValue, result.longValue, "Left shift of 0b0001 by 2 bits should equal 0b0100")
    }

    @Test
    fun `shr operator performs right shift correctly`() {
        val a = 0b1000.toNInt()
        val result = a shr 2
        assertEquals(0b0010.toNInt().longValue, result.longValue, "Right shift of 0b1000 by 2 bits should equal 0b0010")
    }

    @Test
    fun `inv function performs bitwise inversion correctly`() {
        val a = 0b1010.toNInt()
        val result = a.inv()
        // The exact result depends on the platform's bit width, but we can check that bits are flipped
        assertNotEquals(a.longValue, result.longValue, "Bitwise inversion should change the value")
        assertEquals(a.longValue, result.inv().longValue, "Double inversion should restore the original value")
    }

    // Comparison tests

    @Test
    fun `compareTo returns correct ordering`() {
        val a = 10.toNInt()
        val b = 20.toNInt()
        val c = 10.toNInt()

        assertTrue(a < b, "10 should be less than 20") // a < b
        assertTrue(b > a, "20 should be greater than 10") // b > a
        assertEquals(a, c, "Two NInt instances with the same value (10) should be equal") // a == c
    }

    // Conversion function tests

    @Test
    fun `toUnsigned converts to NUInt correctly`() {
        val nIntValue = 42.toNInt()
        val nUIntValue = nIntValue.toUnsigned()
        assertEquals(
            42u.toULong(),
            nUIntValue.ulongValue,
            "Converting NInt(42) to NUInt should result in a value with ulongValue of 42u"
        )
    }

    @ExperimentalStdlibApi
    @Test
    fun `toHexString returns correct hexadecimal representation`() {
        val nIntValue = 0xABCD.toNInt()
        val padding = "0".repeat(2 * Pointer.SIZE_BYTES - 4)
        assertEquals(
            "${padding}abcd",
            nIntValue.toHexString(),
            "Hexadecimal representation of 0xABCD should be '${padding}abcd'"
        )
    }
}
