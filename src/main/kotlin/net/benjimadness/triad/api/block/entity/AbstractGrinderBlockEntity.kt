package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.block.GrinderBlock
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.item.ReusableItem
import net.benjimadness.triad.recipe.GrinderRecipe
import net.benjimadness.triad.registry.TriadRecipes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class AbstractGrinderBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractProcessorBlockEntity(type, pos, state) {
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
    private var outputMatch = false
    private var blade = GrinderBlock.Blades.NONE

    private val check = RecipeManager.createCheck(TriadRecipes.GRINDER_RECIPE_TYPE)
    private val random = RandomSource.create()

    override fun execute() {
        if (!hasLevel()) return
        val input = items.getStackInSlot(INPUT_SLOT)
        val bladeStack = items.getStackInSlot(BLADE_SLOT)
        val result = items.getStackInSlot(OUTPUT_SLOT)
        val holder = check.getRecipeFor(RecipeWrapper(items), level!!)
        if (holder.isPresent) {
            val recipe = holder.get().value as GrinderRecipe
            if ((result.item == recipe.output.item && result.count <= (result.maxStackSize - recipe.output.count)) || result.isEmpty) {
                outputMatch = true
                if (shouldRun()) {
                    if ((level!!.gameTime % 20).toInt() == 0) {
                        if (progress >= getTime()) {
                            if (result.isEmpty)
                                items.setStackInSlot(OUTPUT_SLOT, recipe.output.copyWithCount(recipe.output.count))
                            else result.grow(recipe.output.count)
                            input.shrink(1)
                            if (bladeStack.hurt(BLADE_SLOT, random, null))
                                bladeStack.shrink(1)
                            progress = 0
                        } else progress++
                    }
                }
            }
            else outputMatch = false
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putInt("Progress", progress)
        tag.put("Items", items.serializeNBT())
        saveClientData(tag)
    }

    override fun saveClientData(tag: CompoundTag) {
        super.saveClientData(tag)
        tag.putBoolean("IsOn", isOn)
        tag.putString("Blade", blade.serializedName)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Progress")) progress = tag.getInt("Progress")
        if (tag.contains("Items")) items.deserializeNBT(tag.getCompound("Items"))
        loadClientData(tag)
    }

    override fun loadClientData(tag: CompoundTag) {
        super.loadClientData(tag)
        if (tag.contains("IsOn")) isOn = tag.getBoolean("IsOn")
        if (tag.contains("Blade")) blade = GrinderBlock.Blades.valueOf(tag.getString("Blade").uppercase())
    }
    private fun hasBlade(): Boolean {
        if (!hasLevel()) return false
        val bladeStack = items.getStackInSlot(BLADE_SLOT)
        if (bladeStack.isEmpty) return false
        if (bladeStack.`is`(ItemTags.create(ResourceLocation(TriadMod.MODID, "blades")))) {
            blade = TriadBlockStateProperties.BLADE.getValue((bladeStack.item as ReusableItem).materialName).get()
            level!!.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.BLADE, blade),
                Block.UPDATE_CLIENTS
            )
            return true
        }
        else {
            blade = GrinderBlock.Blades.NONE
            level!!.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.BLADE, blade),
                Block.UPDATE_CLIENTS
            )
            return false
        }
    }
    override fun shouldRun() = super.shouldRun() && hasBlade() && !items.getStackInSlot(INPUT_SLOT).isEmpty && outputMatch
    abstract override fun getTime(): Int
    override fun isFueled(): Boolean {
        if (isPowered()) {
            isOn = true
            level!!.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.POWERED, true),
                Block.UPDATE_CLIENTS
            )
            return true
        }
        else {
            isOn = false
            level!!.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.POWERED, false),
                Block.UPDATE_CLIENTS
            )
            return false
        }
    }
    abstract fun isPowered(): Boolean
}