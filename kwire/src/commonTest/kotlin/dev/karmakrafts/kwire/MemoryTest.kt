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
import kotlin.test.assertNotEquals

class MemoryTest {
    companion object {
        private val testSize: NUInt = (Int.SIZE_BYTES * 4).toNUInt()
    }

    @Test
    fun `Allocate and free`() {
        val address = Memory.allocate(testSize)
        assertNotEquals(nullptr, address)
        Memory.free(address)
    }

    @Test
    fun `Write and read byte`() {
        val address = Memory.allocate(Byte.SIZE_BYTES.toNUInt()).asBytePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0xEE.toByte()
        assertEquals(0xEE.toByte(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read short`() {
        val address = Memory.allocate(Short.SIZE_BYTES.toNUInt()).asShortPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x1234.toShort()
        assertEquals(0x1234.toShort(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read int`() {
        val address = Memory.allocate(Int.SIZE_BYTES.toNUInt()).asIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678
        assertEquals(0x12345678, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read long`() {
        val address = Memory.allocate(Long.SIZE_BYTES.toNUInt()).asLongPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x123456789ABCDEF0L
        assertEquals(0x123456789ABCDEF0L, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read nint`() {
        val address = Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678.toNInt()
        assertEquals(0x12345678.toNInt(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ubyte`() {
        val address = Memory.allocate(UByte.SIZE_BYTES.toNUInt()).asUBytePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0xEEu
        assertEquals(0xEEu, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ushort`() {
        val address = Memory.allocate(UShort.SIZE_BYTES.toNUInt()).asUShortPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x1234u
        assertEquals(0x1234u, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read uint`() {
        val address = Memory.allocate(UInt.SIZE_BYTES.toNUInt()).asUIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678u
        assertEquals(0x12345678u, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read ulong`() {
        val address = Memory.allocate(ULong.SIZE_BYTES.toNUInt()).asULongPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x123456789ABCDEF0uL
        assertEquals(0x123456789ABCDEF0uL, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read nuint`() {
        val address = Memory.allocate(Pointer.SIZE_BYTES.toNUInt()).asNUIntPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 0x12345678u.toNUInt()
        assertEquals(0x12345678u.toNUInt(), address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read float`() {
        val address = Memory.allocate(Float.SIZE_BYTES.toNUInt()).asFloatPtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 3.14159f
        assertEquals(3.14159f, address[0])
        Memory.free(address.reinterpret())
    }

    @Test
    fun `Write and read double`() {
        val address = Memory.allocate(Double.SIZE_BYTES.toNUInt()).asDoublePtr()
        assertNotEquals(nullptr.reinterpret(), address)
        address[0] = 3.14159265358979
        assertEquals(3.14159265358979, address[0])
        Memory.free(address.reinterpret())
    }
}
