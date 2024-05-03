package net.benjimadness.triad.block


import net.benjimadness.triad.api.block.AbstractConduitBlock
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import kotlin.reflect.KClass

class CopperWireBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
        AbstractConduitBlock(properties, blockEntity) {
    override val capability: BlockCapability<IEnergyStorage, Direction?> = Capabilities.EnergyStorage.BLOCK

    override fun isSame(block: Block): Boolean =
        block is CopperWireBlock
}