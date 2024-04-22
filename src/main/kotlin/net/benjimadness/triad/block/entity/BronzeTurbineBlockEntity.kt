package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractTurbineBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BronzeTurbineBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractTurbineBlockEntity (10000, 10, 1, TriadBlockEntities.BRONZE_TURBINE_BLOCK_ENTITY_TYPE, pos, state) {
    override fun getBurnTime(): Int = 1
}