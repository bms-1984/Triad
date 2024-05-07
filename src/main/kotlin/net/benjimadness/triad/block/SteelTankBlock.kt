package net.benjimadness.triad.block

import net.benjimadness.triad.api.block.AbstractTriadEntityBlock
import net.benjimadness.triad.api.block.Contains
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.block.entity.AbstractTankBlockEntity
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.reflect.KClass

class SteelTankBlock(properties: Properties, blockEntity: KClass<out BlockEntity>) :
    AbstractTriadEntityBlock(properties, blockEntity) {
    companion object {
        @JvmStatic
        private val CONTAINS = TriadBlockStateProperties.CONTAINS
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(CONTAINS, Contains.NONE))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(CONTAINS)
    }

    override fun useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos,
                           player: Player, hand: InteractionHand, result: BlockHitResult
    ): ItemInteractionResult {
        when (stack.item) {
            Items.WATER_BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractTankBlockEntity
                val filled = entity.fluidTank.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) {
                    player.setItemInHand(hand, ItemStack(Items.BUCKET))
                    return ItemInteractionResult.CONSUME
                }
                return ItemInteractionResult.SUCCESS
            }
            Items.LAVA_BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractTankBlockEntity
                val filled = entity.fluidTank.fill(FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) {
                    player.setItemInHand(hand, ItemStack(Items.BUCKET))
                    return ItemInteractionResult.CONSUME
                }
                return ItemInteractionResult.SUCCESS
            }
            Items.BUCKET -> {
                val entity = level.getBlockEntity(pos) as AbstractTankBlockEntity
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