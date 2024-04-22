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
import net.benjimadness.triad.api.item.ReusableItem
import net.benjimadness.triad.item.armor.TriadArmorItem
import net.benjimadness.triad.item.armor.TriadArmorMaterials
import net.minecraft.world.item.*
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier

object TriadItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(TriadMod.MODID)

    val TIN_DUST: Item by registerItem("tin_dust")
    val TIN_INGOT: Item by registerItem("tin_ingot")
    val RAW_TIN: Item by registerItem("raw_tin")
    val COPPER_DUST: Item by registerItem("copper_dust")
    val BRONZE_DUST: Item by registerItem("bronze_dust")
    val BRONZE_INGOT: Item by registerItem("bronze_ingot")
    val STEEL_DUST: Item by registerItem("steel_dust")
    val STEEL_INGOT: Item by registerItem("steel_ingot")
    val IRON_DUST: Item by registerItem("iron_dust")
    val GOLD_DUST: Item by registerItem("gold_dust")
    val STONE_MORTAR: Item by registerItem("stone_mortar") {
        ReusableItem(Item.Properties().durability(100).setNoRepair())
    }
    val STEEL_BLADE: Item by registerItem("steel_blade") {
        ReusableItem(Item.Properties().durability(100).setNoRepair(), "steel")
    }
    val BRONZE_BLADE: Item by registerItem("bronze_blade") {
        ReusableItem(Item.Properties().durability(10).setNoRepair(), "bronze")
    }
    val BRONZE_PICKAXE: Item by registerItem("bronze_pickaxe") {
        PickaxeItem(Tiers.IRON, 2, -3F, Item.Properties())
    }
    val BRONZE_AXE: Item by registerItem("bronze_axe") {
        AxeItem(Tiers.IRON, 6F, -3.5F, Item.Properties())
    }
    val BRONZE_SHOVEL: Item by registerItem("bronze_shovel") {
        ShovelItem(Tiers.IRON, 2F, -3.2F, Item.Properties())
    }
    val BRONZE_SWORD: Item by registerItem("bronze_sword") {
        SwordItem(Tiers.IRON, 4, -2.8F, Item.Properties())
    }
    val BRONZE_HOE: Item by registerItem("bronze_hoe") {
        HoeItem(Tiers.IRON, -1, -1.2F, Item.Properties())
    }
    val BRONZE_CHESTPLATE: Item by registerItem("bronze_chestplate") {
        TriadArmorItem(TriadArmorMaterials.BRONZE, ArmorItem.Type.CHESTPLATE)
    }
    val BRONZE_HELMET: Item by registerItem("bronze_helmet") {
        TriadArmorItem(TriadArmorMaterials.BRONZE, ArmorItem.Type.HELMET)
    }
    val BRONZE_LEGGINGS: Item by registerItem("bronze_leggings") {
        TriadArmorItem(TriadArmorMaterials.BRONZE, ArmorItem.Type.LEGGINGS)
    }
    val BRONZE_BOOTS: Item by registerItem("bronze_boots") {
        TriadArmorItem(TriadArmorMaterials.BRONZE, ArmorItem.Type.BOOTS)
    }
    val STEEL_PICKAXE: Item by registerItem("steel_pickaxe") {
        PickaxeItem(Tiers.DIAMOND, 1, -2.8F, Item.Properties())
    }
    val STEEL_AXE: Item by registerItem("steel_axe") {
        AxeItem(Tiers.DIAMOND, 5F, -3F, Item.Properties())
    }
    val STEEL_SHOVEL: Item by registerItem("steel_shovel") {
        ShovelItem(Tiers.DIAMOND, 1.5F, -3F, Item.Properties())
    }
    val STEEL_SWORD: Item by registerItem("steel_sword") {
        SwordItem(Tiers.DIAMOND, 3, -2.4F, Item.Properties())
    }
    val STEEL_HOE: Item by registerItem("steel_hoe") {
        HoeItem(Tiers.DIAMOND, -3, 0F, Item.Properties())
    }
    val STEEL_CHESTPLATE: Item by registerItem("steel_chestplate") {
        TriadArmorItem(TriadArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE)
    }
    val STEEL_HELMET: Item by registerItem("steel_helmet") {
        TriadArmorItem(TriadArmorMaterials.STEEL, ArmorItem.Type.HELMET)
    }
    val STEEL_LEGGINGS: Item by registerItem("steel_leggings") {
        TriadArmorItem(TriadArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS)
    }
    val STEEL_BOOTS: Item by registerItem("steel_boots") {
        TriadArmorItem(TriadArmorMaterials.STEEL, ArmorItem.Type.BOOTS)
    }

    private fun <T : Item> registerItem(name: String, item: Supplier<T>) = REGISTRY.register(name, item)
    private fun registerItem(name: String) = registerItem(name) { Item(Item.Properties()) }
}