package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blocks.alloy.PrimitiveAlloySmelterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PrimitiveAlloySmelterScreen extends MachineScreen<PrimitiveAlloySmelterMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIOBase
            .loc("textures/gui/screen/primitive_alloy_smelter.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public PrimitiveAlloySmelterScreen(PrimitiveAlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new ProgressWidget.BottomUp(BG_TEXTURE, menu::getBurnProgress, getGuiLeft() + 41,
                getGuiTop() + 37, 14, 14, 176, 0, false));
        addRenderableOnly(new ProgressWidget.LeftRight(BG_TEXTURE, menu::getCraftingProgress, getGuiLeft() + 79,
                getGuiTop() + 35, 24, 17, 176, 14));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
