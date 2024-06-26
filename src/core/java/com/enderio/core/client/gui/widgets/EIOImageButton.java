package com.enderio.core.client.gui.widgets;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.client.gui.screen.EIOScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

// Wrapper for ImageButton to draw background.
// Can be removed after GUI redesign if no longer required.
public class EIOImageButton extends ImageButton {

    private final EIOScreen<?> screen;
    @Nullable private final Component message;

    public EIOImageButton(EIOScreen<?> screen, int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress, @Nullable Component message) {
        super(x, y, width, height, sprites, onPress);
        this.screen = screen;
        this.message = message;
    }

    public EIOImageButton(EIOScreen<?> screen, int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        this(screen, x, y, width, height, sprites, onPress, null);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        screen.renderSimpleArea(guiGraphics, new Vector2i(getX(), getY()), new Vector2i(getX() + getWidth(), getY() + getHeight()));
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isHovered() && message != null) {
            guiGraphics.renderTooltip(screen.getMinecraft().font, message, mouseX, mouseY);
        }
    }
}
