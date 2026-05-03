package com.enderio.core.client.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    public static final int BAR_COLOR = 0xB168E4;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        var fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if (fluidHandler == null) {
            return false;
        }

        if (fluidHandler.getFluidInTank(0).getAmount() <= 0) {
            return false;
        }

        float fillRatio = 1.0F
                - (float) fluidHandler.getFluidInTank(0).getAmount() / (float) fluidHandler.getTankCapacity(0);
        ItemBarRenderer.renderBar(guiGraphics, fillRatio, xOffset, yOffset, 0, BAR_COLOR);
        return false;
    }
}
