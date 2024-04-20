package net.benjimadness.triad.blockentity

import net.benjimadness.triad.api.block.AbstractGeneratorBlockEntity
import net.benjimadness.triad.api.util.MiscUtil.getLeverOrientation
import net.benjimadness.triad.block.TriadBlockStateProperties
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class CoalGeneratorBlockEntity(private val pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(TriadBlockEntities.COAL_GENERATOR_BLOCK_ENTITY_TYPE, pos, state) {

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractGeneratorBlockEntity) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER, level.getBlockState(pos).getLeverOrientation(level, pos)), Block.UPDATE_CLIENTS)
        super.serverTick(level, pos, blockEntity)
    }

    override fun generate(level: Level, pos: BlockPos) {
        if (energyStorage.energyStored < energyStorage.maxEnergyStored && isPowered(level)) {
            if (progress <= 0) {
                progress = getTotalTime()
                itemHandler.extractItem(INPUT_SLOT, 1, false)
            }
            else {
                progress--
                energyStorage.receiveEnergy(1, false)
            }
        }
    }

    override fun getTotalTime(): Int = itemHandler.getStackInSlot(INPUT_SLOT).getBurnTime(RecipeType.SMELTING)

    override fun isPowered(level: Level): Boolean = isFuel(itemHandler.getStackInSlot(INPUT_SLOT)) && !level.hasNeighborSignal(pos)

    override fun isFuel(stack: ItemStack): Boolean = stack.getBurnTime(RecipeType.SMELTING) > 0
}