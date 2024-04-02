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

package net.benjimadness.triad.item.armor

import net.benjimadness.triad.registry.TriadItems
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.Supplier

enum class TriadArmorMaterials(
    private val durabilityMultiplier: Int,
    private val protectionFunctionForType: HashMap<ArmorItem.Type, Int>,
    private val enchantmentValue: Int,
    private val sound: SoundEvent,
    private val toughness: Float,
    private val knockbackResistance: Float,
    private val repairIngredient: Supplier<Ingredient>
) : StringRepresentable, ArmorMaterial {
    BRONZE(10,
        hashMapOf
            (
            ArmorItem.Type.HELMET to 2,
            ArmorItem.Type.LEGGINGS to 5,
            ArmorItem.Type.CHESTPLATE to 6,
            ArmorItem.Type.BOOTS to 2
        ),
        15,
        SoundEvents.ARMOR_EQUIP_IRON,
        0F,
        0.25F,
        { Ingredient.of(TriadItems.BRONZE_INGOT) }),
    STEEL(33,
        hashMapOf
            (
            ArmorItem.Type.HELMET to 3,
            ArmorItem.Type.LEGGINGS to 6,
            ArmorItem.Type.CHESTPLATE to 8,
            ArmorItem.Type.BOOTS to 3
        ),
        12,
        SoundEvents.ARMOR_EQUIP_IRON,
        1F,
        0.4F,
        { Ingredient.of(TriadItems.STEEL_INGOT) });


    private val healthFunctionForType =
        hashMapOf(
            ArmorItem.Type.BOOTS to 13,
            ArmorItem.Type.LEGGINGS to 15,
            ArmorItem.Type.CHESTPLATE to 16,
            ArmorItem.Type.HELMET to 11
        )

    override fun getEnchantmentValue(): Int = enchantmentValue
    override fun getEquipSound(): SoundEvent = sound
    override fun getKnockbackResistance(): Float = knockbackResistance
    override fun getName(): String = name.lowercase()
    override fun getRepairIngredient(): Ingredient = repairIngredient.get()
    override fun getToughness(): Float = toughness
    override fun getDefenseForType(type: ArmorItem.Type): Int = protectionFunctionForType[type] ?: 2
    override fun getDurabilityForType(type: ArmorItem.Type): Int =
        (healthFunctionForType[type] ?: 11) * durabilityMultiplier

    override fun getSerializedName(): String = getName()
}