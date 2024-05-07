package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.capabilities.fluid.BoilerFluidHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.math.min

abstract class AbstractBoilerBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(transfer, gen, type, pos, state) {

    private val fluids = BoilerFluidHandler(capacity)
    val tank: IFluidHandler by lazy { fluids }

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        if (hasWater()) level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.WATER, true), Block.UPDATE_CLIENTS)
        else level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.WATER, false), Block.UPDATE_CLIENTS)
        super.serverTick(level, pos, blockEntity)
    }

    override fun distribute() {
        if (fluids.getSteam().amount > 0) {
            val outputs = HashSet<BlockPos>()
            val stack = fluids.getSteam()
            for (dir in Direction.entries) {
                val pos = blockPos.relative(dir)
                val cap = level!!.getCapability(FluidHandler.BLOCK, pos, dir.opposite)
                if (cap != null && cap.fill(stack.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE) > 0) {
                    outputs.add(pos)
                }
            }
            if (outputs.isEmpty()) return
            val amount = min(transfer, stack.amount / outputs.size)
            for (pos in outputs) {
                val cap = level!!.getCapability(FluidHandler.BLOCK, pos, null)
                if (cap != null) {
                    val filled = cap.fill(stack.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE)
                    fluids.drainSteam(filled, IFluidHandler.FluidAction.EXECUTE)
                }
            }
        }
    }

    override fun fill() {
        fluids.fillSteam(gen, IFluidHandler.FluidAction.EXECUTE)
        fluids.drainWater(gen, IFluidHandler.FluidAction.EXECUTE)
    }

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Fluid", fluids.writeToNBT(registry, CompoundTag()))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Fluid")) fluids.readFromNBT(registry, tag.getCompound("Steam"))
    }

    private fun hasWater() = !fluids.isEmpty(0)
    override fun isFull() = fluids.getSpace(1) == 0
    abstract override fun getBurnTime(): Int
    override fun shouldRun(): Boolean = super.shouldRun() && hasWater()
    abstract override fun isFueled(): Boolean
}