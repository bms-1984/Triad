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

package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractGrinderBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class RedstoneGrinderBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractGrinderBlockEntity(TriadBlockEntities.REDSTONE_GRINDER_BLOCK_ENTITY_TYPE, pos, state) {
    override fun getTime(): Int = 240
    override fun isPowered(): Boolean = level?.hasNeighborSignal(blockPos) == true
}