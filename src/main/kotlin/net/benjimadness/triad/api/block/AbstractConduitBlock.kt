package net.benjimadness.triad.api.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.neoforged.neoforge.capabilities.BlockCapability
import kotlin.reflect.KClass

abstract class AbstractConduitBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
    AbstractTriadEntityBlock(properties.noOcclusion(), blockEntity), EntityBlock {
        abstract val capability: BlockCapability<*, *>
    companion object {
        @JvmStatic
        protected val NORTH: BooleanProperty = BlockStateProperties.NORTH
        @JvmStatic
        protected val SOUTH: BooleanProperty = BlockStateProperties.SOUTH
        @JvmStatic
        protected val EAST: BooleanProperty = BlockStateProperties.EAST
        @JvmStatic
        protected val WEST: BooleanProperty = BlockStateProperties.WEST
        @JvmStatic
        protected val UP: BooleanProperty = BlockStateProperties.UP
        @JvmStatic
        protected val DOWN: BooleanProperty = BlockStateProperties.DOWN
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(EAST, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        constructState(defaultBlockState(), context.clickedPos, context.level)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    private fun constructState(state: BlockState, pos: BlockPos, level: Level): BlockState =
        state
            .setValue(NORTH, shouldConnect(pos.relative(Direction.NORTH), level))
            .setValue(SOUTH, shouldConnect(pos.relative(Direction.SOUTH), level))
            .setValue(EAST, shouldConnect(pos.relative(Direction.EAST), level))
            .setValue(WEST, shouldConnect(pos.relative(Direction.WEST), level))
            .setValue(UP, shouldConnect(pos.relative(Direction.UP), level))
            .setValue(DOWN, shouldConnect(pos.relative(Direction.DOWN),  level))

    override fun neighborChanged(state: BlockState, level: Level, pos: BlockPos,
                                 neighborBlock: Block, neighborPos: BlockPos, movedByPiston: Boolean) {
            level.setBlock(pos, constructState(level.getBlockState(pos), pos, level), UPDATE_CLIENTS)
    }

    private fun shouldConnect(pos: BlockPos, level: Level): Boolean {
        val block = level.getBlockState(pos).block
        if (block is AbstractConduitBlock) return checkType(block)
        val cap = level.getCapability(capability, pos, null)
        return cap != null
    }

    abstract fun checkType(block: Block): Boolean
}