/*
 *     Triad, a tech mod for Minecraft
 *     Copyright (C) 2024  Ben M. Sutter
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.benjimadness.triad.config

import net.benjimadness.triad.TriadMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec

@Mod.EventBusSubscriber(modid = TriadMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
object TriadConfig {
    private val BUILDER = ModConfigSpec.Builder()
    private val DISABLE_VANIlLA_BEES: ModConfigSpec.BooleanValue = BUILDER
        .comment("Whether or not to disable vanilla bees")
        .define("disableVanillaBees", true)
    var disableVanillaBees: Boolean = true
    val SPEC: ModConfigSpec = BUILDER.build()

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        disableVanillaBees = DISABLE_VANIlLA_BEES.get()
    }
}
