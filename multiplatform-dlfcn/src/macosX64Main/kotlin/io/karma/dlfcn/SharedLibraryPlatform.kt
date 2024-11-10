/*
 * Copyright 2024 Karma Krafts & associates
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

package io.karma.dlfcn

import kotlinx.cinterop.*
import platform.darwin.relocation_info

// https://github.com/apple-oss-distributions/xnu/blob/8d741a5de7ff4191bf97d57b9f54c2f6d4a15585/EXTERNAL_HEADERS/mach-o/x86_64/reloc.h#L44
private const val X86_64_RELOC_UNSIGNED: UInt = 0U	    // for absolute addresses
private const val X86_64_RELOC_SIGNED: UInt = 1U		// for signed 32-bit displacement
private const val X86_64_RELOC_BRANCH: UInt = 2U		// a CALL/JMP instruction with 32-bit displacement
private const val X86_64_RELOC_GOT_LOAD: UInt = 3U	    // a MOVQ load of a GOT entry
private const val X86_64_RELOC_GOT: UInt = 4U			// other GOT references
private const val X86_64_RELOC_SUBTRACTOR: UInt = 5U	// must be followed by a X86_64_RELOC_UNSIGNED
private const val X86_64_RELOC_SIGNED_1: UInt = 6U	    // for signed 32-bit displacement with a -1 addend
private const val X86_64_RELOC_SIGNED_2: UInt = 7U	    // for signed 32-bit displacement with a -2 addend
private const val X86_64_RELOC_SIGNED_4: UInt = 8U	    // for signed 32-bit displacement with a -4 addend
private const val X86_64_RELOC_TLV: UInt = 9U		    // for thread local variables

@ExperimentalForeignApi
internal actual fun relocateSymbol(address: COpaquePointerVar, relocation: relocation_info, slide: COpaquePointer) {
    when(relocation.r_type) {
        X86_64_RELOC_UNSIGNED, X86_64_RELOC_SIGNED -> {
            if (relocation.r_extern != 0U) return
            address.value = interpretCPointer(address.value!!.rawValue + slide.rawValue.toLong())
        }
    }
}