package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractGeneratorBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractMachineBlockEntity(type, pos, state) {
    var progress = 0

    override fun execute() {
        generate()
        distribute()
    }

    private fun generate() {
        if (shouldRun()) {
            if (progress <= 0) {
                progress = getBurnTime()
                useFuel()
            }
            else {
                progress--
                fill()
            }
        }
    }
    abstract fun fill()
    abstract fun distribute()

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.putInt("Progress", progress)
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Progress")) progress = tag.getInt("Progress")
    }

    abstract fun isFull(): Boolean
    abstract fun useFuel()
    abstract fun getBurnTime(): Int
    override fun shouldRun(): Boolean = super.shouldRun() && !isFull() && level?.hasNeighborSignal(blockPos) == false
    abstract override fun isFueled(): Boolean
}