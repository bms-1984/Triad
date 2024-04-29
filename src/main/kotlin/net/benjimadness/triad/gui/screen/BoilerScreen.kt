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
import net.benjimadness.triad.gui.menu.BoilerMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import kotlin.math.ceil

class BoilerScreen(
    menu: BoilerMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<BoilerMenu>(menu, playerInventory, title) {
    private val bg = ResourceLocation(TriadMod.MODID, "textures/gui/container/boiler.png")
    private val progressSprite = ResourceLocation("container/furnace/lit_progress")
    private val waterSprite = ResourceLocation(TriadMod.MODID, "container/water")
    private val steamSprite = ResourceLocation(TriadMod.MODID, "container/steam")

    override fun init() {
        super.init()
        titleLabelX = (imageWidth - font.width(title)) / 2
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        if (mouseX >= leftPos + 43 && mouseX <= leftPos + 60 && mouseY >= topPos + 29 && mouseY <= topPos + 64)
            graphics.renderTooltip(font, Component.literal("${menu.getAbsoluteWater()} L"), mouseX, mouseY)
        if (mouseX >= leftPos + 115 && mouseX <= leftPos + 132 && mouseY >= topPos + 29 && mouseY <= topPos + 64)
            graphics.renderTooltip(font, Component.literal("${menu.getAbsoluteSteam()} L"), mouseX, mouseY)
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, mouseX: Int, mouseY: Int) {
        graphics.blit(bg, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        if (menu.isPowered()) {
            val spriteAmount = (ceil(menu.getProgress() * 13) + 1).toInt()
            graphics.blitSprite(progressSprite, 14, 14, 0, 14 - spriteAmount, leftPos + 81, topPos + 32 + 14 - spriteAmount, 14, spriteAmount)
        }
        if (menu.getSteam() > 0) {
            val spriteAmount = ceil(menu.getSteam() * 34).toInt()
            graphics.blitSprite(steamSprite, 16, 34, 0, 34 - spriteAmount, leftPos + 116, topPos + 30 + 34 - spriteAmount, 16, spriteAmount)
        }
        if (menu.getWater() > 0) {
            val spriteAmount = ceil(menu.getWater() * 34).toInt()
            graphics.blitSprite(waterSprite, 16, 34, 0, 34 - spriteAmount, leftPos + 44, topPos + 30 + 34 - spriteAmount, 16, spriteAmount)
        }
    }
}