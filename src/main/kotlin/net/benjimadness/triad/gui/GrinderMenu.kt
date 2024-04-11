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

package net.benjimadness.triad.gui

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.blockentity.AbstractGrinderBlockEntity
import net.benjimadness.triad.registry.TriadMenus
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler

class GrinderMenu(
    id: Int, playerInventory: Inventory,
    private val pos: BlockPos
) : AbstractContainerMenu(TriadMenus.GRINDER_MENU_TYPE.get(), id) {
    private var progress = 0
    private var totalTime = 0

    init {
        val entity = playerInventory.player.level().getBlockEntity(pos)
        if (entity is AbstractGrinderBlockEntity) {
            addSlot(SlotItemHandler(entity.itemHandler, 0, 56, 17)) // input
            addSlot(SlotItemHandler(entity.itemHandler, 1, 56, 53)) // blade
            addSlot(SlotItemHandler(entity.itemHandler, 2, 116, 35)) // result
            // player inventory
            for (y in 0 until 3) {
                for (x in 0 until 9) {
                    this.addSlot(Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18))
                }
            }
            // hotbar
            for (x in 0 until 9) {
                this.addSlot(Slot(playerInventory, x, 8 + x * 18, 142))
            }
            addDataSlot(object : DataSlot() { // progress
                override fun get(): Int = entity.progress
                override fun set(p: Int) {
                    progress = p
                }
            })
            addDataSlot(object : DataSlot() { // totalTime
                override fun get(): Int = entity.getTime()
                override fun set(t: Int) {
                    totalTime = t
                }
            })
        }
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = slots[slotIndex]
        if (slot.hasItem()) {
            val slotStack = slot.item
            stack = slotStack.copy()
            if ((0..2).contains(slotIndex)) {
                if (!moveItemStackTo(slotStack, 3, 39, true))
                    return ItemStack.EMPTY
                slot.onQuickCraft(slotStack, stack)
            } else if ((3..39).contains(slotIndex)) {
                if (slot.item.`is`(ItemTags.create(ResourceLocation(TriadMod.MODID, "blades")))) {
                    if (!moveItemStackTo(slotStack, 1, 2, false))
                        return ItemStack.EMPTY
                } else if (
                    slot.item.`is`(ItemTags.create(ResourceLocation("forge", "ingots"))) ||
                    slot.item.`is`(ItemTags.create(ResourceLocation("forge", "ores"))) ||
                    slot.item.`is`(ItemTags.create(ResourceLocation("forge", "raw_materials")))
                ) {
                    if (!moveItemStackTo(slotStack, 0, 1, false))
                        return ItemStack.EMPTY
                } else if ((3..29).contains(slotIndex)) {
                    if (!moveItemStackTo(slotStack, 30, 39, true))
                        return ItemStack.EMPTY
                } else if ((30..39).contains(slotIndex)) {
                    if (!moveItemStackTo(slotStack, 3, 30, true))
                        return ItemStack.EMPTY
                } else return ItemStack.EMPTY
            }
            if (slotStack.isEmpty)
                slot.set(ItemStack.EMPTY)
            else
                slot.setChanged()
            if (slotStack.count == stack.count)
                return ItemStack.EMPTY
            slot.onTake(player, slotStack)
        }
        return stack
    }

    override fun stillValid(player: Player): Boolean =
        stillValid(ContainerLevelAccess.create(player.level(), pos), player, player.level().getBlockState(pos).block)

    fun getProgress(): Float =
        if (progress == 0 || totalTime == 0) 0F
        else Mth.clamp(progress.toFloat() / totalTime.toFloat(), 0F, 1F)
}