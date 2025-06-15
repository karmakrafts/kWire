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

package dev.karmakrafts.kwire.ctype

interface Struct : Pointed {
    /**
     * Usually emitted by the kWire compiler plugin after a structure has been scanned
     * to allow grabbing its flattened memory layout.
     *
     * The memory layout is encoded using a simple tag system which is defined as follows:
     * 0x00 - Byte
     * 0x01 - Short
     * 0x02 - Int
     * 0x03 - Long
     * 0x04 - Float
     * 0x05 - Double
     * 0x06 - Address
     * 0xFE - Small Struct
     *  this is followed by 1 byte of the struct size in bytes
     *  and another byte of the struct alignment in bytes.
     * 0xFF - Large Struct
     *  this is followed by 4 bytes of the struct size in bytes
     *  and another 4 bytes of the struct alignment in bytes.
     */
    @Retention(AnnotationRetention.BINARY)
    @Target(AnnotationTarget.CLASS)
    annotation class Layout(val data: ByteArray)
}