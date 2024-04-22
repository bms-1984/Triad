package net.benjimadness.triad.block

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.entity.AbstractBoilerBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractGeneratorBlockEntity
import net.benjimadness.triad.gui.BoilerMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
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
// TODO: state for water shown on block
class BoilerBlock(properties: Properties, blockEntity: KClass<out AbstractGeneratorBlockEntity>) : AbstractMachineBlock(properties, blockEntity), EntityBlock {
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.getMenuProvider(pState, pLevel, pPos)", "net.minecraft.world.level.block.Block")
    )
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> BoilerMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.boiler_menu", "Boiler")
        )

    @Deprecated(
        "Deprecated in Java",
        replaceWith = ReplaceWith(
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
        if (player.getItemInHand(hand).item == Items.WATER_BUCKET) {
            val entity = level.getBlockEntity(pos) as AbstractBoilerBlockEntity
            entity.waterTank.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
            player.setItemInHand(hand, ItemStack(Items.BUCKET))
            return InteractionResult.sidedSuccess(level.isClientSide())
        }
        return super.use(state, level, pos, player, hand, hit)
    }
}