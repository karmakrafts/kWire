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

@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire

import kotlin.jvm.JvmInline

// Signed

@JvmInline
value class BytePtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): BytePtr = BytePtr(value + other * Byte.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): BytePtr = BytePtr(value - other * Byte.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): BytePtr = BytePtr(value + (other * Byte.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): BytePtr = BytePtr(value - (other * Byte.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Byte =
        Memory.readByte(value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Byte) =
        Memory.writeByte(this.value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Byte =
        Memory.readByte(value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Byte) =
        Memory.writeByte(this.value + index.toNUInt() * Byte.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Byte =
        Memory.readByte(value + index * Byte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Byte) =
        Memory.writeByte(this.value + index * Byte.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> this
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asBytePtr(): BytePtr = BytePtr(this)

@JvmInline
value class ShortPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): ShortPtr = ShortPtr(value + other * Short.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): ShortPtr = ShortPtr(value - other * Short.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): ShortPtr = ShortPtr(value + (other * Short.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): ShortPtr = ShortPtr(value - (other * Short.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Short =
        Memory.readShort(value + index.toNUInt() * Short.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Short) =
        Memory.writeShort(this.value + index.toNUInt() * Short.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Short =
        Memory.readShort(value + index.toNUInt() * Short.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Short) =
        Memory.writeShort(this.value + index.toNUInt() * Short.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Short =
        Memory.readShort(value + index * Short.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Short) =
        Memory.writeShort(this.value + index * Short.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> this
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asShortPtr(): ShortPtr = ShortPtr(this)

@JvmInline
value class IntPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): IntPtr = IntPtr(value + other * Int.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): IntPtr = IntPtr(value - other * Int.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): IntPtr = IntPtr(value + (other * Int.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): IntPtr = IntPtr(value - (other * Int.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Int =
        Memory.readInt(value + index.toNUInt() * Int.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Int) =
        Memory.writeInt(this.value + index.toNUInt() * Int.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Int =
        Memory.readInt(value + index.toNUInt() * Int.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Int) =
        Memory.writeInt(this.value + index.toNUInt() * Int.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Int =
        Memory.readInt(value + index * Int.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Int) =
        Memory.writeInt(this.value + index * Int.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> this
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asIntPtr(): IntPtr = IntPtr(this)

@JvmInline
value class LongPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): LongPtr = LongPtr(value + other * Long.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): LongPtr = LongPtr(value - other * Long.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): LongPtr = LongPtr(value + (other * Long.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): LongPtr = LongPtr(value - (other * Long.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Long =
        Memory.readLong(value + index.toNUInt() * Long.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Long) =
        Memory.writeLong(this.value + index.toNUInt() * Long.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Long =
        Memory.readLong(value + index.toNUInt() * Long.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Long) =
        Memory.writeLong(this.value + index.toNUInt() * Long.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Long =
        Memory.readLong(value + index * Long.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Long) =
        Memory.writeLong(this.value + index * Long.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> this
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asLongPtr(): LongPtr = LongPtr(this)

@JvmInline
value class NIntPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): NIntPtr = NIntPtr(value + other * Pointer.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): NIntPtr = NIntPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): NIntPtr = NIntPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): NIntPtr = NIntPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): NInt =
        Memory.readNInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: NInt) =
        Memory.writeNInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): NInt =
        Memory.readNInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: NInt) =
        Memory.writeNInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): NInt =
        Memory.readNInt(value + index * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: NInt) =
        Memory.writeNInt(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> this
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asNIntPtr(): NIntPtr = NIntPtr(this)

// Unsigned

@JvmInline
value class UBytePtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): UBytePtr = UBytePtr(value + other * UByte.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): UBytePtr = UBytePtr(value - other * UByte.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): UBytePtr = UBytePtr(value + (other * UByte.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): UBytePtr = UBytePtr(value - (other * UByte.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): UByte =
        Memory.readUByte(value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: UByte) =
        Memory.writeUByte(this.value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): UByte =
        Memory.readUByte(value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: UByte) =
        Memory.writeUByte(this.value + index.toNUInt() * UByte.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): UByte =
        Memory.readUByte(value + index * UByte.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: UByte) =
        Memory.writeUByte(this.value + index * UByte.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> this
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asUBytePtr(): UBytePtr = UBytePtr(this)

@JvmInline
value class UShortPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): UShortPtr = UShortPtr(value + other * UShort.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): UShortPtr = UShortPtr(value - other * UShort.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): UShortPtr =
        UShortPtr(value + (other * UShort.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun minus(other: ULong): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): UShortPtr =
        UShortPtr(value - (other * UShort.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): UShortPtr = UShortPtr(value + (other * UShort.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): UShortPtr = UShortPtr(value - (other * UShort.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): UShort =
        Memory.readUShort(value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: UShort) =
        Memory.writeUShort(this.value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): UShort =
        Memory.readUShort(value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: UShort) =
        Memory.writeUShort(this.value + index.toNUInt() * UShort.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): UShort =
        Memory.readUShort(value + index * UShort.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: UShort) =
        Memory.writeUShort(this.value + index * UShort.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> this
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asUShortPtr(): UShortPtr = UShortPtr(this)

@JvmInline
value class UIntPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): UIntPtr = UIntPtr(value + other * UInt.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): UIntPtr = UIntPtr(value - other * UInt.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): UIntPtr = UIntPtr(value + (other * UInt.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): UIntPtr = UIntPtr(value - (other * UInt.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): UInt =
        Memory.readUInt(value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: UInt) =
        Memory.writeUInt(this.value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): UInt =
        Memory.readUInt(value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: UInt) =
        Memory.writeUInt(this.value + index.toNUInt() * UInt.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): UInt =
        Memory.readUInt(value + index * UInt.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: UInt) =
        Memory.writeUInt(this.value + index * UInt.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> this
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asUIntPtr(): UIntPtr = UIntPtr(this)

@JvmInline
value class ULongPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): ULongPtr = ULongPtr(value + other * ULong.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): ULongPtr = ULongPtr(value - other * ULong.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): ULongPtr = ULongPtr(value + (other * ULong.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): ULongPtr = ULongPtr(value - (other * ULong.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): ULong =
        Memory.readULong(value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: ULong) =
        Memory.writeULong(this.value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): ULong =
        Memory.readULong(value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: ULong) =
        Memory.writeULong(this.value + index.toNUInt() * ULong.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): ULong =
        Memory.readULong(value + index * ULong.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: ULong) =
        Memory.writeULong(this.value + index * ULong.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> this
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asULongPtr(): ULongPtr = ULongPtr(this)

@JvmInline
value class NUIntPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): NUIntPtr = NUIntPtr(value + other * Pointer.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): NUIntPtr = NUIntPtr(value - other * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): NUIntPtr =
        NUIntPtr(value + (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun minus(other: ULong): NUIntPtr =
        NUIntPtr(value - (other * Pointer.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): NUIntPtr = NUIntPtr(value + (other * Pointer.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): NUIntPtr = NUIntPtr(value - (other * Pointer.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): NUInt =
        Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: NUInt) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): NUInt =
        Memory.readNUInt(value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: NUInt) =
        Memory.writeNUInt(this.value + index.toNUInt() * Pointer.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): NUInt =
        Memory.readNUInt(value + index * Pointer.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: NUInt) =
        Memory.writeNUInt(this.value + index * Pointer.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> this
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asNUIntPtr(): NUIntPtr = NUIntPtr(this)

// IEEE-754

@JvmInline
value class FloatPtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): FloatPtr = FloatPtr(value + other * Float.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): FloatPtr = FloatPtr(value - other * Float.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toULong()).toNUInt())
    inline operator fun minus(other: ULong): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): FloatPtr = FloatPtr(value + (other * Float.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): FloatPtr = FloatPtr(value - (other * Float.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Float =
        Memory.readFloat(value + index.toNUInt() * Float.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Float) =
        Memory.writeFloat(this.value + index.toNUInt() * Float.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Float =
        Memory.readFloat(value + index.toNUInt() * Float.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Float) =
        Memory.writeFloat(this.value + index.toNUInt() * Float.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Float =
        Memory.readFloat(value + index * Float.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Float) =
        Memory.writeFloat(this.value + index * Float.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> this
        DoublePtr::class -> value.asDoublePtr()
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asFloatPtr(): FloatPtr = FloatPtr(this)

@JvmInline
value class DoublePtr(val value: Pointer) : Reinterpretable {
    inline operator fun plus(other: NUInt): DoublePtr = DoublePtr(value + other * Double.SIZE_BYTES.toNUInt())
    inline operator fun minus(other: NUInt): DoublePtr = DoublePtr(value - other * Double.SIZE_BYTES.toNUInt())

    inline operator fun plus(other: ULong): DoublePtr =
        DoublePtr(value + (other * Double.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun minus(other: ULong): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toULong()).toNUInt())

    inline operator fun plus(other: UInt): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES.toUInt()).toNUInt())
    inline operator fun minus(other: UInt): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toUInt()).toNUInt())

    inline operator fun plus(other: Long): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES.toLong()).toNUInt())
    inline operator fun minus(other: Long): DoublePtr =
        DoublePtr(value - (other * Double.SIZE_BYTES.toLong()).toNUInt())

    inline operator fun plus(other: Int): DoublePtr = DoublePtr(value + (other * Double.SIZE_BYTES).toNUInt())
    inline operator fun minus(other: Int): DoublePtr = DoublePtr(value - (other * Double.SIZE_BYTES).toNUInt())

    inline operator fun get(index: Int): Double =
        Memory.readDouble(value + index.toNUInt() * Double.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Int, value: Double) =
        Memory.writeDouble(this.value + index.toNUInt() * Double.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: Long): Double =
        Memory.readDouble(value + index.toNUInt() * Double.SIZE_BYTES.toNUInt())

    inline operator fun set(index: Long, value: Double) =
        Memory.writeDouble(this.value + index.toNUInt() * Double.SIZE_BYTES.toNUInt(), value)

    inline operator fun get(index: NUInt): Double =
        Memory.readDouble(value + index * Double.SIZE_BYTES.toNUInt())

    inline operator fun set(index: NUInt, value: Double) =
        Memory.writeDouble(this.value + index * Double.SIZE_BYTES.toNUInt(), value)

    inline fun <reified T : Reinterpretable> reinterpret(): T = when (T::class) {
        Pointer::class -> value
        BytePtr::class -> value.asBytePtr()
        ShortPtr::class -> value.asShortPtr()
        IntPtr::class -> value.asIntPtr()
        LongPtr::class -> value.asLongPtr()
        NIntPtr::class -> value.asNIntPtr()
        UBytePtr::class -> value.asUBytePtr()
        UShortPtr::class -> value.asUShortPtr()
        UIntPtr::class -> value.asUIntPtr()
        ULongPtr::class -> value.asULongPtr()
        NUIntPtr::class -> value.asNUIntPtr()
        FloatPtr::class -> value.asFloatPtr()
        DoublePtr::class -> this
        else -> error("Unknown pointer type ${T::class}")
    } as T
}

inline fun Pointer.asDoublePtr(): DoublePtr = DoublePtr(this)
