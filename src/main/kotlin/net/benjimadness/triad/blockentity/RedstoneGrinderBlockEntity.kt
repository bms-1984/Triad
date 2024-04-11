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
import net.minecraft.world.level.block.LeverBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class RedstoneGrinderBlockEntity(pos: BlockPos, state: BlockState) :
    GrinderBlockEntity(TriadBlockEntities.REDSTONE_GRINDER_BLOCK_ENTITY_TYPE, pos, state) {
    override fun getTime(): Int = 240
    override fun isPowered(level: Level): Boolean = level.hasNeighborSignal(blockPos)
    // TODO: Handle sideways placed levers on top and bottom with two new possible values in GrinderBlock.LeverPositions and corresponding property
    override fun serverTick(level: Level, pos: BlockPos, blockEntity: GrinderBlockEntity) {
        val facing = level.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING)
        if (level.getBlockState(pos.above()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.above()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR)
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                GrinderBlock.LeverPositions.TOP), Block.UPDATE_CLIENTS)
        else if(level.getBlockState(pos.below()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.below()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING) {
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                GrinderBlock.LeverPositions.BOTTOM), Block.UPDATE_CLIENTS)
        }
        else if(level.getBlockState(pos.south()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.south()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
            when (facing) {
                Direction.NORTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.SOUTH), Block.UPDATE_CLIENTS)
                Direction.SOUTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
                Direction.EAST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.EAST), Block.UPDATE_CLIENTS)
                Direction.WEST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.WEST), Block.UPDATE_CLIENTS)
                else -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
            }
        }
        else if(level.getBlockState(pos.east()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.east()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.EAST) {
            when (facing) {
                Direction.NORTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.EAST), Block.UPDATE_CLIENTS)
                Direction.SOUTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.WEST), Block.UPDATE_CLIENTS)
                Direction.EAST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
                Direction.WEST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.SOUTH), Block.UPDATE_CLIENTS)
                else -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
            }
        }
        else if(level.getBlockState(pos.west()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.west()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
            when (facing) {
                Direction.NORTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.WEST), Block.UPDATE_CLIENTS)
                Direction.SOUTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.EAST), Block.UPDATE_CLIENTS)
                Direction.EAST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.SOUTH), Block.UPDATE_CLIENTS)
                Direction.WEST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
                else -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
            }
        }
        else if(level.getBlockState(pos.north()).`is`(Blocks.LEVER) &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL &&
            level.getBlockState(pos.north()).getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH) {
            when (facing) {
                Direction.NORTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
                Direction.SOUTH -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.SOUTH), Block.UPDATE_CLIENTS)
                Direction.EAST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.WEST), Block.UPDATE_CLIENTS)
                Direction.WEST -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.EAST), Block.UPDATE_CLIENTS)
                else -> level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                    GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
            }
        }
        else {
            level.setBlock(pos, level.getBlockState(pos).setValue(TriadBlockStateProperties.LEVER,
                GrinderBlock.LeverPositions.NONE), Block.UPDATE_CLIENTS)
        }

        super.serverTick(level, pos, blockEntity)
    }
}