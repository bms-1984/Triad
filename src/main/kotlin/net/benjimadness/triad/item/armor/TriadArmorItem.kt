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

import net.benjimadness.triad.TriadMod
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.ItemStack

class TriadArmorItem(material: ArmorMaterial, type: Type) : ArmorItem(material, type, Properties()) {
    override fun getArmorTexture(stack: ItemStack?, entity: Entity?, slot: EquipmentSlot?, type: String?): String? =
        if (stack == null || stack.item !is ArmorItem) null
        else if ((stack.item as ArmorItem).type == Type.LEGGINGS) TriadMod.MODID + ":" + "textures/models/armor/" + material.name + "_layer_2.png"
        else TriadMod.MODID + ":" + "textures/models/armor/" + material.name + "_layer_1.png"
}