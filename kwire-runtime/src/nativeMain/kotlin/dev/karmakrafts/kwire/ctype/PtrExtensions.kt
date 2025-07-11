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
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.UShortVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCPointer
import platform.posix.nfloat_t
import platform.posix.nfloat_tVar
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar

inline fun COpaquePointer.toPtr(): VoidPtr = rawValue.toLong().asVoidPtr()
inline fun CPointer<CFunction<*>>.toPtr(): VoidPtr = rawValue.toLong().asVoidPtr()

inline fun CPointer<ByteVar>.toPtr(): NumPtr<Byte> = rawValue.toLong().asNumPtr()
inline fun CPointer<ShortVar>.toPtr(): NumPtr<Short> = rawValue.toLong().asNumPtr()
inline fun CPointer<IntVar>.toPtr(): NumPtr<Int> = rawValue.toLong().asNumPtr()
inline fun CPointer<LongVar>.toPtr(): NumPtr<Long> = rawValue.toLong().asNumPtr()
inline fun CPointer<FloatVar>.toPtr(): NumPtr<Float> = rawValue.toLong().asNumPtr()
inline fun CPointer<DoubleVar>.toPtr(): NumPtr<Double> = rawValue.toLong().asNumPtr()

@PermitsConst
inline fun VoidPtr.toCPointer(): CPointer<COpaque> = asLong().toCPointer()!!

@PermitsConst
inline fun Address.toCPointer(): CPointer<COpaque> = asLong().toCPointer()!!

@PermitsConst
inline fun <F : Function<*>> Address.toCFunctionPointer(): CPointer<CFunction<F>> = toCPointer().reinterpret()

@PermitsConst
inline fun NumPtr<Byte>.toCPointer(): CPointer<ByteVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<Short>.toCPointer(): CPointer<ShortVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<Int>.toCPointer(): CPointer<IntVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<Long>.toCPointer(): CPointer<LongVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@PermitsConst
inline fun NumPtr<NInt>.toCPointer(): CPointer<ptrdiff_tVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<UByte>.toCPointer(): CPointer<UByteVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<UShort>.toCPointer(): CPointer<UShortVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<UInt>.toCPointer(): CPointer<UIntVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<ULong>.toCPointer(): CPointer<ULongVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@PermitsConst
inline fun NumPtr<NUInt>.toCPointer(): CPointer<size_tVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<Float>.toCPointer(): CPointer<FloatVar> = asLong().toCPointer()!!

@PermitsConst
inline fun NumPtr<Double>.toCPointer(): CPointer<DoubleVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@PermitsConst
inline fun NumPtr<NFloat>.toCPointer(): CPointer<nfloat_tVar> = asLong().toCPointer()!!