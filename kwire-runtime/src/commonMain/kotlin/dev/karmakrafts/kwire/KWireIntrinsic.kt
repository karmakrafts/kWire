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

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
internal annotation class KWireIntrinsic(val type: Type) {
    enum class Type {
        // @formatter:off
        SIZE_OF,
        ALIGN_OF,
        OFFSET_OF,
        DEFAULT,
        TYPE_OF,
        PTR_REF,
        PTR_DEREF,
        PTR_SET,
        PTR_ARRAY_GET,
        PTR_ARRAY_SET,
        PTR_INVOKE,
        PTR_PLUS,
        PTR_MINUS,
        ALLOCATOR_ALLOC,
        ALLOCATOR_ALLOC_ARRAY,
        ABI_GET_MODULE_DATA
        // @formatter:on
    }
}
