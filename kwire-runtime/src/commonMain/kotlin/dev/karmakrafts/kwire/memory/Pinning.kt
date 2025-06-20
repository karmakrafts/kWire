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

expect class Pinned<T : Any> {
    val value: T
}

@PublishedApi
internal expect fun <T : Any> pinnedFrom(value: T): Pinned<T>

@PublishedApi
internal expect fun <T : Any> T.pin(): Pinned<T>

@PublishedApi
internal expect fun unpin(pinned: Pinned<out Any>)

@PublishedApi
internal expect fun Pinned<out Any>.acquireStableAddress(): VoidPtr

@PublishedApi
internal expect inline fun <reified T : Any> Address.fromStableAddress(): T

@PublishedApi
internal expect fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte>

@PublishedApi
internal expect fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short>

@PublishedApi
internal expect fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int>

@PublishedApi
internal expect fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long>

@PublishedApi
internal expect fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float>

@PublishedApi
internal expect fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double>

@PublishedApi
internal expect fun NumPtr<Byte>.releasePinnedByteAddress(pinned: Pinned<ByteArray>)

@PublishedApi
internal expect fun NumPtr<Short>.releasePinnedShortAddress(pinned: Pinned<ShortArray>)

@PublishedApi
internal expect fun NumPtr<Int>.releasePinnedIntAddress(pinned: Pinned<IntArray>)

@PublishedApi
internal expect fun NumPtr<Long>.releasePinnedLongAddress(pinned: Pinned<LongArray>)

@PublishedApi
internal expect fun NumPtr<Float>.releasePinnedFloatAddress(pinned: Pinned<FloatArray>)

@PublishedApi
internal expect fun NumPtr<Double>.releasePinnedDoubleAddress(pinned: Pinned<DoubleArray>)

inline fun <reified T : Any, reified R> T.stableRef(block: (VoidPtr) -> R): R {
    val pinTack = pin()
    return try {
        block(pinTack.acquireStableAddress())
    }
    finally {
        unpin(pinTack)
    }
}

@JvmInline
value class StableRef<T : Any>(private val pinTack: Pinned<T>) {
    companion object {
        fun <T : Any> of(value: T): StableRef<T> = StableRef(value.pin())

        inline fun <reified T : Any> from(address: Address): StableRef<T> =
            StableRef(pinnedFrom(address.fromStableAddress()))
    }

    val address: VoidPtr get() = pinTack.acquireStableAddress()
    fun dispose() = unpin(pinTack)
}

inline fun <reified R> ByteArray.fixed(block: (NumPtr<Byte>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireByteAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedByteAddress(pinTack)
        unpin(pinTack)
    }
}

inline fun <reified R> ShortArray.fixed(block: (NumPtr<Short>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireShortAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedShortAddress(pinTack)
        unpin(pinTack)
    }
}

inline fun <reified R> IntArray.fixed(block: (NumPtr<Int>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireIntAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedIntAddress(pinTack)
        unpin(pinTack)
    }
}

inline fun <reified R> LongArray.fixed(block: (NumPtr<Long>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireLongAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedLongAddress(pinTack)
        unpin(pinTack)
    }
}

inline fun <reified R> FloatArray.fixed(block: (NumPtr<Float>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireFloatAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedFloatAddress(pinTack)
        unpin(pinTack)
    }
}

inline fun <reified R> DoubleArray.fixed(block: (NumPtr<Double>) -> R): R {
    val pinTack = pin()
    val address = pinTack.acquireDoubleAddress()
    return try {
        block(address)
    }
    finally {
        address.releasePinnedDoubleAddress(pinTack)
        unpin(pinTack)
    }
}