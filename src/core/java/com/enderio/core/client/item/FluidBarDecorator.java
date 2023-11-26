package com.enderio.core.client.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.capabilities.Capabilities;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        stack.getCapability(Capabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            if (handler.getFluidInTank(0).getAmount() <= 0) {
                return;
            }

            float fillRatio = 1.0F - (float) handler.getFluidInTank(0).getAmount() / (float) handler.getTankCapacity(0);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(handler.getFluidInTank(0).getFluid());

            ItemBarRenderer.renderBar(guiGraphics, fillRatio, xOffset, yOffset, 0, props.getTintColor());
        });
        return false;
    }
}
