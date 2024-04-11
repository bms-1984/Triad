package net.benjimadness.triad.block

import net.benjimadness.triad.block.GrinderBlock.Blades
import net.benjimadness.triad.block.GrinderBlock.LeverPositions
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty

object TriadBlockStateProperties {
    val POWERED: BooleanProperty = BooleanProperty.create("powered")
    val BLADE: EnumProperty<Blades> = EnumProperty.create("blade", Blades::class.java)
    val RUNNING: BooleanProperty = BooleanProperty.create("running")
    val LEVER: EnumProperty<LeverPositions> = EnumProperty.create("lever", LeverPositions::class.java)
}