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
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.items.wrapper.RecipeWrapper

abstract class AbstractElectricGrinderBlockEntity(capacity: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractGrinderBlockEntity(type, pos, state) {

    private val energy = EnergyStorage(capacity)
    val energyStorage: IEnergyStorage by lazy { energy }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Energy", energy.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(it) }
    }

    override fun shouldRun(): Boolean = super.shouldRun() && level?.hasNeighborSignal(blockPos) == false
    override fun isPowered(): Boolean = energy.energyStored > 0
}