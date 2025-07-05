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
import dev.karmakrafts.kwire.ShutdownHandler
import dev.karmakrafts.kwire.ctype.Address
import dev.karmakrafts.kwire.ctype.NUInt
import dev.karmakrafts.kwire.ctype.VoidPtr
import dev.karmakrafts.kwire.ctype.minus
import dev.karmakrafts.kwire.ctype.toNUInt

class MemoryStack private constructor() : Allocator {
    companion object {
        val stackSize: NUInt = 8192U.toNUInt()
        val stackAlignment: NUInt = Memory.defaultAlignment
        private val headerSize: NUInt = Address.SIZE_BYTES.toNUInt()
        private val instance: ThreadLocalRef<MemoryStack> = ThreadLocalRef()

        fun get(): MemoryStack {
            var stack = instance.value
            if (stack == null) {
                stack = MemoryStack()
                instance.value = stack
            }
            return stack
        }

        inline fun <reified R> withStackFrame(block: (MemoryStack) -> R): R {
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
        val alignedSize = Memory.align(size + headerSize, alignment)
        val address = frameAddress.align(alignment)
        Memory.writeNUInt(address, size)
        frameAddress += alignedSize
        return address + headerSize
    }

    override fun reallocate(address: Address, size: NUInt, alignment: NUInt): VoidPtr {
        val oldSize = Memory.readNUInt(address - headerSize)
        val newAddress = allocate(size, alignment)
        Memory.copy(address, newAddress, oldSize)
        return newAddress
    }

    override fun free(address: Address) {}

    fun push(): MemoryStack {
        frames.add(frameAddress)
        return this
    }

    fun pop(): MemoryStack {
        frameAddress = frames.removeFirst()
        return this
    }

    internal fun free() {
        Memory.free(address)
    }

    inline fun <reified R> withStackFrame(block: (MemoryStack) -> R): R {
        return try {
            push()
            block(this)
        }
        finally {
            pop()
        }
    }
}