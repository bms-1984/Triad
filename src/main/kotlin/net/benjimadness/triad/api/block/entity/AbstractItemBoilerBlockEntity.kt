package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler

abstract class AbstractItemBoilerBlockEntity(capacity: Int, transfer: Int, gen: Int, type: BlockEntityType<*>, private val pos: BlockPos, state: BlockState) :
    AbstractBoilerBlockEntity(capacity, transfer, gen, type, pos, state) {

    companion object {
        const val INPUT_SLOT = 0
    }

    private val items = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (amount >= getStackInSlot(INPUT_SLOT).count) progress = 0
            return super.extractItem(slot, amount, simulate)
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
            if (!isFuel(stack)) ItemStack.EMPTY
            else  super.insertItem(slot, stack, simulate)
    }
    val itemHandler: IItemHandler by lazy { items }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Items", items.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Items")) items.deserializeNBT(tag.getCompound("Items"))
    }

    override fun useFuel() {
        itemHandler.extractItem(INPUT_SLOT, 1, false)
    }
    override fun getBurnTime(): Int = itemHandler.getStackInSlot(INPUT_SLOT).getBurnTime(RecipeType.SMELTING)
    override fun isFueled(): Boolean = isFuel(itemHandler.getStackInSlot(INPUT_SLOT))
    fun isFuel(stack: ItemStack): Boolean = stack.getBurnTime(RecipeType.SMELTING) > 0
}