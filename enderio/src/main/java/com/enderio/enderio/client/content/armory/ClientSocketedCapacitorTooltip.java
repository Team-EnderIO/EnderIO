package com.enderio.enderio.client.content.armory;

import com.enderio.core.client.gui.tooltip.AbstractClientSlotListTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Renders a single bundle-style slot holding the socketed capacitor (or nothing, for an empty socket).
 */
public class ClientSocketedCapacitorTooltip extends AbstractClientSlotListTooltip {
    private final ItemStack capacitor;

    public ClientSocketedCapacitorTooltip(SocketedCapacitorTooltip tooltip) {
        super(1);
        this.capacitor = tooltip.capacitor();
    }

    @Override
    protected int slotCount() {
        return 1;
    }

    @Override
    protected void renderSlotContent(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (capacitor.isEmpty()) {
            return;
        }

        guiGraphics.renderItem(capacitor, x + 1, y + 1, itemIndex);
        guiGraphics.renderItemDecorations(font, capacitor, x + 1, y + 1);
    }
}
