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

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.VoidPtr
import kotlin.jvm.JvmInline

@RequiresOptIn("The API you are trying to use requires great care, otherwise undefined behaviour may be invoked")
@Retention(AnnotationRetention.BINARY)
annotation class DelicatePinningApi

expect class Pinned<T : Any> {
    val value: T
}

@DelicatePinningApi
expect fun <T : Any> pinnedFrom(value: T): Pinned<T>

@DelicatePinningApi
expect fun <T : Any> T.pin(): Pinned<T>

@DelicatePinningApi
expect fun unpin(pinned: Pinned<out Any>)

@DelicatePinningApi
expect fun Pinned<out Any>.acquireStableAddress(): VoidPtr

@DelicatePinningApi
expect inline fun <reified T : Any> Address.fromStableAddress(): T

@DelicatePinningApi
expect fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte>

@DelicatePinningApi
expect fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short>

@DelicatePinningApi
expect fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int>

@DelicatePinningApi
expect fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long>

@DelicatePinningApi
expect fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float>

@DelicatePinningApi
expect fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double>

@DelicatePinningApi
expect fun Pinned<CharArray>.acquireCharAddress(): NumPtr<Char>

@DelicatePinningApi
expect fun Pinned<ByteArray>.releasePinnedByteAddress(address: NumPtr<Byte>)

@DelicatePinningApi
expect fun Pinned<ShortArray>.releasePinnedShortAddress(address: NumPtr<Short>)

@DelicatePinningApi
expect fun Pinned<IntArray>.releasePinnedIntAddress(address: NumPtr<Int>)

@DelicatePinningApi
expect fun Pinned<LongArray>.releasePinnedLongAddress(address: NumPtr<Long>)

@DelicatePinningApi
expect fun Pinned<FloatArray>.releasePinnedFloatAddress(address: NumPtr<Float>)

@DelicatePinningApi
expect fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: NumPtr<Double>)

@DelicatePinningApi
expect fun Pinned<CharArray>.releasePinnedCharAddress(address: NumPtr<Char>)

@OptIn(DelicatePinningApi::class)
inline fun <reified T : Any, reified R> T.stableRef(block: (VoidPtr) -> R): R {
    val pinTack = pin()
    return try {
        block(pinTack.acquireStableAddress())
    }
    finally {
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
@JvmInline
value class StableRef<T : Any>(
    @PublishedApi internal val pinTack: Pinned<T>
) {
    companion object {
        fun <T : Any> create(value: T): StableRef<T> = StableRef(value.pin())

        inline fun <reified T : Any> from(address: Address): StableRef<T> =
            StableRef(pinnedFrom(address.fromStableAddress()))
    }

    inline val value: T get() = pinTack.value
    inline val address: VoidPtr get() = pinTack.acquireStableAddress()
    fun dispose() = unpin(pinTack)
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> ByteArray.fixed(block: (NumPtr<Byte>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireByteAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedByteAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> ShortArray.fixed(block: (NumPtr<Short>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireShortAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedShortAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> IntArray.fixed(block: (NumPtr<Int>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireIntAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedIntAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> LongArray.fixed(block: (NumPtr<Long>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireLongAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedLongAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> FloatArray.fixed(block: (NumPtr<Float>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireFloatAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedFloatAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> DoubleArray.fixed(block: (NumPtr<Double>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireDoubleAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedDoubleAddress(address)
        unpin(pinTack)
    }
}

@OptIn(DelicatePinningApi::class)
inline fun <reified R> CharArray.fixed(block: (NumPtr<Char>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireCharAddress()
    return try {
        block(address)
    }
    finally {
        pinTack.releasePinnedCharAddress(address)
        unpin(pinTack)
    }
}