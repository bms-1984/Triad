/*
 *     Triad, a tech mod for Minecraft
 *     Copyright (C) 2024  Ben M. Sutter
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.benjimadness.triad.blockentity

import net.benjimadness.triad.block.GrinderBlock
import net.benjimadness.triad.block.TriadBlockStateProperties
import net.benjimadness.triad.registry.TriadBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class RedstoneGrinderBlockEntity(pos: BlockPos, state: BlockState) :
    AbstractGrinderBlockEntity(TriadBlockEntities.REDSTONE_GRINDER_BLOCK_ENTITY_TYPE, pos, state) {
    override fun getTime(): Int = 240
    override fun isPowered(level: Level): Boolean = level.hasNeighborSignal(blockPos)
    override fun serverTick(level: Level, pos: BlockPos, blockEntity: AbstractGrinderBlockEntity) {
        val state = level.getBlockState(pos)
        level.setBlock(pos, state.setValue(TriadBlockStateProperties.LEVER, state.getLeverOrientation(level, pos)), Block.UPDATE_CLIENTS)
        super.serverTick(level, pos, blockEntity)
    }

    private fun BlockState.getLeverOrientation(level: Level, pos: BlockPos): GrinderBlock.LeverPositions {
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