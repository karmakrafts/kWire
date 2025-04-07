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

package dev.karmakrafts.kwire

/**
 * Internal function to get the platform-specific implementation of the ShutdownHandler interface.
 *
 * This function is expected to be implemented by each platform (JVM, Native, etc.)
 * to provide the appropriate ShutdownHandler implementation for that platform.
 *
 * @return The platform-specific ShutdownHandler implementation
 */
internal expect fun getPlatformShutdownHandler(): ShutdownHandler

/**
 * Interface for managing resources that need to be closed when the application shuts down.
 *
 * This interface provides methods for registering and unregistering [AutoCloseable] resources
 * that should be automatically closed when the application terminates. This ensures proper
 * cleanup of resources like native memory, file handles, and other system resources.
 */
interface ShutdownHandler {
    /**
     * Companion object that delegates to the platform-specific ShutdownHandler implementation.
     *
     * This allows for static access to ShutdownHandler functionality through the ShutdownHandler class,
     * e.g., `ShutdownHandler.register(...)` instead of requiring an instance.
     */
    companion object : ShutdownHandler by getPlatformShutdownHandler()

    /**
     * Registers an AutoCloseable resource to be closed when the application shuts down.
     *
     * @param closeable The AutoCloseable resource to register for automatic closing
     */
    fun register(closeable: AutoCloseable)

    /**
     * Unregisters an AutoCloseable resource from being closed when the application shuts down.
     *
     * @param closeable The AutoCloseable resource to unregister
     */
    fun unregister(closeable: AutoCloseable)
}
