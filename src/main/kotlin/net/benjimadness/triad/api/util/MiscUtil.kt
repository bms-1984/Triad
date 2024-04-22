package net.benjimadness.triad.api.util

import net.benjimadness.triad.api.block.LeverPositions
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties

object MiscUtil {
    fun BlockState.getLeverOrientation(level: Level, pos: BlockPos): LeverPositions {
        val facing = this.getValue(BlockStateProperties.HORIZONTAL_FACING)
        return if (level.getBlockState(pos.above()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.above()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR) {
            val leverFacing = level.getBlockState(pos.above()).getValue(BlockStateProperties.HORIZONTAL_FACING)
            when (leverFacing) {
                Direction.SOUTH, Direction.NORTH -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> LeverPositions.TOP
                        Direction.EAST, Direction.WEST -> LeverPositions.TOP_ROT
                        else -> LeverPositions.NONE
                    }
                }
                Direction.EAST, Direction.WEST -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> LeverPositions.TOP_ROT
                        Direction.EAST, Direction.WEST -> LeverPositions.TOP
                        else -> LeverPositions.NONE
                    }
                }
                else -> LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.below()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.below()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
            val leverFacing = level.getBlockState(pos.below()).getValue(BlockStateProperties.HORIZONTAL_FACING)
            when (leverFacing) {
                Direction.SOUTH, Direction.NORTH -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> LeverPositions.BOTTOM
                        Direction.EAST, Direction.WEST -> LeverPositions.BOTTOM_ROT
                        else -> LeverPositions.NONE
                    }
                }
                Direction.EAST, Direction.WEST -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> LeverPositions.BOTTOM_ROT
                        Direction.EAST, Direction.WEST -> LeverPositions.BOTTOM
                        else -> LeverPositions.NONE
                    }
                }
                else -> LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.south()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
            when (facing) {
                Direction.NORTH -> LeverPositions.SOUTH
                Direction.SOUTH -> LeverPositions.NONE
                Direction.EAST -> LeverPositions.EAST
                Direction.WEST -> LeverPositions.WEST
                else -> LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.east()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.EAST) {
            when (facing) {
                Direction.NORTH -> LeverPositions.EAST
                Direction.SOUTH -> LeverPositions.WEST
                Direction.EAST -> LeverPositions.NONE
                Direction.WEST -> LeverPositions.SOUTH
                else -> LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.west()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
            when (facing) {
                Direction.NORTH -> LeverPositions.WEST
                Direction.SOUTH -> LeverPositions.EAST
                Direction.EAST -> LeverPositions.SOUTH
                Direction.WEST -> LeverPositions.NONE
                else -> LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.north()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH) {
            when (facing) {
                Direction.NORTH -> LeverPositions.NONE
                Direction.SOUTH -> LeverPositions.SOUTH
                Direction.EAST -> LeverPositions.WEST
                Direction.WEST -> LeverPositions.EAST
                else -> LeverPositions.NONE
            }
        }
        else LeverPositions.NONE
    }
}