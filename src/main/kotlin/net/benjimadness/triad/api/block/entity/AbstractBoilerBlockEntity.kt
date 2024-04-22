package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import kotlin.math.min

abstract class AbstractBoilerBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(capacity, transfer, gen, type, pos, state) {
        private val water = FluidTank(capacity) { stack ->
            stack.fluid == Fluids.WATER
        }
    private val steam = FluidTank(capacity) { stack ->
        stack.fluid == TriadFluids.STEAM
    }

    val waterTank: IFluidHandler by lazy { water }
    val steamTank: IFluidHandler by lazy { steam }

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

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Steam", steam.writeToNBT(CompoundTag()))
        tag.put("Water", water.writeToNBT(CompoundTag()))
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Steam")) steam.readFromNBT(tag.getCompound("Steam"))
        if (tag.contains("Water")) water.readFromNBT(tag.getCompound("Water"))
    }

    private fun hasWater() = !water.isEmpty
    override fun isFull() = steam.space == 0
    abstract override fun getBurnTime(): Int
    override fun shouldRun(): Boolean = super.shouldRun() && hasWater()
    abstract override fun isFueled(): Boolean
}