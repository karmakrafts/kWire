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

import dev.karmakrafts.conventions.GitLabPackage
import dev.karmakrafts.conventions.configureJava
import dev.karmakrafts.conventions.dependsOn
import dev.karmakrafts.conventions.getBinaryBaseName
import dev.karmakrafts.conventions.getBinaryTaskSuffix
import dev.karmakrafts.conventions.gitlab
import dev.karmakrafts.conventions.setProjectInfo
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.time.ZonedDateTime
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.serialization)
    signing
    `maven-publish`
}

configureJava(rootProject.libs.versions.java)

val jvmPlatforms: List<Pair<String, String>> = listOf( // @formatter:off
    "windows-x64" to "windowsX64",
    "linux-x64" to "linuxX64",
    "linux-arm64" to "linuxArm64",
    "macos-x64" to "macosX64",
    "macos-arm64" to "macosArm64"
) // @formatter:on

infix fun <T, U, V> Pair<T, U>.to(value: V): Triple<T, U, V> = Triple(first, second, value)
val androidPlatforms: List<Triple<String, String, String>> = listOf( // @formatter:off
    "android-x64" to "androidX64" to "x86_64",
    "android-arm64" to "androidArm64" to "arm64-v8a",
    "android-arm32" to "androidArm32" to "armeabi-v7a"
) // @formatter:on

val libffiJvmBinaries: Map<String, String> = mapOf(
    "windows-x64" to "lib/libffi-8.dll",
    "linux-x64" to "lib/libffi.so",
    "linux-arm64" to "lib/libffi.so",
    "macos-x64" to "lib/libffi.dylib",
    "macos-arm64" to "lib/libffi.dylib",
    "android-x64" to "lib/libffi.so",
    "android-arm64" to "lib/libffi.so",
    "android-arm32" to "lib/libffi.so"
)
val libffiPackage: GitLabPackage = gitlab().project(
    "kk/prebuilts/libffi"
).packageRegistry["generic/build", libs.versions.libffi]

val platformJvmBinaries: Map<String, String> = mapOf(
    "windows-x64" to "libkwire-platform.dll",
    "linux-x64" to "libkwire-platform.so",
    "linux-arm64" to "libkwire-platform.so",
    "macos-x64" to "libkwire-platform.dylib",
    "macos-arm64" to "libkwire-platform.dylib",
    "android-x64" to "libkwire-platform.so",
    "android-arm64" to "libkwire-platform.so",
    "android-arm32" to "libkwire-platform.so"
)
val platformPackage: GitLabPackage = gitlab().project(
    "kk/prebuilts/kwire-platform"
).packageRegistry["generic/build", libs.versions.kwirePlatform]

fun KotlinDependencyHandler.lwjglNatives(platform: String) {
    val dependency = libs.lwjgl.asProvider().get()
    implementation("${dependency.module}:${dependency.version}:natives-$platform")
}

kotlin {
    withSourcesJar(true)
    mingwX64()
    linuxX64()
    linuxArm64()
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }
    targets.withType<KotlinNativeTarget>().configureEach { ->
        val libffiArtifact = libffiPackage["build-${getBinaryBaseName()}-release.zip", getBinaryTaskSuffix()]
        compilations {
            val main by getting {
                cinterops {
                    val posix_wrappers by creating
                    val libffi by creating {
                        dependsOn(libffiArtifact)
                    }
                }
            }
        }
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.stately.common)
                implementation(libs.stately.concurrent.collections)
            }
        }

        val nativeMain by getting

        val x32Main by creating { dependsOn(nativeMain) }
        androidNativeArm32Main { dependsOn(x32Main) }

        val x64Main by creating { dependsOn(nativeMain) }
        linuxX64Main { dependsOn(x64Main) }
        linuxArm64Main { dependsOn(x64Main) }
        mingwX64Main { dependsOn(x64Main) }
        macosArm64Main { dependsOn(x64Main) }
        macosX64Main { dependsOn(x64Main) }
        androidNativeX64Main { dependsOn(x64Main) }
        androidNativeArm64Main { dependsOn(x64Main) }
        iosX64Main { dependsOn(x64Main) }
        iosArm64Main { dependsOn(x64Main) }
        iosSimulatorArm64Main { dependsOn(x64Main) }
        tvosArm64Main { dependsOn(x64Main) }
        tvosX64Main { dependsOn(x64Main) }
        tvosSimulatorArm64Main { dependsOn(x64Main) }
        watchosArm64Main { dependsOn(x64Main) }
        watchosX64Main { dependsOn(x64Main) }
        watchosSimulatorArm64Main { dependsOn(x64Main) }

        val jvmAndAndroidMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.oshi.core)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jvmMain {
            dependsOn(jvmAndAndroidMain)
            dependencies {
                implementation(libs.lwjgl)
                lwjglNatives("windows")
                lwjglNatives("linux")
                lwjglNatives("linux-arm64")
                lwjglNatives("macos")
                lwjglNatives("macos-arm64")
            }
        }

        androidMain {
            dependsOn(jvmAndAndroidMain)
            dependencies {
                implementation(libs.panamaPort)
            }
        }

        val posixMain by creating { dependsOn(nativeMain) }
        linuxMain { dependsOn(posixMain) }
        macosMain { dependsOn(posixMain) }
        iosMain { dependsOn(posixMain) }
        tvosMain { dependsOn(posixMain) }
        watchosMain { dependsOn(posixMain) }
        androidNativeMain { dependsOn(posixMain) }
    }
}

