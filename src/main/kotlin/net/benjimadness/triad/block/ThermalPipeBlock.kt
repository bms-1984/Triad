package net.benjimadness.triad.block


import net.benjimadness.triad.api.block.AbstractConduitBlock
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.reflect.KClass

class ThermalPipeBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
        AbstractConduitBlock(properties, blockEntity) {
    override val capability: BlockCapability<IFluidHandler, Direction?> = Capabilities.FluidHandler.BLOCK

    override fun checkType(block: Block): Boolean =
        block is ThermalPipeBlock
}