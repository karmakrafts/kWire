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

import com.v7878.foreign.Arena
import com.v7878.foreign.FunctionDescriptor
import com.v7878.foreign.MemorySegment
import com.v7878.foreign.ValueLayout
import dev.karmakrafts.kwire.ShutdownHandler
import dev.karmakrafts.kwire.ctype.CVoid
import dev.karmakrafts.kwire.ctype.Const
import dev.karmakrafts.kwire.ctype.NFloat
import dev.karmakrafts.kwire.ctype.NInt
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.Ptr
import dev.karmakrafts.kwire.ctype.asPtr
import dev.karmakrafts.kwire.ctype.nullptr
import dev.karmakrafts.kwire.ctype.toMemorySegment
import dev.karmakrafts.kwire.ctype.toNFloat
import dev.karmakrafts.kwire.ctype.toNInt
import dev.karmakrafts.kwire.ctype.toNUInt
import dev.karmakrafts.kwire.ctype.toPtr
import dev.karmakrafts.kwire.memory.Memory
import dev.karmakrafts.kwire.memory.MemoryStack
import dev.karmakrafts.kwire.memory.StableRef
import dev.karmakrafts.kwire.memory.pointer
import dev.karmakrafts.kwire.memory.pointers
import dev.karmakrafts.kwire.util.AndroidNativePlatform
import dev.karmakrafts.kwire.util.getFFIError
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.experimental.ExperimentalTypeInference
import com.v7878.foreign.Linker as JvmLinker

private data class UpcallStub( // @formatter:off
    val cif: Ptr<CVoid>,
    val closure: Ptr<CVoid>,
    val address: Ptr<CVoid>,
    val trampolineRef: StableRef<(FFIArgBuffer) -> Unit>
) : AutoCloseable { // @formatter:on
    override fun close() {
        LibFFI.ffi_closure_free(closure)
        LibFFI.ffi_cif_free(cif)
        trampolineRef.dispose()
    }
}

@OptIn(ExperimentalTypeInference::class)
internal object PanamaFFI : FFI {
    private val upcallStubs: HashMap<Function<*>, UpcallStub> = HashMap()

    init {
        ShutdownHandler.register(AutoCloseable(::cleanup))
    }

    private fun cleanup() {
        for ((_, stub) in upcallStubs) {
            stub.close()
        }
    }

    private fun FFIArgBuffer.toArray(): Array<Any> {
        var offset = 0.toNUInt()
        return Array(types.size) { index ->
            val type = types[index]
            val readAddress = (address.asNUInt() + offset).asPtr<CVoid>()
            val value: Any = when (type) {
                FFIType.BYTE, FFIType.UBYTE -> Memory.readByte(readAddress)
                FFIType.SHORT, FFIType.USHORT -> Memory.readShort(readAddress)
                FFIType.INT, FFIType.UINT -> Memory.readInt(readAddress)
                FFIType.LONG, FFIType.ULONG -> Memory.readLong(readAddress)
                FFIType.NINT, FFIType.NUINT -> Memory.readNInt(readAddress)
                FFIType.FLOAT -> Memory.readFloat(readAddress)
                FFIType.DOUBLE -> Memory.readDouble(readAddress)
                FFIType.NFLOAT -> Memory.readNFloat(readAddress)
                FFIType.PTR -> Memory.readPointer(readAddress).toMemorySegment()
                else -> throw IllegalStateException("Cannot map FFI parameter type $type")
            }
            offset += type.size.toNUInt()
            value
        }
    }

    internal fun getDowncallHandle(
        address: Ptr<*>, descriptor: FFIDescriptor, useSegments: Boolean = true
    ): MethodHandle {
        return JvmLinker.nativeLinker()
            .downcallHandle(address.toMemorySegment(), descriptor.toFunctionDescriptor(useSegments))
    }

    @JvmStatic
    @Suppress("UNUSED", "UNCHECKED_CAST") // This is invoked through a MethodHandle
    private fun invokeUpcallStub( // @formatter:off
        cif: MemorySegment,
        ret: MemorySegment,
        args: MemorySegment,
        userData: MemorySegment
    ) { // @formatter:on
        val trampoline = StableRef.from<(FFIArgBuffer) -> Unit>(userData.toPtr()).value
        val argBuffer = FFIArgBuffer.acquire()
        trampoline(argBuffer)
        val returnType = AndroidNativePlatform.getFFICIFReturnType(cif.address()).asPtr<CVoid>().toFFI()
        argBuffer.rewindToLast() // Rewind to last to copy result back to target
        Memory.copy(argBuffer.currentAddress, ret.toPtr(), returnType.size.toNUInt()) // Copy return value to target
        argBuffer.release()
    }

