package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.enchanter.EnchanterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class EnchanterScreen extends EnderContainerScreen<EnchanterMenu> {
    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/enchanter.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public EnchanterScreen(EnchanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

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
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
