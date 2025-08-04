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

package dev.karmakrafts.kwire.abi.serialization

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import platform.zlib.Z_BEST_COMPRESSION
import platform.zlib.Z_FINISH
import platform.zlib.Z_NO_FLUSH
import platform.zlib.Z_STREAM_END
import platform.zlib.Z_STREAM_ERROR
import platform.zlib.deflate
import platform.zlib.deflateEnd
import platform.zlib.deflateInit
import platform.zlib.inflate
import platform.zlib.inflateEnd
import platform.zlib.inflateInit
import platform.zlib.z_stream

private const val CHUNK_SIZE: Int = 4096

@PublishedApi
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun deflate(buffer: Buffer): Buffer = memScoped outerFrame@{
    val compressed = Buffer()
    val sourceData = buffer.readByteArray()
    println("Compressing ${sourceData.size} bytes")
    val stream = alloc<z_stream>()
    deflateInit(stream.ptr, Z_BEST_COMPRESSION)
    sourceData.usePinned { pinnedSource ->
        stream.next_in = pinnedSource.addressOf(0).reinterpret()
        stream.avail_in = sourceData.size.convert()
        val outChunk = ByteArray(CHUNK_SIZE)
        var result = 0
        do {
            memScoped { // Each loop iteration should use its own virtual stack frame
                outChunk.usePinned { pinnedDest ->
                    stream.next_out = pinnedDest.addressOf(0).reinterpret()
                    stream.avail_out = CHUNK_SIZE.convert()
                    result = deflate(stream.ptr, if (stream.avail_in > 0U) Z_NO_FLUSH else Z_FINISH)
                }
            }
            if (result == Z_STREAM_ERROR) {
                deflateEnd(stream.ptr)
                return@outerFrame compressed
            }
            val bytesWritten = CHUNK_SIZE - stream.avail_out.toInt()
            println("Compressed $bytesWritten byte chunk")
            compressed.write(outChunk, 0, bytesWritten)
        }
        while (result != Z_STREAM_END)
    }
    deflateEnd(stream.ptr)
    compressed
}

@PublishedApi
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal actual fun inflate(buffer: Buffer): Buffer = memScoped outerFrame@{
    val stream = alloc<z_stream>()
    val decompressed = Buffer()
    val sourceData = buffer.readByteArray()
    println("Decompressing ${sourceData.size} bytes")
    inflateInit(stream.ptr)
    sourceData.usePinned { pinnedSource ->
        stream.next_in = pinnedSource.addressOf(0).reinterpret()
        stream.avail_in = sourceData.size.convert()
        val outChunk = ByteArray(CHUNK_SIZE)
        var result = 0
        do {
            memScoped {
                outChunk.usePinned { pinnedDest ->
                    stream.next_out = pinnedDest.addressOf(0).reinterpret()
                    stream.avail_out = CHUNK_SIZE.convert()
                    result = inflate(stream.ptr, Z_NO_FLUSH)
                }
            }
            if (result == Z_STREAM_ERROR) {
                inflateEnd(stream.ptr)
                return@outerFrame decompressed
            }
            val bytesWritten = CHUNK_SIZE - stream.avail_out.toInt()
            println("Decompressed $bytesWritten byte chunk")
            decompressed.write(outChunk, 0, bytesWritten)
        }
        while (result != Z_STREAM_END)
    }
    inflateEnd(stream.ptr)
    decompressed
}