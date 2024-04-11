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

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.blockentity.AbstractGrinderBlockEntity
import net.benjimadness.triad.gui.GrinderMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.item.ItemEntity
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
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class GrinderBlock(properties: Properties, private val blockEntity: KClass<out AbstractGrinderBlockEntity>) : Block(properties), EntityBlock {
    companion object {
        private val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        private val POWERED = TriadBlockStateProperties.POWERED
        private val BLADE = TriadBlockStateProperties.BLADE
        private val RUNNING = TriadBlockStateProperties.RUNNING
        private val LEVER = TriadBlockStateProperties.LEVER
    }
    private val randomSource = RandomSource.create()

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(BLADE, Blades.NONE)
            .setValue(RUNNING, false)
            .setValue(LEVER, LeverPositions.NONE))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, POWERED, BLADE, RUNNING, LEVER)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = blockEntity.primaryConstructor!!.call(pos, state)
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { lLevel, pos, _, lBlockEntity ->
            if (level.isClientSide) return@BlockEntityTicker
            else if (lBlockEntity is AbstractGrinderBlockEntity) lBlockEntity.serverTick(lLevel, pos, lBlockEntity)
        }

    @Deprecated("Deprecated in Java")
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> GrinderMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.grinder_menu", "Grinder")
        )

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide && player is ServerPlayer)
            state.getMenuProvider(level, pos)?.let { player.openMenu(it) { buf -> buf.writeBlockPos(pos) } }
        return InteractionResult.sidedSuccess(level.isClientSide())
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)",
        "net.minecraft.world.level.block.Block"))
    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (state.block != newState.block && !movedByPiston) {
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity is AbstractGrinderBlockEntity) {
                for (slot in 0 until blockEntity.itemHandler.slots) {
                    val stack = blockEntity.itemHandler.getStackInSlot(slot)
                    if (!stack.isEmpty)
                        level.addFreshEntity(ItemEntity(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack))
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    enum class Blades : StringRepresentable {
        NONE, BRONZE, STEEL;
        override fun getSerializedName(): String = name.lowercase()
        override fun toString(): String = serializedName
        fun getComponent(): Component = Component.translatableWithFallback(
            "${TriadMod.MODID}.message.blade.${serializedName}",
            serializedName.replaceFirstChar { it.uppercase() })
    }

    enum class LeverPositions : StringRepresentable {
        NONE, SOUTH, EAST, WEST, BOTTOM, TOP, BOTTOM_ROT, TOP_ROT;
        override fun getSerializedName(): String = name.lowercase()
        override fun toString(): String = serializedName
    }
}