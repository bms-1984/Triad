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

package net.benjimadness.triad.gui.screen

import net.benjimadness.triad.TriadMod
import net.benjimadness.triad.gui.menu.SawmillMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import kotlin.math.ceil

class SawmillScreen(
    menu: SawmillMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<SawmillMenu>(menu, playerInventory, title) {
    private val bg = ResourceLocation(TriadMod.MODID, "textures/gui/container/furnace.png")
    private val progressSprite = ResourceLocation("container/furnace/burn_progress")

    override fun init() {
        super.init()
        titleLabelX = (imageWidth - font.width(title)) / 2
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(bg, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        val spriteAmount = ceil(menu.getProgress() * 24).toInt()
        graphics.blitSprite(progressSprite, 24, 16, 0, 0, leftPos + 79, topPos + 34, spriteAmount, 16)
    }
}