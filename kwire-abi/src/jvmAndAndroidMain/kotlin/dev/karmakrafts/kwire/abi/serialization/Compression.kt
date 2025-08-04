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

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import java.util.zip.Deflater
import java.util.zip.Inflater

private const val CHUNK_SIZE: Int = 4096

@PublishedApi
internal actual fun deflate(buffer: Buffer): Buffer {
    val deflater = Deflater()
    deflater.setInput(buffer.readByteArray())
    deflater.finish()
    val compressed = Buffer()
    val chunkBuffer = ByteArray(CHUNK_SIZE)
    while (!deflater.finished()) {
        val bytesCompressed = deflater.deflate(chunkBuffer)
        compressed.write(chunkBuffer, 0, bytesCompressed)
    }
    deflater.end()
    return compressed
}

@PublishedApi
internal actual fun inflate(buffer: Buffer): Buffer {
    val inflater = Inflater()
    inflater.setInput(buffer.readByteArray())
    val decompressed = Buffer()
    val chunkBuffer = ByteArray(CHUNK_SIZE)
    while (!inflater.finished()) {
        val bytesDecompressed = inflater.inflate(chunkBuffer)
        decompressed.write(chunkBuffer, 0, bytesDecompressed)
    }
    inflater.end()
    return decompressed
}