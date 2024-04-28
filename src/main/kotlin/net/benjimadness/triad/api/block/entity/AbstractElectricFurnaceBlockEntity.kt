package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class AbstractElectricFurnaceBlockEntity(capacity: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractElectricProcessorBlockEntity(capacity, type, pos, state) {
    companion object {
        private const val INPUT_SLOT = 0
        private const val OUTPUT_SLOT = 1
    }
    private val items = object : ItemStackHandler(2) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (slot == INPUT_SLOT && amount >= getStackInSlot(INPUT_SLOT).count) progress = 0
            return super.extractItem(slot, amount, simulate)
        }
    }
    val itemHandler: IItemHandler by lazy { items }
    override val check: RecipeManager.CachedCheck<Container, SmeltingRecipe> = RecipeManager.createCheck(RecipeType.SMELTING)

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Items", items.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Items")) items.deserializeNBT(registry, tag.getCompound("Items"))
    }

    override fun execute() {
        if (!hasLevel()) return
        val input = items.getStackInSlot(INPUT_SLOT)
        val result = items.getStackInSlot(OUTPUT_SLOT)
        val recipe = getRecipe() ?: return
        val resultItem = recipe.getResultItem(level!!.registryAccess())
        if ((result.item == resultItem.item && result.count <= (result.maxStackSize - resultItem.count)) || result.isEmpty) {
            outputMatch = true
            if (shouldRun()) {
                if (progress >= getTime()) {
                    if (result.isEmpty)
                        items.setStackInSlot(OUTPUT_SLOT, resultItem)
                    else result.grow(resultItem.count)
                    input.shrink(1)
                    progress = 0
                } else progress++
            }
        }
        else outputMatch = false
    }

    override fun getTime(): Int = getRecipe()?.cookingTime ?: 0

    override fun getRecipe(): SmeltingRecipe? {
        if (!hasLevel()) return null
        val recipe = check.getRecipeFor(RecipeWrapper(items), level!!)
        if (recipe.isPresent) return recipe.get().value
        return null
    }

    override fun shouldRun(): Boolean =
        super.shouldRun() && !items.getStackInSlot(0).isEmpty
}