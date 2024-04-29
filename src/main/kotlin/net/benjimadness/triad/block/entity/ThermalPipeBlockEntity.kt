package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractBoilerBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractPipeBlockEntity
import net.benjimadness.triad.api.util.MiscUtil.isHot
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class ThermalPipeBlockEntity(pos: BlockPos, state: BlockState) :
AbstractPipeBlockEntity(2, TriadBlockEntities.THERMAL_PIPE_BLOCK_ENTITY_TYPE, pos, state)
{
    override val fluid = FluidTank(15) {
        it.fluid.isHot()
    }

    override fun canBlockEntityReceive(blockEntity: BlockEntity): Boolean =
        blockEntity !is AbstractBoilerBlockEntity
}