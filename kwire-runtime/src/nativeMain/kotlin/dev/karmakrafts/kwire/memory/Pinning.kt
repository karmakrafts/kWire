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

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asNumPtr
import dev.karmakrafts.kwire.ctype.asVoidPtr
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.Pinned as KXPinned
import kotlinx.cinterop.StableRef as KXStableRef
import kotlinx.cinterop.pin as kxPin

actual class Pinned<T : Any>(
    @PublishedApi internal val delegate: KXPinned<T>
) {
    actual val value: T get() = delegate.get()
}

@DelicatePinningApi
actual fun <T : Any> pinnedFrom(value: T): Pinned<T> = Pinned(value.kxPin())

@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> = Pinned(kxPin())

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>) = pinned.delegate.unpin()

@DelicatePinningApi
actual fun Pinned<out Any>.acquireStableAddress(): VoidPtr =
    KXStableRef.create(value).asCPointer().rawValue.toLong().asVoidPtr()

@DelicatePinningApi
actual inline fun <reified T : Any> Address.fromStableAddress(): T =
    rawAddress.toLong().toCPointer<COpaque>()!!.asStableRef<T>().get()

// @formatter:off
@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> =
    delegate.addressOf(0).rawValue.toLong().asNumPtr()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: NumPtr<Byte>) = Unit

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: NumPtr<Short>) = Unit

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: NumPtr<Int>) = Unit

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: NumPtr<Long>) = Unit

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: NumPtr<Float>) = Unit

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: NumPtr<Double>) = Unit
// @formatter:on