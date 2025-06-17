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

package dev.karmakrafts.kwire.compiler.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

internal fun MessageCollector.info(message: String, location: CompilerMessageLocation? = null) =
    report(CompilerMessageSeverity.INFO, message, location)

internal fun MessageCollector.warn(message: String, location: CompilerMessageLocation? = null) =
    report(CompilerMessageSeverity.WARNING, message, location)

internal fun MessageCollector.error(message: String, location: CompilerMessageLocation? = null) =
    report(CompilerMessageSeverity.ERROR, message, location)