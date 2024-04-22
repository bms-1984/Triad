package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractProcessorBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractMachineBlockEntity(type, pos, state) {
        var progress = 0
    abstract fun getTime(): Int
    }