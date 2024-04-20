package net.benjimadness.triad.block

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.AbstractGeneratorBlockEntity
import net.benjimadness.triad.gui.GeneratorMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
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
import net.minecraft.world.phys.BlockHitResult
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class GeneratorBlock(properties: Properties, private val blockEntity: KClass<out AbstractGeneratorBlockEntity>) : Block(properties), EntityBlock {
    companion object {
        private val FACING = BlockStateProperties.HORIZONTAL_FACING
        private val LEVER = TriadBlockStateProperties.LEVER
        private val RUNNING = TriadBlockStateProperties.RUNNING
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LEVER, GrinderBlock.LeverPositions.NONE)
            .setValue(RUNNING, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LEVER, RUNNING)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = blockEntity.primaryConstructor!!.call(pos, state)
    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { lLevel, pos, _, lBlockEntity ->
            if (lLevel.isClientSide) return@BlockEntityTicker
            else if (lBlockEntity is AbstractGeneratorBlockEntity) lBlockEntity.serverTick(lLevel, pos, lBlockEntity)
        }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.getMenuProvider(pState, pLevel, pPos)", "net.minecraft.world.level.block.Block")
    )
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> GeneratorMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.generator_menu", "Generator")
        )

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.use(pState, pLevel, pPos, pPlayer, pHand, pHit)",
        "net.minecraft.world.level.block.Block"))
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
            if (blockEntity is AbstractGeneratorBlockEntity) {
                for (slot in 0 until blockEntity.itemHandler.slots) {
                    val stack = blockEntity.itemHandler.getStackInSlot(slot)
                    if (!stack.isEmpty)
                        level.addFreshEntity(ItemEntity(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack))
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}