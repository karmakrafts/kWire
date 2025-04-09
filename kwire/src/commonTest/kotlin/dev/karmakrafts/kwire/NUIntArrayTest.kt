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

class NUIntArrayTest {

    // Creation tests

    @Test
    fun `nUIntArray creates array with correct size`() {
        val size = 5
        val array = nUIntArray(size)
        assertEquals(size, array.size, "Array size should match the specified size")
    }

    @Test
    fun `nUIntArray with initializer creates array with correct values`() {
        val size = 5
        val array = nUIntArray(size) { it.toUInt().toNUInt() * 2u.toNUInt() }

        for (i in 0 until size) {
            assertEquals(
                (i.toUInt() * 2u).toNUInt(),
                array[i],
                "Array element at index $i should match the initializer value"
            )
        }
    }

    // Property tests

    @Test
    fun `size property returns correct value`() {
        val size = 3
        val array = nUIntArray(size)
        assertEquals(size, array.size, "Size property should return the correct array size")
    }

    // Conversion tests

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

    @Test
    fun `NUIntArray asNIntArray conversion`() {
        val nUIntArray = nUIntArray(3) { it.toUInt().toNUInt() }
        val nIntArray = nUIntArray.asNIntArray()

        assertEquals(nUIntArray.size, nIntArray.size, "NIntArray size should match NUIntArray size")
        for (i in 0 until nUIntArray.size) {
            assertEquals(
                nUIntArray[i].toSigned(),
                nIntArray[i],
                "NIntArray element at index $i should match NUIntArray element converted to signed"
            )
        }
    }

    // Operator tests

    @Test
    fun `contains operator returns correct result`() {
        val array = nUIntArray(3) { it.toUInt().toNUInt() }

        assertTrue(1u.toNUInt() in array, "Array should contain the value 1u")
        assertFalse(5u.toNUInt() in array, "Array should not contain the value 5u")
    }

    @Test
    fun `get operator returns correct element`() {
        val array = nUIntArray(3) { it.toUInt().toNUInt() }

        assertEquals(0u.toNUInt(), array[0], "get operator should return the correct element at index 0")
        assertEquals(1u.toNUInt(), array[1], "get operator should return the correct element at index 1")
        assertEquals(2u.toNUInt(), array[2], "get operator should return the correct element at index 2")
    }

    @Test
    fun `set operator sets correct element`() {
        val array = nUIntArray(3)

        array[0] = 10u.toNUInt()
        array[1] = 20u.toNUInt()
        array[2] = 30u.toNUInt()

        assertEquals(10u.toNUInt(), array[0], "set operator should set the correct element at index 0")
        assertEquals(20u.toNUInt(), array[1], "set operator should set the correct element at index 1")
        assertEquals(30u.toNUInt(), array[2], "set operator should set the correct element at index 2")
    }

    @Test
    fun `plus operator concatenates arrays correctly`() {
        val array1 = nUIntArray(2) { it.toUInt().toNUInt() }  // [0u, 1u]
        val array2 = nUIntArray(3) { (it + 2).toUInt().toNUInt() }  // [2u, 3u, 4u]

        val result = array1 + array2

        assertEquals(5, result.size, "Concatenated array size should be the sum of the input array sizes")
        assertEquals(0u.toNUInt(), result[0], "First element should be from the first array")
        assertEquals(1u.toNUInt(), result[1], "Second element should be from the first array")
        assertEquals(2u.toNUInt(), result[2], "Third element should be from the second array")
        assertEquals(3u.toNUInt(), result[3], "Fourth element should be from the second array")
        assertEquals(4u.toNUInt(), result[4], "Fifth element should be from the second array")
    }

    @Test
    fun `minus operator removes elements correctly`() {
        val array1 = nUIntArray(5) { it.toUInt().toNUInt() }  // [0u, 1u, 2u, 3u, 4u]
        val array2 = nUIntArray(2) { (it * 2 + 1).toUInt().toNUInt() }  // [1u, 3u]

        val result = array1 - array2

        // The exact behavior of minus might vary by platform, but we can check that elements from array2 are not in result
        assertFalse(1u.toNUInt() in result, "Result should not contain elements from the second array")
        assertFalse(3u.toNUInt() in result, "Result should not contain elements from the second array")
        assertTrue(
            0u.toNUInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            2u.toNUInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            4u.toNUInt() in result,
            "Result should contain elements from the first array that are not in the second array"
        )
    }

    // Other functionality tests

    @Test
    fun `asSequence returns correct sequence`() {
        val array = nUIntArray(3) { it.toUInt().toNUInt() }
        val sequence = array.asSequence()

        val list = sequence.toList()
        assertEquals(3, list.size, "Sequence should have the same number of elements as the array")
        assertEquals(0u.toNUInt(), list[0], "First element in sequence should match first element in array")
        assertEquals(1u.toNUInt(), list[1], "Second element in sequence should match second element in array")
        assertEquals(2u.toNUInt(), list[2], "Third element in sequence should match third element in array")
    }
}
