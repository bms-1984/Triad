package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import kotlin.math.min

abstract class AbstractPipeBlockEntity(private val transfer: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractConduitBlockEntity(type, pos, state) {
    protected abstract val fluid: FluidTank
    val fluidTank: IFluidHandler by lazy { fluid }

    override fun distribute() {
        val amount = min(transfer,fluid.fluidAmount / outputs.size)
        for (pos in outputs) {
            val cap = level!!.getCapability(Capabilities.FluidHandler.BLOCK, pos,null)
            if (cap != null) {
                val stack = fluid.getFluidInTank(0).copyWithAmount(amount)
                val filled = cap.fill(stack, IFluidHandler.FluidAction.EXECUTE)
                fluid.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            }
        }
    }

    override fun isEmpty(): Boolean = fluid.isEmpty

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Fluid", fluid.writeToNBT(registry, CompoundTag()))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Fluid")) fluid.readFromNBT(registry, tag.getCompound("Fluid"))
    }

    override fun addOutput(pos: BlockPos) {
        val cap = level!!.getCapability(Capabilities.FluidHandler.BLOCK, pos,null)
        val blockEntity = level!!.getBlockEntity(pos)
        if (cap != null && blockEntity != null && canBlockEntityReceive(blockEntity))
            outputs.add(pos)
    }

    abstract fun canBlockEntityReceive(blockEntity: BlockEntity): Boolean
}