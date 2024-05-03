package net.benjimadness.triad.block.entity

import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.entity.AbstractPumpBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.reflect.KClass

class SteelPumpBlock(properties: Properties, blockEntity: KClass<out AbstractPumpBlockEntity>) :
    AbstractMachineBlock(properties, blockEntity), EntityBlock {

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos,
                                player: Player, hit: BlockHitResult): InteractionResult =
        InteractionResult.SUCCESS

    override fun useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos,
                           player: Player, hand: InteractionHand, result: BlockHitResult): ItemInteractionResult {
        if (stack.item == Items.WATER_BUCKET) {
            val entity = level.getBlockEntity(pos) as AbstractPumpBlockEntity
            val filled = entity.waterTank.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
            if (filled > 0) {
                player.setItemInHand(hand, ItemStack(Items.BUCKET))
                return ItemInteractionResult.CONSUME
            }
            return ItemInteractionResult.SUCCESS
        }
        if (stack.item == Items.BUCKET) {
            val entity = level.getBlockEntity(pos) as AbstractPumpBlockEntity
            if (entity.waterTank.getFluidInTank(0).amount >= 1000) {
                entity.waterTank.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                player.setItemInHand(hand, ItemStack(Items.WATER_BUCKET))
                return ItemInteractionResult.CONSUME
            }
            return ItemInteractionResult.SUCCESS
        }
        return super.useItemOn(stack, state, level, pos, player, hand, result)
    }
}