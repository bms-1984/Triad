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

import net.benjimadness.triad.api.block.entity.AbstractGeneratorBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractTurbineBlockEntity
import net.benjimadness.triad.registry.TriadMenus
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth.clamp
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.SlotItemHandler

class TurbineMenu(
    id: Int, playerInventory: Inventory,
    private val pos: BlockPos
) : AbstractContainerMenu(TriadMenus.TURBINE_MENU_TYPE.get(), id) {
    private var progress = 0
    private var totalTime = 0
    private var power = 0
    private var totalPower = 0
    private var steam = 0
    private var totalSteam = 0

    init {
        val entity = playerInventory.player.level().getBlockEntity(pos)
        if (entity is AbstractTurbineBlockEntity) {
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
                override fun get(): Int = entity.getBurnTime()
                override fun set(t: Int) {
                    totalTime = t
                }
            })
            addDataSlot(object : DataSlot() { // power
                override fun get(): Int = entity.energyStorage.energyStored
                override fun set(p: Int) {
                    power = p
                }
            })
            addDataSlot(object : DataSlot() { // totalPower
                override fun get(): Int = entity.energyStorage.maxEnergyStored
                override fun set(t: Int) {
                    totalPower = t
                }
            })
            addDataSlot(object : DataSlot() { // steam
                override fun get(): Int = entity.steamTank.getFluidInTank(0).amount
                override fun set(s: Int) {
                    steam = s
                }
            })
            addDataSlot(object : DataSlot() { // totalSteam
                override fun get(): Int = entity.steamTank.getTankCapacity(0)
                override fun set(t: Int) {
                    totalSteam = t
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
            if ((0 until 37).contains(slotIndex)) {
                if ((0 until 27).contains(slotIndex)) {
                    if (!moveItemStackTo(slotStack, 28, 36, true))
                        return ItemStack.EMPTY
                } else if ((27 until 37).contains(slotIndex)) {
                    if (!moveItemStackTo(slotStack, 0, 27, true))
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
        else clamp(progress.toFloat() / totalTime.toFloat(), 0F, 1F)

    fun getPower(): Float =
        if (power == 0 || totalPower == 0) 0F
        else clamp(power.toFloat() / totalPower.toFloat(), 0F, 1F)

    fun getSteam(): Float =
        if (steam == 0 || totalSteam == 0) 0F
        else clamp(steam.toFloat() / totalSteam.toFloat(), 0F, 1F)

    fun getAbsolutePower() = power
    fun getAbsoluteSteam() = steam
    fun isPowered(): Boolean = progress > 0
}