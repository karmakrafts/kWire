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
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Tests for the FFIType class.
 * These tests verify the behavior of FFIType's predefined types, type conversion methods,
 * and array type creation functionality.
 */
class FFITypeTest {
    /**
     * Tests that predefined FFI types have the correct properties.
     */
    @Test
    fun `predefined FFI types have correct properties`() {
        // Test VOID type
        assertEquals(0, FFIType.VOID.size, "VOID size should be 0")
        assertEquals(0, FFIType.VOID.dimensions, "VOID dimensions should be 0")
        assertSame(FFIType.VOID, FFIType.VOID.elementType, "VOID elementType should be itself")

        // Test BYTE type
        assertEquals(Byte.SIZE_BYTES, FFIType.BYTE.size, "BYTE size should be ${Byte.SIZE_BYTES}")
        assertEquals(0, FFIType.BYTE.dimensions, "BYTE dimensions should be 0")
        assertSame(FFIType.BYTE, FFIType.BYTE.elementType, "BYTE elementType should be itself")

        // Test SHORT type
        assertEquals(Short.SIZE_BYTES, FFIType.SHORT.size, "SHORT size should be ${Short.SIZE_BYTES}")
        assertEquals(0, FFIType.SHORT.dimensions, "SHORT dimensions should be 0")
        assertSame(FFIType.SHORT, FFIType.SHORT.elementType, "SHORT elementType should be itself")

        // Test INT type
        assertEquals(Int.SIZE_BYTES, FFIType.INT.size, "INT size should be ${Int.SIZE_BYTES}")
        assertEquals(0, FFIType.INT.dimensions, "INT dimensions should be 0")
        assertSame(FFIType.INT, FFIType.INT.elementType, "INT elementType should be itself")

        // Test LONG type
        assertEquals(Long.SIZE_BYTES, FFIType.LONG.size, "LONG size should be ${Long.SIZE_BYTES}")
        assertEquals(0, FFIType.LONG.dimensions, "LONG dimensions should be 0")
        assertSame(FFIType.LONG, FFIType.LONG.elementType, "LONG elementType should be itself")

        // Test NINT type
        assertEquals(Pointer.SIZE_BYTES, FFIType.NINT.size, "NINT size should be ${Pointer.SIZE_BYTES}")
        assertEquals(0, FFIType.NINT.dimensions, "NINT dimensions should be 0")
        assertSame(FFIType.NINT, FFIType.NINT.elementType, "NINT elementType should be itself")

        // Test UBYTE type
        assertEquals(UByte.SIZE_BYTES, FFIType.UBYTE.size, "UBYTE size should be ${UByte.SIZE_BYTES}")
        assertEquals(0, FFIType.UBYTE.dimensions, "UBYTE dimensions should be 0")
        assertSame(FFIType.UBYTE, FFIType.UBYTE.elementType, "UBYTE elementType should be itself")

        // Test USHORT type
        assertEquals(UShort.SIZE_BYTES, FFIType.USHORT.size, "USHORT size should be ${UShort.SIZE_BYTES}")
        assertEquals(0, FFIType.USHORT.dimensions, "USHORT dimensions should be 0")
        assertSame(FFIType.USHORT, FFIType.USHORT.elementType, "USHORT elementType should be itself")

        // Test UINT type
        assertEquals(UInt.SIZE_BYTES, FFIType.UINT.size, "UINT size should be ${UInt.SIZE_BYTES}")
        assertEquals(0, FFIType.UINT.dimensions, "UINT dimensions should be 0")
        assertSame(FFIType.UINT, FFIType.UINT.elementType, "UINT elementType should be itself")

        // Test ULONG type
        assertEquals(ULong.SIZE_BYTES, FFIType.ULONG.size, "ULONG size should be ${ULong.SIZE_BYTES}")
        assertEquals(0, FFIType.ULONG.dimensions, "ULONG dimensions should be 0")
        assertSame(FFIType.ULONG, FFIType.ULONG.elementType, "ULONG elementType should be itself")

        // Test NUINT type
        assertEquals(Pointer.SIZE_BYTES, FFIType.NUINT.size, "NUINT size should be ${Pointer.SIZE_BYTES}")
        assertEquals(0, FFIType.NUINT.dimensions, "NUINT dimensions should be 0")
        assertSame(FFIType.NUINT, FFIType.NUINT.elementType, "NUINT elementType should be itself")

        // Test FLOAT type
        assertEquals(Float.SIZE_BYTES, FFIType.FLOAT.size, "FLOAT size should be ${Float.SIZE_BYTES}")
        assertEquals(0, FFIType.FLOAT.dimensions, "FLOAT dimensions should be 0")
        assertSame(FFIType.FLOAT, FFIType.FLOAT.elementType, "FLOAT elementType should be itself")

        // Test DOUBLE type
        assertEquals(Double.SIZE_BYTES, FFIType.DOUBLE.size, "DOUBLE size should be ${Double.SIZE_BYTES}")
        assertEquals(0, FFIType.DOUBLE.dimensions, "DOUBLE dimensions should be 0")
        assertSame(FFIType.DOUBLE, FFIType.DOUBLE.elementType, "DOUBLE elementType should be itself")

        // Test PTR type
        assertEquals(Pointer.SIZE_BYTES, FFIType.PTR.size, "PTR size should be ${Pointer.SIZE_BYTES}")
        assertEquals(0, FFIType.PTR.dimensions, "PTR dimensions should be 0")
        assertSame(FFIType.PTR, FFIType.PTR.elementType, "PTR elementType should be itself")
    }

