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

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asLong
import dev.karmakrafts.kwire.ctype.asNumPtr
import dev.karmakrafts.kwire.ctype.asVoidPtr
import dev.karmakrafts.kwire.util.NativePlatform

// Objects

@PublishedApi
@DelicatePinningApi
internal actual fun acquireStableAddress(value: Any): @Const VoidPtr = NativePlatform.pin(value).asVoidPtr()

@PublishedApi
@Suppress("UNCHECKED_CAST")
@DelicatePinningApi
internal actual fun <T : Any> derefStableAddress(address: @Const Address): T =
    NativePlatform.derefObject(address.asLong()) as T

@PublishedApi
@DelicatePinningApi
internal actual fun releaseStableAddress(address: @Const Address) = NativePlatform.unpin(address.asLong())

// Arrays

@DelicatePinningApi
actual class Pinned<T : Any>(actual val value: T)

@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> = Pinned(this)

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>) = Unit

@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> = NativePlatform.getByteArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> =
    NativePlatform.getShortArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> = NativePlatform.getIntArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> = NativePlatform.getLongArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> =
    NativePlatform.getFloatArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> =
    NativePlatform.getDoubleArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<CharArray>.acquireCharAddress(): NumPtr<Char> = NativePlatform.getCharArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: NumPtr<Byte>) =
    NativePlatform.releaseByteArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: NumPtr<Short>) =
    NativePlatform.releaseShortArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: NumPtr<Int>) =
    NativePlatform.releaseIntArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: NumPtr<Long>) =
    NativePlatform.releaseLongArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: NumPtr<Float>) =
    NativePlatform.releaseFloatArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: NumPtr<Double>) =
    NativePlatform.releaseDoubleArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<CharArray>.releasePinnedCharAddress(address: NumPtr<Char>) =
    NativePlatform.releaseCharArrayAddress(value, address.asLong())