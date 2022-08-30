package com.enderio.core.client.item;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidBarDecorator implements IItemDecorator {
    public static final FluidBarDecorator INSTANCE = new FluidBarDecorator();

    @Override
    public boolean render(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset) {
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
            if (handler.getFluidInTank(0).getAmount() <= 0)
                return;

            float fillRatio = 1.0F - (float) handler.getFluidInTank(0).getAmount() / (float) handler.getTankCapacity(0);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(handler.getFluidInTank(0).getFluid());

            ItemBarRenderer.renderBar(fillRatio, xOffset, yOffset, blitOffset, props.getTintColor());
        });
        return false;
    }
}
