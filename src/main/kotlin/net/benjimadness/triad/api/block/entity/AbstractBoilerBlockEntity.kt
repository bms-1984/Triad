package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import kotlin.math.min

abstract class AbstractBoilerBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(transfer, gen, type, pos, state) {
        private val water = FluidTank(capacity) { stack ->
            stack.fluid == Fluids.WATER
        }
    private val steam = FluidTank(capacity) { stack ->
        stack.fluid == TriadFluids.STEAM
    }

    val waterTank: IFluidHandler by lazy { water }
    val steamTank: IFluidHandler by lazy { steam }

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        if (hasWater()) level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.WATER, true), Block.UPDATE_CLIENTS)
        else level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.WATER, false), Block.UPDATE_CLIENTS)
        super.serverTick(level, pos, blockEntity)
    }

    override fun distribute() {
        if (steam.fluidAmount > 0) {
            for (dir in Direction.entries) {
                if (hasLevel()) {
                    val dirTank = level!!.getCapability(FluidHandler.BLOCK, blockPos.relative(dir), null)
                    if (dirTank != null) {
                        val received = dirTank.fill(steam.getFluidInTank(0).copyWithAmount(min(transfer, steam.fluidAmount)), IFluidHandler.FluidAction.EXECUTE)
                        steam.drain(received, IFluidHandler.FluidAction.EXECUTE)
                    }
                }
            }
        }
    }

    override fun fill() {
        steam.fill(FluidStack(TriadFluids.STEAM, gen), IFluidHandler.FluidAction.EXECUTE)
        water.drain(gen, IFluidHandler.FluidAction.EXECUTE)
    }

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Steam", steam.writeToNBT(registry, CompoundTag()))
        tag.put("Water", water.writeToNBT(registry, CompoundTag()))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Steam")) steam.readFromNBT(registry, tag.getCompound("Steam"))
        if (tag.contains("Water")) water.readFromNBT(registry, tag.getCompound("Water"))
    }

    private fun hasWater() = !water.isEmpty
    override fun isFull() = steam.space == 0
    abstract override fun getBurnTime(): Int
    override fun shouldRun(): Boolean = super.shouldRun() && hasWater()
    abstract override fun isFueled(): Boolean
}