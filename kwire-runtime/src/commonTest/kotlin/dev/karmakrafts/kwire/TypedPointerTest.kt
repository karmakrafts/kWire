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
import kotlin.test.assertNotEquals

class TypedPointerTest {

    // BytePtr Tests

    @Test
    fun `BytePtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val bytePtr = BytePtr(ptr)

        assertEquals(ptr.value, bytePtr.value.value, "BytePtr should have the same address as the original pointer")
    }

    @Test
    fun `BytePtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val bytePtr = BytePtr(ptr)
        assertNotEquals(nullptr(), bytePtr, "BytePtr should have a valid pointer")

        bytePtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `BytePtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4.toNUInt()
        val result = bytePtr + offset

        assertEquals(
            ptr.value + offset, result.value.value, "BytePtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `BytePtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4.toNUInt()
        val result = bytePtr - offset

        assertEquals(
            ptr.value - offset,
            result.value.value,
            "BytePtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `BytePtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4UL
        val result = bytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "BytePtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `BytePtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4UL
        val result = bytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "BytePtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `BytePtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4U
        val result = bytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "BytePtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `BytePtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4U
        val result = bytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "BytePtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `BytePtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4L
        val result = bytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "BytePtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `BytePtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4L
        val result = bytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "BytePtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `BytePtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4
        val result = bytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "BytePtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `BytePtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val offset = 4
        val result = bytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "BytePtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `BytePtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val testValue: Byte = 42

        bytePtr[0] = testValue
        assertEquals(
            testValue, bytePtr[0], "BytePtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `BytePtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val testValue: Byte = 42

        bytePtr[0L] = testValue
        assertEquals(
            testValue, bytePtr[0L], "BytePtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `BytePtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val testValue: Byte = 42

        bytePtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            bytePtr[0.toNUInt()],
            "BytePtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `BytePtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)

        val intPtr = bytePtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "BytePtr reinterpreted to IntPtr should have the same address")

        val shortPtr = bytePtr.reinterpret<ShortPtr>()
        assertEquals(ptr.value, shortPtr.value.value, "BytePtr reinterpreted to ShortPtr should have the same address")
    }

    @Test
    fun `BytePtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val result = bytePtr.inc()

        assertEquals(
            ptr.value + Byte.SIZE_BYTES.toNUInt(), result.value.value, "BytePtr increment should add Byte.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `BytePtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = BytePtr(ptr)
        val result = bytePtr.dec()

        assertEquals(
            ptr.value - Byte.SIZE_BYTES.toNUInt(), result.value.value, "BytePtr decrement should subtract Byte.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asBytePtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = ptr.asBytePtr()

        assertEquals(ptr.value, bytePtr.value.value, "asBytePtr should create a BytePtr with the same address")
    }

    // ShortPtr Tests

    @Test
    fun `ShortPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val shortPtr = ShortPtr(ptr)

        assertEquals(ptr.value, shortPtr.value.value, "ShortPtr should have the same address as the original pointer")
    }

    @Test
    fun `ShortPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val shortPtr = ShortPtr(ptr)
        assertNotEquals(nullptr(), shortPtr, "ShortPtr should have a valid pointer")

        shortPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `ShortPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4.toNUInt()
        val result = shortPtr + offset

        assertEquals(
            ptr.value + offset * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `ShortPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4.toNUInt()
        val result = shortPtr - offset

        assertEquals(
            ptr.value - offset * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `ShortPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4UL
        val result = shortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `ShortPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4UL
        val result = shortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `ShortPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4U
        val result = shortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `ShortPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4U
        val result = shortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `ShortPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4L
        val result = shortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `ShortPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4L
        val result = shortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `ShortPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4
        val result = shortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `ShortPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val offset = 4
        val result = shortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Short.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ShortPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `ShortPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val testValue: Short = 42

        shortPtr[0] = testValue
        assertEquals(
            testValue, shortPtr[0], "ShortPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ShortPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val testValue: Short = 42

        shortPtr[0L] = testValue
        assertEquals(
            testValue, shortPtr[0L], "ShortPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ShortPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val testValue: Short = 42

        shortPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            shortPtr[0.toNUInt()],
            "ShortPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ShortPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)

        val intPtr = shortPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "ShortPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = shortPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "ShortPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `ShortPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val result = shortPtr.inc()

        assertEquals(
            ptr.value + Short.SIZE_BYTES.toNUInt(), result.value.value, "ShortPtr increment should add Short.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `ShortPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ShortPtr(ptr)
        val result = shortPtr.dec()

        assertEquals(
            ptr.value - Short.SIZE_BYTES.toNUInt(), result.value.value, "ShortPtr decrement should subtract Short.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asShortPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ptr.asShortPtr()

        assertEquals(ptr.value, shortPtr.value.value, "asShortPtr should create a ShortPtr with the same address")
    }

    // IntPtr Tests

    @Test
    fun `IntPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val intPtr = IntPtr(ptr)

        assertEquals(ptr.value, intPtr.value.value, "IntPtr should have the same address as the original pointer")
    }

    @Test
    fun `IntPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val intPtr = IntPtr(ptr)
        assertNotEquals(nullptr(), intPtr, "IntPtr should have a valid pointer")

        intPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `IntPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4.toNUInt()
        val result = intPtr + offset

        assertEquals(
            ptr.value + offset * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `IntPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4.toNUInt()
        val result = intPtr - offset

        assertEquals(
            ptr.value - offset * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `IntPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4UL
        val result = intPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `IntPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4UL
        val result = intPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `IntPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4U
        val result = intPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `IntPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4U
        val result = intPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `IntPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4L
        val result = intPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `IntPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4L
        val result = intPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `IntPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4
        val result = intPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `IntPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val offset = 4
        val result = intPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Int.SIZE_BYTES.toNUInt(),
            result.value.value,
            "IntPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `IntPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val testValue: Int = 42

        intPtr[0] = testValue
        assertEquals(testValue, intPtr[0], "IntPtr get/set with Int index should store and retrieve the correct value")
    }

    @Test
    fun `IntPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val testValue: Int = 42

        intPtr[0L] = testValue
        assertEquals(
            testValue, intPtr[0L], "IntPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `IntPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val testValue: Int = 42

        intPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            intPtr[0.toNUInt()],
            "IntPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `IntPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)

        val shortPtr = intPtr.reinterpret<ShortPtr>()
        assertEquals(ptr.value, shortPtr.value.value, "IntPtr reinterpreted to ShortPtr should have the same address")

        val bytePtr = intPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "IntPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `IntPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val result = intPtr.inc()

        assertEquals(
            ptr.value + Int.SIZE_BYTES.toNUInt(), result.value.value, "IntPtr increment should add Int.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `IntPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = IntPtr(ptr)
        val result = intPtr.dec()

        assertEquals(
            ptr.value - Int.SIZE_BYTES.toNUInt(), result.value.value, "IntPtr decrement should subtract Int.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asIntPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = ptr.asIntPtr()

        assertEquals(ptr.value, intPtr.value.value, "asIntPtr should create an IntPtr with the same address")
    }

    // LongPtr Tests

    @Test
    fun `LongPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val longPtr = LongPtr(ptr)

        assertEquals(ptr.value, longPtr.value.value, "LongPtr should have the same address as the original pointer")
    }

    @Test
    fun `LongPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val longPtr = LongPtr(ptr)
        assertNotEquals(nullptr(), longPtr, "LongPtr should have a valid pointer")

        longPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `LongPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4.toNUInt()
        val result = longPtr + offset

        assertEquals(
            ptr.value + offset * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `LongPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4.toNUInt()
        val result = longPtr - offset

        assertEquals(
            ptr.value - offset * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `LongPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4UL
        val result = longPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `LongPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4UL
        val result = longPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `LongPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4U
        val result = longPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `LongPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4U
        val result = longPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `LongPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4L
        val result = longPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `LongPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4L
        val result = longPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `LongPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4
        val result = longPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `LongPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val offset = 4
        val result = longPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Long.SIZE_BYTES.toNUInt(),
            result.value.value,
            "LongPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `LongPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val testValue: Long = 42L

        longPtr[0] = testValue
        assertEquals(
            testValue, longPtr[0], "LongPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `LongPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val testValue: Long = 42L

        longPtr[0L] = testValue
        assertEquals(
            testValue, longPtr[0L], "LongPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `LongPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val testValue: Long = 42L

        longPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            longPtr[0.toNUInt()],
            "LongPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `LongPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)

        val intPtr = longPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "LongPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = longPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "LongPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `LongPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val result = longPtr.inc()

        assertEquals(
            ptr.value + Long.SIZE_BYTES.toNUInt(), result.value.value, "LongPtr increment should add Long.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `LongPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = LongPtr(ptr)
        val result = longPtr.dec()

        assertEquals(
            ptr.value - Long.SIZE_BYTES.toNUInt(), result.value.value, "LongPtr decrement should subtract Long.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asLongPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = ptr.asLongPtr()

        assertEquals(ptr.value, longPtr.value.value, "asLongPtr should create a LongPtr with the same address")
    }

    // NIntPtr Tests

    @Test
    fun `NIntPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val nIntPtr = NIntPtr(ptr)

        assertEquals(ptr.value, nIntPtr.value.value, "NIntPtr should have the same address as the original pointer")
    }

    @Test
    fun `NIntPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val nIntPtr = NIntPtr(ptr)
        assertNotEquals(nullptr(), nIntPtr, "NIntPtr should have a valid pointer")

        nIntPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `NIntPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = nIntPtr + offset

        assertEquals(
            ptr.value + offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `NIntPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = nIntPtr - offset

        assertEquals(
            ptr.value - offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `NIntPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4UL
        val result = nIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `NIntPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4UL
        val result = nIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `NIntPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4U
        val result = nIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `NIntPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4U
        val result = nIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `NIntPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4L
        val result = nIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `NIntPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4L
        val result = nIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `NIntPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4
        val result = nIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `NIntPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val offset = 4
        val result = nIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NIntPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `NIntPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val testValue = 42.toNInt()

        nIntPtr[0] = testValue
        assertEquals(
            testValue, nIntPtr[0], "NIntPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NIntPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val testValue = 42.toNInt()

        nIntPtr[0L] = testValue
        assertEquals(
            testValue, nIntPtr[0L], "NIntPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NIntPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val testValue = 42.toNInt()

        nIntPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            nIntPtr[0.toNUInt()],
            "NIntPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NIntPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)

        val intPtr = nIntPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "NIntPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = nIntPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "NIntPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `NIntPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val result = nIntPtr.inc()

        assertEquals(
            ptr.value + Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NIntPtr increment should add Pointer.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `NIntPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = NIntPtr(ptr)
        val result = nIntPtr.dec()

        assertEquals(
            ptr.value - Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NIntPtr decrement should subtract Pointer.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asNIntPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = ptr.asNIntPtr()

        assertEquals(ptr.value, nIntPtr.value.value, "asNIntPtr should create a NIntPtr with the same address")
    }

    // UBytePtr Tests

    @Test
    fun `UBytePtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val uBytePtr = UBytePtr(ptr)

        assertEquals(ptr.value, uBytePtr.value.value, "UBytePtr should have the same address as the original pointer")
    }

    @Test
    fun `UBytePtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val uBytePtr = UBytePtr(ptr)
        assertNotEquals(nullptr(), uBytePtr, "UBytePtr should have a valid pointer")

        uBytePtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `UBytePtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4.toNUInt()
        val result = uBytePtr + offset

        assertEquals(
            ptr.value + offset, result.value.value, "UBytePtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `UBytePtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4.toNUInt()
        val result = uBytePtr - offset

        assertEquals(
            ptr.value - offset,
            result.value.value,
            "UBytePtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UBytePtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4UL
        val result = uBytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "UBytePtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `UBytePtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4UL
        val result = uBytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "UBytePtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `UBytePtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4U
        val result = uBytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "UBytePtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `UBytePtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4U
        val result = uBytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "UBytePtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UBytePtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4L
        val result = uBytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "UBytePtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `UBytePtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4L
        val result = uBytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "UBytePtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `UBytePtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4
        val result = uBytePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value.value,
            "UBytePtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `UBytePtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val offset = 4
        val result = uBytePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value.value,
            "UBytePtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `UBytePtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val testValue: UByte = 42u

        uBytePtr[0] = testValue
        assertEquals(
            testValue, uBytePtr[0], "UBytePtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UBytePtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val testValue: UByte = 42u

        uBytePtr[0L] = testValue
        assertEquals(
            testValue, uBytePtr[0L], "UBytePtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UBytePtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val testValue: UByte = 42u

        uBytePtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            uBytePtr[0.toNUInt()],
            "UBytePtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UBytePtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)

        val intPtr = uBytePtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "UBytePtr reinterpreted to IntPtr should have the same address")

        val bytePtr = uBytePtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "UBytePtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `UBytePtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val result = uBytePtr.inc()

        assertEquals(
            ptr.value + UByte.SIZE_BYTES.toNUInt(), result.value.value, "UBytePtr increment should add UByte.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `UBytePtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = UBytePtr(ptr)
        val result = uBytePtr.dec()

        assertEquals(
            ptr.value - UByte.SIZE_BYTES.toNUInt(), result.value.value, "UBytePtr decrement should subtract UByte.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asUBytePtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = ptr.asUBytePtr()

        assertEquals(ptr.value, uBytePtr.value.value, "asUBytePtr should create a UBytePtr with the same address")
    }

    // UShortPtr Tests

    @Test
    fun `UShortPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val uShortPtr = UShortPtr(ptr)

        assertEquals(ptr.value, uShortPtr.value.value, "UShortPtr should have the same address as the original pointer")
    }

    @Test
    fun `UShortPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val uShortPtr = UShortPtr(ptr)
        assertNotEquals(nullptr(), uShortPtr, "UShortPtr should have a valid pointer")

        uShortPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `UShortPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4.toNUInt()
        val result = uShortPtr + offset

        assertEquals(
            ptr.value + offset * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `UShortPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4.toNUInt()
        val result = uShortPtr - offset

        assertEquals(
            ptr.value - offset * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UShortPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4UL
        val result = uShortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `UShortPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4UL
        val result = uShortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `UShortPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4U
        val result = uShortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `UShortPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4U
        val result = uShortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UShortPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4L
        val result = uShortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `UShortPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4L
        val result = uShortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `UShortPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4
        val result = uShortPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `UShortPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val offset = 4
        val result = uShortPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UShort.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UShortPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `UShortPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val testValue: UShort = 42u

        uShortPtr[0] = testValue
        assertEquals(
            testValue, uShortPtr[0], "UShortPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UShortPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val testValue: UShort = 42u

        uShortPtr[0L] = testValue
        assertEquals(
            testValue, uShortPtr[0L], "UShortPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UShortPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val testValue: UShort = 42u

        uShortPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            uShortPtr[0.toNUInt()],
            "UShortPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UShortPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)

        val intPtr = uShortPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "UShortPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = uShortPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "UShortPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `UShortPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val result = uShortPtr.inc()

        assertEquals(
            ptr.value + UShort.SIZE_BYTES.toNUInt(), result.value.value, "UShortPtr increment should add UShort.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `UShortPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = UShortPtr(ptr)
        val result = uShortPtr.dec()

        assertEquals(
            ptr.value - UShort.SIZE_BYTES.toNUInt(), result.value.value, "UShortPtr decrement should subtract UShort.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asUShortPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = ptr.asUShortPtr()

        assertEquals(ptr.value, uShortPtr.value.value, "asUShortPtr should create a UShortPtr with the same address")
    }

    // UIntPtr Tests

    @Test
    fun `UIntPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val uIntPtr = UIntPtr(ptr)

        assertEquals(ptr.value, uIntPtr.value.value, "UIntPtr should have the same address as the original pointer")
    }

    @Test
    fun `UIntPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val uIntPtr = UIntPtr(ptr)
        assertNotEquals(nullptr(), uIntPtr, "UIntPtr should have a valid pointer")

        uIntPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `UIntPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = uIntPtr + offset

        assertEquals(
            ptr.value + offset * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `UIntPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = uIntPtr - offset

        assertEquals(
            ptr.value - offset * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UIntPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4UL
        val result = uIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `UIntPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4UL
        val result = uIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `UIntPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4U
        val result = uIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `UIntPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4U
        val result = uIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `UIntPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4L
        val result = uIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `UIntPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4L
        val result = uIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `UIntPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4
        val result = uIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `UIntPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val offset = 4
        val result = uIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * UInt.SIZE_BYTES.toNUInt(),
            result.value.value,
            "UIntPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `UIntPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val testValue: UInt = 42u

        uIntPtr[0] = testValue
        assertEquals(
            testValue, uIntPtr[0], "UIntPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UIntPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val testValue: UInt = 42u

        uIntPtr[0L] = testValue
        assertEquals(
            testValue, uIntPtr[0L], "UIntPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UIntPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val testValue: UInt = 42u

        uIntPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            uIntPtr[0.toNUInt()],
            "UIntPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `UIntPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)

        val intPtr = uIntPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "UIntPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = uIntPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "UIntPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `UIntPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val result = uIntPtr.inc()

        assertEquals(
            ptr.value + UInt.SIZE_BYTES.toNUInt(), result.value.value, "UIntPtr increment should add UInt.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `UIntPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = UIntPtr(ptr)
        val result = uIntPtr.dec()

        assertEquals(
            ptr.value - UInt.SIZE_BYTES.toNUInt(), result.value.value, "UIntPtr decrement should subtract UInt.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asUIntPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = ptr.asUIntPtr()

        assertEquals(ptr.value, uIntPtr.value.value, "asUIntPtr should create a UIntPtr with the same address")
    }

    // ULongPtr Tests

    @Test
    fun `ULongPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val uLongPtr = ULongPtr(ptr)

        assertEquals(ptr.value, uLongPtr.value.value, "ULongPtr should have the same address as the original pointer")
    }

    @Test
    fun `ULongPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val uLongPtr = ULongPtr(ptr)
        assertNotEquals(nullptr(), uLongPtr, "ULongPtr should have a valid pointer")

        uLongPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `ULongPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4.toNUInt()
        val result = uLongPtr + offset

        assertEquals(
            ptr.value + offset * ULong.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ULongPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `ULongPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4.toNUInt()
        val result = uLongPtr - offset

        assertEquals(
            ptr.value - offset * ULong.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ULongPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `ULongPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4UL
        val result = uLongPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * ULong.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ULongPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `ULongPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4UL
        val result = uLongPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * ULong.SIZE_BYTES.toNUInt(),
            result.value.value,
            "ULongPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `ULongPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4U
        val result = uLongPtr + offset

        assertEquals(
            ptr.value + (offset * ULong.SIZE_BYTES.toUInt()).toNUInt(),
            result.value.value,
            "ULongPtr addition with UInt should add the scaled offset to the address"
        )
    }

    @Test
    fun `ULongPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4U
        val result = uLongPtr - offset

        assertEquals(
            ptr.value - (offset * ULong.SIZE_BYTES.toUInt()).toNUInt(),
            result.value.value,
            "ULongPtr subtraction with UInt should subtract the scaled offset from the address"
        )
    }

    @Test
    fun `ULongPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4L
        val result = uLongPtr + offset

        assertEquals(
            ptr.value + (offset * ULong.SIZE_BYTES.toLong()).toNUInt(),
            result.value.value,
            "ULongPtr addition with Long should add the scaled offset to the address"
        )
    }

    @Test
    fun `ULongPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4L
        val result = uLongPtr - offset

        assertEquals(
            ptr.value - (offset * ULong.SIZE_BYTES.toLong()).toNUInt(),
            result.value.value,
            "ULongPtr subtraction with Long should subtract the scaled offset from the address"
        )
    }

    @Test
    fun `ULongPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4
        val result = uLongPtr + offset

        assertEquals(
            ptr.value + (offset * ULong.SIZE_BYTES).toNUInt(),
            result.value.value,
            "ULongPtr addition with Int should add the scaled offset to the address"
        )
    }

    @Test
    fun `ULongPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val offset = 4
        val result = uLongPtr - offset

        assertEquals(
            ptr.value - (offset * ULong.SIZE_BYTES).toNUInt(),
            result.value.value,
            "ULongPtr subtraction with Int should subtract the scaled offset from the address"
        )
    }

    @Test
    fun `ULongPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val testValue: ULong = 42UL

        uLongPtr[0] = testValue
        assertEquals(
            testValue, uLongPtr[0], "ULongPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ULongPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val testValue: ULong = 42UL

        uLongPtr[0L] = testValue
        assertEquals(
            testValue, uLongPtr[0L], "ULongPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ULongPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val testValue: ULong = 42UL

        uLongPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            uLongPtr[0.toNUInt()],
            "ULongPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `ULongPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)

        val intPtr = uLongPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "ULongPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = uLongPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "ULongPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `ULongPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val result = uLongPtr.inc()

        assertEquals(
            ptr.value + ULong.SIZE_BYTES.toNUInt(), result.value.value, "ULongPtr increment should add ULong.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `ULongPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ULongPtr(ptr)
        val result = uLongPtr.dec()

        assertEquals(
            ptr.value - ULong.SIZE_BYTES.toNUInt(), result.value.value, "ULongPtr decrement should subtract ULong.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asULongPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ptr.asULongPtr()

        assertEquals(ptr.value, uLongPtr.value.value, "asULongPtr should create a ULongPtr with the same address")
    }

    // NUIntPtr Tests

    @Test
    fun `NUIntPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val nUIntPtr = NUIntPtr(ptr)

        assertEquals(ptr.value, nUIntPtr.value.value, "NUIntPtr should have the same address as the original pointer")
    }

    @Test
    fun `NUIntPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val nUIntPtr = NUIntPtr(ptr)
        assertNotEquals(nullptr(), nUIntPtr, "NUIntPtr should have a valid pointer")

        nUIntPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `NUIntPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = nUIntPtr + offset

        assertEquals(
            ptr.value + offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `NUIntPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4.toNUInt()
        val result = nUIntPtr - offset

        assertEquals(
            ptr.value - offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `NUIntPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4UL
        val result = nUIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `NUIntPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4UL
        val result = nUIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `NUIntPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4U
        val result = nUIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `NUIntPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4U
        val result = nUIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `NUIntPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4L
        val result = nUIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `NUIntPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4L
        val result = nUIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `NUIntPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4
        val result = nUIntPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `NUIntPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val offset = 4
        val result = nUIntPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NUIntPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `NUIntPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val testValue = 42u.toNUInt()

        nUIntPtr[0] = testValue
        assertEquals(
            testValue, nUIntPtr[0], "NUIntPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NUIntPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val testValue = 42u.toNUInt()

        nUIntPtr[0L] = testValue
        assertEquals(
            testValue, nUIntPtr[0L], "NUIntPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NUIntPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val testValue = 42u.toNUInt()

        nUIntPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            nUIntPtr[0.toNUInt()],
            "NUIntPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `NUIntPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)

        val intPtr = nUIntPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "NUIntPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = nUIntPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "NUIntPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `NUIntPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val result = nUIntPtr.inc()

        assertEquals(
            ptr.value + Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NUIntPtr increment should add Pointer.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `NUIntPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = NUIntPtr(ptr)
        val result = nUIntPtr.dec()

        assertEquals(
            ptr.value - Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NUIntPtr decrement should subtract Pointer.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asNUIntPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = ptr.asNUIntPtr()

        assertEquals(ptr.value, nUIntPtr.value.value, "asNUIntPtr should create a NUIntPtr with the same address")
    }

    // FloatPtr Tests

    @Test
    fun `FloatPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val floatPtr = FloatPtr(ptr)

        assertEquals(ptr.value, floatPtr.value.value, "FloatPtr should have the same address as the original pointer")
    }

    @Test
    fun `FloatPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val floatPtr = FloatPtr(ptr)
        assertNotEquals(nullptr(), floatPtr, "FloatPtr should have a valid pointer")

        floatPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `FloatPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4.toNUInt()
        val result = floatPtr + offset

        assertEquals(
            ptr.value + offset * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `FloatPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4.toNUInt()
        val result = floatPtr - offset

        assertEquals(
            ptr.value - offset * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `FloatPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4UL
        val result = floatPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `FloatPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4UL
        val result = floatPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `FloatPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4U
        val result = floatPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `FloatPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4U
        val result = floatPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `FloatPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4L
        val result = floatPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `FloatPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4L
        val result = floatPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `FloatPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4
        val result = floatPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `FloatPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val offset = 4
        val result = floatPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Float.SIZE_BYTES.toNUInt(),
            result.value.value,
            "FloatPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `FloatPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val testValue: Float = 42.0f

        floatPtr[0] = testValue
        assertEquals(
            testValue, floatPtr[0], "FloatPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `FloatPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val testValue: Float = 42.0f

        floatPtr[0L] = testValue
        assertEquals(
            testValue, floatPtr[0L], "FloatPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `FloatPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val testValue: Float = 42.0f

        floatPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            floatPtr[0.toNUInt()],
            "FloatPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `FloatPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)

        val intPtr = floatPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "FloatPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = floatPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "FloatPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `FloatPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val result = floatPtr.inc()

        assertEquals(
            ptr.value + Float.SIZE_BYTES.toNUInt(), result.value.value, "FloatPtr increment should add Float.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `FloatPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = FloatPtr(ptr)
        val result = floatPtr.dec()

        assertEquals(
            ptr.value - Float.SIZE_BYTES.toNUInt(), result.value.value, "FloatPtr decrement should subtract Float.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asFloatPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = ptr.asFloatPtr()

        assertEquals(ptr.value, floatPtr.value.value, "asFloatPtr should create a FloatPtr with the same address")
    }

    // DoublePtr Tests

    @Test
    fun `DoublePtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val doublePtr = DoublePtr(ptr)

        assertEquals(ptr.value, doublePtr.value.value, "DoublePtr should have the same address as the original pointer")
    }

    @Test
    fun `DoublePtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val doublePtr = DoublePtr(ptr)
        assertNotEquals(nullptr(), doublePtr, "DoublePtr should have a valid pointer")

        doublePtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `DoublePtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4.toNUInt()
        val result = doublePtr + offset

        assertEquals(
            ptr.value + offset * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `DoublePtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4.toNUInt()
        val result = doublePtr - offset

        assertEquals(
            ptr.value - offset * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `DoublePtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4UL
        val result = doublePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `DoublePtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4UL
        val result = doublePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `DoublePtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4U
        val result = doublePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `DoublePtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4U
        val result = doublePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `DoublePtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4L
        val result = doublePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `DoublePtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4L
        val result = doublePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `DoublePtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4
        val result = doublePtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `DoublePtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val offset = 4
        val result = doublePtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Double.SIZE_BYTES.toNUInt(),
            result.value.value,
            "DoublePtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `DoublePtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val testValue: Double = 42.0

        doublePtr[0] = testValue
        assertEquals(
            testValue, doublePtr[0], "DoublePtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `DoublePtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val testValue: Double = 42.0

        doublePtr[0L] = testValue
        assertEquals(
            testValue, doublePtr[0L], "DoublePtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `DoublePtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val testValue: Double = 42.0

        doublePtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            doublePtr[0.toNUInt()],
            "DoublePtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `DoublePtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)

        val intPtr = doublePtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "DoublePtr reinterpreted to IntPtr should have the same address")

        val bytePtr = doublePtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "DoublePtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `DoublePtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val result = doublePtr.inc()

        assertEquals(
            ptr.value + Double.SIZE_BYTES.toNUInt(), result.value.value, "DoublePtr increment should add Double.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `DoublePtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = DoublePtr(ptr)
        val result = doublePtr.dec()

        assertEquals(
            ptr.value - Double.SIZE_BYTES.toNUInt(), result.value.value, "DoublePtr decrement should subtract Double.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asDoublePtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = ptr.asDoublePtr()

        assertEquals(ptr.value, doublePtr.value.value, "asDoublePtr should create a DoublePtr with the same address")
    }

    // PointerPtr Tests

    @Test
    fun `PointerPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val pointerPtr = PointerPtr(ptr)

        assertEquals(
            ptr.value, pointerPtr.value.value, "PointerPtr should have the same address as the original pointer"
        )
    }

    @Test
    fun `PointerPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val pointerPtr = PointerPtr(ptr)
        assertNotEquals(nullptr(), pointerPtr, "PointerPtr should have a valid pointer")

        pointerPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `PointerPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4.toNUInt()
        val result = pointerPtr + offset

        assertEquals(
            ptr.value + offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `PointerPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4.toNUInt()
        val result = pointerPtr - offset

        assertEquals(
            ptr.value - offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `PointerPtr plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4UL
        val result = pointerPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `PointerPtr minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4UL
        val result = pointerPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `PointerPtr plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4U
        val result = pointerPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `PointerPtr minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4U
        val result = pointerPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `PointerPtr plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4L
        val result = pointerPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `PointerPtr minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4L
        val result = pointerPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `PointerPtr plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4
        val result = pointerPtr + offset

        assertEquals(
            ptr.value + offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `PointerPtr minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val offset = 4
        val result = pointerPtr - offset

        assertEquals(
            ptr.value - offset.toNUInt() * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "PointerPtr subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `PointerPtr get and set with int index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val testValue = Pointer(42u.toNUInt())

        pointerPtr[0] = testValue
        assertEquals(
            testValue, pointerPtr[0], "PointerPtr get/set with Int index should store and retrieve the correct value"
        )
    }

    @Test
    fun `PointerPtr get and set with long index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val testValue = Pointer(42u.toNUInt())

        pointerPtr[0L] = testValue
        assertEquals(
            testValue, pointerPtr[0L], "PointerPtr get/set with Long index should store and retrieve the correct value"
        )
    }

    @Test
    fun `PointerPtr get and set with nuint index work correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val testValue = Pointer(42u.toNUInt())

        pointerPtr[0.toNUInt()] = testValue
        assertEquals(
            testValue,
            pointerPtr[0.toNUInt()],
            "PointerPtr get/set with NUInt index should store and retrieve the correct value"
        )
    }

    @Test
    fun `PointerPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)

        val intPtr = pointerPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "PointerPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = pointerPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "PointerPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `PointerPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val result = pointerPtr.inc()

        assertEquals(
            ptr.value + Pointer.SIZE_BYTES.toNUInt(), result.value.value, "PointerPtr increment should add Pointer.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `PointerPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = PointerPtr(ptr)
        val result = pointerPtr.dec()

        assertEquals(
            ptr.value - Pointer.SIZE_BYTES.toNUInt(), result.value.value, "PointerPtr decrement should subtract Pointer.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `asPointerPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = ptr.asPointerPtr()

        assertEquals(ptr.value, pointerPtr.value.value, "asPointerPtr should create a PointerPtr with the same address")
    }

    // NFloatPtr Tests

    @Test
    fun `NFloatPtr constructor creates valid pointer`() {
        val ptr = Pointer(42u.toNUInt())
        val nFloatPtr = NFloatPtr(ptr)

        assertEquals(ptr.value, nFloatPtr.value.value, "NFloatPtr should have the same address as the original pointer")
    }

    @Test
    fun `NFloatPtr close works correctly`() {
        val ptr = Memory.allocate(16.toNUInt())
        val nFloatPtr = NFloatPtr(ptr)
        assertNotEquals(nullptr(), nFloatPtr, "NFloatPtr should have a valid pointer")

        nFloatPtr.close()
        // If we reach here without crashing, the test passes
    }

    @Test
    fun `NFloatPtr plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = NFloatPtr(ptr)
        val offset = 4.toNUInt()
        val result = nFloatPtr + offset

        assertEquals(
            ptr.value + offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NFloatPtr addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `NFloatPtr minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = NFloatPtr(ptr)
        val offset = 4.toNUInt()
        val result = nFloatPtr - offset

        assertEquals(
            ptr.value - offset * Pointer.SIZE_BYTES.toNUInt(),
            result.value.value,
            "NFloatPtr subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `NFloatPtr inc works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = NFloatPtr(ptr)
        val result = nFloatPtr.inc()

        assertEquals(
            ptr.value + Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NFloatPtr increment should add Pointer.SIZE_BYTES to the address"
        )
    }

    @Test
    fun `NFloatPtr dec works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = NFloatPtr(ptr)
        val result = nFloatPtr.dec()

        assertEquals(
            ptr.value - Pointer.SIZE_BYTES.toNUInt(), result.value.value, "NFloatPtr decrement should subtract Pointer.SIZE_BYTES from the address"
        )
    }

    @Test
    fun `NFloatPtr reinterpret works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = NFloatPtr(ptr)

        val intPtr = nFloatPtr.reinterpret<IntPtr>()
        assertEquals(ptr.value, intPtr.value.value, "NFloatPtr reinterpreted to IntPtr should have the same address")

        val bytePtr = nFloatPtr.reinterpret<BytePtr>()
        assertEquals(ptr.value, bytePtr.value.value, "NFloatPtr reinterpreted to BytePtr should have the same address")
    }

    @Test
    fun `asNFloatPtr extension function works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nFloatPtr = ptr.asNFloatPtr()

        assertEquals(ptr.value, nFloatPtr.value.value, "asNFloatPtr should create a NFloatPtr with the same address")
    }
}
