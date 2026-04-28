package com.enderio.core.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public abstract class AbstractClientSlotListTooltip implements ClientTooltipComponent {

    private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");

    private static final int MAX_SLOTS_SHOWN = 12;
    private static final int WIDTH = 4 * 24;

    protected abstract int slotCount();

    private int displayedSlotCount() {
        return Math.min(slotCount(), MAX_SLOTS_SHOWN);
    }

    protected abstract void extractSlotContents(int x, int y, int itemIndex, GuiGraphicsExtractor guiGraphics, Font font);

    @Override
    public int getHeight(Font font) {
        return backgroundHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return WIDTH;
    }

    private int getContentXOffset(int tooltipWidth) {
        return (tooltipWidth - WIDTH) / 2;
    }

    private int gridSizeY() {
        return Mth.positiveCeilDiv(this.displayedSlotCount(), 4);
    }

    private int backgroundHeight() {
        return this.itemGridHeight();
    }

    private int itemGridHeight() {
        return this.gridSizeY() * 24;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        boolean isOverflowing = slotCount() > 12;

        int xStartPos = x + getContentXOffset(w);
        int yStartPos = y;
        int slotIndex = 0;

        for(int rowNumber = 1; rowNumber <= this.gridSizeY(); ++rowNumber) {
            for(int columnNumber = 1; columnNumber <= 4; ++columnNumber) {
                int drawX = xStartPos + (columnNumber - 1) * 24;
                int drawY = yStartPos + (rowNumber - 1) * 24;
                if (shouldRenderSurplusText(isOverflowing, slotIndex)) {
                    extractCount(drawX, drawY, slotCount() - displayedSlotCount(), font, graphics);
                } else if (shouldRenderItemSlot(slotIndex)) {
                    this.extractSlot(drawX, drawY, slotIndex, graphics, font);
                    ++slotIndex;
                }
            }
        }
    }

    private static void extractCount(int drawX, int drawY, int hiddenItemCount, Font font, GuiGraphicsExtractor graphics) {
        graphics.centeredText(font, "+" + hiddenItemCount, drawX + 12, drawY + 10, -1);
    }

    private static boolean shouldRenderSurplusText(boolean isOverflowing, int slotIndex) {
        return isOverflowing && slotIndex == 11;
    }

    private boolean shouldRenderItemSlot(int slotNumber) {
        return slotCount() >= slotNumber;
    }

    private void extractSlot(int x, int y, int itemIndex, GuiGraphicsExtractor guiGraphics, Font font) {
        if (itemIndex >= slotCount()) {
            return;
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, x, y, 24, 24);
        extractSlotContents(x + 4, y + 4, itemIndex, guiGraphics, font);
    }
}
