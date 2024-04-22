package net.benjimadness.triad.block

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.AbstractMachineBlock
import net.benjimadness.triad.api.block.entity.AbstractGeneratorBlockEntity
import net.benjimadness.triad.gui.TurbineMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import kotlin.reflect.KClass

class TurbineBlock(properties: Properties, blockEntity: KClass<out AbstractGeneratorBlockEntity>) : AbstractMachineBlock(properties, blockEntity), EntityBlock {
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.getMenuProvider(pState, pLevel, pPos)", "net.minecraft.world.level.block.Block")
    )
    override fun getMenuProvider(state: BlockState, level: Level, pos: BlockPos): MenuProvider =
        SimpleMenuProvider(
            { id, inv, _ -> TurbineMenu(id, inv, pos) },
            Component.translatableWithFallback("menu.title.${TriadMod.MODID}.turbine_menu", "Turbine")
        )
}