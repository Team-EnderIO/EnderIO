package com.enderio.core.client.item;

import com.enderio.core.common.util.EnergyUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyBarDecorator implements IItemDecorator {
    public static final EnergyBarDecorator INSTANCE = new EnergyBarDecorator();

    // TODO: This color is difficult to see
    public static final int BAR_COLOR = 0x00B168E4;

    @Override
    public boolean render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset) {
        // Hide bar if no energy
        if (EnergyUtil.getMaxEnergyStored(stack) <= 0) {
            return false;
        }

        // Determine fill ratio
        float fillRatio = stack
            .getCapability(ForgeCapabilities.ENERGY)
            .map(energyStorage -> 1.0f - (float) energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored())
            .orElse(0f);

        // Render the bar overlay
        ItemBarRenderer.renderBar(fillRatio, xOffset, yOffset, blitOffset, BAR_COLOR);
        return false;
    }
}
