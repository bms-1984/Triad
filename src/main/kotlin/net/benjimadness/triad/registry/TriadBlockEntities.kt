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

package net.benjimadness.triad.registry

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.blockentity.BlockEntityRedstoneGrinder
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier

object TriadBlockEntities {
    val REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TriadMod.MODID)

    val REDSTONE_GRINDER_BLOCK_ENTITY_TYPE: BlockEntityType<BlockEntityRedstoneGrinder> by registerBlockEntity("redstone_grinder_block_entity") {
        BlockEntityType.Builder.of(::BlockEntityRedstoneGrinder, TriadBlocks.REDSTONE_GRINDER).build(null)
    }

    private fun <T : BlockEntityType<*>> registerBlockEntity(name: String, blockEntity: Supplier<T>) =
        REGISTRY.register(name, blockEntity)
}