package net.benjimadness.triad.block

import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.Contains
import net.benjimadness.triad.api.block.LeverPositions
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.block.entity.AbstractPumpBlockEntity
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.reflect.KClass

class SteelPumpBlock(properties: Properties, blockEntity: KClass<out AbstractPumpBlockEntity>) :
    AbstractMachineBlock(properties, blockEntity) {
    companion object {
        @JvmStatic
        private val CONTAINS = TriadBlockStateProperties.CONTAINS
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LEVER, LeverPositions.NONE)
            .setValue(RUNNING, false)
            .setValue(CONTAINS, Contains.NONE))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LEVER, RUNNING, CONTAINS)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos,
                                player: Player, hit: BlockHitResult): InteractionResult =
        InteractionResult.SUCCESS

    override fun useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos,
                           player: Player, hand: InteractionHand, result: BlockHitResult): ItemInteractionResult {
        when (stack.item) {
            Items.WATER_BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractPumpBlockEntity
                val filled = entity.fluidTank.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) {
                    player.setItemInHand(hand, ItemStack(Items.BUCKET))
                    return ItemInteractionResult.CONSUME
                }
                return ItemInteractionResult.SUCCESS
            }
            Items.LAVA_BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractPumpBlockEntity
                val filled = entity.fluidTank.fill(FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) {
                    player.setItemInHand(hand, ItemStack(Items.BUCKET))
                    return ItemInteractionResult.CONSUME
                }
                return ItemInteractionResult.SUCCESS
            }
            Items.BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractPumpBlockEntity
                val fluid = entity.fluidTank.getFluidInTank(0)
                if (fluid.`is`(TriadFluids.STEAM)) return ItemInteractionResult.SUCCESS
                if (fluid.amount >= 1000) {
                    val bucket = fluid.fluid.bucket
                    entity.fluidTank.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                    player.setItemInHand(hand, ItemStack(bucket))
                    return ItemInteractionResult.CONSUME
                }
                return ItemInteractionResult.SUCCESS
            }
            else -> return super.useItemOn(stack, state, level, pos, player, hand, result)
        }
    }
}