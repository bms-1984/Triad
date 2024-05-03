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
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.math.min

abstract class AbstractEnergyGeneratorBlockEntity(capacity: Int, private val transfer: Int, private val gen: Int, type: BlockEntityType<*>,
                                                  pos: BlockPos, state: BlockState) :
    AbstractGeneratorBlockEntity(transfer, gen, type, pos, state) {

    private val energy = EnergyStorage(capacity, transfer)
    val energyStorage: IEnergyStorage by lazy { energy }

    override fun distribute() {
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
                val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null)
                if (cap != null) {
                    val filled = cap.receiveEnergy(amount, false)
                    energy.extractEnergy(filled, false)
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