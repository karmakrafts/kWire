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
import dev.karmakrafts.kwire.ctype.NumPtr
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asNumPtr
import dev.karmakrafts.kwire.ctype.asVoidPtr
import dev.karmakrafts.kwire.util.JNIUtils

actual class Pinned<T : Any>(actual val value: T)

@DelicatePinningApi
actual fun <T : Any> pinnedFrom(value: T): Pinned<T> = Pinned(value)

@Suppress("UNCHECKED_CAST")
@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> {
    return Pinned(JNIUtils.pin(this) as T)
}

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>) {
    JNIUtils.unpin(pinned.value)
}

@DelicatePinningApi
actual fun Pinned<out Any>.acquireStableAddress(): VoidPtr {
    return JNIUtils.refObject(value).asVoidPtr()
}

@Suppress("UNCHECKED_CAST")
@DelicatePinningApi
actual inline fun <reified T : Any> Address.fromStableAddress(): T {
    return JNIUtils.derefObject(rawAddress.toLong()) as T
}

@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> = JNIUtils.getByteArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> = JNIUtils.getShortArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> = JNIUtils.getIntArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> = JNIUtils.getLongArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> = JNIUtils.getFloatArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> = JNIUtils.getDoubleArrayAddress(value).asNumPtr()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: NumPtr<Byte>) =
    JNIUtils.releaseByteArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: NumPtr<Short>) =
    JNIUtils.releaseShortArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: NumPtr<Int>) =
    JNIUtils.releaseIntArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: NumPtr<Long>) =
    JNIUtils.releaseLongArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: NumPtr<Float>) =
    JNIUtils.releaseFloatArrayAddress(value, address.asLong())

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: NumPtr<Double>) =
    JNIUtils.releaseDoubleArrayAddress(value, address.asLong())