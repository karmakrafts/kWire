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

package dev.karmakrafts.kwire.compiler.mangler

internal object ManglingConstants {
    // Signed types
    const val BYTE_NAME: String = "a"
    const val SHORT_NAME: String = "b"
    const val INT_NAME: String = "c"
    const val LONG_NAME: String = "d"
    const val NINT_NAME: String = "e"

    // Unsigned types
    const val UBYTE_NAME: String = "f"
    const val USHORT_NAME: String = "g"
    const val UINT_NAME: String = "h"
    const val ULONG_NAME: String = "i"
    const val NUINT_NAME: String = "j"

    // IEEE-754
    const val FLOAT_NAME: String = "k"
    const val DOUBLE_NAME: String = "l"
    const val NFLOAT_NAME: String = "m"

    // Misc
    const val PTR_NAME: String = "n"
    const val BOOLEAN_NAME: String = "o"
    const val CHAR_NAME: String = "p"

    // Builtin objects
    const val STRING_NAME: String = "x"
    const val UNIT_NAME: String = "y"
    const val NOTHING_NAME: String = "z"

    // Markers
    const val TYPE_LIST_BEGIN: String = "T"
    const val TYPE_LIST_END: String = "\$T"
    const val CLASS_BEGIN: String = "C"
    const val CLASS_END: String = "\$C"
    const val ARRAY_BEGIN: String = "A"
    const val ARRAY_END: String = "\$A"
    const val NULLABLE_SUFFIX: String = "N"
    const val STAR_PROJECTION: String = "_"
    const val PACKAGE_DELIMITER: String = "_"
}