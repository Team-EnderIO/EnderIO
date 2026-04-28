package com.enderio.core.client.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public abstract class AbstractClientSlotListTooltip implements ClientTooltipComponent {

    private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");

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

    protected abstract void extractSlotContent(int x, int y, int itemIndex, GuiGraphicsExtractor guiGraphics, Font font);

    @Override
    public int getHeight(Font font) {
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
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        if (slotCount() <= 0) {
            return;
        }

        int i = this.gridSizeX();
        int j = this.gridSizeY();
//        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, x, y, this.backgroundWidth(), this.backgroundHeight());

        int k = 0;
        for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
                int j1 = x + i1 * 18 + 1;
                int k1 = y + l * 20 + 1;
                this.renderSlot(j1, k1, k++, graphics, font);
            }
        }
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphicsExtractor guiGraphics, Font font) {
        if (itemIndex >= slotCount()) {
            return;
        }

        // TODO: need to adopt the new bundle GUI format...
//        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, x, y, 24, 24);
        extractSlotContent(x, y, itemIndex, guiGraphics, font);
    }
}
