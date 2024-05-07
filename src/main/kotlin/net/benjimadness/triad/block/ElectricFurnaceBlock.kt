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
import net.benjimadness.triad.gui.menu.ElectricFurnaceMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KClass

class ElectricFurnaceBlock(properties: Properties, blockEntity: KClass<out AbstractMachineBlockEntity>) : AbstractMachineBlock(properties, blockEntity) {
    @Deprecated("Deprecated in Java")
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> ElectricFurnaceMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.furnace_menu", "Furnace")
        )

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(RUNNING)) {
            val d0 = pos.x.toDouble() + 0.5
            val d1 = pos.y.toDouble()
            val d2 = pos.z.toDouble() + 0.5
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(
                    d0,
                    d1,
                    d2,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f,
                    false
                )
            }
            val direction = state.getValue(FACING)
            val axis = direction.axis
            val d4 = random.nextDouble() * 0.6 - 0.3
            val d5 = if (axis === Direction.Axis.X) direction.stepX.toDouble() * 0.52 else d4
            val d6 = random.nextDouble() * 6.0 / 16.0
            val d7 = if (axis === Direction.Axis.Z) direction.stepZ.toDouble() * 0.52 else d4
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
        }
    }
}