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
import net.benjimadness.triad.api.block.Blades
import net.benjimadness.triad.api.item.ReusableItem
import net.benjimadness.triad.item.BladeItem
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier

object TriadItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(TriadMod.MODID)
    val ARMOR_MATERIAL_REGISTRY: DeferredRegister<ArmorMaterial> = DeferredRegister.create(Registries.ARMOR_MATERIAL, TriadMod.MODID)

    private val BRONZE_ARMOR_MATERIAL: DeferredHolder<ArmorMaterial, ArmorMaterial> = registerArmorMaterial("bronze") {
        ArmorMaterial(
            hashMapOf
                (
                ArmorItem.Type.HELMET to 2,
                ArmorItem.Type.LEGGINGS to 5,
                ArmorItem.Type.CHESTPLATE to 6,
                ArmorItem.Type.BOOTS to 2
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            { Ingredient.of(BRONZE_INGOT) },
            listOf(ArmorMaterial.Layer(ResourceLocation(TriadMod.MODID, "bronze"))),
            0F,
            0.25F
        )
    }
    private val STEEL_ARMOR_MATERIAL: DeferredHolder<ArmorMaterial, ArmorMaterial> = registerArmorMaterial("steel") {
        ArmorMaterial(
            hashMapOf
                (
                ArmorItem.Type.HELMET to 3,
                ArmorItem.Type.LEGGINGS to 6,
                ArmorItem.Type.CHESTPLATE to 8,
                ArmorItem.Type.BOOTS to 3
            ),
            12,
            SoundEvents.ARMOR_EQUIP_IRON,
            { Ingredient.of(STEEL_INGOT) },
            listOf(ArmorMaterial.Layer(ResourceLocation(TriadMod.MODID, "steel"))),
            1F,
            0.4F
        )
    }

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
        BladeItem(Item.Properties().durability(100).setNoRepair(), Blades.STEEL)
    }
    val BRONZE_BLADE: Item by registerItem("bronze_blade") {
        BladeItem(Item.Properties().durability(10).setNoRepair(), Blades.BRONZE)
    }
    val BRONZE_PICKAXE: Item by registerItem("bronze_pickaxe") {
        PickaxeItem(Tiers.IRON, Item.Properties())
    }
    val BRONZE_AXE: Item by registerItem("bronze_axe") {
        AxeItem(Tiers.IRON, Item.Properties())
    }
    val BRONZE_SHOVEL: Item by registerItem("bronze_shovel") {
        ShovelItem(Tiers.IRON, Item.Properties())
    }
    val BRONZE_SWORD: Item by registerItem("bronze_sword") {
        SwordItem(Tiers.IRON, Item.Properties())
    }
    val BRONZE_HOE: Item by registerItem("bronze_hoe") {
        HoeItem(Tiers.IRON, Item.Properties())
    }
    val BRONZE_CHESTPLATE: Item by registerItem("bronze_chestplate") {
        ArmorItem(BRONZE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, Item.Properties())
    }
    val BRONZE_HELMET: Item by registerItem("bronze_helmet") {
        ArmorItem(BRONZE_ARMOR_MATERIAL, ArmorItem.Type.HELMET, Item.Properties())
    }
    val BRONZE_LEGGINGS: Item by registerItem("bronze_leggings") {
        ArmorItem(BRONZE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, Item.Properties())
    }
    val BRONZE_BOOTS: Item by registerItem("bronze_boots") {
        ArmorItem(BRONZE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, Item.Properties())
    }
    val STEEL_PICKAXE: Item by registerItem("steel_pickaxe") {
        PickaxeItem(Tiers.DIAMOND, Item.Properties())
    }
    val STEEL_AXE: Item by registerItem("steel_axe") {
        AxeItem(Tiers.DIAMOND, Item.Properties())
    }
    val STEEL_SHOVEL: Item by registerItem("steel_shovel") {
        ShovelItem(Tiers.DIAMOND, Item.Properties())
    }
    val STEEL_SWORD: Item by registerItem("steel_sword") {
        SwordItem(Tiers.DIAMOND, Item.Properties())
    }
    val STEEL_HOE: Item by registerItem("steel_hoe") {
        HoeItem(Tiers.DIAMOND, Item.Properties())
    }
    val STEEL_CHESTPLATE: Item by registerItem("steel_chestplate") {
        ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, Item.Properties())
    }
    val STEEL_HELMET: Item by registerItem("steel_helmet") {
        ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, Item.Properties())
    }
    val STEEL_LEGGINGS: Item by registerItem("steel_leggings") {
        ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, Item.Properties())
    }
    val STEEL_BOOTS: Item by registerItem("steel_boots") {
        ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, Item.Properties())
    }

    private fun registerArmorMaterial(name: String, material: Supplier<ArmorMaterial>) = ARMOR_MATERIAL_REGISTRY.register(name, material)
    private fun <T : Item> registerItem(name: String, item: Supplier<T>) = REGISTRY.register(name, item)
    private fun registerItem(name: String) = registerItem(name) { Item(Item.Properties()) }
}