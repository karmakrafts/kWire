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

actual class Pinned<T : Any>(actual val value: T)

@DelicatePinningApi
actual fun <T : Any> pinnedFrom(value: T): Pinned<T> = TODO()

@DelicatePinningApi
actual fun <T : Any> T.pin(): Pinned<T> = TODO()

@DelicatePinningApi
actual fun unpin(pinned: Pinned<out Any>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<out Any>.acquireStableAddress(): VoidPtr = TODO()

@DelicatePinningApi
actual inline fun <reified T : Any> Address.fromStableAddress(): T = TODO()

@DelicatePinningApi
actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> = TODO()

@DelicatePinningApi
actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> = TODO()

@DelicatePinningApi
actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> = TODO()

@DelicatePinningApi
actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> = TODO()

@DelicatePinningApi
actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> = TODO()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> = TODO()

@DelicatePinningApi
actual fun Pinned<ByteArray>.releasePinnedByteAddress(address: NumPtr<Byte>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<ShortArray>.releasePinnedShortAddress(address: NumPtr<Short>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<IntArray>.releasePinnedIntAddress(address: NumPtr<Int>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<LongArray>.releasePinnedLongAddress(address: NumPtr<Long>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<FloatArray>.releasePinnedFloatAddress(address: NumPtr<Float>): Unit = TODO()

@DelicatePinningApi
actual fun Pinned<DoubleArray>.releasePinnedDoubleAddress(address: NumPtr<Double>): Unit = TODO()