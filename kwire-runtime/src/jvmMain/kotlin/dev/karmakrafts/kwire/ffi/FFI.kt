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
@file:JvmName("FFIImpl")
// @formatter:on

package dev.karmakrafts.kwire.ffi

import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.asVoidPtr
import dev.karmakrafts.kwire.ctype.toMemorySegment
import dev.karmakrafts.kwire.ctype.toNFloat
import dev.karmakrafts.kwire.ctype.toNInt
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.memory.Memory
import org.lwjgl.system.libffi.FFICIF
import org.lwjgl.system.libffi.FFIClosure
import org.lwjgl.system.libffi.LibFFI
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.experimental.ExperimentalTypeInference
import org.lwjgl.system.MemoryStack as LWJGLMemoryStack
import org.lwjgl.system.libffi.FFIType as LibFFIType
import java.lang.foreign.Linker as JvmLinker

private data class UpcallStub(
    val cif: FFICIF, val closure: FFIClosure
) : AutoCloseable {
    override fun close() {
        closure.close()
        cif.close()
    }
}

@OptIn(ExperimentalTypeInference::class)
internal object PanamaFFI : FFI {
    private val upcallStubs: HashMap<Function<*>, UpcallStub> = HashMap()

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::cleanup))
    }

    private fun cleanup() {
        for ((_, stub) in upcallStubs) {
            stub.close()
        }
    }

    fun FFIArgBuffer.toArray(): Array<Any> {
        var offset = 0.toNUInt()
        return Array(types.size) { index ->
            val type = types[index]
            val value: Any = when (type) {
                FFIType.BYTE, FFIType.UBYTE -> Memory.readByte(address + offset)
                FFIType.SHORT, FFIType.USHORT -> Memory.readShort(address + offset)
                FFIType.INT, FFIType.UINT -> Memory.readInt(address + offset)
                FFIType.LONG, FFIType.ULONG -> Memory.readLong(address + offset)
                FFIType.NINT, FFIType.NUINT -> Memory.readNInt(address + offset)
                FFIType.FLOAT -> Memory.readFloat(address + offset)
                FFIType.DOUBLE -> Memory.readDouble(address + offset)
                FFIType.NFLOAT -> Memory.readNFloat(address + offset)
                FFIType.PTR -> Memory.readPointer(address + offset).rawAddress.value
                else -> throw IllegalStateException("Cannot map FFI parameter type $type")
            }
            offset += type.size.toNUInt()
            value
        }
    }

    internal fun getHandle(address: Address, descriptor: FFIDescriptor, useSegments: Boolean = false): MethodHandle {
        return JvmLinker.nativeLinker()
            .downcallHandle(address.toMemorySegment(), descriptor.toFunctionDescriptor(useSegments))
    }

    private fun FFIType.toLibFFI(): LibFFIType = when (this) {
        FFIType.VOID -> LibFFI.ffi_type_void
        FFIType.BYTE -> LibFFI.ffi_type_sint8
        FFIType.SHORT -> LibFFI.ffi_type_sint16
        FFIType.INT -> LibFFI.ffi_type_sint32
        FFIType.LONG -> LibFFI.ffi_type_sint64
        FFIType.NINT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_sint32 else LibFFI.ffi_type_sint64
        FFIType.UBYTE -> LibFFI.ffi_type_uint8
        FFIType.USHORT -> LibFFI.ffi_type_uint16
        FFIType.UINT -> LibFFI.ffi_type_uint32
        FFIType.ULONG -> LibFFI.ffi_type_uint64
        FFIType.NUINT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_uint64 else LibFFI.ffi_type_uint64
        FFIType.FLOAT -> LibFFI.ffi_type_float
        FFIType.DOUBLE -> LibFFI.ffi_type_double
        FFIType.NFLOAT -> if (Address.SIZE_BYTES == 4) LibFFI.ffi_type_float else LibFFI.ffi_type_double
        FFIType.PTR -> LibFFI.ffi_type_pointer
    }

    private fun invokeUpcallStub(
        cifAddr: MemorySegment, ret: MemorySegment, args: MemorySegment, userData: MemorySegment
    ) {
        FFICIF.create(cifAddr.address())

    }

    private val invokeUpcallStubAddress: MemorySegment by lazy {
        val method = PanamaFFI::class.java.getDeclaredMethod("invokeUpcallStub")
        val handle = MethodHandles.lookup().unreflect(method)
        handle.bindTo(this) // Bind the function to this instance so it is properly callable
        val descriptor = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
        )
        JvmLinker.nativeLinker().upcallStub(handle, descriptor, Arena.global())
    }

    private fun getOrCreateUpcallStub(descriptor: FFIDescriptor, function: Function<*>): UpcallStub {
        return upcallStubs.getOrPut(function) { // @formatter:off
            LWJGLMemoryStack.stackPush().use { stackFrame ->
                val cif = FFICIF.create()
                val returnType = descriptor.returnType.toLibFFI()
                val parameterTypes = descriptor.parameterTypes.map { it.toLibFFI() }.toTypedArray()
                check(LibFFI.ffi_prep_cif(cif, LibFFI.FFI_DEFAULT_ABI, returnType, stackFrame.pointers(*parameterTypes)) == LibFFI.FFI_OK) {
                    "Could not initialize CIF for upcall stub closure"
                }

                val codeBuffer = stackFrame.mallocPointer(1)
                val closure = LibFFI.ffi_closure_alloc(FFIClosure.SIZEOF.toLong(), codeBuffer)
                check(closure != null) { "Could not allocate upcall stub closure memory" }
                LibFFI.ffi_prep_closure_loc(closure, cif, invokeUpcallStubAddress.address(), 0L, codeBuffer.get())

                UpcallStub(cif, closure)
            }
        } // @formatter:on
    }

    override fun createUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Unit): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createByteUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Byte): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createShortUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Short): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createIntUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Int): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createLongUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Long): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUByteUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UByte): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUShortUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UShort): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createUIntUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> UInt): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createULongUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> ULong): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createFloatUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Float): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun createDoubleUpcallStub(descriptor: FFIDescriptor, function: (FFIArgBuffer) -> Double): VoidPtr {
        TODO("Not yet implemented")
    }

    override fun call(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer) {
        getHandle(address, descriptor).invokeWithArguments(*args.toArray())
    }

    override fun callByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Byte {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Byte
    }

    override fun callShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Short {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Short
    }

    override fun callInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Int {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int
    }

    override fun callLong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Long {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long
    }

    override fun callNInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NInt {
        return if (Address.SIZE_BYTES == Int.SIZE_BYTES) {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toNInt()
        }
        else {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toNInt()
        }
    }

    override fun callUByte(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UByte {
        return (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Byte).toUByte()
    }

    override fun callUShort(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UShort {
        return (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Short).toUShort()
    }

    override fun callUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): UInt {
        return (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toUInt()
    }

    override fun callULong(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): ULong {
        return (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toULong()
    }

    override fun callNUInt(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NUInt {
        return if (Address.SIZE_BYTES == Int.SIZE_BYTES) {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toNUInt()
        }
        else {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toNUInt()
        }
    }

    override fun callFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Float {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Float
    }

    override fun callDouble(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): Double {
        return getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Double
    }

    override fun callNFloat(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): NFloat {
        return if (Address.SIZE_BYTES == Int.SIZE_BYTES) {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Float).toNFloat()
        }
        else {
            (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Double).toNFloat()
        }
    }

    override fun callPointer(address: Address, descriptor: FFIDescriptor, args: FFIArgBuffer): VoidPtr {
        return (getHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).asVoidPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = PanamaFFI