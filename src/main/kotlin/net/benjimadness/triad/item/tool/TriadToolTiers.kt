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

package net.benjimadness.triad.item.tool

import net.benjimadness.triad.registry.TriadItems
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block
import java.util.function.Supplier

enum class TriadToolTiers(
    private val uses: Int,
    private val speed: Float,
    private val damageBonus: Float,
    private val level: Int,
    private val enchantmentValue: Int,
    private val tag: TagKey<Block>,
    private val repairIngredient: Supplier<Ingredient>
) : Tier {
    BRONZE(
        200,
        6F,
        2F,
        2,
        22,
        BlockTags.NEEDS_IRON_TOOL,
        { Ingredient.of(TriadItems.BRONZE_INGOT) }),
    STEEL(
        1700,
        8F,
        3F,
        3,
        17,
        BlockTags.NEEDS_DIAMOND_TOOL,
        { Ingredient.of(TriadItems.STEEL_INGOT) });

    override fun getUses(): Int = uses

    override fun getSpeed(): Float = speed

    override fun getAttackDamageBonus(): Float = damageBonus

    @Deprecated("Deprecated by Mojang")
    override fun getLevel(): Int = level

    override fun getEnchantmentValue(): Int = enchantmentValue

    override fun getRepairIngredient(): Ingredient = repairIngredient.get()

    override fun getTag(): TagKey<Block>? = tag
}