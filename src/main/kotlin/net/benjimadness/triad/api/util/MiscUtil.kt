package net.benjimadness.triad.api.util

import net.benjimadness.triad.block.GrinderBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties

object MiscUtil {
    fun BlockState.getLeverOrientation(level: Level, pos: BlockPos): GrinderBlock.LeverPositions {
        val facing = this.getValue(BlockStateProperties.HORIZONTAL_FACING)
        return if (level.getBlockState(pos.above()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.above()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR) {
            val leverFacing = level.getBlockState(pos.above()).getValue(BlockStateProperties.HORIZONTAL_FACING)
            when (leverFacing) {
                Direction.SOUTH, Direction.NORTH -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> GrinderBlock.LeverPositions.TOP
                        Direction.EAST, Direction.WEST -> GrinderBlock.LeverPositions.TOP_ROT
                        else -> GrinderBlock.LeverPositions.NONE
                    }
                }
                Direction.EAST, Direction.WEST -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> GrinderBlock.LeverPositions.TOP_ROT
                        Direction.EAST, Direction.WEST -> GrinderBlock.LeverPositions.TOP
                        else -> GrinderBlock.LeverPositions.NONE
                    }
                }
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.below()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.below()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
            val leverFacing = level.getBlockState(pos.below()).getValue(BlockStateProperties.HORIZONTAL_FACING)
            when (leverFacing) {
                Direction.SOUTH, Direction.NORTH -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> GrinderBlock.LeverPositions.BOTTOM
                        Direction.EAST, Direction.WEST -> GrinderBlock.LeverPositions.BOTTOM_ROT
                        else -> GrinderBlock.LeverPositions.NONE
                    }
                }
                Direction.EAST, Direction.WEST -> {
                    when (facing) {
                        Direction.NORTH, Direction.SOUTH -> GrinderBlock.LeverPositions.BOTTOM_ROT
                        Direction.EAST, Direction.WEST -> GrinderBlock.LeverPositions.BOTTOM
                        else -> GrinderBlock.LeverPositions.NONE
                    }
                }
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.south()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
            when (facing) {
                Direction.NORTH -> GrinderBlock.LeverPositions.SOUTH
                Direction.SOUTH -> GrinderBlock.LeverPositions.NONE
                Direction.EAST -> GrinderBlock.LeverPositions.EAST
                Direction.WEST -> GrinderBlock.LeverPositions.WEST
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.east()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.EAST) {
            when (facing) {
                Direction.NORTH -> GrinderBlock.LeverPositions.EAST
                Direction.SOUTH -> GrinderBlock.LeverPositions.WEST
                Direction.EAST -> GrinderBlock.LeverPositions.NONE
                Direction.WEST -> GrinderBlock.LeverPositions.SOUTH
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.west()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
            when (facing) {
                Direction.NORTH -> GrinderBlock.LeverPositions.WEST
                Direction.SOUTH -> GrinderBlock.LeverPositions.EAST
                Direction.EAST -> GrinderBlock.LeverPositions.SOUTH
                Direction.WEST -> GrinderBlock.LeverPositions.NONE
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else if(level.getBlockState(pos.north()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH) {
            when (facing) {
                Direction.NORTH -> GrinderBlock.LeverPositions.NONE
                Direction.SOUTH -> GrinderBlock.LeverPositions.SOUTH
                Direction.EAST -> GrinderBlock.LeverPositions.WEST
                Direction.WEST -> GrinderBlock.LeverPositions.EAST
                else -> GrinderBlock.LeverPositions.NONE
            }
        }
        else GrinderBlock.LeverPositions.NONE
    }
}