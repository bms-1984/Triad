package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractElectricGrinderBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BronzeGrinderBlockEntity(pos: BlockPos, state: BlockState) :
AbstractElectricGrinderBlockEntity(1000, TriadBlockEntities.BRONZE_GRINDER_BLOCK_ENTITY_TYPE, pos, state) {
    override fun getTime(): Int = 30
}