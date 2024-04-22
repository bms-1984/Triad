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

package net.benjimadness.triad.block

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.LeverPositions
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.block.entity.AbstractMachineBlockEntity
import net.benjimadness.triad.gui.GrinderMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import kotlin.reflect.KClass

class GrinderBlock(properties: Properties, private val blockEntity: KClass<out AbstractMachineBlockEntity>) : AbstractMachineBlock(properties, blockEntity), EntityBlock {
    companion object {
        private val POWERED = TriadBlockStateProperties.POWERED
        private val BLADE = TriadBlockStateProperties.BLADE
    }
    private val randomSource = RandomSource.create()

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(BLADE, Blades.NONE)
            .setValue(RUNNING, false)
            .setValue(LEVER, LeverPositions.NONE))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, POWERED, BLADE, RUNNING, LEVER)
    }


    @Deprecated("Deprecated in Java")
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> GrinderMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.grinder_menu", "Grinder")
        )

    enum class Blades : StringRepresentable {
        NONE, BRONZE, STEEL;
        override fun getSerializedName(): String = name.lowercase()
        override fun toString(): String = serializedName
        fun getComponent(): Component = Component.translatableWithFallback(
            "${TriadMod.MODID}.message.blade.${serializedName}",
            serializedName.replaceFirstChar { it.uppercase() })
    }
}