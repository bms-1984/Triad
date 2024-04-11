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
import net.benjimadness.triad.block.GrinderBlock
import net.benjimadness.triad.block.TriadBlockStateProperties
import net.benjimadness.triad.item.ReusableItem
import net.benjimadness.triad.recipe.GrinderRecipe
import net.benjimadness.triad.registry.TriadRecipes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class GrinderBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : BlockEntity(
    type, pos, state
) {
    companion object {
        private const val INPUT_SLOT = 0
        private const val BLADE_SLOT = 1
        private const val OUTPUT_SLOT = 2
    }
    private val items = object : ItemStackHandler(3) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == INPUT_SLOT && amount >= getStackInSlot(INPUT_SLOT).count) progress = 0
            return super.extractItem(slot, amount, simulate)
        }
    }
    val itemHandler: IItemHandler by lazy { items }
    private var isOn = false
    private var blade = GrinderBlock.Blades.NONE
    private var isRunning = false
    var progress = 0
    private val check = RecipeManager.createCheck(TriadRecipes.GRINDER_RECIPE_TYPE)
    private val random = RandomSource.create()

    open fun serverTick(level: Level, pos: BlockPos, blockEntity: GrinderBlockEntity) {
        val input = blockEntity.items.getStackInSlot(INPUT_SLOT)
        val bladeStack = blockEntity.items.getStackInSlot(BLADE_SLOT)
        val result = blockEntity.items.getStackInSlot(OUTPUT_SLOT)
        if (bladeStack.`is`(ItemTags.create(ResourceLocation(TriadMod.MODID, "blades")))) {
            blade = TriadBlockStateProperties.BLADE.getValue((bladeStack.item as ReusableItem).materialName).get()
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.BLADE, blade), Block.UPDATE_CLIENTS)
        }
        else {
            blade = GrinderBlock.Blades.NONE
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.BLADE, blade), Block.UPDATE_CLIENTS)
        }
        if (blockEntity.isPowered(level)) {
            isOn = true
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.POWERED, true), Block.UPDATE_CLIENTS)
        }
        else {
            isOn = false
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.POWERED, false), Block.UPDATE_CLIENTS)
        }
        if (isRunning && (!isOn || blade == GrinderBlock.Blades.NONE || input.isEmpty ||
                    result.count >= result.maxStackSize)) {
            isRunning = false
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, false), Block.UPDATE_CLIENTS)
        }
        if (isOn && blade != GrinderBlock.Blades.NONE && input != ItemStack.EMPTY) {
            val holder = blockEntity.check.getRecipeFor(RecipeWrapper(items), level)
            if (holder.isPresent) {
                val recipe = holder.get().value as GrinderRecipe
                if ((result.item == recipe.output.item && result.count <= (result.maxStackSize - recipe.output.count)) || result.isEmpty) {
                    if (!isRunning) {
                        isRunning = true
                        level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.RUNNING, true), Block.UPDATE_CLIENTS)
                    }
                    else if ((level.gameTime % 20).toInt() == 0) {
                        if (progress >= getTime()) {
                            if (result.isEmpty)
                                blockEntity.items.setStackInSlot(OUTPUT_SLOT, recipe.output.copyWithCount(recipe.output.count))
                            else result.grow(recipe.output.count)
                            input.shrink(1)
                            if (bladeStack.hurt(BLADE_SLOT, random, null))
                                bladeStack.shrink(1)
                            progress = 0
                        }
                        else progress++
                    }
                }
            }
        }
        setChanged(level, pos, level.getBlockState(pos))
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("Progress", progress)
        tag.put("Items", items.serializeNBT())
        saveClientData(tag)
    }

    private fun saveClientData(tag: CompoundTag) {
        tag.putBoolean("IsOn", isOn)
        tag.putBoolean("IsRunning", isRunning)
        tag.putString("Blade", blade.serializedName)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Progress")) progress = tag.getInt("Progress")
        if (tag.contains("Items")) items.deserializeNBT(tag.getCompound("Items"))
        loadClientData(tag)
    }

    private fun loadClientData(tag: CompoundTag) {
        if (tag.contains("IsOn")) isOn = tag.getBoolean("IsOn")
        if (tag.contains("IsRunning")) isRunning = tag.getBoolean("IsRunning")
        if (tag.contains("Blade")) blade = GrinderBlock.Blades.valueOf(tag.getString("Blade").uppercase())
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        saveClientData(tag)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        loadClientData(tag)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? =
        ClientboundBlockEntityDataPacket.create(this)

    override fun onDataPacket(network: Connection, packet: ClientboundBlockEntityDataPacket) {
        if (packet.tag != null)
            handleUpdateTag(packet.tag!!)
    }

    abstract fun getTime(): Int
    abstract fun isPowered(level: Level): Boolean
}