    /**
     * Tests the array extension function for creating array types.
     */
    @Test
    fun `array extension function creates array types with correct properties`() {
        // Create array types for all base types
        val voidArray = FFIType.VOID.array(10)
        val byteArray = FFIType.BYTE.array(10)
        val shortArray = FFIType.SHORT.array(10)
        val intArray = FFIType.INT.array(10)
        val longArray = FFIType.LONG.array(10)
        val nintArray = FFIType.NINT.array(10)
        val ubyteArray = FFIType.UBYTE.array(10)
        val ushortArray = FFIType.USHORT.array(10)
        val uintArray = FFIType.UINT.array(10)
        val ulongArray = FFIType.ULONG.array(10)
        val nuintArray = FFIType.NUINT.array(10)
        val floatArray = FFIType.FLOAT.array(10)
        val doubleArray = FFIType.DOUBLE.array(10)
        val ptrArray = FFIType.PTR.array(10)

        // Test void array properties
        assertEquals(1, voidArray.dimensions, "Void array should have 1 dimension")
        assertSame(FFIType.VOID, voidArray.elementType, "Void array element type should be VOID")
        assertEquals(0 * 10, voidArray.size, "Void array size should be element size * count")

        // Test byte array properties
        assertEquals(1, byteArray.dimensions, "Byte array should have 1 dimension")
        assertSame(FFIType.BYTE, byteArray.elementType, "Byte array element type should be BYTE")
        assertEquals(Byte.SIZE_BYTES * 10, byteArray.size, "Byte array size should be element size * count")

        // Test short array properties
        assertEquals(1, shortArray.dimensions, "Short array should have 1 dimension")
        assertSame(FFIType.SHORT, shortArray.elementType, "Short array element type should be SHORT")
        assertEquals(Short.SIZE_BYTES * 10, shortArray.size, "Short array size should be element size * count")

        // Test int array properties
        assertEquals(1, intArray.dimensions, "Int array should have 1 dimension")
        assertSame(FFIType.INT, intArray.elementType, "Int array element type should be INT")
        assertEquals(Int.SIZE_BYTES * 10, intArray.size, "Int array size should be element size * count")

        // Test long array properties
        assertEquals(1, longArray.dimensions, "Long array should have 1 dimension")
        assertSame(FFIType.LONG, longArray.elementType, "Long array element type should be LONG")
        assertEquals(Long.SIZE_BYTES * 10, longArray.size, "Long array size should be element size * count")

        // Test nint array properties
        assertEquals(1, nintArray.dimensions, "NInt array should have 1 dimension")
        assertSame(FFIType.NINT, nintArray.elementType, "NInt array element type should be NINT")
        assertEquals(Pointer.SIZE_BYTES * 10, nintArray.size, "NInt array size should be element size * count")

        // Test ubyte array properties
        assertEquals(1, ubyteArray.dimensions, "UByte array should have 1 dimension")
        assertSame(FFIType.UBYTE, ubyteArray.elementType, "UByte array element type should be UBYTE")
        assertEquals(UByte.SIZE_BYTES * 10, ubyteArray.size, "UByte array size should be element size * count")

        // Test ushort array properties
        assertEquals(1, ushortArray.dimensions, "UShort array should have 1 dimension")
        assertSame(FFIType.USHORT, ushortArray.elementType, "UShort array element type should be USHORT")
        assertEquals(UShort.SIZE_BYTES * 10, ushortArray.size, "UShort array size should be element size * count")

        // Test uint array properties
        assertEquals(1, uintArray.dimensions, "UInt array should have 1 dimension")
        assertSame(FFIType.UINT, uintArray.elementType, "UInt array element type should be UINT")
        assertEquals(UInt.SIZE_BYTES * 10, uintArray.size, "UInt array size should be element size * count")

        // Test ulong array properties
        assertEquals(1, ulongArray.dimensions, "ULong array should have 1 dimension")
        assertSame(FFIType.ULONG, ulongArray.elementType, "ULong array element type should be ULONG")
        assertEquals(ULong.SIZE_BYTES * 10, ulongArray.size, "ULong array size should be element size * count")

        // Test nuint array properties
        assertEquals(1, nuintArray.dimensions, "NUInt array should have 1 dimension")
        assertSame(FFIType.NUINT, nuintArray.elementType, "NUInt array element type should be NUINT")
        assertEquals(Pointer.SIZE_BYTES * 10, nuintArray.size, "NUInt array size should be element size * count")

        // Test float array properties
        assertEquals(1, floatArray.dimensions, "Float array should have 1 dimension")
        assertSame(FFIType.FLOAT, floatArray.elementType, "Float array element type should be FLOAT")
        assertEquals(Float.SIZE_BYTES * 10, floatArray.size, "Float array size should be element size * count")

        // Test double array properties
        assertEquals(1, doubleArray.dimensions, "Double array should have 1 dimension")
        assertSame(FFIType.DOUBLE, doubleArray.elementType, "Double array element type should be DOUBLE")
        assertEquals(Double.SIZE_BYTES * 10, doubleArray.size, "Double array size should be element size * count")

        // Test ptr array properties
        assertEquals(1, ptrArray.dimensions, "Pointer array should have 1 dimension")
        assertSame(FFIType.PTR, ptrArray.elementType, "Pointer array element type should be PTR")
        assertEquals(Pointer.SIZE_BYTES * 10, ptrArray.size, "Pointer array size should be element size * count")

        // Test multi-dimensional arrays
        val twoDimArray = FFIType.INT.array(3).array(2)
        assertEquals(2, twoDimArray.dimensions, "Two-dimensional array should have 2 dimensions")
        assertSame(
            FFIType.INT,
            twoDimArray.elementType.elementType,
            "Two-dimensional array base element type should be INT"
        )
        assertEquals(
            Int.SIZE_BYTES * 3 * 2,
            twoDimArray.size,
            "Two-dimensional array size should be element size * all dimensions"
        )
    }

