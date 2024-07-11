package com.enderio.core.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;

@Deprecated(forRemoval = true, since = "7.0")
public interface EnderScreen {

    default Screen getScreen() {
        return (Screen) this;
    }

    default void renderSimpleArea(GuiGraphics guiGraphics, Vector2i pos, Vector2i pos2) {
        guiGraphics.fill(pos.x(), pos.y(), pos2.x(), pos2.y(), 0xFF8B8B8B);
        guiGraphics.fill(pos.x(), pos.y(), pos2.x() - 1, pos2.y() - 1, 0xFF373737);
        guiGraphics.fill(pos.x() + 1, pos.y() + 1, pos2.x(), pos2.y(), 0xFFFFFFFF);
        guiGraphics.fill(pos.x() + 1, pos.y() + 1, pos2.x() - 1, pos2.y() - 1, 0xFF8B8B8B);
    }
}
