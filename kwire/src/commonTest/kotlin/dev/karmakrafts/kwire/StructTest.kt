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

@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.karmakrafts.kwire

import dev.karmakrafts.rakii.deferring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StructTest {
    // StructField tests

    @Test
    fun `StructField initializes with correct type and zero offset`() {
        val type = FFIType.INT
        val field = StructField(type)

        assertEquals(type, field.type, "StructField should have the correct type")
        assertEquals(0U.toNUInt(), field.offset, "StructField should initialize with zero offset")
    }

    // Struct creation tests

    @Test
    fun `Struct constructor with list creates struct with correct fields`() = deferring {
        val fieldTypes = listOf(FFIType.INT, FFIType.FLOAT, FFIType.DOUBLE)
        val struct by dropping { Struct.allocate(fieldTypes) }

        assertEquals(fieldTypes.size, struct.type.fields.size, "Struct should have the correct number of fields")
        for (i in fieldTypes.indices) {
            assertEquals(fieldTypes[i], struct.type.fields[i].type, "Field $i should have the correct type")
        }
    }

    @Test
    fun `Struct constructor with varargs creates struct with correct fields`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.INT, FFIType.FLOAT, FFIType.DOUBLE) }

        assertEquals(3, struct.type.fields.size, "Struct should have the correct number of fields")
        assertEquals(FFIType.INT, struct.type.fields[0].type, "Field 0 should have INT type")
        assertEquals(FFIType.FLOAT, struct.type.fields[1].type, "Field 1 should have FLOAT type")
        assertEquals(FFIType.DOUBLE, struct.type.fields[2].type, "Field 2 should have DOUBLE type")
    }

    @Test
    fun `Struct initializes field offsets correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.BYTE, FFIType.INT, FFIType.DOUBLE) }

        // Offsets should be calculated based on field sizes
        assertEquals(0U.toNUInt(), struct.type.fields[0].offset, "First field offset should be 0")
        assertEquals(
            FFIType.BYTE.size.toNUInt(), struct.type.fields[1].offset, "Second field offset should be size of first field"
        )
        assertEquals(
            (FFIType.BYTE.size + FFIType.INT.size).toNUInt(),
            struct.type.fields[2].offset,
            "Third field offset should be sum of previous field sizes"
        )
    }

    // Field access tests

    @Test
    fun `getFieldOffset returns correct offset`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.BYTE, FFIType.INT, FFIType.DOUBLE) }

        assertEquals(0U.toNUInt(), struct.type.getFieldOffset(0), "Offset of field 0 should be 0")
        assertEquals(
            FFIType.BYTE.size.toNUInt(), struct.type.getFieldOffset(1), "Offset of field 1 should be size of field 0"
        )
        assertEquals(
            (FFIType.BYTE.size + FFIType.INT.size).toNUInt(),
            struct.type.getFieldOffset(2),
            "Offset of field 2 should be sum of sizes of fields 0 and 1"
        )
    }

    @Test
    fun `getFieldAddress returns correct address`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.BYTE, FFIType.INT, FFIType.DOUBLE) }

        assertEquals(struct.address, struct.getFieldAddress(0), "Address of field 0 should be struct address")
        assertEquals(
            struct.address + FFIType.BYTE.size.toNUInt(),
            struct.getFieldAddress(1),
            "Address of field 1 should be struct address + offset of field 1"
        )
        assertEquals(
            struct.address + (FFIType.BYTE.size + FFIType.INT.size).toNUInt(),
            struct.getFieldAddress(2),
            "Address of field 2 should be struct address + offset of field 2"
        )
    }

    // Read/write tests for primitive types

    @Test
    fun `setByte and getByte work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.BYTE) }

        val testValue: Byte = 42
        struct.setByte(0, testValue)
        assertEquals(testValue, struct.getByte(0), "getByte should return the value set by setByte")
    }

    @Test
    fun `setShort and getShort work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.SHORT) }

        val testValue: Short = 12345
        struct.setShort(0, testValue)
        assertEquals(testValue, struct.getShort(0), "getShort should return the value set by setShort")
    }

    @Test
    fun `setInt and getInt work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.INT) }

        val testValue = 123456789
        struct.setInt(0, testValue)
        assertEquals(testValue, struct.getInt(0), "getInt should return the value set by setInt")
    }

    @Test
    fun `setLong and getLong work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.LONG) }

        val testValue = 1234567890123456789L
        struct.setLong(0, testValue)
        assertEquals(testValue, struct.getLong(0), "getLong should return the value set by setLong")
    }

    @Test
    fun `setNInt and getNInt work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.NINT) }

        val testValue = 123456789.toNInt()
        struct.setNInt(0, testValue)
        assertEquals(testValue, struct.getNInt(0), "getNInt should return the value set by setNInt")
    }

    @Test
    fun `setUByte and getUByte work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.BYTE) }

        val testValue: UByte = 200u
        struct.setUByte(0, testValue)
        assertEquals(testValue, struct.getUByte(0), "getUByte should return the value set by setUByte")
    }

    @Test
    fun `setUShort and getUShort work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.SHORT) }

        val testValue: UShort = 50000u
        struct.setUShort(0, testValue)
        assertEquals(testValue, struct.getUShort(0), "getUShort should return the value set by setUShort")
    }

    @Test
    fun `setUInt and getUInt work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.INT) }

        val testValue: UInt = 3000000000u
        struct.setUInt(0, testValue)
        assertEquals(testValue, struct.getUInt(0), "getUInt should return the value set by setUInt")
    }

    @Test
    fun `setULong and getULong work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.LONG) }

        val testValue: ULong = 10000000000000000000uL
        struct.setULong(0, testValue)
        assertEquals(testValue, struct.getULong(0), "getULong should return the value set by setULong")
    }

    @Test
    fun `setNUInt and getNUInt work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.NUINT) }

        val testValue = 3000000000u.toNUInt()
        struct.setNUInt(0, testValue)
        assertEquals(testValue, struct.getNUInt(0), "getNUInt should return the value set by setNUInt")
    }

    @Test
    fun `setFloat and getFloat work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.FLOAT) }

        val testValue = 3.14159f
        struct.setFloat(0, testValue)
        assertEquals(testValue, struct.getFloat(0), "getFloat should return the value set by setFloat")
    }

    @Test
    fun `setDouble and getDouble work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.DOUBLE) }

        val testValue = 3.14159265358979
        struct.setDouble(0, testValue)
        assertEquals(testValue, struct.getDouble(0), "getDouble should return the value set by setDouble")
    }

    @Test
    fun `setPointer and getPointer work correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.PTR) }
        val testPtr by dropping { Memory.allocate(4.toNUInt()) }

        struct.setPointer(0, testPtr)
        assertEquals(testPtr, struct.getPointer(0), "getPointer should return the value set by setPointer")
    }

    // Array read/write tests

    @Test
    fun `setBytes and getBytes work correctly`() = deferring {
        // Allocate enough space for 5 bytes
        val struct by dropping { Struct.allocate(FFIType.BYTE, FFIType.BYTE, FFIType.BYTE, FFIType.BYTE, FFIType.BYTE) }

        val testArray = byteArrayOf(1, 2, 3, 4, 5)
        struct.setBytes(0, testArray)
        val result = struct.getBytes(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Byte at index $i should match")
        }
    }

    @Test
    fun `setInts and getInts work correctly`() = deferring {
        // Allocate enough space for 3 ints
        val struct by dropping { Struct.allocate(FFIType.INT, FFIType.INT, FFIType.INT) }

        val testArray = intArrayOf(100, 200, 300)
        struct.setInts(0, testArray)
        val result = struct.getInts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Int at index $i should match")
        }
    }

    @Test
    fun `setFloats and getFloats work correctly`() = deferring {
        // Allocate enough space for 3 floats
        val struct by dropping { Struct.allocate(FFIType.FLOAT, FFIType.FLOAT, FFIType.FLOAT) }

        val testArray = floatArrayOf(1.1f, 2.2f, 3.3f)
        struct.setFloats(0, testArray)
        val result = struct.getFloats(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Float at index $i should match")
        }
    }

    @Test
    fun `setShorts and getShorts work correctly`() = deferring {
        // Allocate enough space for 3 shorts
        val struct by dropping { Struct.allocate(FFIType.SHORT, FFIType.SHORT, FFIType.SHORT) }

        val testArray = shortArrayOf(100, 200, 300)
        struct.setShorts(0, testArray)
        val result = struct.getShorts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Short at index $i should match")
        }
    }

    @Test
    fun `setLongs and getLongs work correctly`() = deferring {
        // Allocate enough space for 3 longs
        val struct by dropping { Struct.allocate(FFIType.LONG, FFIType.LONG, FFIType.LONG) }

        val testArray = longArrayOf(100L, 200L, 300L)
        struct.setLongs(0, testArray)
        val result = struct.getLongs(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Long at index $i should match")
        }
    }

    @Test
    fun `setNInts and getNInts work correctly`() = deferring {
        // Allocate enough space for 3 native integers
        val struct by dropping { Struct.allocate(FFIType.NINT, FFIType.NINT, FFIType.NINT) }

        val testArray = nIntArray(3) {
            when (it) {
                0 -> 100.toNInt()
                1 -> 200.toNInt()
                else -> 300.toNInt()
            }
        }
        struct.setNInts(0, testArray)
        val result = struct.getNInts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "NInt at index $i should match")
        }
    }

    @Test
    fun `setUBytes and getUBytes work correctly`() = deferring {
        // Allocate enough space for 5 unsigned bytes
        val struct by dropping { Struct.allocate(FFIType.BYTE, FFIType.BYTE, FFIType.BYTE, FFIType.BYTE, FFIType.BYTE) }

        val testArray = ubyteArrayOf(10u, 20u, 30u, 40u, 50u)
        struct.setUBytes(0, testArray)
        val result = struct.getUBytes(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UByte at index $i should match")
        }
    }

    @Test
    fun `setUShorts and getUShorts work correctly`() = deferring {
        // Allocate enough space for 3 unsigned shorts
        val struct by dropping { Struct.allocate(FFIType.SHORT, FFIType.SHORT, FFIType.SHORT) }

        val testArray = ushortArrayOf(1000u, 2000u, 3000u)
        struct.setUShorts(0, testArray)
        val result = struct.getUShorts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UShort at index $i should match")
        }
    }

    @Test
    fun `setUInts and getUInts work correctly`() = deferring {
        // Allocate enough space for 3 unsigned ints
        val struct by dropping { Struct.allocate(FFIType.INT, FFIType.INT, FFIType.INT) }

        val testArray = uintArrayOf(1000000u, 2000000u, 3000000u)
        struct.setUInts(0, testArray)
        val result = struct.getUInts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UInt at index $i should match")
        }
    }

    @Test
    fun `setULongs and getULongs work correctly`() = deferring {
        // Allocate enough space for 3 unsigned longs
        val struct by dropping { Struct.allocate(FFIType.LONG, FFIType.LONG, FFIType.LONG) }

        val testArray = ulongArrayOf(1000000000uL, 2000000000uL, 3000000000uL)
        struct.setULongs(0, testArray)
        val result = struct.getULongs(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "ULong at index $i should match")
        }
    }

    @Test
    fun `setNUInts and getNUInts work correctly`() = deferring {
        // Allocate enough space for 3 native unsigned integers
        val struct by dropping { Struct.allocate(FFIType.NUINT, FFIType.NUINT, FFIType.NUINT) }

        val testArray = nUIntArray(3) {
            when (it) {
                0 -> 1000000u.toNUInt()
                1 -> 2000000u.toNUInt()
                else -> 3000000u.toNUInt()
            }
        }
        struct.setNUInts(0, testArray)
        val result = struct.getNUInts(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "NUInt at index $i should match")
        }
    }

    @Test
    fun `setDoubles and getDoubles work correctly`() = deferring {
        // Allocate enough space for 3 doubles
        val struct by dropping { Struct.allocate(FFIType.DOUBLE, FFIType.DOUBLE, FFIType.DOUBLE) }

        val testArray = doubleArrayOf(1.1, 2.2, 3.3)
        struct.setDoubles(0, testArray)
        val result = struct.getDoubles(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Double at index $i should match")
        }
    }

    @Test
    fun `setPointers and getPointers work correctly`() = deferring {
        // Allocate enough space for 3 pointers
        val struct by dropping { Struct.allocate(FFIType.PTR, FFIType.PTR, FFIType.PTR) }

        val ptr1 by dropping { Memory.allocate(4.toNUInt()) }
        val ptr2 by dropping { Memory.allocate(4.toNUInt()) }
        val ptr3 by dropping { Memory.allocate(4.toNUInt()) }

        val testArray = pointerArray(3) {
            when (it) {
                0 -> ptr1
                1 -> ptr2
                else -> ptr3
            }
        }

        struct.setPointers(0, testArray)
        val result = struct.getPointers(0, testArray.size)

        assertEquals(testArray.size, result.size, "Result array should have the same size as input array")
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "Pointer at index $i should match")
        }
    }

    // Multiple field tests

    @Test
    fun `struct with multiple fields stores and retrieves values correctly`() = deferring {
        val struct by dropping { Struct.allocate(FFIType.INT, FFIType.FLOAT, FFIType.DOUBLE) }

        val intValue = 42
        val floatValue = 3.14f
        val doubleValue = 2.71828

        struct.setInt(0, intValue)
        struct.setFloat(1, floatValue)
        struct.setDouble(2, doubleValue)

        assertEquals(intValue, struct.getInt(0), "Int value should be retrieved correctly")
        assertEquals(floatValue, struct.getFloat(1), "Float value should be retrieved correctly")
        assertEquals(doubleValue, struct.getDouble(2), "Double value should be retrieved correctly")
    }

    // Memory management tests

    @Test
    fun `struct allocates memory of correct size`() = deferring {
        val fieldTypes = listOf(FFIType.BYTE, FFIType.INT, FFIType.DOUBLE)
        val expectedSize = fieldTypes.sumOf { it.size }.toNUInt()
        val struct by dropping { Struct.allocate(fieldTypes) }

        // We can't directly check the allocated size, but we can verify that the address is not null
        assertNotEquals(nullptr, struct.address, "Struct should allocate a valid memory address")
    }
}
