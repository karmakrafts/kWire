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

class MemoryTest {
    companion object {
        private val testSize: NUInt = 16U.toNUInt()
        private const val TEST_VALUE_1: UInt = 0xDEADBEEFU
        private const val TEST_VALUE_2: UInt = 0xCAFEBABEU
    }

    //@Test
    //fun `Allocate and free`() {
    //    val address = Memory.allocate(testSize)
    //    assertEquals(nullptr, address)
    //    Memory.free(address)
    //}

    //@Test
    //fun `Write and read`() {
    //    val address = Memory.allocate(testSize).asUIntPtr()
    //    assertNotEquals(nullptr, address.value)
    //    address[0] = TEST_VALUE_1
    //    address[1] = TEST_VALUE_2
    //    address[2] = TEST_VALUE_1
    //    address[3] = TEST_VALUE_2
    //    assertEquals(TEST_VALUE_1, address[0])
    //    assertEquals(TEST_VALUE_2, address[1])
    //    assertEquals(TEST_VALUE_1, address[2])
    //    assertEquals(TEST_VALUE_2, address[3])
    //    Memory.free(address.value)
    //}
}