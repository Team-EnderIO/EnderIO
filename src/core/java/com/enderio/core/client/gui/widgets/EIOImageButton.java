package com.enderio.core.client.gui.widgets;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.EnderCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Wrapper for ImageButton to draw background.
// Can be removed after GUI redesign if no longer required.
public class EIOImageButton extends ImageButton {

    private final EIOScreen<?> addedOn;
    private static final ResourceLocation BUTTON_TEXTURE = EnderCore.loc("textures/gui/checkbox.png");

    public EIOImageButton(EIOScreen<?> addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation resourceLocation,
        OnPress onPress) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.addedOn = addedOn;
    }

    public EIOImageButton(EIOScreen<?> addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
        ResourceLocation resourceLocation, OnPress onPress) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, onPress);
        this.addedOn = addedOn;
    }

    public EIOImageButton(EIOScreen<?> addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress);
        this.addedOn = addedOn;
    }

    public EIOImageButton(EIOScreen<?> addedOn, int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex,
        ResourceLocation resourceLocation, int textureWidth, int textureHeight, OnPress onPress, Component message) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffTex, resourceLocation, textureWidth, textureHeight, onPress, message);
        this.addedOn = addedOn;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(BUTTON_TEXTURE, getX(), getY(), this.width, this.height, this.isHovered ? 28 : 0, 0, 14, 14, 256, 256);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
