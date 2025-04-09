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

class NUIntTest {

    // Conversion tests

    @Test
    fun `Int to NUInt conversion`() {
        val intValue = 42
        val nUIntValue = intValue.toNUInt()
        assertEquals(
            intValue.toULong(),
            nUIntValue.ulongValue,
            "NUInt ulongValue should match the original Int value converted to ULong"
        )
        assertEquals(
            intValue.toUInt(),
            nUIntValue.uintValue,
            "NUInt uintValue should match the original Int value converted to UInt"
        )
    }

    @Test
    fun `UInt to NUInt conversion`() {
        val uIntValue = 42u
        val nUIntValue = uIntValue.toNUInt()
        assertEquals(
            uIntValue.toULong(),
            nUIntValue.ulongValue,
            "NUInt ulongValue should match the original UInt value converted to ULong"
        )
        assertEquals(uIntValue, nUIntValue.uintValue, "NUInt uintValue should match the original UInt value")
    }

    @Test
    fun `Long to NUInt conversion`() {
        val longValue = 42L
        val nUIntValue = longValue.toNUInt()
        assertEquals(
            longValue.toULong(),
            nUIntValue.ulongValue,
            "NUInt ulongValue should match the original Long value converted to ULong"
        )
        assertEquals(
            longValue.toUInt(),
            nUIntValue.uintValue,
            "NUInt uintValue should match the original Long value converted to UInt"
        )
    }

    @Test
    fun `ULong to NUInt conversion`() {
        val uLongValue = 42uL
        val nUIntValue = uLongValue.toNUInt()
        assertEquals(uLongValue, nUIntValue.ulongValue, "NUInt ulongValue should match the original ULong value")
        assertEquals(
            uLongValue.toUInt(),
            nUIntValue.uintValue,
            "NUInt uintValue should match the original ULong value converted to UInt"
        )
    }

    @Test
    fun `NFloat to NUInt conversion`() {
        val nFloatValue = 42.5.toNFloat()
        val nUIntValue = nFloatValue.toNUInt()
        // NFloat to NUInt conversion truncates the decimal part
        assertEquals(
            42uL,
            nUIntValue.ulongValue,
            "NUInt ulongValue should be the truncated value of NFloat (decimal part removed)"
        )
        assertEquals(
            42u,
            nUIntValue.uintValue,
            "NUInt uintValue should be the truncated value of NFloat (decimal part removed)"
        )
    }

    // Property tests

    @Test
    fun `uintValue property returns correct UInt value`() {
        val nUIntValue = 42u.toNUInt()
        assertEquals(42u, nUIntValue.uintValue, "uintValue property should return the correct UInt representation")
    }

    @Test
    fun `ulongValue property returns correct ULong value`() {
        val nUIntValue = 42u.toNUInt()
        assertEquals(42uL, nUIntValue.ulongValue, "ulongValue property should return the correct ULong representation")
    }

    // Arithmetic operator tests

    @Test
    fun `plus operator adds two NUInt values correctly`() {
        val a = 40u.toNUInt()
        val b = 2u.toNUInt()
        val result = a + b
        assertEquals(42u.toNUInt().ulongValue, result.ulongValue, "Addition of 40u + 2u should equal 42u")
    }

    @Test
    fun `minus operator subtracts two NUInt values correctly`() {
        val a = 44u.toNUInt()
        val b = 2u.toNUInt()
        val result = a - b
        assertEquals(42u.toNUInt().ulongValue, result.ulongValue, "Subtraction of 44u - 2u should equal 42u")
    }

    @Test
    fun `times operator multiplies two NUInt values correctly`() {
        val a = 21u.toNUInt()
        val b = 2u.toNUInt()
        val result = a * b
        assertEquals(42u.toNUInt().ulongValue, result.ulongValue, "Multiplication of 21u * 2u should equal 42u")
    }

