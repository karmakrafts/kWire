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
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.NativePointed
import kotlinx.cinterop.ShortVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.UShortVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.toCPointer
import platform.posix.nfloat_tVar
import platform.posix.ptrdiff_tVar
import platform.posix.size_tVar

/**
 * Converts a native pointed object to a platform-independent [Pointer].
 *
 * This extension function allows converting any native pointed object to the library's
 * platform-independent pointer representation.
 *
 * @return A [Pointer] that points to the same memory address as this native pointed object.
 */
@ExperimentalForeignApi
inline fun NativePointed.toPointer(): Pointer = Pointer(rawPtr.toLong().toNUInt())

/**
 * Converts a C pointer to a platform-independent [Pointer].
 *
 * This extension function allows converting any C pointer type to the library's
 * platform-independent pointer representation.
 *
 * @return A [Pointer] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<*>.toPointer(): Pointer = Pointer(rawValue.toLong().toNUInt())

/**
 * Converts a platform-independent [Pointer] to a typed C pointer.
 *
 * This extension function allows converting the library's platform-independent pointer
 * representation to a typed C pointer that can be used with Kotlin/Native C interop.
 *
 * @param T The C type that the pointer points to.
 * @return A typed C pointer that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun <reified T : CPointed> Pointer.toCPointer(): CPointer<T>? = value.value.longValue.toCPointer()

/**
 * Converts a platform-independent [Pointer] to a C function pointer.
 *
 * This extension function allows converting the library's platform-independent pointer
 * representation to a C function pointer that can be used to call native functions.
 *
 * @param T The function type that the pointer points to.
 * @return A C function pointer that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun <reified T : Function<*>> Pointer.toCFunctionPointer(): CPointer<CFunction<T>>? = toCPointer()

/**
 * Converts a platform-independent [Pointer] to an opaque C pointer.
 *
 * This extension function allows converting the library's platform-independent pointer
 * representation to an opaque C pointer that can be used with Kotlin/Native C interop.
 *
 * @return An opaque C pointer that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun Pointer.toCOpaquePointer(): COpaquePointer? = toCPointer<COpaque>()

// Signed integer pointer conversions

/**
 * Converts a C pointer to a byte variable to a typed [BytePtr].
 *
 * This extension function allows converting a C pointer to a byte variable
 * to the library's typed pointer representation for bytes.
 *
 * @return A [BytePtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<ByteVar>.toBytePtr(): BytePtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a short variable to a typed [ShortPtr].
 *
 * This extension function allows converting a C pointer to a short variable
 * to the library's typed pointer representation for shorts.
 *
 * @return A [ShortPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<ShortVar>.toShortPtr(): ShortPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to an int variable to a typed [IntPtr].
 *
 * This extension function allows converting a C pointer to an int variable
 * to the library's typed pointer representation for ints.
 *
 * @return An [IntPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<IntVar>.toIntPtr(): IntPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a long variable to a typed [LongPtr].
 *
 * This extension function allows converting a C pointer to a long variable
 * to the library's typed pointer representation for longs.
 *
 * @return A [LongPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<LongVar>.toLongPtr(): LongPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a platform-specific integer variable to a typed [NIntPtr].
 *
 * This extension function allows converting a C pointer to a platform-specific integer variable
 * to the library's typed pointer representation for platform-specific integers.
 *
 * @return An [NIntPtr] that points to the same memory address as this C pointer.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun CPointer<ptrdiff_tVar>.toNIntPtr(): NIntPtr = toPointer().reinterpret()

/**
 * Converts a typed [BytePtr] to a C pointer to a byte variable.
 *
 * This extension function allows converting the library's typed pointer representation for bytes
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a byte variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun BytePtr.toCPointer(): CPointer<ByteVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [ShortPtr] to a C pointer to a short variable.
 *
 * This extension function allows converting the library's typed pointer representation for shorts
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a short variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun ShortPtr.toCPointer(): CPointer<ShortVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [IntPtr] to a C pointer to an int variable.
 *
 * This extension function allows converting the library's typed pointer representation for ints
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to an int variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun IntPtr.toCPointer(): CPointer<IntVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [LongPtr] to a C pointer to a long variable.
 *
 * This extension function allows converting the library's typed pointer representation for longs
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a long variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun LongPtr.toCPointer(): CPointer<LongVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [NIntPtr] to a C pointer to a platform-specific integer variable.
 *
 * This extension function allows converting the library's typed pointer representation for platform-specific integers
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a platform-specific integer variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun NIntPtr.toCPointer(): CPointer<ptrdiff_tVar>? = reinterpret<Pointer>().toCPointer()

// Unsigned integer pointer conversions

/**
 * Converts a C pointer to an unsigned byte variable to a typed [UBytePtr].
 *
 * This extension function allows converting a C pointer to an unsigned byte variable
 * to the library's typed pointer representation for unsigned bytes.
 *
 * @return A [UBytePtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<UByteVar>.toBytePtr(): UBytePtr = toPointer().reinterpret()

/**
 * Converts a C pointer to an unsigned short variable to a typed [UShortPtr].
 *
 * This extension function allows converting a C pointer to an unsigned short variable
 * to the library's typed pointer representation for unsigned shorts.
 *
 * @return A [UShortPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<UShortVar>.toShortPtr(): UShortPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to an unsigned int variable to a typed [UIntPtr].
 *
 * This extension function allows converting a C pointer to an unsigned int variable
 * to the library's typed pointer representation for unsigned ints.
 *
 * @return An [UIntPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<UIntVar>.toIntPtr(): UIntPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to an unsigned long variable to a typed [ULongPtr].
 *
 * This extension function allows converting a C pointer to an unsigned long variable
 * to the library's typed pointer representation for unsigned longs.
 *
 * @return A [ULongPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<ULongVar>.toLongPtr(): ULongPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a platform-specific unsigned integer variable to a typed [NUIntPtr].
 *
 * This extension function allows converting a C pointer to a platform-specific unsigned integer variable
 * to the library's typed pointer representation for platform-specific unsigned integers.
 *
 * @return An [NUIntPtr] that points to the same memory address as this C pointer.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun CPointer<size_tVar>.toNUIntPtr(): NUIntPtr = toPointer().reinterpret()

/**
 * Converts a typed [UBytePtr] to a C pointer to an unsigned byte variable.
 *
 * This extension function allows converting the library's typed pointer representation for unsigned bytes
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to an unsigned byte variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun UBytePtr.toCPointer(): CPointer<UByteVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [UShortPtr] to a C pointer to an unsigned short variable.
 *
 * This extension function allows converting the library's typed pointer representation for unsigned shorts
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to an unsigned short variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun UShortPtr.toCPointer(): CPointer<UShortVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [UIntPtr] to a C pointer to an unsigned int variable.
 *
 * This extension function allows converting the library's typed pointer representation for unsigned ints
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to an unsigned int variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun UIntPtr.toCPointer(): CPointer<UIntVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [ULongPtr] to a C pointer to an unsigned long variable.
 *
 * This extension function allows converting the library's typed pointer representation for unsigned longs
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to an unsigned long variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun ULongPtr.toCPointer(): CPointer<ULongVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [NUIntPtr] to a C pointer to a platform-specific unsigned integer variable.
 *
 * This extension function allows converting the library's typed pointer representation for platform-specific unsigned integers
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a platform-specific unsigned integer variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun NUIntPtr.toCPointer(): CPointer<size_tVar>? = reinterpret<Pointer>().toCPointer()

// Pointer to pointer conversions

/**
 * Converts a C pointer to a typed [PointerPtr].
 *
 * This extension function allows converting a C pointer
 * to the library's typed pointer representation for pointers.
 *
 * @return A [PointerPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<CPointerVar<*>>.toPointerPtr(): PointerPtr = toPointer().reinterpret()

/**
 * Converts a typed [PointerPtr] to a C opaque pointer.
 *
 * This extension function allows converting the library's typed pointer representation for pointers
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C opaque pointer that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun PointerPtr.toCOpaquePointer(): COpaquePointer? = reinterpret<Pointer>().toCOpaquePointer()

/**
 * Converts a typed [PointerPtr] to a typed C pointer.
 *
 * This extension function allows converting the library's typed pointer representation for pointers
 * to a typed C pointer that can be used with Kotlin/Native C interop.
 *
 * @param T The C type that the pointer points to.
 * @return A typed C pointer that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun <reified T : CPointed> PointerPtr.toCPointer(): CPointer<CPointerVar<T>>? =
    reinterpret<Pointer>().toCPointer()

// IEEE-754 pointer conversions

/**
 * Converts a C pointer to a float variable to a typed [FloatPtr].
 *
 * This extension function allows converting a C pointer to a float variable
 * to the library's typed pointer representation for floats.
 *
 * @return A [FloatPtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<FloatVar>.toFloatPtr(): FloatPtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a double variable to a typed [DoublePtr].
 *
 * This extension function allows converting a C pointer to a double variable
 * to the library's typed pointer representation for doubles.
 *
 * @return A [DoublePtr] that points to the same memory address as this C pointer.
 */
