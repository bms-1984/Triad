package net.benjimadness.triad.api.block

import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import kotlin.reflect.KClass

abstract class AbstractCapacitorBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
    AbstractTriadEntityBlock(properties, blockEntity) {
    companion object {
        @JvmStatic
        protected val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        @JvmStatic
        protected val LEVER = TriadBlockStateProperties.LEVER
        @JvmStatic
        protected val POWERED = TriadBlockStateProperties.POWERED
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LEVER, LeverPositions.NONE)
            .setValue(POWERED, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LEVER, POWERED)
    }
}