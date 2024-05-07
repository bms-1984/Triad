package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.Contains
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import kotlin.math.min

abstract class AbstractPumpBlockEntity(capacity: Int, private val transfer: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
AbstractMachineBlockEntity(type, pos, state) {
    private val fluid = FluidTank(5000) {
        it.fluid == Fluids.WATER || it.fluid == Fluids.LAVA
    }
    val fluidTank: IFluidHandler by lazy { fluid }

    private val energy = EnergyStorage(capacity, transfer)
    val energyStorage: IEnergyStorage by lazy { energy }

    override fun execute() {
        when (fluid.fluid.fluid) {
            Fluids.WATER -> level?.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.CONTAINS, Contains.WATER), Block.UPDATE_CLIENTS)
            Fluids.LAVA -> level?.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.CONTAINS, Contains.LAVA), Block.UPDATE_CLIENTS)
            TriadFluids.STEAM -> level?.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.CONTAINS, Contains.STEAM), Block.UPDATE_CLIENTS)
            else -> level?.setBlock(blockPos, level!!.getBlockState(blockPos).setValue(TriadBlockStateProperties.CONTAINS, Contains.NONE), Block.UPDATE_CLIENTS)
        }
        if (shouldRun()) {
            if (fluid.space > 1000) {
                val below = level?.getFluidState(blockPos.below())
                if (below != null && below.isSource) {
                    fluid.fill(FluidStack(below.type, 1000), IFluidHandler.FluidAction.EXECUTE)
                    energy.extractEnergy(100, false)
                    level?.setBlock(blockPos.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS)
                }
            }
        }
        if (!fluid.isEmpty) distribute()
    }

    private fun hasFluidBelow(): Boolean {
        val below = level?.getFluidState(blockPos.below())
        return below != null && below.isSource
    }

    private fun distribute() {
        val outputs = HashSet<BlockPos>()
        val stack = fluid.getFluidInTank(0)
        for (dir in Direction.entries) {
            val pos = blockPos.relative(dir)
            val cap = level!!.getCapability(Capabilities.FluidHandler.BLOCK, pos, dir.opposite)
            if (cap != null && cap.fill(stack.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE) > 0) {
                outputs.add(pos)
            }
        }
        if (outputs.isEmpty()) return
        val amount = min(transfer, stack.amount / outputs.size)
        for (pos in outputs) {
            val cap = level!!.getCapability(Capabilities.FluidHandler.BLOCK, pos, null)
            if (cap != null) {
                val filled = cap.fill(stack.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE)
                fluid.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Fluid", fluid.writeToNBT(registry, CompoundTag()))
        tag.put("Energy", energy.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Fluid")) fluid.readFromNBT(registry, tag.getCompound("Fluid"))
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(registry, it) }
    }

    override fun isFueled(): Boolean = energy.energyStored > 0
    override fun shouldRun(): Boolean = super.shouldRun() && level?.hasNeighborSignal(blockPos) == false && hasFluidBelow()
}