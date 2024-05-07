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

package net.benjimadness.triad.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.benjimadness.triad.registry.TriadRecipes
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class SawmillRecipe(
    val input: Ingredient,
    val output: ItemStack
) : Recipe<Container> {

    override fun matches(container: Container, level: Level): Boolean = input.test(container.getItem(0))

    override fun getRemainingItems(container: Container): NonNullList<ItemStack> =
        NonNullList.create()

    override fun assemble(container: Container, registry: HolderLookup.Provider): ItemStack = getResultItem(registry).copy()

    override fun canCraftInDimensions(x: Int, y: Int): Boolean = false

    override fun getResultItem(registry: HolderLookup.Provider): ItemStack = output

    override fun getSerializer(): RecipeSerializer<*> = TriadRecipes.SAWMILL_RECIPE_SERIALIZER

    override fun getType(): RecipeType<*> = TriadRecipes.SAWMILL_RECIPE_TYPE

    class Serializer : RecipeSerializer<SawmillRecipe> {
        override fun codec(): MapCodec<SawmillRecipe> = RecordCodecBuilder.mapCodec {
            it.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SawmillRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(SawmillRecipe::output)
            ).apply(it, ::SawmillRecipe)
        }

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SawmillRecipe> =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SawmillRecipe::input,
                ItemStack.STREAM_CODEC, SawmillRecipe::output,
                ::SawmillRecipe
            )
    }
}

