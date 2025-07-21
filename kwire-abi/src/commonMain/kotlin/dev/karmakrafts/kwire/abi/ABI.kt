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
 * Application Binary Interface (ABI) information for the current platform.
 * This object provides platform-specific size information for fundamental types.
 * It is expected to have platform-specific implementations.
 */
expect object ABI {
    /**
     * The storage size of a pointer in bytes.
     * This represents how many bytes are used to store a pointer in memory.
     */
    val pointerStorageSize: Int
    
    /**
     * The size of a pointer in bytes.
     * This represents the actual size of a pointer when used in operations.
     */
    val pointerSize: Int
    
    /**
     * The size of a boolean value in bytes.
     * This represents how many bytes are used to store a boolean value in memory.
     */
    val booleanSize: Int
}