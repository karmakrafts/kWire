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

package dev.karmakrafts.kwire.memory

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.ShutdownHandler
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.toNUInt
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KWireCompilerApi
class MemoryStack private constructor() : Allocator {
    @KWireCompilerApi
    companion object {
        val stackSize: NUInt = 8192U.toNUInt()
        val stackAlignment: NUInt = Memory.defaultAlignment
        private val headerSize: NUInt = Address.SIZE_BYTES.toNUInt()
        private val instance: ThreadLocalRef<MemoryStack> = ThreadLocalRef()

        @KWireCompilerApi
        fun get(): MemoryStack {
            var stack = instance.value
            if (stack == null) {
                stack = MemoryStack()
                instance.value = stack
            }
            return stack
        }

        @OptIn(ExperimentalContracts::class)
        inline fun <reified R> withStackFrame(block: (MemoryStack) -> R): R {
            contract {
                callsInPlace(block, InvocationKind.EXACTLY_ONCE)
            }
            return get().withStackFrame(block)
        }
    }

    private val address: VoidPtr = Memory.allocate(stackSize, stackAlignment)
    private val frames: ArrayList<VoidPtr> = ArrayList()
    var frameAddress: VoidPtr = address
        private set

    init {
        ShutdownHandler.register(AutoCloseable(::free))
    }

    override fun allocate(size: NUInt, alignment: NUInt): VoidPtr {
        val alignedSize = Memory.align(size, alignment)
        val address = frameAddress
        frameAddress = frameAddress + alignedSize
        return address.align(alignment)
    }

    override fun reallocate(address: Address, size: NUInt, alignment: NUInt): VoidPtr {
        return allocate(size, alignment)
    }

    override fun free(address: Address) {}

    @KWireCompilerApi
    fun push(): MemoryStack {
        frames.add(frameAddress)
        return this
    }

    @KWireCompilerApi
    fun pop(): MemoryStack {
        frameAddress = frames.removeLast()
        return this
    }

    internal fun free() {
        Memory.free(address)
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <reified R> withStackFrame(block: (MemoryStack) -> R): R {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return try {
            push()
            block(this)
        }
        finally {
            pop()
        }
    }
}