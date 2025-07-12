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

@file:OptIn(ExperimentalForeignApi::class)

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.asPtr
import dev.karmakrafts.kwire.ctype.toCPointer
import dev.karmakrafts.kwire.ctype.toPtr
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.Pinned as KXPinned
import kotlinx.cinterop.StableRef as KXStableRef
import kotlinx.cinterop.pin as kxPin

// Objects

@PublishedApi
@DelicatePinningApi
internal actual fun acquireStableAddress(value: Any): @Const Ptr<CVoid> = KXStableRef.create(value).asCPointer().toPtr()

@PublishedApi
@Suppress("UNCHECKED_CAST")
@DelicatePinningApi
internal actual fun <T : Any> derefStableAddress(address: @Const Ptr<*>): T =
    address.reinterpret<CVoid>().toCPointer().asStableRef<Any>().get() as T

@PublishedApi
@DelicatePinningApi
internal actual fun releaseStableAddress(address: @Const Ptr<*>) =
    address.reinterpret<CVoid>().toCPointer().asStableRef<Any>().dispose()

// Arrays

@DelicatePinningApi
actual class Pinned<T : Any>(internal val delegate: KXPinned<T>) {
    actual val value: T get() = delegate.get()
}

@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> = Pinned(kxPin())

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>) = pinned.delegate.unpin()

// @formatter:off
@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): Ptr<Byte> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): Ptr<Short> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): Ptr<Int> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): Ptr<Long> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): Ptr<Float> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): Ptr<Double> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<CharArray>.acquireCharAddress(): Ptr<Char> =
    delegate.addressOf(0).rawValue.toLong().asPtr()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: Ptr<Byte>) = Unit

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: Ptr<Short>) = Unit

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: Ptr<Int>) = Unit

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: Ptr<Long>) = Unit

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: Ptr<Float>) = Unit

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: Ptr<Double>) = Unit

@DelicatePinningApi
actual fun Pinned<CharArray>.releasePinnedCharAddress(address: Ptr<Char>) = Unit
// @formatter:on