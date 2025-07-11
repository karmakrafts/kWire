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

package dev.karmakrafts.kwire.compiler.ffi

import dev.karmakrafts.kwire.compiler.util.isCDecl
import dev.karmakrafts.kwire.compiler.util.isFastCall
import dev.karmakrafts.kwire.compiler.util.isStdCall
import dev.karmakrafts.kwire.compiler.util.isThisCall
import org.jetbrains.kotlin.ir.types.IrType

internal enum class CallingConvention {
    CDECL, THISCALL, STDCALL, FASTCALL
}

internal fun IrType.getCallingConvention(): CallingConvention? = when {
    isCDecl() -> CallingConvention.CDECL
    isThisCall() -> CallingConvention.THISCALL
    isStdCall() -> CallingConvention.STDCALL
    isFastCall() -> CallingConvention.FASTCALL
    else -> null
}

internal fun IrType.getCallingConventionOrDefault(): CallingConvention =
    getCallingConvention() ?: CallingConvention.CDECL