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

package dev.karmakrafts.kwire.util

@PublishedApi
internal object NativePlatform {
    init {
        NativeLoader.ensureLoaded() // Ensure natives are loaded before we access any functions
    }

    @JvmStatic
    external fun pin(instance: Any): Long

    @JvmStatic
    external fun unpin(address: Long)

    @JvmStatic
    external fun derefObject(address: Long): Any

    @JvmStatic
    external fun getByteArrayAddress(array: ByteArray): Long

    @JvmStatic
    external fun getShortArrayAddress(array: ShortArray): Long

    @JvmStatic
    external fun getIntArrayAddress(array: IntArray): Long

    @JvmStatic
    external fun getLongArrayAddress(array: LongArray): Long

    @JvmStatic
    external fun getFloatArrayAddress(array: FloatArray): Long

    @JvmStatic
    external fun getDoubleArrayAddress(array: DoubleArray): Long

    @JvmStatic
    external fun getCharArrayAddress(array: CharArray): Long

    @JvmStatic
    external fun releaseByteArrayAddress(array: ByteArray, address: Long)

    @JvmStatic
    external fun releaseShortArrayAddress(array: ShortArray, address: Long)

    @JvmStatic
    external fun releaseIntArrayAddress(array: IntArray, address: Long)

    @JvmStatic
    external fun releaseLongArrayAddress(array: LongArray, address: Long)

    @JvmStatic
    external fun releaseFloatArrayAddress(array: FloatArray, address: Long)

    @JvmStatic
    external fun releaseDoubleArrayAddress(array: DoubleArray, address: Long)

    @JvmStatic
    external fun releaseCharArrayAddress(array: CharArray, address: Long)
}