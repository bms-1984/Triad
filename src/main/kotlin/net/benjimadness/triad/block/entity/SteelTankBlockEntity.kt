package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractTankBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class SteelTankBlockEntity(pos: BlockPos, state: BlockState) :
AbstractTankBlockEntity(10000, 10, TriadBlockEntities.STEEL_TANK_BLOCK_ENTITY_TYPE, pos, state)