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

import org.jetbrains.kotlin.backend.common.getCompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile

interface MessageCollectorExtensions {
    val irFile: IrFile
    val messageCollector: MessageCollector

    fun reportInfo(message: String, location: CompilerMessageLocation? = null) =
        messageCollector.info(message, location)

    fun reportInfo(message: String, element: IrElement) =
        reportInfo(message, element.getCompilerMessageLocation(irFile))

    fun reportWarn(message: String, location: CompilerMessageLocation? = null) =
        messageCollector.warn(message, location)

    fun reportWarn(message: String, element: IrElement) =
        reportWarn(message, element.getCompilerMessageLocation(irFile))

    fun reportError(message: String, location: CompilerMessageLocation? = null) =
        messageCollector.error(message, location)

    fun reportError(message: String, element: IrElement) =
        reportError(message, element.getCompilerMessageLocation(irFile))
}