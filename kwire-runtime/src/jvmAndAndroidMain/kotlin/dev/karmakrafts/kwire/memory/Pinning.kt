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

@file:JvmName("PinningImpl")

package dev.karmakrafts.kwire.memory

import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.asPtr
import dev.karmakrafts.kwire.util.NativePlatform

// Objects

@PublishedApi
@DelicatePinningApi
internal actual fun acquireStableAddress(value: Any): @Const Ptr<CVoid> = NativePlatform.pin(value).asPtr()

@PublishedApi
@Suppress("UNCHECKED_CAST")
@DelicatePinningApi
internal actual fun <T : Any> derefStableAddress(address: @Const Ptr<*>): T =
    NativePlatform.derefObject(address.asLong()) as T

@PublishedApi
@DelicatePinningApi
internal actual fun releaseStableAddress(address: @Const Ptr<*>) = NativePlatform.unpin(address.asLong())

// Arrays

@DelicatePinningApi
actual class Pinned<T : Any>(actual val value: T)

@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> = Pinned(this)

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>) = Unit

@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): Ptr<Byte> = NativePlatform.getByteArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): Ptr<Short> = NativePlatform.getShortArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): Ptr<Int> = NativePlatform.getIntArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): Ptr<Long> = NativePlatform.getLongArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): Ptr<Float> = NativePlatform.getFloatArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): Ptr<Double> = NativePlatform.getDoubleArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<CharArray>.acquireCharAddress(): Ptr<Char> = NativePlatform.getCharArrayAddress(value).asPtr()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: Ptr<Byte>) =
    NativePlatform.releaseByteArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: Ptr<Short>) =
    NativePlatform.releaseShortArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: Ptr<Int>) =
    NativePlatform.releaseIntArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: Ptr<Long>) =
    NativePlatform.releaseLongArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: Ptr<Float>) =
    NativePlatform.releaseFloatArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: Ptr<Double>) =
    NativePlatform.releaseDoubleArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<CharArray>.releasePinnedCharAddress(address: Ptr<Char>) =
    NativePlatform.releaseCharArrayAddress(value, address.asLong())