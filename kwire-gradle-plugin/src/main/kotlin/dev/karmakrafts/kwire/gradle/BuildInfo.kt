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

package dev.karmakrafts.kwire.gradle

import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact

/**
 * Internal object containing build information and constants for the kWire Gradle plugin.
 * This object provides access to plugin metadata such as group ID, plugin name, and version.
 */
internal object BuildInfo {
    /**
     * The group ID for the kWire plugin artifacts.
     */
    const val GROUP: String = "dev.karmakrafts.kwire"

    /**
     * The name of the kWire compiler plugin.
     */
    const val PLUGIN_NAME: String = "kwire-compiler-plugin"

    /**
     * The version of the kWire plugin, read from the 'kwire.version' resource file.
     * This is used to ensure version consistency across the plugin components.
     */
    val version: String = BuildInfo::class.java.getResourceAsStream("/kwire.version")?.bufferedReader().use {
        it?.readText()
    }!!

    /**
     * The SubpluginArtifact instance used by the Kotlin compiler plugin API.
     * This is used to identify the compiler plugin artifact when registering with the Kotlin compiler.
     */
    val pluginArtifact: SubpluginArtifact = SubpluginArtifact(GROUP, PLUGIN_NAME, version)
}