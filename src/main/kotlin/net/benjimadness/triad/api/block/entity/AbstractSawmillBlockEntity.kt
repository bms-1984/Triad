package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.recipe.SawmillRecipe
import net.benjimadness.triad.registry.TriadRecipes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class AbstractSawmillBlockEntity(capacity: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
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
    override val check: RecipeManager.CachedCheck<Container, SawmillRecipe> = RecipeManager.createCheck(TriadRecipes.SAWMILL_RECIPE_TYPE)

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

    override fun getTime(): Int = 200

    override fun getRecipe(): SawmillRecipe? {
        if (!hasLevel()) return null
        val recipe = check.getRecipeFor(RecipeWrapper(items), level!!)
        if (recipe.isPresent) return recipe.get().value
        return null
    }

    override fun shouldRun(): Boolean =
        super.shouldRun() && !items.getStackInSlot(0).isEmpty
}