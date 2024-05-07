package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractSawmillBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BronzeSawmillBlockEntity(pos: BlockPos, state: BlockState) :
AbstractSawmillBlockEntity(1000, TriadBlockEntities.BRONZE_SAWMILL_BLOCK_ENTITY_TYPE, pos, state)