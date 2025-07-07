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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import oshi.PlatformEnum
import oshi.SystemInfo
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.div
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
internal data class NativeLoaderArchitectureConfig( // @formatter:off
    val resources: List<String> = emptyList(),
    val systemLibs: List<String> = emptyList()
) // @formatter:on

@Serializable
internal data class NativeLoaderPlatformConfig( // @formatter:off
    val architectures: Map<String, NativeLoaderArchitectureConfig> = emptyMap()
) // @formatter:on

@Serializable
internal data class NativeLoaderConfig( // @formatter:off
    val version: Int = VERSION,
    val platforms: Map<PlatformEnum, NativeLoaderPlatformConfig> = emptyMap()
) { // @formatter:on
    companion object {
        const val VERSION: Int = 1
    }
}

@OptIn(ExperimentalAtomicApi::class)
internal object NativeLoader {
    private val json: Json = Json { ignoreUnknownKeys = true }
    private val isLoaded: AtomicBoolean = AtomicBoolean(false)

    @OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class, ExperimentalPathApi::class)
    fun ensureLoaded() {
        if (!isLoaded.compareAndSet(expectedValue = false, newValue = true)) return
        val config = NativeLoader::class.java.getResourceAsStream("/kwire-natives/config.json")?.use {
            json.decodeFromStream<NativeLoaderConfig>(it)
        }!!
        check(config.version == NativeLoaderConfig.VERSION) {
            "Mismatched NativeLoader config version: expected ${NativeLoaderConfig.VERSION} but got ${config.version}"
        }
        val currentArch = System.getProperty("os.arch") ?: error("os.arch is not set, cannot determine architecture")
        val platformConfig = config.platforms[SystemInfo.getCurrentPlatform()]!!
        for ((compoundArch, archConfig) in platformConfig.architectures) {
            val architectures = compoundArch.split(",") // We can match multiple architectures
            if (currentArch !in architectures) continue
            for (library in archConfig.systemLibs) try {
                System.loadLibrary(library)
            } catch (error: Throwable) {
                System.err.println("Could not load kWire system library $library: ${error.stackTraceToString()}")
            }
            if (archConfig.resources.isNotEmpty()) {
                val userHome = Path(System.getProperty("user.home") ?: ".").absolute()
                val kwireHome = userHome / ".kwire"
                val tempDir = kwireHome / Uuid.random().toHexString()
                tempDir.createDirectories()
                for (resource in archConfig.resources) {
                    val relativePath = Path(resource)
                    val fileName = relativePath.fileName
                    val tempFile = tempDir / fileName
                    try {
                        NativeLoader::class.java.getResourceAsStream("/$resource")!!.use {
                            Files.copy(it, tempFile, StandardCopyOption.REPLACE_EXISTING)
                        }
                        System.load(tempFile.absolutePathString())
                    } catch (error: Throwable) {
                        System.err.println("Could not load kWire platform library $resource: ${error.stackTraceToString()}")
                    }
                }
                Runtime.getRuntime().addShutdownHook(Thread {
                    tempDir.deleteRecursively()
                })
            }
        }
    }
}