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
import kotlin.test.assertTrue

class PointerTest {

    @Test
    fun `nullptr has zero address`() {
        assertEquals(0U.toNUInt(), nullptr.value, "nullptr should have a zero address")
    }

    @Test
    fun `pointer plus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4.toNUInt()
        val result = ptr + offset

        assertEquals(
            ptr.value + offset, result.value, "Pointer addition with NUInt should add the offset to the address"
        )
    }

    @Test
    fun `pointer minus nuint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4.toNUInt()
        val result = ptr - offset

        assertEquals(
            ptr.value - offset,
            result.value,
            "Pointer subtraction with NUInt should subtract the offset from the address"
        )
    }

    @Test
    fun `pointer plus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4UL
        val result = ptr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value,
            "Pointer addition with ULong should add the offset to the address"
        )
    }

    @Test
    fun `pointer minus ulong works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4UL
        val result = ptr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value,
            "Pointer subtraction with ULong should subtract the offset from the address"
        )
    }

    @Test
    fun `pointer plus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4U
        val result = ptr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value,
            "Pointer addition with UInt should add the offset to the address"
        )
    }

    @Test
    fun `pointer minus uint works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4U
        val result = ptr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value,
            "Pointer subtraction with UInt should subtract the offset from the address"
        )
    }

    @Test
    fun `pointer plus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4L
        val result = ptr + offset

        assertEquals(
            ptr.value + offset.toNUInt(),
            result.value,
            "Pointer addition with Long should add the offset to the address"
        )
    }

    @Test
    fun `pointer minus long works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4L
        val result = ptr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value,
            "Pointer subtraction with Long should subtract the offset from the address"
        )
    }

    @Test
    fun `pointer plus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4
        val result = ptr + offset

        assertEquals(
            ptr.value + offset.toNUInt(), result.value, "Pointer addition with Int should add the offset to the address"
        )
    }

    @Test
    fun `pointer minus int works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val offset = 4
        val result = ptr - offset

        assertEquals(
            ptr.value - offset.toNUInt(),
            result.value,
            "Pointer subtraction with Int should subtract the offset from the address"
        )
    }

    @Test
    fun `align aligns pointer to specified boundary`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val alignment = 8.toNUInt()
        val aligned = ptr.align(alignment)

        assertEquals(
            0.toNUInt(), aligned.value % alignment, "Aligned pointer should be divisible by the alignment value"
        )
    }

    @Test
    fun `align with default alignment uses Memory default alignment`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val aligned = ptr.align()

        assertEquals(
            0.toNUInt(),
            aligned.value % Memory.defaultAlignment,
            "Aligned pointer with default alignment should be divisible by Memory.defaultAlignment"
        )
    }

    @Test
    fun `reinterpret to Pointer returns same pointer`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val reinterpreted = ptr.reinterpret<Pointer>()

        assertEquals(ptr, reinterpreted, "Reinterpreting to Pointer should return the same pointer")
    }

    @Test
    fun `reinterpret to BytePtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val bytePtr = ptr.reinterpret<BytePtr>()

        assertEquals(ptr.value, bytePtr.value.value, "Reinterpreted BytePtr should have the same address")
    }

    @Test
    fun `reinterpret to IntPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val intPtr = ptr.reinterpret<IntPtr>()

        assertEquals(ptr.value, intPtr.value.value, "Reinterpreted IntPtr should have the same address")
    }

    @Test
    fun `reinterpret to PointerPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val pointerPtr = ptr.reinterpret<PointerPtr>()

        assertEquals(ptr.value, pointerPtr.value.value, "Reinterpreted PointerPtr should have the same address")
    }

    @Test
    fun `reinterpret to ShortPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val shortPtr = ptr.reinterpret<ShortPtr>()

        assertEquals(ptr.value, shortPtr.value.value, "Reinterpreted ShortPtr should have the same address")
    }

    @Test
    fun `reinterpret to LongPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val longPtr = ptr.reinterpret<LongPtr>()

        assertEquals(ptr.value, longPtr.value.value, "Reinterpreted LongPtr should have the same address")
    }

    @Test
    fun `reinterpret to NIntPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nIntPtr = ptr.reinterpret<NIntPtr>()

        assertEquals(ptr.value, nIntPtr.value.value, "Reinterpreted NIntPtr should have the same address")
    }

    @Test
    fun `reinterpret to UBytePtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uBytePtr = ptr.reinterpret<UBytePtr>()

        assertEquals(ptr.value, uBytePtr.value.value, "Reinterpreted UBytePtr should have the same address")
    }

    @Test
    fun `reinterpret to UShortPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uShortPtr = ptr.reinterpret<UShortPtr>()

        assertEquals(ptr.value, uShortPtr.value.value, "Reinterpreted UShortPtr should have the same address")
    }

    @Test
    fun `reinterpret to UIntPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uIntPtr = ptr.reinterpret<UIntPtr>()

        assertEquals(ptr.value, uIntPtr.value.value, "Reinterpreted UIntPtr should have the same address")
    }

    @Test
    fun `reinterpret to ULongPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val uLongPtr = ptr.reinterpret<ULongPtr>()

        assertEquals(ptr.value, uLongPtr.value.value, "Reinterpreted ULongPtr should have the same address")
    }

    @Test
    fun `reinterpret to NUIntPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val nUIntPtr = ptr.reinterpret<NUIntPtr>()

        assertEquals(ptr.value, nUIntPtr.value.value, "Reinterpreted NUIntPtr should have the same address")
    }

    @Test
    fun `reinterpret to FloatPtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val floatPtr = ptr.reinterpret<FloatPtr>()

        assertEquals(ptr.value, floatPtr.value.value, "Reinterpreted FloatPtr should have the same address")
    }

    @Test
    fun `reinterpret to DoublePtr works correctly`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val doublePtr = ptr.reinterpret<DoublePtr>()

        assertEquals(ptr.value, doublePtr.value.value, "Reinterpreted DoublePtr should have the same address")
    }

    @Test
    fun `close frees memory`() {
        // This test can only verify that close doesn't crash, as we can't directly test
        // that memory has been released without accessing freed memory (which is undefined behavior)
        val ptr = Memory.allocate(16.toNUInt())
        assertNotEquals(nullptr, ptr, "Memory allocation should return a valid pointer")

        // Close the pointer (which should free the memory)
        ptr.close()

        // If we reach here without crashing, the test passes
    }

    @Test
    fun `toString returns hexadecimal representation`() = deferring {
        val ptr by dropping { Memory.allocate(16.toNUInt()) }
        val hexString = ptr.toString()

        assertTrue(hexString.startsWith("0x"), "Pointer toString should start with 0x")
        // We can't test the exact string since the address is platform-dependent,
        // but we can verify it's a valid hex representation
        assertTrue(
            hexString.substring(2).all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' },
            "Pointer toString should contain valid hexadecimal digits after 0x"
        )
    }
}
