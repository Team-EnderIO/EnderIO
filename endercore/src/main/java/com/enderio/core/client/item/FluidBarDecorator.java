package com.enderio.core.client.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    public static final int BAR_COLOR = 0xB168E4;

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
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

        // Get fluid model
        FluidState fluidState = fluidResource.getFluid().defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        // Get tint color
        int color = 0xFFFFFFFF;
        if (fluidModel.fluidTintSource() != null) {
            color = fluidModel.fluidTintSource().color(fluidState);
        }

        ItemBarRenderer.extractBar(graphics, fillRatio, xOffset, yOffset, color);
        return false;
    }
}
