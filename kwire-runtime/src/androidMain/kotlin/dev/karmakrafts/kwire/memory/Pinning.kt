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

@PublishedApi
internal actual fun <T : Any> pinnedFrom(value: T): Pinned<T> = TODO()

@PublishedApi
internal actual fun <T : Any> T.pin(): Pinned<T> = TODO()

@PublishedApi
internal actual fun unpin(pinned: Pinned<out Any>): Unit = TODO()

@PublishedApi
internal actual fun Pinned<out Any>.acquireStableAddress(): VoidPtr = TODO()

@PublishedApi
internal actual inline fun <reified T : Any> Address.fromStableAddress(): T = TODO()

@PublishedApi
internal actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> = TODO()

@PublishedApi
internal actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> = TODO()

@PublishedApi
internal actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> = TODO()

@PublishedApi
internal actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> = TODO()

@PublishedApi
internal actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> = TODO()

@PublishedApi
internal actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> = TODO()

@PublishedApi
internal actual fun NumPtr<Byte>.releasePinnedByteAddress(pinned: Pinned<ByteArray>): Unit = TODO()

@PublishedApi
internal actual fun NumPtr<Short>.releasePinnedShortAddress(pinned: Pinned<ShortArray>): Unit = TODO()

@PublishedApi
internal actual fun NumPtr<Int>.releasePinnedIntAddress(pinned: Pinned<IntArray>): Unit = TODO()

@PublishedApi
internal actual fun NumPtr<Long>.releasePinnedLongAddress(pinned: Pinned<LongArray>): Unit = TODO()

@PublishedApi
internal actual fun NumPtr<Float>.releasePinnedFloatAddress(pinned: Pinned<FloatArray>): Unit = TODO()

@PublishedApi
internal actual fun NumPtr<Double>.releasePinnedDoubleAddress(pinned: Pinned<DoubleArray>): Unit = TODO()