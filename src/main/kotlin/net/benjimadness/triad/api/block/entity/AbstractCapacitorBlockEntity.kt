package net.benjimadness.triad.api.block.entity

import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.util.MiscUtil.getLeverOrientation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup.Provider
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
        if (energy.energyStored > 0) {
            val outputs = HashSet<BlockPos>()
            for (dir in Direction.entries) {
                val pos = blockPos.relative(dir)
                val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos, dir.opposite)
                if (cap != null && cap.canReceive()) {
                    outputs.add(pos)
                }
            }
            if (outputs.isEmpty()) return
            val amount = min(transfer, energy.energyStored / outputs.size)
            for (pos in outputs) {
                val blockEntity = level!!.getBlockEntity(pos)
                val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null)
                if (cap != null && blockEntity !is AbstractGeneratorBlockEntity) {
                    val filled = cap.receiveEnergy(amount, false)
                    energy.extractEnergy(filled, false)
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

    override fun saveAdditional(tag: CompoundTag, registry: Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Energy", energy.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(registry, it) }
    }
}