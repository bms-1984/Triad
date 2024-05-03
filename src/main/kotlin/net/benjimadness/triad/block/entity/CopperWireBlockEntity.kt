package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractWireBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.EnergyStorage

class CopperWireBlockEntity(pos: BlockPos, state: BlockState) :
AbstractWireBlockEntity(2, TriadBlockEntities.COPPER_WIRE_BLOCK_ENTITY_TYPE, pos, state)
{
    override val energy = EnergyStorage(15, 2)
    override fun isSame(blockEntity: BlockEntity): Boolean =
        blockEntity is CopperWireBlockEntity
}