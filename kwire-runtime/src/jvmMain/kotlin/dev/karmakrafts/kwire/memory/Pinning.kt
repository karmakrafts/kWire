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
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.jni.JNINativeInterface

actual class Pinned<T : Any>(actual val value: T)

@PublishedApi
internal actual fun <T : Any> pinnedFrom(value: T): Pinned<T> = Pinned(value)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal actual fun <T : Any> T.pin(): Pinned<T> {
    return Pinned(JNIUtils.pin(this) as T)
}

@PublishedApi
internal actual fun unpin(pinned: Pinned<out Any>) {
    JNIUtils.unpin(pinned.value)
}

@PublishedApi
internal actual fun Pinned<out Any>.acquireStableAddress(): VoidPtr {
    return JNIUtils.refObject(value).asVoidPtr()
}

@PublishedApi
@Suppress("UNCHECKED_CAST")
internal actual inline fun <reified T : Any> Address.fromStableAddress(): T {
    return JNIUtils.derefObject(rawAddress.toLong()) as T
}

// @formatter:off
@PublishedApi
internal actual fun Pinned<ByteArray>.acquireByteAddress(): NumPtr<Byte> =
    MemoryUtil.memAddress(JNINativeInterface.GetByteArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun Pinned<ShortArray>.acquireShortAddress(): NumPtr<Short> =
    MemoryUtil.memAddress(JNINativeInterface.GetShortArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun Pinned<IntArray>.acquireIntAddress(): NumPtr<Int> =
    MemoryUtil.memAddress(JNINativeInterface.GetIntArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun Pinned<LongArray>.acquireLongAddress(): NumPtr<Long> =
    MemoryUtil.memAddress(JNINativeInterface.GetLongArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun Pinned<FloatArray>.acquireFloatAddress(): NumPtr<Float> =
    MemoryUtil.memAddress(JNINativeInterface.GetFloatArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun Pinned<DoubleArray>.acquireDoubleAddress(): NumPtr<Double> =
    MemoryUtil.memAddress(JNINativeInterface.GetDoubleArrayElements(value, null)!!).asNumPtr()

@PublishedApi
internal actual fun NumPtr<Byte>.releasePinnedByteAddress(pinned: Pinned<ByteArray>) =
    JNINativeInterface.ReleaseByteArrayElements(
        pinned.value, MemoryUtil.memByteBuffer(asLong(), pinned.value.size), 0)

@PublishedApi
internal actual fun NumPtr<Short>.releasePinnedShortAddress(pinned: Pinned<ShortArray>) =
    JNINativeInterface.ReleaseShortArrayElements(
        pinned.value, MemoryUtil.memShortBuffer(asLong(), pinned.value.size), 0)

@PublishedApi
internal actual fun NumPtr<Int>.releasePinnedIntAddress(pinned: Pinned<IntArray>) =
    JNINativeInterface.ReleaseIntArrayElements(
        pinned.value, MemoryUtil.memIntBuffer(asLong(), pinned.value.size), 0)

@PublishedApi
internal actual fun NumPtr<Long>.releasePinnedLongAddress(pinned: Pinned<LongArray>) =
    JNINativeInterface.ReleaseLongArrayElements(
        pinned.value, MemoryUtil.memLongBuffer(asLong(), pinned.value.size), 0)

@PublishedApi
internal actual fun NumPtr<Float>.releasePinnedFloatAddress(pinned: Pinned<FloatArray>) =
    JNINativeInterface.ReleaseFloatArrayElements(
        pinned.value, MemoryUtil.memFloatBuffer(asLong(), pinned.value.size), 0)

@PublishedApi
internal actual fun NumPtr<Double>.releasePinnedDoubleAddress(pinned: Pinned<DoubleArray>) =
    JNINativeInterface.ReleaseDoubleArrayElements(
        pinned.value, MemoryUtil.memDoubleBuffer(asLong(), pinned.value.size), 0)
// @formatter:on