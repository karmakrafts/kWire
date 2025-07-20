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

import dev.karmakrafts.conventions.setProjectInfo
import java.time.ZonedDateTime

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    signing
    `maven-publish`
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
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.stately.common)
                implementation(libs.stately.concurrent.collections)
                api(libs.kotlinx.io.core)
            }
        }
        val jvmAndAndroidMain by creating { dependsOn(commonMain) }
        val jvmMain by getting { dependsOn(jvmAndAndroidMain) }
        val androidMain by getting { dependsOn(jvmAndAndroidMain) }

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

tasks {
    System.getProperty("publishDocs.root")?.let { docsDir ->
        register("publishDocs", Copy::class) {
            dependsOn(dokkaJar)
            mustRunAfter(dokkaJar)
            from(zipTree(dokkaJar.get().outputs.files.first()))
            into("$docsDir/${project.name}")
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(dokkaJar)
    }
    setProjectInfo("kWire ABI", "Native interop as a first class feature for Kotlin/Multiplatform")
}