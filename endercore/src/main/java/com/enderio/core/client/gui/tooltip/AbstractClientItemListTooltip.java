package com.enderio.core.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractClientItemListTooltip extends AbstractClientSlotListTooltip {

    public AbstractClientItemListTooltip() {
        super();
    }

    public AbstractClientItemListTooltip(int maxSlotWidth) {
        super(maxSlotWidth);
    }

    protected abstract List<ItemStack> itemStacksToDisplay();

    @Override
    protected final int slotCount() {
        return itemStacksToDisplay().size();
    }

    @Override
    protected void renderSlotContent(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        var items = itemStacksToDisplay();
        if (itemIndex >= items.size()) {
            return;
        }

        ItemStack itemstack = items.get(itemIndex);
        guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
        guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
    }
}
