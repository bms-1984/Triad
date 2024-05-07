package net.benjimadness.triad.block

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.LeverPositions
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.block.entity.AbstractBoilerBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractGeneratorBlockEntity
import net.benjimadness.triad.api.capabilities.fluid.BoilerFluidHandler
import net.benjimadness.triad.gui.menu.BoilerMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import kotlin.reflect.KClass

class BoilerBlock(properties: Properties, blockEntity: KClass<out AbstractGeneratorBlockEntity>) : AbstractMachineBlock(properties, blockEntity), EntityBlock {
    companion object {
        private val WATER = TriadBlockStateProperties.WATER
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LEVER, LeverPositions.NONE)
            .setValue(RUNNING, false)
            .setValue(WATER, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, LEVER, RUNNING, WATER)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.getMenuProvider(pState, pLevel, pPos)", "net.minecraft.world.level.block.Block")
    )
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> BoilerMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.boiler_menu", "Boiler")
        )

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        result: BlockHitResult
    ): ItemInteractionResult {
        if (stack.item == Items.WATER_BUCKET) {
            val entity = level.getBlockEntity(pos) as AbstractBoilerBlockEntity
            val tank = entity.tank as BoilerFluidHandler
            val filled = tank.fillWater(1000, IFluidHandler.FluidAction.EXECUTE)
            if (filled > 0) {
                player.setItemInHand(hand, ItemStack(Items.BUCKET))
                return ItemInteractionResult.CONSUME
            }
            return ItemInteractionResult.SUCCESS
        }
        if (stack.item == Items.BUCKET) {
            val entity = level.getBlockEntity(pos) as AbstractBoilerBlockEntity
            val tank = entity.tank as BoilerFluidHandler
            if (tank.getFluidInTank(0).amount >= 1000) {
                tank.drainWater(1000, IFluidHandler.FluidAction.EXECUTE)
                player.setItemInHand(hand, ItemStack(Items.WATER_BUCKET))
                return ItemInteractionResult.CONSUME
            }
            return ItemInteractionResult.SUCCESS
        }
        return super.useItemOn(stack, state, level, pos, player, hand, result)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(RUNNING)) {
            val d0 = pos.x.toDouble() + 0.5
            val d1 = pos.y.toDouble()
            val d2 = pos.z.toDouble() + 0.5
            if (random.nextDouble() < 0.1) {
                level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                    1.0f, 1.0f, false)
            }
            val direction = state.getValue(FACING)
            val axis = direction.axis
            val d4 = random.nextDouble() * 0.6 - 0.3
            val d5 = if (axis === Direction.Axis.X) direction.stepX.toDouble() * 0.52 else d4
            val d6 = random.nextDouble() * 6.0 / 16.0
            val d7 = if (axis === Direction.Axis.Z) direction.stepZ.toDouble() * 0.52 else d4
            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
        }
    }
}