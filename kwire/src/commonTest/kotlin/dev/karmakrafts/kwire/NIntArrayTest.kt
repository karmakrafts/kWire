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

class NIntArrayTest {

    // Creation tests

    @Test
    fun `nIntArray creates array with correct size`() {
        val size = 5
        val array = nIntArray(size)
        assertEquals(size, array.size, "Array size should match the specified size")
    }

    @Test
    fun `nIntArray with initializer creates array with correct values`() {
        val size = 5
        val array = nIntArray(size) { it.toNInt() * 2.toNInt() }

        for (i in 0 until size) {
            assertEquals((i * 2).toNInt(), array[i], "Array element at index $i should match the initializer value")
        }
    }

    // Property tests

    @Test
    fun `size property returns correct value`() {
        val size = 3
        val array = nIntArray(size)
        assertEquals(size, array.size, "Size property should return the correct array size")
    }

    @Test
    fun `intArrayValue property returns correct IntArray`() {
        val array = nIntArray(3) { it.toNInt() }
        val intArray = array.intArrayValue

        assertEquals(array.size, intArray.size, "IntArray size should match NIntArray size")
        for (i in 0 until array.size) {
            assertEquals(array[i].intValue, intArray[i], "IntArray element at index $i should match NIntArray element")
        }
    }

    @Test
    fun `longArrayValue property returns correct LongArray`() {
        val array = nIntArray(3) { it.toNInt() }
        val longArray = array.longArrayValue

        assertEquals(array.size, longArray.size, "LongArray size should match NIntArray size")
        for (i in 0 until array.size) {
            assertEquals(
                array[i].longValue,
                longArray[i],
                "LongArray element at index $i should match NIntArray element"
            )
        }
    }

    // Conversion tests

    @Test
    fun `IntArray toNIntArray conversion`() {
        val intArray = intArrayOf(1, 2, 3)
        val nIntArray = intArray.toNIntArray()

        assertEquals(intArray.size, nIntArray.size, "NIntArray size should match IntArray size")
        for (i in intArray.indices) {
            assertEquals(
                intArray[i].toNInt(),
                nIntArray[i],
                "NIntArray element at index $i should match IntArray element"
            )
        }
    }

    @Test
    fun `LongArray toNIntArray conversion`() {
        val longArray = longArrayOf(1L, 2L, 3L)
        val nIntArray = longArray.toNIntArray()

        assertEquals(longArray.size, nIntArray.size, "NIntArray size should match LongArray size")
        for (i in longArray.indices) {
            assertEquals(
                longArray[i].toNInt(),
                nIntArray[i],
                "NIntArray element at index $i should match LongArray element"
            )
        }
    }

    @Test
    fun `NIntArray asNUIntArray conversion`() {
        val nIntArray = nIntArray(3) { it.toNInt() }
        val nUIntArray = nIntArray.asNUIntArray()

        assertEquals(nIntArray.size, nUIntArray.size, "NUIntArray size should match NIntArray size")
        for (i in 0 until nIntArray.size) {
            assertEquals(
                nIntArray[i].toUnsigned(),
                nUIntArray[i],
                "NUIntArray element at index $i should match NIntArray element converted to unsigned"
            )
        }
    }

    // Operator tests

    @Test
    fun `contains operator returns correct result`() {
        val array = nIntArray(3) { it.toNInt() }

        assertTrue(1.toNInt() in array, "Array should contain the value 1")
        assertFalse(5.toNInt() in array, "Array should not contain the value 5")
    }

    @Test
    fun `get operator returns correct element`() {
        val array = nIntArray(3) { it.toNInt() }

        assertEquals(0.toNInt(), array[0], "get operator should return the correct element at index 0")
        assertEquals(1.toNInt(), array[1], "get operator should return the correct element at index 1")
        assertEquals(2.toNInt(), array[2], "get operator should return the correct element at index 2")
    }

    @Test
    fun `set operator sets correct element`() {
        val array = nIntArray(3)

        array[0] = 10.toNInt()
        array[1] = 20.toNInt()
        array[2] = 30.toNInt()

        assertEquals(10.toNInt(), array[0], "set operator should set the correct element at index 0")
        assertEquals(20.toNInt(), array[1], "set operator should set the correct element at index 1")
        assertEquals(30.toNInt(), array[2], "set operator should set the correct element at index 2")
    }

    @Test
    fun `plus operator concatenates arrays correctly`() {
        val array1 = nIntArray(2) { it.toNInt() }  // [0, 1]
        val array2 = nIntArray(3) { (it + 2).toNInt() }  // [2, 3, 4]

        val result = array1 + array2

        assertEquals(5, result.size, "Concatenated array size should be the sum of the input array sizes")
        assertEquals(0.toNInt(), result[0], "First element should be from the first array")
        assertEquals(1.toNInt(), result[1], "Second element should be from the first array")
        assertEquals(2.toNInt(), result[2], "Third element should be from the second array")
        assertEquals(3.toNInt(), result[3], "Fourth element should be from the second array")
        assertEquals(4.toNInt(), result[4], "Fifth element should be from the second array")
    }

    @Test
    fun `minus operator removes elements correctly`() {
        val array1 = nIntArray(5) { it.toNInt() }  // [0, 1, 2, 3, 4]
        val array2 = nIntArray(2) { (it * 2 + 1).toNInt() }  // [1, 3]

        val result = array1 - array2

        // The exact behavior of minus might vary by platform, but we can check that elements from array2 are not in result
        assertFalse(1.toNInt() in result, "Result should not contain elements from the second array")
        assertFalse(3.toNInt() in result, "Result should not contain elements from the second array")
        assertTrue(
            0.toNInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            2.toNInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            4.toNInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
    }

    // Other functionality tests

    @Test
    fun `asSequence returns correct sequence`() {
        val array = nIntArray(3) { it.toNInt() }
        val sequence = array.asSequence()

        val list = sequence.toList()
        assertEquals(3, list.size, "Sequence should have the same number of elements as the array")
        assertEquals(0.toNInt(), list[0], "First element in sequence should match first element in array")
        assertEquals(1.toNInt(), list[1], "Second element in sequence should match second element in array")
        assertEquals(2.toNInt(), list[2], "Third element in sequence should match third element in array")
    }
}
