package com.enderio.core.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    protected void extractSlotContent(int x, int y, int itemIndex, GuiGraphicsExtractor guiGraphics, Font font) {
        var items = itemStacksToDisplay();
        if (itemIndex >= items.size()) {
            return;
        }

        ItemStack itemstack = items.get(itemIndex);
        guiGraphics.item(itemstack, x + 1, y + 1, itemIndex);
        guiGraphics.itemDecorations(font, itemstack, x + 1, y + 1);
    }
}
