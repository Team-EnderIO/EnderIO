package com.enderio.enderio.client.content.filters.soul;

import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.foundation.soul.SoulUtility;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientEnderSoulFilterTooltip implements ClientTooltipComponent {

    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/background");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot");
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;

    private final EnderSoulFilter filter;

    public ClientEnderSoulFilterTooltip(EnderSoulFilter filter) {
        this.filter = filter;
    }

    @Override
    public int getHeight() {
        return backgroundHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return backgroundWidth();
    }

    private int gridSizeX() {
        return Math.min(5, nonEmptyItems().size());
    }

    private int gridSizeY() {
        return (int)Math.ceil(((double)nonEmptyItems().size()) / (double)this.gridSizeX());
    }

    private int backgroundWidth() {
        return this.gridSizeX() * 18 + 2;
    }

    private int backgroundHeight() {
        return this.gridSizeY() * 20 + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
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
        if (itemIndex >= nonEmptyItems().size()) {
            return;
        }

        ItemStack itemstack = nonEmptyItems().get(itemIndex);
        this.blit(guiGraphics, x, y, SLOT_SPRITE, 18, 20);
        guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
        guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, ResourceLocation sprite, int w, int h) {
        guiGraphics.blitSprite(sprite, x, y, 0, w, h);
    }

    private List<ItemStack> nonEmptyItems() {
        return this.filter.matches().stream()
            .filter(soul -> !soul.isEmpty())
            .map(SoulUtility::getStackForDisplay)
            .toList();
    }
}
