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

@OptIn(ExperimentalUnsignedTypes::class)
class FFIArgBufferTest {

    @Test
    fun `putByte stores byte value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: Byte = 42

        buffer.putByte(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readByte(buffer.address), "Byte value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.BYTE, buffer.types[0], "Type should be BYTE")
    }

    @Test
    fun `putShort stores short value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: Short = 12345

        buffer.putShort(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readShort(buffer.address), "Short value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.SHORT, buffer.types[0], "Type should be SHORT")
    }

    @Test
    fun `putInt stores int value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 123456789

        buffer.putInt(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readInt(buffer.address), "Int value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.INT, buffer.types[0], "Type should be INT")
    }

    @Test
    fun `putLong stores long value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 1234567890123456789L

        buffer.putLong(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readLong(buffer.address), "Long value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.LONG, buffer.types[0], "Type should be LONG")
    }

    @Test
    fun `putNInt stores native integer value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 123456789.toNInt()

        buffer.putNInt(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readNInt(buffer.address), "NInt value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.NINT, buffer.types[0], "Type should be NINT")
    }

    @Test
    fun `putPointer stores pointer value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testPointer by dropping { Memory.allocate(4.toNUInt()) }

        buffer.putPointer(testPointer)

        // Verify the value was written correctly
        assertEquals(testPointer, Memory.readPointer(buffer.address), "Pointer value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.PTR, buffer.types[0], "Type should be PTR")
    }

    @Test
    fun `putFloat stores float value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 3.14159f

        buffer.putFloat(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readFloat(buffer.address), "Float value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.FLOAT, buffer.types[0], "Type should be FLOAT")
    }

    @Test
    fun `putDouble stores double value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 3.14159265358979

        buffer.putDouble(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readDouble(buffer.address), "Double value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.DOUBLE, buffer.types[0], "Type should be DOUBLE")
    }

    @Test
    fun `putUByte stores unsigned byte value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: UByte = 200u

        buffer.putUByte(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readUByte(buffer.address), "UByte value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.UBYTE, buffer.types[0], "Type should be UBYTE")
    }

    @Test
    fun `putUShort stores unsigned short value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: UShort = 50000u

        buffer.putUShort(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readUShort(buffer.address), "UShort value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.USHORT, buffer.types[0], "Type should be USHORT")
    }

    @Test
    fun `putUInt stores unsigned int value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: UInt = 3000000000u

        buffer.putUInt(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readUInt(buffer.address), "UInt value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.UINT, buffer.types[0], "Type should be UINT")
    }

    @Test
    fun `putULong stores unsigned long value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue: ULong = 10000000000000000000uL

        buffer.putULong(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readULong(buffer.address), "ULong value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.ULONG, buffer.types[0], "Type should be ULONG")
    }

    @Test
    fun `putNUInt stores native unsigned integer value correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testValue = 3000000000u.toNUInt()

        buffer.putNUInt(testValue)

        // Verify the value was written correctly
        assertEquals(testValue, Memory.readNUInt(buffer.address), "NUInt value should be stored correctly")
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.NUINT, buffer.types[0], "Type should be NUINT")
    }

    @Test
    fun `putBytes stores byte array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = byteArrayOf(1, 2, 3, 4, 5)

        buffer.putBytes(testArray)

