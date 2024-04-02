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

package net.benjimadness.triad.event

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.config.TriadConfig
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.animal.Bee
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.level.ChunkEvent

@Mod.EventBusSubscriber(modid = TriadMod.MODID)
object TriadEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onEntityJoinLevel(event: EntityJoinLevelEvent) {
        if (TriadConfig.disableVanillaBees && event.entity is Bee) event.isCanceled = true
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onChunkLoad(event: ChunkEvent.Load) {
        val chunk = event.chunk
        if (TriadConfig.disableVanillaBees)
            if (event.isNewChunk)
                for (x in 0 until 16)
                    for (z in 0 until 16)
                        for (y in 0 until chunk.height) {
                            val pos = BlockPos(x, y, z)
                            if (chunk.getBlockState(pos).block == Blocks.BEE_NEST)
                                chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), true)
                        }
    }
}
