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

@file:JvmName("CompressionImpl")

package dev.karmakrafts.kwire.abi.serialization

import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

@PublishedApi
internal actual fun deflate(data: ByteArray): ByteArray {
    return data.inputStream().use { inputStream ->
        DeflaterInputStream(inputStream).use { compressor ->
            compressor.readAllBytes()
        }
    }
}

@PublishedApi
internal actual fun inflate(data: ByteArray): ByteArray {
    return data.inputStream().use { inputStream ->
        InflaterInputStream(inputStream).use { decompressor ->
            decompressor.readAllBytes()
        }
    }
}