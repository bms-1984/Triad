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

package net.benjimadness.triad.blockentity

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.block.BlockGrinder
import net.benjimadness.triad.recipe.GrinderRecipe
import net.benjimadness.triad.registry.TriadRecipes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class BlockEntityGrinder(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(
    type, pos, state
) {
    private val items = object : ItemStackHandler(3) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
    }
    val itemHandler: IItemHandler by lazy { items }
    private var isOn = false
    var progress = 0
    private val check = RecipeManager.createCheck(TriadRecipes.GRINDER_RECIPE_TYPE)
    private val random = RandomSource.create()

    fun serverTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntityGrinder) {
        var changed = false
        if (blockEntity.isPowered(level) && blockEntity.items.getStackInSlot(1)
                .`is`(ItemTags.create(ResourceLocation(TriadMod.MODID, "blades")))
        ) {
            if (blockEntity.items.getStackInSlot(0) != ItemStack.EMPTY) {
                val input = blockEntity.items.getStackInSlot(0)
                val blade = blockEntity.items.getStackInSlot(1)
                val result = blockEntity.items.getStackInSlot(2)
                val holder = blockEntity.check.getRecipeFor(RecipeWrapper(items), level)
                if (holder.isPresent) {
                    val recipe = holder.get().value as GrinderRecipe
                    if ((result.item == recipe.output.item && result.count <= (result.maxStackSize - recipe.output.count)) || result.isEmpty) {
                        if (!isOn && progress == 0) {
                            isOn = true
                            changed = true
                        } else if ((level.gameTime % 20).toInt() == 0) {
                            if (progress >= getTime()) {
                                isOn = false
                                if (result.isEmpty)
                                    blockEntity.items.setStackInSlot(
                                        2,
                                        recipe.output.copyWithCount(recipe.output.count)
                                    )
                                else result.grow(recipe.output.count)
                                input.shrink(1)
                                if (blade.hurt(1, random, null))
                                    blade.shrink(1)
                                progress = 0
                            } else progress++
                            changed = true
                        }
                    }
                }
            }
        }
        if (!blockEntity.isPowered(level)) {
            isOn = false
            progress = 0
            changed = true
        }
        if (changed) {
            level.setBlock(pos, state.setValue(BlockGrinder.LIT, isOn), 0)
            setChanged(level, pos, level.getBlockState(pos))
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putBoolean("IsOn", isOn)
        tag.putInt("Progress", progress)
        tag.put("Items", items.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("IsOn")) isOn = tag.getBoolean("IsOn")
        if (tag.contains("Progress")) progress = tag.getInt("Progress")
        if (tag.contains("Items")) items.deserializeNBT(tag.getCompound("Items"))
    }

    abstract fun getTime(): Int
    abstract fun isPowered(level: Level): Boolean
}