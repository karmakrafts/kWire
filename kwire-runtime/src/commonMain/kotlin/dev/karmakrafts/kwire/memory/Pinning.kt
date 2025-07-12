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

@file:OptIn(ExperimentalContracts::class)

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.Ptr
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@RequiresOptIn("The API you are trying to use requires great care, otherwise undefined behaviour may be invoked")
@Retention(AnnotationRetention.BINARY)
annotation class DelicatePinningApi

// Objects

@PublishedApi
@DelicatePinningApi
internal expect fun acquireStableAddress(value: Any): @Const Ptr<CVoid>

@PublishedApi
@DelicatePinningApi
internal expect fun <T : Any> derefStableAddress(address: @Const Ptr<*>): T

@PublishedApi
@DelicatePinningApi
internal expect fun releaseStableAddress(address: @Const Ptr<*>)

@Suppress("NOTHING_TO_INLINE")
@OptIn(DelicatePinningApi::class)
@JvmInline
value class StableRef<T : Any> @PublishedApi internal constructor(val address: @Const Ptr<CVoid>) {
    companion object {
        inline fun <T : Any> create(value: T): StableRef<T> = StableRef(acquireStableAddress(value))

        inline fun <T : Any> from(address: Ptr<CVoid>): StableRef<T> = StableRef(address.reinterpret())
    }

    inline val value: T get() = derefStableAddress(address)

    inline fun dispose() = releaseStableAddress(address)
}

// Arrays

@DelicatePinningApi
expect class Pinned<T : Any> {
    val value: T
}

@DelicatePinningApi
expect fun <T : Any> T.pin(): Pinned<T>

@DelicatePinningApi
expect fun unpin(pinned: Pinned<out Any>)

@DelicatePinningApi
expect fun Pinned<ByteArray>.acquireByteAddress(): Ptr<Byte>

@DelicatePinningApi
expect fun Pinned<ShortArray>.acquireShortAddress(): Ptr<Short>

@DelicatePinningApi
expect fun Pinned<IntArray>.acquireIntAddress(): Ptr<Int>

@DelicatePinningApi
expect fun Pinned<LongArray>.acquireLongAddress(): Ptr<Long>

@DelicatePinningApi
expect fun Pinned<FloatArray>.acquireFloatAddress(): Ptr<Float>

@DelicatePinningApi
expect fun Pinned<DoubleArray>.acquireDoubleAddress(): Ptr<Double>

@DelicatePinningApi
expect fun Pinned<CharArray>.acquireCharAddress(): Ptr<Char>

@DelicatePinningApi
expect fun Pinned<ByteArray>.releasePinnedByteAddress(address: Ptr<Byte>)

@DelicatePinningApi
expect fun Pinned<ShortArray>.releasePinnedShortAddress(address: Ptr<Short>)

@DelicatePinningApi
expect fun Pinned<IntArray>.releasePinnedIntAddress(address: Ptr<Int>)

@DelicatePinningApi
expect fun Pinned<LongArray>.releasePinnedLongAddress(address: Ptr<Long>)

@DelicatePinningApi
expect fun Pinned<FloatArray>.releasePinnedFloatAddress(address: Ptr<Float>)

@DelicatePinningApi
expect fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: Ptr<Double>)

@DelicatePinningApi
expect fun Pinned<CharArray>.releasePinnedCharAddress(address: Ptr<Char>)

@OptIn(DelicatePinningApi::class)
inline fun <reified R> ByteArray.fixed(block: (Ptr<Byte>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> ShortArray.fixed(block: (Ptr<Short>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> IntArray.fixed(block: (Ptr<Int>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> LongArray.fixed(block: (Ptr<Long>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> FloatArray.fixed(block: (Ptr<Float>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> DoubleArray.fixed(block: (Ptr<Double>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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
inline fun <reified R> CharArray.fixed(block: (Ptr<Char>) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
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