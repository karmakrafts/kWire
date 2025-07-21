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

package dev.karmakrafts.kwire.abi

/**
 * Constants related to the Application Binary Interface (ABI).
 * This object contains package name constants used throughout the kwire project
 * for consistent package naming and references.
 */
object ABIConstants {
    /**
     * The standard Kotlin package name.
     * Used for referencing Kotlin standard library types.
     */
    const val KOTLIN_PACKAGE: String = "kotlin"
    
    /**
     * The base package name for the kwire project.
     * All kwire-related packages are prefixed with this value.
     */
    const val KWIRE_PACKAGE: String = "dev.karmakrafts.kwire"
    
    /**
     * The package name for C-type related classes in kwire.
     * Contains C-compatible type definitions and utilities.
     */
    const val CTYPE_PACKAGE: String = "$KWIRE_PACKAGE.ctype"
}