package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.entity.AbstractElectricFurnaceBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractElectricGrinderBlockEntity
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BronzeFurnaceBlockEntity(pos: BlockPos, state: BlockState) :
AbstractElectricFurnaceBlockEntity(1000, TriadBlockEntities.BRONZE_FURNACE_BLOCK_ENTITY_TYPE, pos, state)