    private val invokeUpcallStub: MemorySegment by lazy {
        val method = PanamaFFI::class.java.getDeclaredMethod("invokeUpcallStub", *(0..<4).map {
            MemorySegment::class.java
        }.toTypedArray())
        val handle = MethodHandles.lookup().unreflect(method)
        val descriptor = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS, // cif
            ValueLayout.ADDRESS, // ret
            ValueLayout.ADDRESS, // args
            ValueLayout.ADDRESS  // userData
        )
        JvmLinker.nativeLinker().upcallStub(handle, descriptor, Arena.global())
    }

    override fun createUpcallStub( // @formatter:off
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        function: (FFIArgBuffer) -> Unit
    ): @Const Ptr<CVoid> { // @formatter:on
        return upcallStubs.getOrPut(function) {
            MemoryStack.withStackFrame { stackFrame ->
                val cif = LibFFI.ffi_cif_alloc()

                val returnType = descriptor.returnType.toLibFFI()
                val parameterTypes = descriptor.parameterTypes.map { it.toLibFFI() }.toTypedArray()

                var result = LibFFI.ffi_prep_cif( // @formatter:off
                    cif,
                    callingConvention.toLibFFI(),
                    parameterTypes.size,
                    returnType,
                    stackFrame.pointers(*parameterTypes)
                ) // @formatter:on
                check(result == LibFFI.FFI_OK) {
                    "Could not initialize CIF for upcall stub closure: ${getFFIError(result)}"
                }

                val codeBuffer = stackFrame.pointer(nullptr<CVoid>())
                val closure = LibFFI.ffi_closure_alloc(codeBuffer)
                check(closure.isNotNull()) { "Could not allocate upcall stub closure memory: ${getFFIError(result)}" }
                val codePtr = Memory.readPointer(codeBuffer)
                val trampolineRef = StableRef.create(function)

                result = LibFFI.ffi_prep_closure_loc( // @formatter:off
                    closure,
                    cif,
                    invokeUpcallStub.toPtr(),
                    trampolineRef.address,
                    codePtr
                ) // @formatter:on
                check(result == LibFFI.FFI_OK) {
                    "Could not prepare FFI closure for upcall stub: ${getFFIError(result)}"
                }

                UpcallStub(cif, closure, codePtr, trampolineRef)
            }
        }.address
    }

    override fun call( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ) { // @formatter:on
        getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray())
    }

    override fun callByte( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Byte { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Byte
    }

    override fun callShort( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Short { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Short
    }

    override fun callInt( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Int { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int
    }

    override fun callLong( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Long { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long
    }

    override fun callNInt( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NInt { // @formatter:on
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toNInt()
        }
        else {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toNInt()
        }
    }

    override fun callUByte( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UByte { // @formatter:on
        return (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Byte).toUByte()
    }

    override fun callUShort( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UShort { // @formatter:on
        return (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Short).toUShort()
    }

    override fun callUInt( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): UInt { // @formatter:on
        return (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toUInt()
    }

    override fun callULong( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): ULong { // @formatter:on
        return (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toULong()
    }

    override fun callNUInt( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NUInt { // @formatter:on
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Int).toNUInt()
        }
        else {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Long).toNUInt()
        }
    }

    override fun callFloat( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Float { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Float
    }

    override fun callDouble( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Double { // @formatter:on
        return getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Double
    }

    override fun callNFloat( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): NFloat { // @formatter:on
        return if (Ptr.SIZE_BYTES == Int.SIZE_BYTES) {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Float).toNFloat()
        }
        else {
            (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as Double).toNFloat()
        }
    }

    override fun callPointer( // @formatter:off
        address: @Const Ptr<*>,
        descriptor: FFIDescriptor,
        callingConvention: CallingConvention,
        args: FFIArgBuffer
    ): Ptr<CVoid> { // @formatter:on
        return (getDowncallHandle(address, descriptor).invokeWithArguments(*args.toArray()) as MemorySegment).toPtr()
    }
}

internal actual fun getPlatformFFI(): FFI = PanamaFFI