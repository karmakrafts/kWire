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
import kotlin.test.assertTrue

class NFloatTest {

    // Conversion tests

    @Test
    fun `Float to NFloat conversion`() {
        val floatValue = 42.5f
        val nFloatValue = floatValue.toNFloat()
        assertEquals(
            floatValue.toDouble(),
            nFloatValue.doubleValue,
            0.0001,
            "NFloat doubleValue should match the original Float value converted to Double"
        )
        assertEquals(
            floatValue, nFloatValue.floatValue, 0.0001f, "NFloat floatValue should match the original Float value"
        )
    }

    @Test
    fun `Double to NFloat conversion`() {
        val doubleValue = 42.5
        val nFloatValue = doubleValue.toNFloat()
        assertEquals(
            doubleValue, nFloatValue.doubleValue, 0.0001, "NFloat doubleValue should match the original Double value"
        )
        assertEquals(
            doubleValue.toFloat(),
            nFloatValue.floatValue,
            0.0001f,
            "NFloat floatValue should match the original Double value converted to Float"
        )
    }

    @Test
    fun `NInt to NFloat conversion`() {
        val nIntValue = 42.toNInt()
        val nFloatValue = nIntValue.toNFloat()
        assertEquals(
            42.0, nFloatValue.doubleValue, 0.0001, "NFloat doubleValue should match the NInt value converted to Double"
        )
        assertEquals(
            42.0f, nFloatValue.floatValue, 0.0001f, "NFloat floatValue should match the NInt value converted to Float"
        )
    }

    @Test
    fun `NUInt to NFloat conversion`() {
        val nUIntValue = 42u.toNUInt()
        val nFloatValue = nUIntValue.toNFloat()
        assertEquals(
            42.0, nFloatValue.doubleValue, 0.0001, "NFloat doubleValue should match the NUInt value converted to Double"
        )
        assertEquals(
            42.0f, nFloatValue.floatValue, 0.0001f, "NFloat floatValue should match the NUInt value converted to Float"
        )
    }

    // Property tests

    @Test
    fun `floatValue property returns correct Float value`() {
        val nFloatValue = 42.5.toNFloat()
        assertEquals(
            42.5f, nFloatValue.floatValue, 0.0001f, "floatValue property should return the correct Float representation"
        )
    }

    @Test
    fun `doubleValue property returns correct Double value`() {
        val nFloatValue = 42.5.toNFloat()
        assertEquals(
            42.5,
            nFloatValue.doubleValue,
            0.0001,
            "doubleValue property should return the correct Double representation"
        )
    }

    // Arithmetic operator tests

    @Test
    fun `plus operator adds two NFloat values correctly`() {
        val a = 40.5.toNFloat()
        val b = 2.0.toNFloat()
        val result = a + b
        assertEquals(42.5, result.doubleValue, 0.0001, "Addition of 40.5 + 2.0 should equal 42.5")
    }

    @Test
    fun `minus operator subtracts two NFloat values correctly`() {
        val a = 44.5.toNFloat()
        val b = 2.0.toNFloat()
        val result = a - b
        assertEquals(42.5, result.doubleValue, 0.0001, "Subtraction of 44.5 - 2.0 should equal 42.5")
    }

    @Test
    fun `times operator multiplies two NFloat values correctly`() {
        val a = 8.5.toNFloat()
        val b = 5.0.toNFloat()
        val result = a * b
        assertEquals(42.5, result.doubleValue, 0.0001, "Multiplication of 8.5 * 5.0 should equal 42.5")
    }

    @Test
    fun `div operator divides two NFloat values correctly`() {
        val a = 85.0.toNFloat()
        val b = 2.0.toNFloat()
        val result = a / b
        assertEquals(42.5, result.doubleValue, 0.0001, "Division of 85.0 / 2.0 should equal 42.5")
    }

    @Test
    fun `rem operator calculates remainder correctly`() {
        val a = 44.5.toNFloat()
        val b = 10.0.toNFloat()
        val result = a % b
        assertEquals(4.5, result.doubleValue, 0.0001, "Remainder of 44.5 % 10.0 should equal 4.5")
    }

    // Comparison tests

    @Test
    fun `compareTo returns correct ordering`() {
        val a = 10.5.toNFloat()
        val b = 20.5.toNFloat()
        val c = 10.5.toNFloat()

        assertTrue(a < b, "10.5 should be less than 20.5") // a < b
        assertTrue(b > a, "20.5 should be greater than 10.5") // b > a
        assertEquals(a, c, "Two NFloat instances with the same value (10.5) should be equal") // a == c
    }

    // Edge cases

    @Test
    fun `operations with very large numbers`() {
        val largeValue = 1.0e20.toNFloat()
        val smallValue = 1.0.toNFloat()

        // Addition with large disparity should approximately equal the larger number
        val sum = largeValue + smallValue
        assertEquals(
            largeValue.doubleValue,
            sum.doubleValue,
            largeValue.doubleValue * 1e-10,
            "Adding a small number to a very large number should approximately equal the large number"
        )
    }

    @Test
    fun `operations with very small numbers`() {
        val smallValue = 1.0e-20.toNFloat()
        val normalValue = 1.0.toNFloat()

        // Addition with large disparity should approximately equal the larger number
        val sum = normalValue + smallValue
        assertEquals(
            normalValue.doubleValue,
            sum.doubleValue,
            1e-10,
            "Adding a very small number to a normal number should approximately equal the normal number"
        )
    }

    @Test
    fun `division by very small number`() {
        val normalValue = 1.0.toNFloat()
        val smallValue = 1.0e-20.toNFloat()

        val result = normalValue / smallValue
        assertTrue(
            result.doubleValue > 1.0e19, "Division by a very small number should result in a very large number"
        ) // Should be very large
    }
}
