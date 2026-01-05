package com.enderio.core.client.gui.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

// The same as AbstractButton but does not show the white outline when focussed.
public abstract class EnderButton extends AbstractWidget {
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            Identifier.withDefaultNamespace("widget/button"),
            Identifier.withDefaultNamespace("widget/button_disabled"),
            Identifier.withDefaultNamespace("widget/button_highlighted"));

    public EnderButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public abstract void onPress(InputWithModifiers input);

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(),
                this.getHeight());
        renderButtonFace(guiGraphics, mouseX, mouseY, partialTick);
    }

    public abstract void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.onPress(event);
    }
}
