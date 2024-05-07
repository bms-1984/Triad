/*
 *     Triad, a tech mod for Minecraft
 *     Copyright (C) 2024  Ben M. Sutter
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.benjimadness.triad.compat

import mcjty.theoneprobe.api.*
import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.api.block.TriadBlockStateProperties
import net.benjimadness.triad.api.block.entity.AbstractBoilerBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractPumpBlockEntity
import net.benjimadness.triad.api.block.entity.AbstractTurbineBlockEntity
import net.benjimadness.triad.api.capabilities.fluid.BoilerFluidHandler
import net.benjimadness.triad.api.util.ComponentUtil.combine
import net.benjimadness.triad.block.*
import net.benjimadness.triad.block.SteelPumpBlock
import net.benjimadness.triad.registry.TriadFluids
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.fml.InterModComms
import net.neoforged.fml.ModList
import net.neoforged.neoforge.fluids.FluidStack
import java.util.function.Function

object TheOneProbe {
    fun register() {
        if (!ModList.get().isLoaded("theoneprobe"))
            return
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", ::GetTheOneProbe)
    }

    private fun fluidType(stack: FluidStack): Component = combine(
        Component.translatableWithFallback(
            "${TriadMod.MODID}.message.type", "Type"),
        Component.literal(": "),
        if (stack.`is`(TriadFluids.STEAM))
            Component.translatableWithFallback("${TriadMod.MODID}.message.steam", "Steam")
        else
            stack.hoverName
    )

    private fun fluidAmount(stack: FluidStack): Component = combine(
        Component.translatableWithFallback(
            "${TriadMod.MODID}.message.amount", "Amount"),
        Component.literal(": "),
        Component.literal(
            "${stack.amount} L"
        )
    )


    class GetTheOneProbe : Function<ITheOneProbe, Unit> {
        override fun apply(probe: ITheOneProbe) {
            TriadMod.LOGGER.info("Singing my support for The One Probe")
            probe.registerProvider(object : IProbeInfoProvider {
                override fun getID(): ResourceLocation = ResourceLocation(TriadMod.MODID, "the_one_probe_provider")
                override fun addProbeInfo(
                    mode: ProbeMode?, info: IProbeInfo?, player: Player?, level: Level?,
                    state: BlockState?, hitData: IProbeHitData?
                ) {
                    if (state != null && info != null && level != null && hitData != null) {
                        when (state.block) {
                            is GrinderBlock -> {
                                info.horizontal().text(
                                    combine(
                                        Component.translatableWithFallback(
                                            "${TriadMod.MODID}.message.blade", "Blade"),
                                        Component.literal(": "),
                                        state.getValue(TriadBlockStateProperties.BLADE).getComponent()
                                    )
                                )
                            }
                            is BoilerBlock -> {
                                val blockEntity = level.getBlockEntity(hitData.pos) as AbstractBoilerBlockEntity
                                val tank = blockEntity.tank as BoilerFluidHandler
                                val waterStack = tank.getWater()
                                val steamStack = tank.getSteam()
                                if (waterStack.amount > 0) {
                                    info.horizontal().text(fluidType(waterStack))
                                    info.horizontal().text(fluidAmount(waterStack))
                                }
                                if (steamStack.amount > 0) {
                                    info.horizontal().text(fluidType(steamStack))
                                    info.horizontal().text(fluidAmount(steamStack))
                                }
                            }
                            is TurbineBlock -> {
                                val blockEntity = level.getBlockEntity(hitData.pos) as AbstractTurbineBlockEntity
                                val stack = blockEntity.steamTank.getFluidInTank(0)
                                if (stack.amount > 0) {
                                    info.horizontal().text(fluidType(stack))
                                    info.horizontal().text(fluidAmount(stack))
                                }
                            }
                            is SteelPumpBlock -> {
                                val blockEntity = level.getBlockEntity(hitData.pos) as AbstractPumpBlockEntity
                                val stack = blockEntity.fluidTank.getFluidInTank(0)
                                if (stack.amount > 0) {
                                    info.horizontal().text(fluidType(stack))
                                    info.horizontal().text(fluidAmount(stack))
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}