@ExperimentalForeignApi
inline fun CPointer<DoubleVar>.toDoublePtr(): DoublePtr = toPointer().reinterpret()

/**
 * Converts a C pointer to a platform-specific floating-point variable to a typed [NFloatPtr].
 *
 * This extension function allows converting a C pointer to a platform-specific floating-point variable
 * to the library's typed pointer representation for platform-specific floating-point numbers.
 *
 * @return An [NFloatPtr] that points to the same memory address as this C pointer.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun CPointer<nfloat_tVar>.toNFloatPtr(): NFloatPtr = toPointer().reinterpret()

/**
 * Converts a typed [FloatPtr] to a C pointer to a float variable.
 *
 * This extension function allows converting the library's typed pointer representation for floats
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a float variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun FloatPtr.toCPointer(): CPointer<FloatVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [DoublePtr] to a C pointer to a double variable.
 *
 * This extension function allows converting the library's typed pointer representation for doubles
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a double variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@ExperimentalForeignApi
inline fun DoublePtr.toCPointer(): CPointer<DoubleVar>? = reinterpret<Pointer>().toCPointer()

/**
 * Converts a typed [NFloatPtr] to a C pointer to a platform-specific floating-point variable.
 *
 * This extension function allows converting the library's typed pointer representation for platform-specific floating-point numbers
 * to a C pointer that can be used with Kotlin/Native C interop.
 *
 * @return A C pointer to a platform-specific floating-point variable that points to the same memory address as this pointer,
 *         or null if the conversion fails.
 */
@OptIn(UnsafeNumber::class)
@ExperimentalForeignApi
inline fun NFloatPtr.toCPointer(): CPointer<nfloat_tVar>? = reinterpret<Pointer>().toCPointer()
