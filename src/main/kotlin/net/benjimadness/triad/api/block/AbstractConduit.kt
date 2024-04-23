package net.benjimadness.triad.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.BlockCapability

// TODO: implement steam pipe and copper wire, simple capacitor
abstract class AbstractConduit(properties: Properties) : Block(properties.randomTicks()) {
    companion object {
        private val FACING = BlockStateProperties.FACING
    }

    abstract val testCapability: BlockCapability<*,*>

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, getFacing(context.level, context.clickedPos))

    abstract fun distribute()

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.randomTick(pState, pLevel, pPos, pRandom)",
        "net.minecraft.world.level.block.Block"))
    override fun randomTick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        super.randomTick(state, level, pos, random)
        val newFacing = getFacing(level, pos)
        val currentState = level.getBlockState(pos)
        if (currentState.getValue(FACING) != newFacing)
            level.setBlock(pos, currentState.setValue(FACING, newFacing), Block.UPDATE_CLIENTS)
        distribute()
    }

    private fun getFacing(level: Level, pos: BlockPos): Direction {
        for (dir in Direction.entries) {
            val cap = level.getCapability(testCapability, pos.relative(dir), null)
            if (cap != null) return dir
        }
        return Direction.NORTH
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }
}