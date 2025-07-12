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
import platform.posix.nfloat_tVar
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar

inline fun COpaquePointer?.toPtr(): Ptr<CVoid> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun <reified F : Function<*>> CPointer<CFunction<F>>?.toPtr(): Ptr<F> =
    this?.rawValue?.toLong()?.asPtr() ?: nullptr()

inline fun CPointer<ByteVar>?.toPtr(): Ptr<Byte> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<ShortVar>?.toPtr(): Ptr<Short> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<IntVar>?.toPtr(): Ptr<Int> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<LongVar>?.toPtr(): Ptr<Long> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

@OptIn(UnsafeNumber::class)
inline fun CPointer<ptrdiff_tVar>?.toPtrN(): Ptr<NInt> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

inline fun CPointer<UByteVar>?.toPtr(): Ptr<UByte> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<UShortVar>?.toPtr(): Ptr<UShort> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<UIntVar>?.toPtr(): Ptr<UInt> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<ULongVar>?.toPtr(): Ptr<ULong> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

@OptIn(UnsafeNumber::class)
inline fun CPointer<size_tVar>?.toPtrN(): Ptr<NUInt> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

inline fun CPointer<FloatVar>?.toPtr(): Ptr<Float> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()
inline fun CPointer<DoubleVar>?.toPtr(): Ptr<Double> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

@OptIn(UnsafeNumber::class)
inline fun CPointer<nfloat_tVar>?.toPtrN(): Ptr<NFloat> = this?.rawValue?.toLong()?.asPtr() ?: nullptr()

@ConstCallable
inline fun Ptr<CVoid>.toCPointer(): CPointer<COpaque> = asLong().toCPointer()!!

@ConstCallable
inline fun <reified F : Function<*>> Ptr<F>.toCFunctionPointer(): CPointer<CFunction<F>> =
    reinterpret<CVoid>().toCPointer().reinterpret()

@ConstCallable
inline fun Ptr<Byte>.toCPointer(): CPointer<ByteVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<Short>.toCPointer(): CPointer<ShortVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<Int>.toCPointer(): CPointer<IntVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<Long>.toCPointer(): CPointer<LongVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@ConstCallable
inline fun Ptr<NInt>.toCPointerN(): CPointer<ptrdiff_tVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<UByte>.toCPointer(): CPointer<UByteVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<UShort>.toCPointer(): CPointer<UShortVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<UInt>.toCPointer(): CPointer<UIntVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<ULong>.toCPointer(): CPointer<ULongVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@ConstCallable
inline fun Ptr<NUInt>.toCPointerN(): CPointer<size_tVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<Float>.toCPointer(): CPointer<FloatVar> = asLong().toCPointer()!!

@ConstCallable
inline fun Ptr<Double>.toCPointer(): CPointer<DoubleVar> = asLong().toCPointer()!!

@OptIn(UnsafeNumber::class)
@ConstCallable
inline fun Ptr<NFloat>.toCPointerN(): CPointer<nfloat_tVar> = asLong().toCPointer()!!