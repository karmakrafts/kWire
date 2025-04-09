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

class PointerArrayTest {

    // Creation tests

    @Test
    fun `pointerArray creates array with correct size`() {
        val size = 5
        val array = pointerArray(size)
        assertEquals(size, array.size, "Array size should match the specified size")
    }

    @Test
    fun `pointerArray with initializer creates array with correct values`() {
        val size = 5
        val array = pointerArray(size) { Pointer(it.toUInt().toNUInt()) }

        for (i in 0 until size) {
            assertEquals(
                Pointer(i.toUInt().toNUInt()),
                array[i],
                "Array element at index $i should match the initializer value"
            )
        }
    }

    // Property tests

    @Test
    fun `size property returns correct value`() {
        val size = 3
        val array = pointerArray(size)
        assertEquals(size, array.size, "Size property should return the correct array size")
    }

    // Conversion tests

    @Test
    fun `NUIntArray asPointerArray conversion`() {
        val nUIntArray = nUIntArray(3) { it.toUInt().toNUInt() }
        val pointerArray = nUIntArray.asPointerArray()

        assertEquals(nUIntArray.size, pointerArray.size, "PointerArray size should match NUIntArray size")
        for (i in 0 until nUIntArray.size) {
            assertEquals(
                Pointer(nUIntArray[i]),
                pointerArray[i],
                "PointerArray element at index $i should match NUIntArray element converted to Pointer"
            )
        }
    }

    @Test
    fun `NIntArray asPointerArray conversion`() {
        val nIntArray = nIntArray(3) { it.toNInt() }
        val pointerArray = nIntArray.asPointerArray()

        assertEquals(nIntArray.size, pointerArray.size, "PointerArray size should match NIntArray size")
        for (i in 0 until nIntArray.size) {
            assertEquals(
                Pointer(nIntArray[i].toUnsigned()),
                pointerArray[i],
                "PointerArray element at index $i should match NIntArray element converted to Pointer"
            )
        }
    }

    @Test
    fun `PointerArray asNUIntArray conversion`() {
        val pointerArray = pointerArray(3) { Pointer(it.toUInt().toNUInt()) }
        val nUIntArray = pointerArray.asNUIntArray()

        assertEquals(pointerArray.size, nUIntArray.size, "NUIntArray size should match PointerArray size")
        for (i in 0 until pointerArray.size) {
            assertEquals(
                pointerArray[i].value,
                nUIntArray[i],
                "NUIntArray element at index $i should match PointerArray element's value"
            )
        }
    }

    @Test
    fun `PointerArray asNIntArray conversion`() {
        val pointerArray = pointerArray(3) { Pointer(it.toUInt().toNUInt()) }
        val nIntArray = pointerArray.asNIntArray()

        assertEquals(pointerArray.size, nIntArray.size, "NIntArray size should match PointerArray size")
        for (i in 0 until pointerArray.size) {
            assertEquals(
                pointerArray[i].value.toSigned(),
                nIntArray[i],
                "NIntArray element at index $i should match PointerArray element's value converted to signed"
            )
        }
    }

    // Operator tests

    @Test
    fun `contains operator returns correct result`() {
        val array = pointerArray(3) { Pointer(it.toUInt().toNUInt()) }
        val pointer1 = Pointer(1u.toNUInt())
        val pointer5 = Pointer(5u.toNUInt())

        assertTrue(pointer1 in array, "Array should contain the pointer with value 1u")
        assertFalse(pointer5 in array, "Array should not contain the pointer with value 5u")
    }

    @Test
    fun `get operator returns correct element`() {
        val array = pointerArray(3) { Pointer(it.toUInt().toNUInt()) }

        assertEquals(Pointer(0u.toNUInt()), array[0], "get operator should return the correct element at index 0")
        assertEquals(Pointer(1u.toNUInt()), array[1], "get operator should return the correct element at index 1")
        assertEquals(Pointer(2u.toNUInt()), array[2], "get operator should return the correct element at index 2")
    }

    @Test
    fun `set operator sets correct element`() {
        val array = pointerArray(3)

        array[0] = Pointer(10u.toNUInt())
        array[1] = Pointer(20u.toNUInt())
        array[2] = Pointer(30u.toNUInt())

        assertEquals(Pointer(10u.toNUInt()), array[0], "set operator should set the correct element at index 0")
        assertEquals(Pointer(20u.toNUInt()), array[1], "set operator should set the correct element at index 1")
        assertEquals(Pointer(30u.toNUInt()), array[2], "set operator should set the correct element at index 2")
    }

    @Test
    fun `plus operator concatenates arrays correctly`() {
        val array1 = pointerArray(2) { Pointer(it.toUInt().toNUInt()) }  // [0u, 1u]
        val array2 = pointerArray(3) { Pointer((it + 2).toUInt().toNUInt()) }  // [2u, 3u, 4u]

        val result = array1 + array2

        assertEquals(5, result.size, "Concatenated array size should be the sum of the input array sizes")
        assertEquals(Pointer(0u.toNUInt()), result[0], "First element should be from the first array")
        assertEquals(Pointer(1u.toNUInt()), result[1], "Second element should be from the first array")
        assertEquals(Pointer(2u.toNUInt()), result[2], "Third element should be from the second array")
        assertEquals(Pointer(3u.toNUInt()), result[3], "Fourth element should be from the second array")
        assertEquals(Pointer(4u.toNUInt()), result[4], "Fifth element should be from the second array")
    }

    @Test
    fun `minus operator removes elements correctly`() {
        val array1 = pointerArray(5) { Pointer(it.toUInt().toNUInt()) }  // [0u, 1u, 2u, 3u, 4u]
        val array2 = pointerArray(2) { Pointer((it * 2 + 1).toUInt().toNUInt()) }  // [1u, 3u]

        val result = array1 - array2

        // The exact behavior of minus might vary by platform, but we can check that elements from array2 are not in result
        assertFalse(Pointer(1u.toNUInt()) in result, "Result should not contain elements from the second array")
        assertFalse(Pointer(3u.toNUInt()) in result, "Result should not contain elements from the second array")
        assertTrue(
            Pointer(0u.toNUInt()) in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            Pointer(2u.toNUInt()) in result,
            "Result should contain elements from the first array that are not in the second array"
        )
        assertTrue(
            Pointer(4u.toNUInt()) in result,
            "Result should contain elements from the first array that are not in the second array"
        )
    }

    // Other functionality tests

    @Test
    fun `asSequence returns correct sequence`() {
        val array = pointerArray(3) { Pointer(it.toUInt().toNUInt()) }
        val sequence = array.asSequence()

        val list = sequence.toList()
        assertEquals(3, list.size, "Sequence should have the same number of elements as the array")
        assertEquals(Pointer(0u.toNUInt()), list[0], "First element in sequence should match first element in array")
        assertEquals(Pointer(1u.toNUInt()), list[1], "Second element in sequence should match second element in array")
        assertEquals(Pointer(2u.toNUInt()), list[2], "Third element in sequence should match third element in array")
    }
}
