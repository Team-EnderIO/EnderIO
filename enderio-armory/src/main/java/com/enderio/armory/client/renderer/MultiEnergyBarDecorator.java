package com.enderio.armory.client.renderer;

import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.common.energy.ItemStackEnergy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class MultiEnergyBarDecorator implements IItemDecorator {

    public static final MultiEnergyBarDecorator INSTANCE = new MultiEnergyBarDecorator();

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        // Hide bar if no energy to hold
        int maxEnergyStored = ItemStackEnergy.getMaxEnergyStored(stack);
        if (maxEnergyStored <= 0) {
            return false;
        }

        int energyStored = ItemStackEnergy.getEnergyStored(stack);
        float fillRatio = energyStored / (float) maxEnergyStored;

        int x = xOffset + 2;
        int y = yOffset + 12;
        boolean renderShadow = false;
        if (stack.getDamageValue() == 0) {
            y++;
            renderShadow = true;
        }
        int width = Math.round(13.0F * fillRatio);
        int height = 1;

        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + height, 0xFF000000);
        guiGraphics.fill(RenderType.guiOverlay(), x, y, x + width, y + height, EnergyBarDecorator.BAR_COLOR_ARGB);

        if (renderShadow) {
            y++;
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 13, y + height, 0xFF000000);
        }
        return true;
    }

}
