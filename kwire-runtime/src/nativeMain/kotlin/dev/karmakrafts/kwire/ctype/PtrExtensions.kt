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

// @formatter:off
@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(ExperimentalForeignApi::class)
// @formatter:on

package dev.karmakrafts.kwire.ctype

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer

inline fun COpaquePointer.toPtr(): VoidPtr = rawValue.toLong().asVoidPtr()
inline fun CPointer<CFunction<*>>.toPtr(): VoidPtr = rawValue.toLong().asVoidPtr()

inline fun CPointer<ByteVar>.toPtr(): NumPtr<Byte> = rawValue.toLong().asNumPtr()
inline fun CPointer<ShortVar>.toPtr(): NumPtr<Short> = rawValue.toLong().asNumPtr()
inline fun CPointer<IntVar>.toPtr(): NumPtr<Int> = rawValue.toLong().asNumPtr()
inline fun CPointer<LongVar>.toPtr(): NumPtr<Long> = rawValue.toLong().asNumPtr()
inline fun CPointer<FloatVar>.toPtr(): NumPtr<Float> = rawValue.toLong().asNumPtr()
inline fun CPointer<DoubleVar>.toPtr(): NumPtr<Double> = rawValue.toLong().asNumPtr()

inline fun VoidPtr.toCPointer(): CPointer<COpaque> = asLong().toCPointer()!!
inline fun Address.toCPointer(): CPointer<COpaque> = asLong().toCPointer()!!
inline fun <F : Function<*>> Address.toCFunctionPointer(): CPointer<CFunction<F>> = toCPointer().reinterpret()

inline fun NumPtr<Byte>.toCPointer(): CPointer<ByteVar> = asLong().toCPointer()!!
inline fun NumPtr<Short>.toCPointer(): CPointer<ShortVar> = asLong().toCPointer()!!
inline fun NumPtr<Int>.toCPointer(): CPointer<IntVar> = asLong().toCPointer()!!
inline fun NumPtr<Long>.toCPointer(): CPointer<LongVar> = asLong().toCPointer()!!
inline fun NumPtr<Float>.toCPointer(): CPointer<FloatVar> = asLong().toCPointer()!!
inline fun NumPtr<Double>.toCPointer(): CPointer<DoubleVar> = asLong().toCPointer()!!