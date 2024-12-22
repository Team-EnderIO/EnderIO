package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.machines.common.blocks.enchanter.EnchanterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnchanterScreen extends EnderContainerScreen<EnchanterMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/screen/enchanter.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public EnchanterScreen(EnchanterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        if (menu.getCurrentCost() < 0) {
            return;
        }

        int colour = 8453920; // green
        MutableComponent component = Component.translatable("container.repair.cost", this.getMenu().getCurrentCost());
        if (Minecraft.getInstance().player.experienceLevel < this.getMenu().getCurrentCost()
                && !Minecraft.getInstance().player.isCreative()) {
            colour = 16736352; // red
        }

        guiGraphics.drawCenteredString(this.font, component, (width - getXSize()) / 2 + getXSize() / 2,
                (height - getYSize()) / 2 + 57, colour);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
