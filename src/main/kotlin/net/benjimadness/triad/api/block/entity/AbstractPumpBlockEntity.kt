package net.benjimadness.triad.api.block.entity

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
    private var counter = 0
    private val water = FluidTank(5000) { stack ->
        stack.fluid == Fluids.WATER
    }
    val waterTank: IFluidHandler by lazy { water }

    private val energy = EnergyStorage(capacity, transfer)
    val energyStorage: IEnergyStorage by lazy { energy }

    override fun execute() {
        if (shouldRun()) {
            if (water.space > 1000) {
                val below = level?.getFluidState(blockPos.below())
                if (below != null && below.isSourceOfType(Fluids.WATER)) {
                    counter++
                    water.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                    energy.extractEnergy(5, false)
                    if (counter > 5) {
                        level?.setBlock(blockPos.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS)
                        counter = 0
                    }
                }
            }
            if (!water.isEmpty) distribute()
        }
    }

    private fun distribute() {
        val outputs = HashSet<BlockPos>()
        val stack = water.getFluidInTank(0)
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
                water.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Water", water.writeToNBT(registry, CompoundTag()))
        tag.put("Energy", energy.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Water")) water.readFromNBT(registry, tag.getCompound("Water"))
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(registry, it) }
    }

    override fun isFueled(): Boolean = energy.energyStored > 0
    override fun shouldRun(): Boolean = super.shouldRun() && level?.hasNeighborSignal(blockPos) == false
}