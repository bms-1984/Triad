package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractItemBoilerBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BronzeItemBoilerBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractItemBoilerBlockEntity (10000, 10, 1, TriadBlockEntities.BRONZE_ITEM_BOILER_BLOCK_ENTITY_TYPE, pos, state)