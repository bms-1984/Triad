package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage

abstract class AbstractElectricProcessorBlockEntity(capacity: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractProcessorBlockEntity(type, pos, state) {
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
    override fun isFueled(): Boolean = energy.energyStored > 0
}