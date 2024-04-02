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

package net.benjimadness.triad.block

import net.benjimadness.triad.blockentity.BlockEntityGrinder
import net.benjimadness.triad.blockentity.BlockEntityRedstoneGrinder
import net.benjimadness.triad.gui.GrinderMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult

class BlockGrinder(properties: Properties) : Block(properties), EntityBlock {
    companion object {
        val FACING: DirectionProperty = BlockStateProperties.FACING
        val LIT: BooleanProperty = BlockStateProperties.LIT
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LIT)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BlockEntityRedstoneGrinder(pos, state)
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { lLevel, pos, lState, blockEntity ->
            if (level.isClientSide) return@BlockEntityTicker
            else if (blockEntity is BlockEntityGrinder) blockEntity.serverTick(lLevel, pos, lState, blockEntity)
        }

    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> GrinderMenu(id, inv, pos) },
            Component.translatable("menu.title.triad.grinder_menu")
        )

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide && player is ServerPlayer)
            player.openMenu(state.getMenuProvider(level, pos)) { buf -> buf.writeBlockPos(pos) }
        return InteractionResult.sidedSuccess(level.isClientSide())
    }

}