    @Test
    fun `div operator divides two NUInt values correctly`() {
        val a = 84u.toNUInt()
        val b = 2u.toNUInt()
        val result = a / b
        assertEquals(42u.toNUInt().ulongValue, result.ulongValue, "Division of 84u / 2u should equal 42u")
    }

    @Test
    fun `rem operator calculates remainder correctly`() {
        val a = 44u.toNUInt()
        val b = 10u.toNUInt()
        val result = a % b
        assertEquals(4u.toNUInt().ulongValue, result.ulongValue, "Remainder of 44u % 10u should equal 4u")
    }

    // Bitwise operation tests

    @Test
    fun `and operator performs bitwise AND correctly`() {
        val a = 0b1010u.toNUInt()
        val b = 0b1100u.toNUInt()
        val result = a and b
        assertEquals(
            0b1000u.toNUInt().ulongValue,
            result.ulongValue,
            "Bitwise AND of 0b1010u and 0b1100u should equal 0b1000u"
        )
    }

    @Test
    fun `or operator performs bitwise OR correctly`() {
        val a = 0b1010u.toNUInt()
        val b = 0b1100u.toNUInt()
        val result = a or b
        assertEquals(
            0b1110u.toNUInt().ulongValue,
            result.ulongValue,
            "Bitwise OR of 0b1010u and 0b1100u should equal 0b1110u"
        )
    }

    @Test
    fun `xor operator performs bitwise XOR correctly`() {
        val a = 0b1010u.toNUInt()
        val b = 0b1100u.toNUInt()
        val result = a xor b
        assertEquals(
            0b0110u.toNUInt().ulongValue,
            result.ulongValue,
            "Bitwise XOR of 0b1010u and 0b1100u should equal 0b0110u"
        )
    }

    @Test
    fun `shl operator performs left shift correctly`() {
        val a = 0b0001u.toNUInt()
        val result = a shl 2
        assertEquals(
            0b0100u.toNUInt().ulongValue,
            result.ulongValue,
            "Left shift of 0b0001u by 2 bits should equal 0b0100u"
        )
    }

    @Test
    fun `shr operator performs right shift correctly`() {
        val a = 0b1000u.toNUInt()
        val result = a shr 2
        assertEquals(
            0b0010u.toNUInt().ulongValue,
            result.ulongValue,
            "Right shift of 0b1000u by 2 bits should equal 0b0010u"
        )
    }

    @Test
    fun `inv function performs bitwise inversion correctly`() {
        val a = 0b1010u.toNUInt()
        val result = a.inv()
        // The exact result depends on the platform's bit width, but we can check that bits are flipped
        assertNotEquals(a.ulongValue, result.ulongValue, "Bitwise inversion should change the value")
        assertEquals(a.ulongValue, result.inv().ulongValue, "Double inversion should restore the original value")
    }

    // Comparison tests

    @Test
    fun `compareTo returns correct ordering`() {
        val a = 10u.toNUInt()
        val b = 20u.toNUInt()
        val c = 10u.toNUInt()

        assertTrue(a < b, "10u should be less than 20u") // a < b
        assertTrue(b > a, "20u should be greater than 10u") // b > a
        assertEquals(a, c, "Two NUInt instances with the same value (10u) should be equal") // a == c
    }

    // Conversion function tests

    @Test
    fun `toSigned converts to NInt correctly`() {
        val nUIntValue = 42u.toNUInt()
        val nIntValue = nUIntValue.toSigned()
        assertEquals(
            42L,
            nIntValue.longValue,
            "Converting NUInt(42u) to NInt should result in a value with longValue of 42L"
        )
    }

    @ExperimentalStdlibApi
    @Test
    fun `toHexString returns correct hexadecimal representation`() {
        val nUIntValue = 0xABCDu.toNUInt()
        val padding = "0".repeat(2 * Pointer.SIZE_BYTES - 4)
        assertEquals(
            "${padding}abcd",
            nUIntValue.toHexString(),
            "Hexadecimal representation of 0xABCDu should be '${padding}abcd'"
        )
    }
}
