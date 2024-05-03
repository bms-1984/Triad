package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.math.min

abstract class AbstractEnergyGeneratorBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>,
                                                  pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(transfer, gen, type, pos, state) {

    private val energy = EnergyStorage(capacity, transfer)
    val energyStorage: IEnergyStorage by lazy { energy }

    override fun distribute() {
        if (energy.energyStored > 0) {
            for (dir in Direction.entries) {
                if (hasLevel()) {
                    val dirEnergy = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, blockPos.relative(dir), dir.opposite)
                    if (dirEnergy != null) {
                        val received = dirEnergy.receiveEnergy(min (transfer, energy.energyStored), false)
                        energy.extractEnergy(received, false)
                    }
                }
            }
        }
    }

    override fun saveAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.saveAdditional(tag, registry)
        tag.put("Energy", energy.serializeNBT(registry))
    }

    override fun loadAdditional(tag: CompoundTag, registry: HolderLookup.Provider) {
        super.loadAdditional(tag, registry)
        if (tag.contains("Energy")) tag.get("Energy")?.let { energy.deserializeNBT(registry, it) }
    }

    override fun fill() {
        energy.receiveEnergy(gen, false)
    }

    override fun isFull(): Boolean = energy.energyStored >= energy.maxEnergyStored
}