    /**
     * Tests the types array in the companion object.
     */
    @Test
    fun `types array contains all predefined FFI types`() {
        val types = FFIType.types

        // Check that all predefined types are in the array
        assertTrue(types.contains(FFIType.VOID), "types array should contain VOID")
        assertTrue(types.contains(FFIType.BYTE), "types array should contain BYTE")
        assertTrue(types.contains(FFIType.SHORT), "types array should contain SHORT")
        assertTrue(types.contains(FFIType.INT), "types array should contain INT")
        assertTrue(types.contains(FFIType.LONG), "types array should contain LONG")
        assertTrue(types.contains(FFIType.NINT), "types array should contain NINT")
        assertTrue(types.contains(FFIType.UBYTE), "types array should contain UBYTE")
        assertTrue(types.contains(FFIType.USHORT), "types array should contain USHORT")
        assertTrue(types.contains(FFIType.UINT), "types array should contain UINT")
        assertTrue(types.contains(FFIType.ULONG), "types array should contain ULONG")
        assertTrue(types.contains(FFIType.NUINT), "types array should contain NUINT")
        assertTrue(types.contains(FFIType.FLOAT), "types array should contain FLOAT")
        assertTrue(types.contains(FFIType.DOUBLE), "types array should contain DOUBLE")
        assertTrue(types.contains(FFIType.PTR), "types array should contain PTR")

        // Check that the array has the expected size
        assertEquals(14, types.size, "types array should contain 14 elements")
    }
}
