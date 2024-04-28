package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.util.MiscUtil.getLeverOrientation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.math.min

abstract class AbstractCapacitorBlockEntity(capacity: Int, private val transfer: Int, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
AbstractTriadBlockEntity(type, pos, state) {
    private val energy = EnergyStorage(capacity, transfer)
    val energyStorage: IEnergyStorage by lazy { energy }

    open fun shouldDistribute(): Boolean =
        energyStorage.energyStored > 0 && level?.hasNeighborSignal(blockPos) == false

    open fun distribute() {
        for (dir in Direction.entries) {
            if (hasLevel()) {
                val dirEnergy = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, blockPos.relative(dir), null)
                if (dirEnergy != null && dirEnergy.canReceive()) {
                    val dirBlock = level!!.getBlockEntity(blockPos.relative(dir))
                    if (dirBlock !is AbstractGeneratorBlockEntity) {
                        val received = dirEnergy.receiveEnergy(min(transfer, energy.energyStored), false)
                        energy.extractEnergy(received, false)
                    }
                }
            }
        }
    }

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER, level.getBlockState(pos).getLeverOrientation(level, pos)), Block.UPDATE_CLIENTS)
        if (energy.energyStored > 0) level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.POWERED, true), Block.UPDATE_CLIENTS)
        else level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.POWERED, false), Block.UPDATE_CLIENTS)
        if (shouldDistribute()) distribute()
        setChanged()
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.put("Energy", energy.serializeNBT())
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(it) }
    }
}