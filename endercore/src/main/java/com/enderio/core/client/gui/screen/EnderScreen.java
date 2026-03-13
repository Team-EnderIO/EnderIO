package com.enderio.core.client.gui.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;

@Deprecated(forRemoval = true, since = "7.0")
public interface EnderScreen {

    default Screen getScreen() {
        return (Screen) this;
    }

    default void renderSimpleArea(GuiGraphicsExtractor graphics, Vector2i pos, Vector2i pos2) {
        graphics.fill(pos.x(), pos.y(), pos2.x(), pos2.y(), 0xFF8B8B8B);
        graphics.fill(pos.x(), pos.y(), pos2.x() - 1, pos2.y() - 1, 0xFF373737);
        graphics.fill(pos.x() + 1, pos.y() + 1, pos2.x(), pos2.y(), 0xFFFFFFFF);
        graphics.fill(pos.x() + 1, pos.y() + 1, pos2.x() - 1, pos2.y() - 1, 0xFF8B8B8B);
    }
}
