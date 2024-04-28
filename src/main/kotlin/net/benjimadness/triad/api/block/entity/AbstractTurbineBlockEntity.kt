package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

abstract class AbstractTurbineBlockEntity(capacity: Int, transfer: Int, private val gen: Int, type: BlockEntityType<*>,
                                 pos: BlockPos, state: BlockState) :
    AbstractEnergyGeneratorBlockEntity(capacity, transfer, gen, type, pos, state) {

    private val steam = FluidTank(capacity) { stack ->
        stack.fluid == TriadFluids.STEAM
    }
    val steamTank: IFluidHandler by lazy { steam }

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        if (isFueled()) level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.STEAM, true), Block.UPDATE_CLIENTS)
        else level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.STEAM, false), Block.UPDATE_CLIENTS)
        super.serverTick(level, pos, blockEntity)
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Steam", steam.writeToNBT(CompoundTag()))
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Steam")) steam.readFromNBT(tag.getCompound("Steam"))
    }

    override fun useFuel() {
        steam.drain(gen, IFluidHandler.FluidAction.EXECUTE)
    }
    override fun isFueled(): Boolean = steam.fluidAmount > 0
}