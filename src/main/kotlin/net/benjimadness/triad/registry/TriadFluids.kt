package net.benjimadness.triad.registry

import net.benjimadness.triad.TriadMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.level.material.EmptyFluid
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object TriadFluids {
    val REGISTRY: DeferredRegister<Fluid> = DeferredRegister.create(BuiltInRegistries.FLUID, TriadMod.MODID)

    val STEAM: Fluid by registerFluid("steam") { object : EmptyFluid() {
        override fun getFluidType(): FluidType =
            object : FluidType(Properties.create()) {
                override fun getDescription(): Component =
                    Component.translatableWithFallback("${TriadMod.MODID}.message.steam", "Steam")
            }
    } }

    private fun <T : Fluid> registerFluid(name: String, fluid: Supplier<T>) =
        REGISTRY.register(name, fluid)
}