package com.enderio.core.client.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {
            if (fluidHandler.getFluidInTank(0).getAmount() <= 0) {
                return;
            }

            float fillRatio = 1.0F
                - (float) fluidHandler.getFluidInTank(0).getAmount() / (float) fluidHandler.getTankCapacity(0);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidHandler.getFluidInTank(0).getFluid());

            ItemBarRenderer.renderBar(guiGraphics, fillRatio, xOffset, yOffset, 0, props.getTintColor());
        });

        return false;
    }
}
