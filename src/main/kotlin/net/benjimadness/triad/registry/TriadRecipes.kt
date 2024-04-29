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
import net.benjimadness.triad.recipe.GrinderRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier

object TriadRecipes {
    val TYPE_REGISTRY: DeferredRegister<RecipeType<*>> = DeferredRegister.create(Registries.RECIPE_TYPE, TriadMod.MODID)
    val SERIALIZER_REGISTRY: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, TriadMod.MODID)

    val GRINDER_RECIPE_TYPE: RecipeType<GrinderRecipe> by registerRecipeType("grinder_recipe_type") { object : RecipeType<GrinderRecipe>{} }
    val GRINDER_RECIPE_SERIALIZER: RecipeSerializer<*> by registerRecipeSerializer("grinding") { GrinderRecipe.Serializer() }

    private fun <T : RecipeType<*>> registerRecipeType(name: String, recipeType: Supplier<T>) =
        TYPE_REGISTRY.register(name, recipeType)

    private fun <T : RecipeSerializer<*>> registerRecipeSerializer(name: String, recipeSerializer: Supplier<T>) =
        SERIALIZER_REGISTRY.register(name, recipeSerializer)
}

