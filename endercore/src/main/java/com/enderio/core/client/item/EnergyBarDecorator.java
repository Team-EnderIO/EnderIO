package com.enderio.core.client.item;

import com.enderio.core.common.energy.ItemStackEnergy;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class EnergyBarDecorator implements IItemDecorator {
    public static final EnergyBarDecorator INSTANCE = new EnergyBarDecorator();

    public static final int BAR_COLOR = 0xFF5CDBD5;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        // Hide bar if no energy to hold
        int maxEnergyStored = ItemStackEnergy.getMaxEnergyStored(stack);
        if (maxEnergyStored <= 0) {
            return false;
        }

        int energyStored = ItemStackEnergy.getEnergyStored(stack);
        float filledRatio = energyStored / (float) maxEnergyStored;

        // Take the durability bar's place when it isn't shown, otherwise sit directly above it.
        int row = stack.isBarVisible() ? ItemBarRenderer.DURABILITY_BAR_ROW - 2 : ItemBarRenderer.DURABILITY_BAR_ROW;

        ItemBarRenderer.renderBarWithBackground(guiGraphics, filledRatio, xOffset, yOffset, row, BAR_COLOR);
        return false;
    }
}
