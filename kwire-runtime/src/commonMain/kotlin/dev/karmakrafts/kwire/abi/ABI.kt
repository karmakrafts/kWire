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

package dev.karmakrafts.kwire.abi

import dev.karmakrafts.kwire.KWireIntrinsic
import dev.karmakrafts.kwire.KWirePluginNotAppliedException

object ABI {
    /**
     * Allows accessing the current modules [ModuleData] that is
     * created by the kWire compiler.
     * This allows retrieving dependency information and symbol
     * table data among other things.
     *
     * @return The current modules [ModuleData] instance.
     *  The returned instance is initialized lazily.
     */
    @KWireIntrinsic(KWireIntrinsic.Type.ABI_GET_MODULE_DATA)
    fun getModuleData(): ModuleData = throw KWirePluginNotAppliedException()

    /**
     * The symbol table of the current module, initialized lazily
     * on first property access.
     */
    val symbolTable: SymbolTable by lazy { getModuleData().loadAllSymbolTables() }
}