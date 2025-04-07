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

import java.lang.invoke.MethodHandle

/**
 * Converts this [FFIFunction] to a Java [MethodHandle].
 *
 * This extension function creates a method handle that can be used to invoke the native function
 * directly using Java's Foreign Function Interface. The method handle is created using the function's
 * address and descriptor.
 *
 * @return A [MethodHandle] that can be used to invoke the native function.
 * @throws IllegalArgumentException if the method handle could not be obtained.
 */
fun FFIFunction.toMethodHandle(): MethodHandle = PanamaFFI.getHandle(address, descriptor)
