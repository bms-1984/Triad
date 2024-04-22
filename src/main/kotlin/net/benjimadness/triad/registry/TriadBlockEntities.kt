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
import net.benjimadness.triad.block.entity.BronzeItemBoilerBlockEntity
import net.benjimadness.triad.block.entity.BronzeTurbineBlockEntity
import net.benjimadness.triad.block.entity.RedstoneGrinderBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier

object TriadBlockEntities {
    val REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TriadMod.MODID)

    val REDSTONE_GRINDER_BLOCK_ENTITY_TYPE: BlockEntityType<RedstoneGrinderBlockEntity> by registerBlockEntity("redstone_grinder_block_entity") {
        BlockEntityType.Builder.of(::RedstoneGrinderBlockEntity, TriadBlocks.REDSTONE_GRINDER).build(null)
    }
    val BRONZE_ITEM_BOILER_BLOCK_ENTITY_TYPE: BlockEntityType<BronzeItemBoilerBlockEntity> by registerBlockEntity("bronze_item_boiler_block_entity") {
        BlockEntityType.Builder.of(::BronzeItemBoilerBlockEntity, TriadBlocks.BRONZE_ITEM_BOILER).build(null)
    }
    val BRONZE_TURBINE_BLOCK_ENTITY_TYPE: BlockEntityType<BronzeTurbineBlockEntity> by registerBlockEntity("bronze_turbine_block_entity") {
        BlockEntityType.Builder.of(::BronzeTurbineBlockEntity, TriadBlocks.BRONZE_TURBINE).build(null)
    }

    private fun <T : BlockEntityType<*>> registerBlockEntity(name: String, blockEntity: Supplier<T>) =
        REGISTRY.register(name, blockEntity)
}