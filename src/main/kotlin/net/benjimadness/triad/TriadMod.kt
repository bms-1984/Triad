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

package net.benjimadness.triad

import com.mojang.logging.LogUtils
import net.benjimadness.triad.config.TriadConfig
import net.benjimadness.triad.gui.GrinderScreen
import net.benjimadness.triad.item.tool.TriadToolTiers
import net.benjimadness.triad.registry.*
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Tiers
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.TierSortingRegistry
import org.slf4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(TriadMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object TriadMod {
    const val MODID = "triad"
    val LOGGER: Logger = LogUtils.getLogger()

    init {
        TierSortingRegistry.registerTier(
            TriadToolTiers.BRONZE,
            ResourceLocation(MODID, "bronze"),
            listOf(Tiers.IRON),
            listOf(Tiers.DIAMOND)
        )
        TierSortingRegistry.registerTier(
            TriadToolTiers.STEEL,
            ResourceLocation(MODID, "steel"),
            listOf(Tiers.DIAMOND),
            listOf(Tiers.NETHERITE)
        )
        runForDist(
            clientTarget = { MOD_BUS.addListener(::onClientSetup) },
            serverTarget = { MOD_BUS.addListener(::onServerSetup) })
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TriadConfig.SPEC)
        TriadBlocks.REGISTRY.register(MOD_BUS)
        TriadItems.REGISTRY.register(MOD_BUS)
        TriadBlockEntities.REGISTRY.register(MOD_BUS)
        MOD_BUS.addListener(::registerCapabilities)
        TriadRecipes.TYPE_REGISTRY.register(MOD_BUS)
        TriadRecipes.SERIALIZER_REGISTRY.register(MOD_BUS)
        TriadTabs.REGISTRY.register(MOD_BUS)
        TriadMenus.REGISTRY.register(MOD_BUS)
    }

    @SubscribeEvent
    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Trying Triad, David, Stephen, Neil, Graham, Joni, YESSIREE!")
        if (TriadConfig.disableVanillaBees) {
            LOGGER.info("Disabling vanilla bees, sir! Send her on forward.")
        }
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            TriadBlockEntities.REDSTONE_GRINDER_BLOCK_ENTITY_TYPE
        ) { o, _ -> o.itemHandler }
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork { MenuScreens.register(TriadMenus.GRINDER_MENU_TYPE.get(), ::GrinderScreen) }
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {}
}
