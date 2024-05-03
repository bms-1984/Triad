package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractPumpBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class SteelPumpBlockEntity(pos: BlockPos, state: BlockState) :
AbstractPumpBlockEntity(1000, 10, TriadBlockEntities.STEEL_PUMP_BLOCK_ENTITY_TYPE, pos, state)