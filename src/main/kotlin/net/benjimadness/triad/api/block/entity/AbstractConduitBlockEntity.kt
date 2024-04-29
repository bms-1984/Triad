package net.benjimadness.triad.api.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractConduitBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    AbstractTriadBlockEntity(type, pos, state) {

    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractTriadBlockEntity) {
        if (shouldDistribute(pos, level)) distribute(pos, level)
    }
    private fun shouldDistribute(pos: BlockPos, level: Level): Boolean {
        if (isEmpty()) return false
        for (dir in Direction.entries) {
            if (shouldDistribute(dir, pos, level)) {
                return true
            }
        }
        return false
    }
    abstract fun isEmpty(): Boolean
    abstract fun shouldDistribute(dir: Direction, pos: BlockPos, level: Level): Boolean
    abstract fun canNeighborReceive(dir: Direction, pos: BlockPos, level: Level, amount: Int): Boolean
    abstract fun distribute(pos: BlockPos, level: Level)
}