        // Verify the values were written correctly
        val result = Memory.readBytes(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Byte at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.BYTE, buffer.types[0], "Type should be BYTE")
    }

    @Test
    fun `putShorts stores short array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = shortArrayOf(1000, 2000, 3000, 4000, 5000)

        buffer.putShorts(testArray)

        // Verify the values were written correctly
        val result = Memory.readShorts(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Short at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.SHORT, buffer.types[0], "Type should be SHORT")
    }

    @Test
    fun `putInts stores int array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = intArrayOf(100000, 200000, 300000, 400000, 500000)

        buffer.putInts(testArray)

        // Verify the values were written correctly
        val result = Memory.readInts(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Int at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.INT, buffer.types[0], "Type should be INT")
    }

    @Test
    fun `putLongs stores long array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = longArrayOf(
            1000000000000L, 2000000000000L, 3000000000000L, 4000000000000L, 5000000000000L
        )

        buffer.putLongs(testArray)

        // Verify the values were written correctly
        val result = Memory.readLongs(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Long at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.LONG, buffer.types[0], "Type should be LONG")
    }

    @Test
    fun `putNInts stores native integer array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = nIntArray(5).apply {
            this[0] = 100000.toNInt()
            this[1] = 200000.toNInt()
            this[2] = 300000.toNInt()
            this[3] = 400000.toNInt()
            this[4] = 500000.toNInt()
        }

        buffer.putNInts(testArray)

        // Verify the values were written correctly
        val result = Memory.readNInts(buffer.address, testArray.size)
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "NInt at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.NINT, buffer.types[0], "Type should be NINT")
    }

    @Test
    fun `putUBytes stores unsigned byte array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = ubyteArrayOf(100u, 150u, 200u, 250u, 255u)

        buffer.putUBytes(testArray)

        // Verify the values were written correctly
        val result = Memory.readUBytes(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UByte at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.UBYTE, buffer.types[0], "Type should be UBYTE")
    }

    @Test
    fun `putUShorts stores unsigned short array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = ushortArrayOf(10000u, 20000u, 30000u, 40000u, 50000u)

        buffer.putUShorts(testArray)

        // Verify the values were written correctly
        val result = Memory.readUShorts(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UShort at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.USHORT, buffer.types[0], "Type should be USHORT")
    }

    @Test
    fun `putUInts stores unsigned int array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = uintArrayOf(1000000000u, 2000000000u, 3000000000u, 4000000000u)

        buffer.putUInts(testArray)

        // Verify the values were written correctly
        val result = Memory.readUInts(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "UInt at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.UINT, buffer.types[0], "Type should be UINT")
    }

    @Test
    fun `putULongs stores unsigned long array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = ulongArrayOf(
            10000000000000000000uL, 12000000000000000000uL, 14000000000000000000uL, 16000000000000000000uL
        )

        buffer.putULongs(testArray)

        // Verify the values were written correctly
        val result = Memory.readULongs(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "ULong at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.ULONG, buffer.types[0], "Type should be ULONG")
    }

    @Test
    fun `putNUInts stores native unsigned integer array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = nUIntArray(5).apply {
            this[0] = 1000000000u.toNUInt()
            this[1] = 2000000000u.toNUInt()
            this[2] = 3000000000u.toNUInt()
            this[3] = 4000000000u.toNUInt()
            this[4] = 4294967295u.toNUInt()
        }

        buffer.putNUInts(testArray)

        // Verify the values were written correctly
        val result = Memory.readNUInts(buffer.address, testArray.size)
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "NUInt at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.NUINT, buffer.types[0], "Type should be NUINT")
    }

    @Test
    fun `putFloats stores float array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = floatArrayOf(1.1f, 2.2f, 3.3f, 4.4f, 5.5f)

        buffer.putFloats(testArray)

        // Verify the values were written correctly
        val result = Memory.readFloats(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Float at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.FLOAT, buffer.types[0], "Type should be FLOAT")
    }

    @Test
    fun `putDoubles stores double array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        val testArray = doubleArrayOf(1.1, 2.2, 3.3, 4.4, 5.5)

        buffer.putDoubles(testArray)

        // Verify the values were written correctly
        val result = Memory.readDoubles(buffer.address, testArray.size)
        for (i in testArray.indices) {
            assertEquals(testArray[i], result[i], "Double at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.DOUBLE, buffer.types[0], "Type should be DOUBLE")
    }

    @Test
    fun `putPointers stores pointer array correctly`() = deferring {
        val buffer = FFIArgBuffer.get()
        // Create test pointers
        val testPointers = Array(5) { Memory.allocate(4.toNUInt()) }
        val testArray = pointerArray(5).apply {
            for (i in 0 until 5) {
                this[i] = testPointers[i]
            }
        }

        buffer.putPointers(testArray)

        // Verify the values were written correctly
        val result = Memory.readPointers(buffer.address, testArray.size)
        for (i in 0 until testArray.size) {
            assertEquals(testArray[i], result[i], "Pointer at index $i should match")
        }
        // Verify the type was added to the types list
        assertEquals(1, buffer.types.size, "Types list should have one entry")
        assertEquals(FFIType.PTR, buffer.types[0], "Type should be PTR")

        // Free test pointers
        for (ptr in testPointers) {
            Memory.free(ptr)
        }
    }

    @Test
    fun `multiple puts store values correctly and update types list`() = deferring {
        val buffer = FFIArgBuffer.get()

        // Put multiple values of different types
        buffer.putInt(123)
        buffer.putFloat(3.14f)
        buffer.putDouble(2.71828)

        // Verify the types list has the correct entries
        assertEquals(3, buffer.types.size, "Types list should have three entries")
        assertEquals(FFIType.INT, buffer.types[0], "First type should be INT")
        assertEquals(FFIType.FLOAT, buffer.types[1], "Second type should be FLOAT")
        assertEquals(FFIType.DOUBLE, buffer.types[2], "Third type should be DOUBLE")

        // Verify the values were written at the correct offsets
        assertEquals(123, Memory.readInt(buffer.address), "Int value should be stored correctly")
        assertEquals(
            3.14f,
            Memory.readFloat(buffer.address + FFIType.INT.size.toNUInt()),
            "Float value should be stored correctly"
        )
        assertEquals(
            2.71828,
            Memory.readDouble(buffer.address + (FFIType.INT.size + FFIType.FLOAT.size).toNUInt()),
            "Double value should be stored correctly"
        )
    }

    @Test
    fun `clear resets buffer state`() = deferring {
        val buffer = FFIArgBuffer.get()

        // Put some values
        buffer.putInt(123)
        buffer.putFloat(3.14f)

        // Verify the types list has entries
        assertEquals(2, buffer.types.size, "Types list should have two entries before clear")

        // Clear the buffer
        (buffer as FFIArgBufferImpl).clear()

        // Verify the types list is empty
        assertEquals(0, buffer.types.size, "Types list should be empty after clear")
    }
}
