package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractCapacitorBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class SteelCapacitorBlockEntity(pos: BlockPos, state: BlockState) :
AbstractCapacitorBlockEntity(50000, 5, TriadBlockEntities.STEEL_CAPACITOR_BLOCK_ENTITY_TYPE, pos, state)