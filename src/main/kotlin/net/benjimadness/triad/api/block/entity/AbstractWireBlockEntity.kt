package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.math.min

abstract class AbstractWireBlockEntity(private val transfer: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractConduitBlockEntity(type, pos, state) {
    protected abstract val energy: EnergyStorage
    val energyStorage: IEnergyStorage by lazy { energy }
    override fun shouldDistribute(dir: Direction, pos: BlockPos, level: Level): Boolean =
        canNeighborReceive(dir, pos, level, min(transfer, energy.energyStored))

    override fun canNeighborReceive(dir: Direction, pos: BlockPos, level: Level, amount: Int): Boolean {
        val blockEntity = level.getBlockEntity(pos.relative(dir))
        if (blockEntity is AbstractEnergyGeneratorBlockEntity) return false
        val cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.opposite)
        if (cap != null) {
            val filled = cap.receiveEnergy(min(transfer, amount), true)
            return filled == min(transfer, amount)
        }
        return false
    }

    override fun distribute(pos: BlockPos, level: Level) {
        for (dir in Direction.entries) {
            if (shouldDistribute(dir, pos, level)) {
                val cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.opposite)
                if (cap != null) {
                    val filled = cap.receiveEnergy(min(transfer, energy.energyStored), false)
                    energy.extractEnergy(filled, false)
                }
            }
        }
    }

    override fun isEmpty(): Boolean = energy.energyStored <= 0
    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Energy", energy.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(registry, it) }
    }
}