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

@file:Suppress("NOTHING_TO_INLINE")

package dev.karmakrafts.kwire

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.COpaque
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointed
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
import kotlinx.cinterop.toCPointer
import platform.posix.ptrdiff_tVar
import platform.posix.size_t
import platform.posix.size_tVar

@ExperimentalForeignApi
inline fun CPointer<*>.toPointer(): Pointer = Pointer(rawValue.toLong().toNUInt())

@ExperimentalForeignApi
inline fun <reified T : CPointed> Pointer.toCPointer(): CPointer<T>? = value.value.longValue.toCPointer()

@ExperimentalForeignApi
inline fun <reified T : Function<*>> Pointer.toCFunctionPointer(): CPointer<CFunction<T>>? = toCPointer()

@ExperimentalForeignApi
inline fun Pointer.toCOpaquePointer(): COpaquePointer? = toCPointer<COpaque>()

// Signed integer pointer conversions

@ExperimentalForeignApi
inline fun CPointer<ByteVar>.toBytePtr(): BytePtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<ShortVar>.toShortPtr(): ShortPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<IntVar>.toIntPtr(): IntPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<LongVar>.toLongPtr(): LongPtr = toPointer().reinterpret()

@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun CPointer<ptrdiff_tVar>.toNIntPtr(): NIntPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun BytePtr.toCPointer(): CPointer<ByteVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun ShortPtr.toCPointer(): CPointer<ShortVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun IntPtr.toCPointer(): CPointer<IntVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun LongPtr.toCPointer(): CPointer<LongVar>? = reinterpret<Pointer>().toCPointer()

@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun NIntPtr.toCPointer(): CPointer<ptrdiff_tVar>? = reinterpret<Pointer>().toCPointer()

// Unsigned integer pointer conversions

@ExperimentalForeignApi
inline fun CPointer<UByteVar>.toBytePtr(): UBytePtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<UShortVar>.toShortPtr(): UShortPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<UIntVar>.toIntPtr(): UIntPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<ULongVar>.toLongPtr(): ULongPtr = toPointer().reinterpret()

@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun CPointer<size_tVar>.toNUIntPtr(): NUIntPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun UBytePtr.toCPointer(): CPointer<UByteVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun UShortPtr.toCPointer(): CPointer<UShortVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun UIntPtr.toCPointer(): CPointer<UIntVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun ULongPtr.toCPointer(): CPointer<ULongVar>? = reinterpret<Pointer>().toCPointer()

@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun NUIntPtr.toCPointer(): CPointer<size_tVar>? = reinterpret<Pointer>().toCPointer()

// IEEE-754 pointeer conversions

@ExperimentalForeignApi
inline fun CPointer<FloatVar>.toFloatPtr(): FloatPtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun CPointer<DoubleVar>.toDoublePtr(): DoublePtr = toPointer().reinterpret()

@ExperimentalForeignApi
inline fun FloatPtr.toCPointer(): CPointer<FloatVar>? = reinterpret<Pointer>().toCPointer()

@ExperimentalForeignApi
inline fun DoublePtr.toCPointer(): CPointer<DoubleVar>? = reinterpret<Pointer>().toCPointer()