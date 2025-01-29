package com.enderio.core.client.item;

import com.enderio.core.common.energy.ItemStackEnergy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class EnergyBarDecorator implements IItemDecorator {
    public static final EnergyBarDecorator INSTANCE = new EnergyBarDecorator();

    // TODO: This color is difficult to see
    public static final int BAR_COLOR = 0xB168E4;
    public static final int BAR_COLOR_ARGB = 0xFFB168E4;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        // Hide bar if no energy to hold
        int maxEnergyStored = ItemStackEnergy.getMaxEnergyStored(stack);
        if (maxEnergyStored <= 0) {
            return false;
        }

        int energyStored = ItemStackEnergy.getEnergyStored(stack);

        // Determine fill ratio
        float fillRatio = energyStored / (float) maxEnergyStored;

        // Render the bar overlay
        ItemBarRenderer.renderBar(guiGraphics, fillRatio, xOffset, yOffset, 0, BAR_COLOR);
        return false;
    }
}
