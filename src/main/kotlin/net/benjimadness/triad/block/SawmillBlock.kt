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
import net.benjimadness.triad.api.block.entity.AbstractMachineBlockEntity
import net.benjimadness.triad.gui.menu.SawmillMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KClass

class SawmillBlock(properties: Properties, blockEntity: KClass<out AbstractMachineBlockEntity>) : AbstractMachineBlock(properties, blockEntity) {
    @Deprecated("Deprecated in Java")
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> SawmillMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.sawmill_menu", "Sawmill")
        )
}