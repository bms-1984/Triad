package net.benjimadness.triad.api.block

import net.benjimadness.triad.api.block.entity.AbstractMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import kotlin.reflect.KClass

abstract class AbstractMachineBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
    AbstractTriadEntityBlock(properties, blockEntity) {
    companion object {
        @JvmStatic
        protected val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        @JvmStatic
        protected val LEVER = TriadBlockStateProperties.LEVER
        @JvmStatic
        protected val RUNNING = TriadBlockStateProperties.RUNNING
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LEVER, LeverPositions.NONE)
            .setValue(RUNNING, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LEVER, RUNNING)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide && player is ServerPlayer)
            state.getMenuProvider(level, pos)?.let { player.openMenu(it) { buf -> buf.writeBlockPos(pos) } }
        return InteractionResult.sidedSuccess(level.isClientSide())
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        result: BlockHitResult
    ): ItemInteractionResult {
        if (!level.isClientSide && player is ServerPlayer)
            state.getMenuProvider(level, pos)?.let { player.openMenu(it) { buf -> buf.writeBlockPos(pos) } }
        return ItemInteractionResult.sidedSuccess(level.isClientSide())
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
            if (blockEntity is AbstractMachineBlockEntity) {
                val itemHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null)
                if (itemHandler != null) {
                    for (slot in 0 until itemHandler.slots) {
                        val stack = itemHandler.getStackInSlot(slot)
                        if (!stack.isEmpty)
                            level.addFreshEntity(
                                ItemEntity(
                                    level,
                                    pos.x.toDouble(),
                                    pos.y.toDouble(),
                                    pos.z.toDouble(),
                                    stack
                                )
                            )
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}