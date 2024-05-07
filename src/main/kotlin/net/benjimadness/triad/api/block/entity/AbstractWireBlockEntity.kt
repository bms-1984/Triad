package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
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

    override fun distribute() {
        val amount = min(transfer,energy.energyStored / outputs.size)
        for (pos in outputs) {
            val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos,null)
            if (cap != null) {
                val filled = cap.receiveEnergy(amount, false)
                energy.extractEnergy(filled, false)
            }
        }
    }
    override fun addOutput(pos: BlockPos) {
        val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos,null)
        if (cap != null)
            outputs.add(pos)
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