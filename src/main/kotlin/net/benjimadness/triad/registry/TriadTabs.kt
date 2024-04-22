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

package net.benjimadness.triad.registry

import net.benjimadness.triad.TriadMod
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object TriadTabs {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB,
        TriadMod.MODID
    )
    private val TRIAD_RAW_MATERIALS_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> =
        REGISTRY.register("${TriadMod.MODID}_raw_materials") { ->
            CreativeModeTab.builder()
                .title(Component.translatableWithFallback("itemGroup.${TriadMod.MODID}_raw_materials", "Triad Raw Materials"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon { TriadItems.RAW_TIN.defaultInstance }
                .displayItems { _, output ->
                    run {
                        output.accept(TriadBlocks.TIN_ORE)
                        output.accept(TriadBlocks.DEEPSLATE_TIN_ORE)
                        output.accept(TriadBlocks.RAW_TIN_BLOCK)
                        output.accept(TriadBlocks.TIN_BLOCK)
                        output.accept(TriadBlocks.BRONZE_BLOCK)
                        output.accept(TriadBlocks.STEEL_BLOCK)
                        output.accept(TriadItems.RAW_TIN)
                        output.accept(TriadItems.TIN_INGOT)
                        output.accept(TriadItems.BRONZE_INGOT)
                        output.accept(TriadItems.STEEL_INGOT)
                        output.accept(TriadItems.TIN_DUST)
                        output.accept(TriadItems.BRONZE_DUST)
                        output.accept(TriadItems.STEEL_DUST)
                        output.accept(TriadItems.COPPER_DUST)
                        output.accept(TriadItems.IRON_DUST)
                        output.accept(TriadItems.GOLD_DUST)
                    }
                }
                .build()
        }
    private val TRIAD_TOOLS_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> =
        REGISTRY.register("${TriadMod.MODID}_tools") { ->
            CreativeModeTab.builder()
                .title(Component.translatableWithFallback("itemGroup.${TriadMod.MODID}_tools", "Triad Tools"))
                .withTabsBefore(TRIAD_RAW_MATERIALS_TAB.key)
                .icon { TriadItems.STONE_MORTAR.defaultInstance }
                .displayItems { _, output ->
                    run {
                        output.accept(TriadItems.STONE_MORTAR)
                        output.accept(TriadItems.BRONZE_SHOVEL)
                        output.accept(TriadItems.BRONZE_PICKAXE)
                        output.accept(TriadItems.BRONZE_AXE)
                        output.accept(TriadItems.BRONZE_HOE)
                        output.accept(TriadItems.STEEL_SHOVEL)
                        output.accept(TriadItems.STEEL_PICKAXE)
                        output.accept(TriadItems.STEEL_AXE)
                        output.accept(TriadItems.STEEL_HOE)
                    }
                }
                .build()
        }
    private val TRIAD_COMBAT_TAB = REGISTRY.register("${TriadMod.MODID}_combat") { ->
        CreativeModeTab.builder()
            .title(Component.translatableWithFallback("itemGroup.${TriadMod.MODID}_combat", "Triad Combat"))
            .withTabsBefore(TRIAD_TOOLS_TAB.key)
            .icon { TriadItems.BRONZE_SWORD.defaultInstance }
            .displayItems { _, output ->
                run {
                    output.accept(TriadItems.BRONZE_SWORD)
                    output.accept(TriadItems.STEEL_SWORD)
                    output.accept(TriadItems.BRONZE_AXE)
                    output.accept(TriadItems.STEEL_AXE)
                    output.accept(TriadItems.BRONZE_HELMET)
                    output.accept(TriadItems.BRONZE_CHESTPLATE)
                    output.accept(TriadItems.BRONZE_LEGGINGS)
                    output.accept(TriadItems.BRONZE_BOOTS)
                    output.accept(TriadItems.STEEL_HELMET)
                    output.accept(TriadItems.STEEL_CHESTPLATE)
                    output.accept(TriadItems.STEEL_LEGGINGS)
                    output.accept(TriadItems.STEEL_BOOTS)
                }
            }
            .build()
    }
    private val TRIAD_MACHINES_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> =
        REGISTRY.register("${TriadMod.MODID}_machines") { ->
            CreativeModeTab.builder()
                .title(Component.translatableWithFallback("itemGroup.${TriadMod.MODID}_machines", "Triad Machines"))
                .withTabsBefore(TRIAD_COMBAT_TAB.key)
                .icon { TriadBlocks.REDSTONE_GRINDER.asItem().defaultInstance }
                .displayItems { _, output ->
                    run {
                        output.accept(TriadBlocks.REDSTONE_GRINDER)
                        output.accept(TriadBlocks.BRONZE_ITEM_BOILER)
                        output.accept(TriadBlocks.BRONZE_TURBINE)
                    }
                }
                .build()
        }
    private val TRIAD_PARTS_TAB = REGISTRY.register("${TriadMod.MODID}_parts") { ->
        CreativeModeTab.builder()
            .title(Component.translatableWithFallback("itemGroup.${TriadMod.MODID}_parts", "Triad Parts"))
            .withTabsBefore(TRIAD_MACHINES_TAB.key)
            .icon { TriadItems.BRONZE_BLADE.defaultInstance }
            .displayItems { _, output ->
                run {
                    output.accept(TriadItems.BRONZE_BLADE)
                    output.accept(TriadItems.STEEL_BLADE)
                }
            }
            .build()
    }
}