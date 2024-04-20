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

package net.benjimadness.triad.gui

import net.benjimadness.triad.TriadMod
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractFurnaceMenu
import kotlin.math.ceil

class GeneratorScreen(
    menu: GeneratorMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<GeneratorMenu>(menu, playerInventory, title) {
    private val bg = ResourceLocation(TriadMod.MODID, "textures/gui/container/generator.png")
    private val progressSprite = ResourceLocation("container/furnace/lit_progress")
    private val powerSprite = ResourceLocation(TriadMod.MODID, "container/generator/power")

    override fun init() {
        super.init()
        titleLabelX = (imageWidth - font.width(title)) / 2
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        if (mouseX >= leftPos + 97 && mouseX <= leftPos + 115 && mouseY >= topPos + 29 && mouseY <= topPos + 64)
            graphics.renderTooltip(font, Component.literal("${menu.getAbsolutePower()} FE"), mouseX, mouseY)
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(bg, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        if (menu.isPowered()) {
            val spriteAmount = (ceil(menu.getProgress() * 13) + 1).toInt()
            graphics.blitSprite(progressSprite, 14, 14, 0, 14 - spriteAmount, leftPos + 63, topPos + 32 + 14 - spriteAmount, 14, spriteAmount)
        }
        if (menu.getPower() > 0) {
            val spriteAmount = ceil(menu.getPower() * 34).toInt()
            graphics.blitSprite(powerSprite, 16, 34, 0, 34 - spriteAmount, leftPos + 98, topPos + 30 + 34 - spriteAmount, 16, spriteAmount)
        }
    }
}