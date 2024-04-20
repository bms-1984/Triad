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
import net.benjimadness.triad.gui.GeneratorMenu
import net.benjimadness.triad.gui.GrinderMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object TriadMenus {
    val REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, TriadMod.MODID)

    val GRINDER_MENU_TYPE: DeferredHolder<MenuType<*>, MenuType<GrinderMenu>> = register("grinder_menu") {
        MenuType(
            IContainerFactory { id, inv, data -> GrinderMenu(id, inv, data.readBlockPos()) },
            FeatureFlags.DEFAULT_FLAGS
        )
    }
    val GENERATOR_MENU_TYPE: DeferredHolder<MenuType<*>, MenuType<GeneratorMenu>> = register("generator_menu") {
        MenuType(
            IContainerFactory { id, inv, data -> GeneratorMenu(id, inv, data.readBlockPos()) },
            FeatureFlags.DEFAULT_FLAGS
        )
    }

    private fun <T : MenuType<*>> register(name: String, menu: Supplier<T>): DeferredHolder<MenuType<*>, T> =
        REGISTRY.register(name, menu)
}