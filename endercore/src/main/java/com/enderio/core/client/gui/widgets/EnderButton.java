package com.enderio.core.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// The same as AbstractButton but does not show the white outline when focussed.
public abstract class EnderButton extends AbstractWidget {
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"),
            ResourceLocation.withDefaultNamespace("widget/button_disabled"),
            ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

    public EnderButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public abstract void onPress();

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(),
                this.getHeight());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        renderButtonFace(guiGraphics, mouseX, mouseY, partialTick);
    }

    public abstract void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }
}
