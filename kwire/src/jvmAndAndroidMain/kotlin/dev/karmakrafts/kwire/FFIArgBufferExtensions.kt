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

fun FFIArgBuffer.toArray(): Array<Any> {
    var offset = 0UL
    return Array(types.size) { index ->
        val type = types[index]
        val value: Any = when (type) {
            FFIType.BYTE, FFIType.UBYTE -> Memory.readByte(address + offset)
            FFIType.SHORT, FFIType.USHORT -> Memory.readShort(address + offset)
            FFIType.INT, FFIType.UINT -> Memory.readInt(address + offset)
            FFIType.LONG, FFIType.ULONG -> Memory.readLong(address + offset)
            FFIType.NINT, FFIType.NUINT -> Memory.readNInt(address + offset)
            FFIType.FLOAT -> Memory.readFloat(address + offset)
            FFIType.DOUBLE -> Memory.readDouble(address + offset)
            FFIType.PTR -> Memory.readPointer(address + offset).toMemorySegment() // Addresses are passed as segments
            else -> throw IllegalStateException("Cannot map FFI parameter type $type")
        }
        offset += type.size.toULong()
        value
    }
}