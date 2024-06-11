package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

// Wrapper for ImageButton to draw background.
// Can be removed after GUI redesign if no longer required.
public class EIOImageButton extends ImageButton {

    private final EIOScreen<?> addedOn;

    public EIOImageButton(EIOScreen<?> addedOn, int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
        this.addedOn = addedOn;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        addedOn.renderSimpleArea(guiGraphics, new Vector2i(getX(), getY()), new Vector2i(getX() + getWidth(), getY() + getHeight()));
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }
}