android {
    namespace = "$group.${rootProject.name}"
    compileSdk = libs.versions.androidCompileSDK.get().toInt()
    defaultConfig {
        minSdk = libs.versions.androidMinimalSDK.get().toInt()
    }
}

dokka {
    moduleName = project.name
    pluginsConfiguration {
        html {
            footerMessage = "(c) ${ZonedDateTime.now().year} Karma Krafts & associates"
        }
    }
}

val dokkaJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaGeneratePublicationHtml)
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

@OptIn(ExperimentalPathApi::class)
fun TaskContainer.registerCopyJvmJniLibraryTasks(
    taskPrefix: String, pkg: GitLabPackage, binaryNames: Map<String, String>
): List<TaskProvider<Copy>> {
    val tasks = ArrayList<TaskProvider<Copy>>()
    for ((name, suffix) in jvmPlatforms) {
        val platformArtifact = pkg["build-$name-release.zip", suffix]
        val copyTask = register<Copy>("copy${taskPrefix.capitalized()}JniLibraries${suffix.capitalized()}") {
            group = "JNI Libraries"
            dependsOn(platformArtifact.extractTask)
            from((platformArtifact.outputDirectoryPath / binaryNames[name]!!).toFile())
            into("src/jvmMain/resources/kwire-natives/$name")
            onlyIf { true } // Always copy these over when building/setting up
        }
        named("prepareKotlinIdeaImport") { dependsOn(copyTask) }
        named("assemble") { dependsOn(copyTask) }
        tasks += copyTask
    }
    return tasks
}

@OptIn(ExperimentalPathApi::class)
fun TaskContainer.registerCopyAndroidJniLibraryTasks(
    taskPrefix: String, pkg: GitLabPackage, binaryNames: Map<String, String>
): List<TaskProvider<Copy>> {
    val tasks = ArrayList<TaskProvider<Copy>>()
    for ((name, suffix, jniTarget) in androidPlatforms) {
        val platformArtifact = pkg["build-$name-release.zip", suffix]
        val copyTask = register<Copy>("copy${taskPrefix.capitalized()}JniLibraries${suffix.capitalized()}") {
            group = "JNI Libraries"
            dependsOn(platformArtifact.extractTask)
            from((platformArtifact.outputDirectoryPath / binaryNames[name]!!).toFile())
            into("src/androidMain/jniLibs/$jniTarget")
            onlyIf { true } // Always copy these over when building/setting up
        }
        named("prepareKotlinIdeaImport") { dependsOn(copyTask) }
        named("assemble") { dependsOn(copyTask) }
        tasks += copyTask
    }
    return tasks
}

tasks {
    val jvmPlatformCopyTasks = registerCopyJvmJniLibraryTasks("platform", platformPackage, platformJvmBinaries)
    val androidLibffiCopyTasks = registerCopyAndroidJniLibraryTasks("libffi", libffiPackage, libffiJvmBinaries)
    val androidPlatformCopyTasks = registerCopyAndroidJniLibraryTasks("platform", platformPackage, platformJvmBinaries)
    System.getProperty("publishDocs.root")?.let { docsDir ->
        register("publishDocs", Copy::class) {
            dependsOn(dokkaJar)
            mustRunAfter(dokkaJar)
            from(zipTree(dokkaJar.get().outputs.files.first()))
            into("$docsDir/${project.name}")
        }
    }
    val jvmProcessResources by getting {
        dependsOn(jvmPlatformCopyTasks)
    }
    afterEvaluate {
        val mergeReleaseJniLibFolders by getting {
            dependsOn(androidLibffiCopyTasks)
            dependsOn(androidPlatformCopyTasks)
        }
        val mergeDebugJniLibFolders by getting {
            dependsOn(androidLibffiCopyTasks)
            dependsOn(androidPlatformCopyTasks)
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(dokkaJar)
    }
    setProjectInfo("kWire Runtime", "Native interop as a first class feature for Kotlin/Multiplatform")
}