package com.enderio.core.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractClientSlotListTooltip implements ClientTooltipComponent {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot");

    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;

    private final int maxSlotWidth;

    public AbstractClientSlotListTooltip() {
        this.maxSlotWidth = 5;
    }

    public AbstractClientSlotListTooltip(int maxSlotWidth) {
        this.maxSlotWidth = maxSlotWidth;
    }

    protected abstract int slotCount();

    protected abstract void renderSlotContent(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font);

    @Override
    public int getHeight() {
        return backgroundHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return backgroundWidth();
    }

    private int gridSizeX() {
        return Math.min(maxSlotWidth, slotCount());
    }

    private int gridSizeY() {
        return (int)Math.ceil(((double)slotCount()) / (double)this.gridSizeX());
    }

    private int backgroundWidth() {
        return this.gridSizeX() * SLOT_SIZE_X + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * SLOT_SIZE_Y + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if (slotCount() <= 0) {
            return;
        }
        
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        guiGraphics.blitSprite(BACKGROUND_SPRITE, x, y, this.backgroundWidth(), this.backgroundHeight());

        int k = 0;
        for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
                int j1 = x + i1 * 18 + 1;
                int k1 = y + l * 20 + 1;
                this.renderSlot(j1, k1, k++, guiGraphics, font);
            }
        }
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (itemIndex >= slotCount()) {
            return;
        }

        this.blit(guiGraphics, x, y, SLOT_SPRITE, 18, 20);
        renderSlotContent(x, y, itemIndex, guiGraphics, font);
    }

    protected void blit(GuiGraphics guiGraphics, int x, int y, ResourceLocation sprite, int w, int h) {
        guiGraphics.blitSprite(sprite, x, y, 0, w, h);
    }
}
