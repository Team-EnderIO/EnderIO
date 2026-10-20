package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.recipebook.EnchanterRecipeBookComponent;
import com.enderio.enderio.content.enchanter.EnchanterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class EnchanterScreen extends AbstractRecipeBookScreen<EnchanterMenu> {
    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/enchanter.png");
    public EnchanterScreen(EnchanterMenu menu, Inventory playerInventory, Component title) {
        super(menu, new EnchanterRecipeBookComponent(menu), playerInventory, title);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 20);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Ender IO machine screens leave labels to their background artwork.
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (menu.getCurrentCost() < 0) {
            return;
        }

        int colour = 8453920; // green
        MutableComponent component = Component.translatable("container.repair.cost", this.getMenu().getCurrentCost());
        if (Minecraft.getInstance().player.experienceLevel < this.getMenu().getCurrentCost()
                && !Minecraft.getInstance().player.isCreative()) {
            colour = 16736352; // red
        }

        graphics.centeredText(this.font, component, this.leftPos + this.imageWidth / 2,
                this.topPos + 57, colour);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
