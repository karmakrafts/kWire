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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NFloatArrayTest {

    // Creation tests

    @Test
    fun `nFloatArray creates array with correct size`() {
        val size = 5
        val array = nFloatArray(size)
        assertEquals(size, array.size, "Array size should match the specified size")
    }

    @Test
    fun `nFloatArray with initializer creates array with correct values`() {
        val size = 5
        val array = nFloatArray(size) { (it * 2.5).toNFloat() }

        for (i in 0 until size) {
            assertEquals(
                (i * 2.5).toNFloat().doubleValue,
                array[i].doubleValue,
                0.0001,
                "Array element at index $i should match the initializer value"
            )
        }
    }

    // Property tests

    @Test
    fun `size property returns correct value`() {
        val size = 3
        val array = nFloatArray(size)
        assertEquals(size, array.size, "Size property should return the correct array size")
    }

    @Test
    fun `floatArrayValue property returns correct FloatArray`() {
        val array = nFloatArray(3) { (it * 1.5).toNFloat() }
        val floatArray = array.floatArrayValue

        assertEquals(array.size, floatArray.size, "FloatArray size should match NFloatArray size")
        for (i in 0 until array.size) {
            assertEquals(
                array[i].floatValue,
                floatArray[i],
                0.0001f,
                "FloatArray element at index $i should match NFloatArray element"
            )
        }
    }

    @Test
    fun `doubleArrayValue property returns correct DoubleArray`() {
        val array = nFloatArray(3) { (it * 1.5).toNFloat() }
        val doubleArray = array.doubleArrayValue

        assertEquals(array.size, doubleArray.size, "DoubleArray size should match NFloatArray size")
        for (i in 0 until array.size) {
            assertEquals(
                array[i].doubleValue,
                doubleArray[i],
                0.0001,
                "DoubleArray element at index $i should match NFloatArray element"
            )
        }
    }

    // Conversion tests

    @Test
    fun `FloatArray toNFloatArray conversion`() {
        val floatArray = floatArrayOf(1.5f, 2.5f, 3.5f)
        val nFloatArray = floatArray.toNFloatArray()

        assertEquals(floatArray.size, nFloatArray.size, "NFloatArray size should match FloatArray size")
        for (i in floatArray.indices) {
            assertEquals(
                floatArray[i].toDouble(),
                nFloatArray[i].doubleValue,
                0.0001,
                "NFloatArray element at index $i should match FloatArray element"
            )
        }
    }

    @Test
    fun `DoubleArray toNFloatArray conversion`() {
        val doubleArray = doubleArrayOf(1.5, 2.5, 3.5)
        val nFloatArray = doubleArray.toNFloatArray()

        assertEquals(doubleArray.size, nFloatArray.size, "NFloatArray size should match DoubleArray size")
        for (i in doubleArray.indices) {
            assertEquals(
                doubleArray[i],
                nFloatArray[i].doubleValue,
                0.0001,
                "NFloatArray element at index $i should match DoubleArray element"
            )
        }
    }

    // Operator tests

    @Test
    fun `contains operator returns correct result`() {
        val array = nFloatArray(3) { (it * 1.5).toNFloat() }

        assertTrue(1.5.toNFloat() in array, "Array should contain the value 1.5")
        assertFalse(5.5.toNFloat() in array, "Array should not contain the value 5.5")
    }

    @Test
    fun `get operator returns correct element`() {
        val array = nFloatArray(3) { (it * 1.5).toNFloat() }

        assertEquals(
            0.0.toNFloat().doubleValue,
            array[0].doubleValue,
            0.0001,
            "get operator should return the correct element at index 0"
        )
        assertEquals(
            1.5.toNFloat().doubleValue,
            array[1].doubleValue,
            0.0001,
            "get operator should return the correct element at index 1"
        )
        assertEquals(
            3.0.toNFloat().doubleValue,
            array[2].doubleValue,
            0.0001,
            "get operator should return the correct element at index 2"
        )
    }

    @Test
    fun `set operator sets correct element`() {
        val array = nFloatArray(3)

        array[0] = 10.5.toNFloat()
        array[1] = 20.5.toNFloat()
        array[2] = 30.5.toNFloat()

        assertEquals(
            10.5.toNFloat().doubleValue,
            array[0].doubleValue,
            0.0001,
            "set operator should set the correct element at index 0"
        )
        assertEquals(
            20.5.toNFloat().doubleValue,
            array[1].doubleValue,
            0.0001,
            "set operator should set the correct element at index 1"
        )
        assertEquals(
            30.5.toNFloat().doubleValue,
            array[2].doubleValue,
            0.0001,
            "set operator should set the correct element at index 2"
        )
    }

    @Test
    fun `plus operator concatenates arrays correctly`() {
        val array1 = nFloatArray(2) { (it * 1.5).toNFloat() }  // [0.0, 1.5]
        val array2 = nFloatArray(3) { ((it + 2) * 1.5).toNFloat() }  // [3.0, 4.5, 6.0]

        val result = array1 + array2

        assertEquals(5, result.size, "Concatenated array size should be the sum of the input array sizes")
        assertEquals(
            0.0.toNFloat().doubleValue, result[0].doubleValue, 0.0001, "First element should be from the first array"
        )
        assertEquals(
            1.5.toNFloat().doubleValue, result[1].doubleValue, 0.0001, "Second element should be from the first array"
        )
        assertEquals(
            3.0.toNFloat().doubleValue, result[2].doubleValue, 0.0001, "Third element should be from the second array"
        )
        assertEquals(
            4.5.toNFloat().doubleValue, result[3].doubleValue, 0.0001, "Fourth element should be from the second array"
        )
        assertEquals(
            6.0.toNFloat().doubleValue, result[4].doubleValue, 0.0001, "Fifth element should be from the second array"
        )
    }

    @Test
    fun `minus operator removes elements correctly`() {
        val array1 = nFloatArray(5) { (it * 1.5).toNFloat() }  // [0.0, 1.5, 3.0, 4.5, 6.0]
        val array2 = nFloatArray(2) { ((it * 2 + 1) * 1.5).toNFloat() }  // [1.5, 4.5]

        val result = array1 - array2

        // The exact behavior of minus might vary by platform, but we can check that elements from array2 are not in result
        assertFalse(1.5.toNFloat() in result, "Result should not contain elements from the second array")
        assertFalse(4.5.toNFloat() in result, "Result should not contain elements from the second array")
        assertTrue(
            0.0.toNFloat() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            3.0.toNFloat() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            6.0.toNFloat() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
    }

    // Other functionality tests

    @Test
    fun `asSequence returns correct sequence`() {
        val array = nFloatArray(3) { (it * 1.5).toNFloat() }
        val sequence = array.asSequence()

        val list = sequence.toList()
        assertEquals(3, list.size, "Sequence should have the same number of elements as the array")
        assertEquals(
            0.0.toNFloat().doubleValue,
            list[0].doubleValue,
            0.0001,
            "First element in sequence should match first element in array"
        )
        assertEquals(
            1.5.toNFloat().doubleValue,
            list[1].doubleValue,
            0.0001,
            "Second element in sequence should match second element in array"
        )
        assertEquals(
            3.0.toNFloat().doubleValue,
            list[2].doubleValue,
            0.0001,
            "Third element in sequence should match third element in array"
        )
    }
}
