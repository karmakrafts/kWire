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

package dev.karmakrafts.kwire.ctype

import dev.karmakrafts.kwire.KWireCompilerApi
import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException

/**
 * A marker interface which denotes the type of a C function
 * when defining a [Ptr] type.
 * For example:
 * ```
 * typealias MyFunction = @Const @CDecl Ptr<CFn<() -> CVoid>>
 * ```
 */
@KWireCompilerApi
sealed interface CFn<F : Function<*>>

// This overloads T.ref() in Ptr.kt so we can slip in the CFn
@ConstCallable
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.ref(): @Const @CDecl Ptr<CFn<F>> = throw KWirePluginNotAppliedException()

@ConstCallable
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refStdCall(): @Const @StdCall Ptr<CFn<F>> = throw KWirePluginNotAppliedException()

@ConstCallable
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refThisCall(): @Const @ThisCall Ptr<CFn<F>> = throw KWirePluginNotAppliedException()

@ConstCallable
@KWireIntrinsic(KWireIntrinsic.Type.PTR_REF)
fun <F : Function<*>> F.refFastCall(): @Const @FastCall Ptr<CFn<F>> = throw KWirePluginNotAppliedException()

@ConstCallable
@KWireIntrinsic(KWireIntrinsic.Type.PTR_INVOKE)
operator fun <R, F : Function<R>> Ptr<CFn<F>>.invoke(vararg args: Any?): R = throw KWirePluginNotAppliedException()