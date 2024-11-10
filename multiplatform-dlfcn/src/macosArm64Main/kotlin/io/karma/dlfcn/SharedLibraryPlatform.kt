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

// https://github.com/apple-oss-distributions/xnu/blob/8d741a5de7ff4191bf97d57b9f54c2f6d4a15585/EXTERNAL_HEADERS/mach-o/arm64/reloc.h
private const val ARM64_RELOC_UNSIGNED: UInt = 0U             // for pointers
private const val ARM64_RELOC_SUBTRACTOR: UInt = 1U           // must be followed by a ARM64_RELOC_UNSIGNED
private const val ARM64_RELOC_BRANCH26: UInt = 2U             // a B/BL instruction with 26-bit displacement
private const val ARM64_RELOC_PAGE21: UInt = 3U               // pc-rel distance to page of target
private const val ARM64_RELOC_PAGEOFF12: UInt = 4U            // offset within page, scaled by r_length
private const val ARM64_RELOC_GOT_LOAD_PAGE21: UInt = 5U      // pc-rel distance to page of GOT slot
private const val ARM64_RELOC_GOT_LOAD_PAGEOFF12: UInt = 6U   // offset within page of GOT slot, scaled by r_length
private const val ARM64_RELOC_POINTER_TO_GOT: UInt = 7U       // for pointers to GOT slots
private const val ARM64_RELOC_TLVP_LOAD_PAGE21: UInt = 8U     // pc-rel distance to page of TLVP slot
private const val ARM64_RELOC_TLVP_LOAD_PAGEOFF12: UInt = 9U  // offset within page of TLVP slot, scaled by r_length
private const val ARM64_RELOC_ADDEND: UInt = 10U              // must be followed by PAGE21 or PAGEOFF12

@ExperimentalForeignApi
internal actual fun relocateSymbol(address: COpaquePointerVar, relocation: relocation_info, slide: COpaquePointer) {
    when (relocation.r_type) {
        ARM64_RELOC_UNSIGNED, ARM64_RELOC_POINTER_TO_GOT -> {
            if (relocation.r_extern != 0U) return
            address.value = interpretCPointer(address.value!!.rawValue + slide.rawValue.toLong())
        }

        ARM64_RELOC_BRANCH26 -> {
            if (relocation.r_extern != 0U) return
            val instruction = interpretCPointer<COpaquePointerVar>(address.rawPtr)?.pointed ?: return
            var offset = (instruction.value.rawValue.toLong() and 0x03FFFFFF) shl 2
            offset = offset or (if ((offset and 0x02000000L) == 0x02000000L) 0xFC000000 else 0)
            offset += slide.rawValue.toLong()
            instruction.value =
                ((instruction.value.rawValue.toLong() and 0x03FFFFFF.inv()) or ((offset shr 2) and 0x03FFFFFF)).toCPointer<COpaque>()
        }
    }
}