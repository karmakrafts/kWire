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

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    `maven-publish`
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
    compileOnly(libs.autoService)
    kapt(libs.autoService)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.iridium)
    testImplementation(projects.kwireRuntime)
}

tasks {
    test {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors()
    }
}

publishing {
    publications {
        create<MavenPublication>("compilerPlugin") {
            from(components["java"])
        }
    }
}