package com.enderio.core.client.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        var fluidHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);

        if (fluidHandler == null) {
            return false;
        }

        int amount = fluidHandler.getAmountAsInt(0);

        if (amount <= 0) {
            return false;
        }

        var fluidResource = fluidHandler.getResource(0);

        float fillRatio = 1.0F
                - (float) amount / (float) fluidHandler.getCapacityAsInt(0, fluidResource);
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidResource.getFluid());

        ItemBarRenderer.renderBar(guiGraphics, fillRatio, xOffset, yOffset, props.getTintColor());
        return false;
    }
}
