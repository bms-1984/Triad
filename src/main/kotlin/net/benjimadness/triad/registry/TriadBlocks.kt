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
import net.benjimadness.triad.block.GrinderBlock
import net.benjimadness.triad.block.TriadBlockStateProperties
import net.benjimadness.triad.blockentity.RedstoneGrinderBlockEntity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier
import java.util.function.ToIntFunction

object TriadBlocks {
    val REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(TriadMod.MODID)

    val TIN_ORE by registerBlock("tin_ore") {
        Block(Properties.ofFullCopy(Blocks.STONE))
    }
    val DEEPSLATE_TIN_ORE by registerBlock("deepslate_tin_ore") {
        Block(Properties.ofFullCopy(Blocks.DEEPSLATE))
    }
    val RAW_TIN_BLOCK by registerBlock("raw_tin_block") {
        Block(Properties.ofFullCopy(Blocks.RAW_COPPER_BLOCK))
    }
    val TIN_BLOCK by registerBlock("tin_block") {
        Block(Properties.ofFullCopy(Blocks.COPPER_BLOCK).strength(2F, 6F))
    }
    val BRONZE_BLOCK by registerBlock("bronze_block") {
        Block(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4F, 6F))
    }
    val STEEL_BLOCK by registerBlock("steel_block") {
        Block(Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(6F, 8F))
    }
    val REDSTONE_GRINDER by registerBlock("redstone_grinder") {
        GrinderBlock(Properties.ofFullCopy(Blocks.STONE).lightLevel(calculateLightLevel(13, TriadBlockStateProperties.POWERED)),
            RedstoneGrinderBlockEntity::class)
    }

    private fun <T : Block> registerBlock(name: String, block: Supplier<T>): DeferredBlock<T> {
        val ret = REGISTRY.register(name, block)
        TriadItems.REGISTRY.registerSimpleBlockItem(name, ret)
        return ret
    }

    private fun calculateLightLevel(level: Int, prop: BooleanProperty) = { state: BlockState ->
        if (state.getValue(prop)) level else 0
    }
}