package com.enderio.core.client.gui.screen;

import com.enderio.api.misc.Icon;
import com.enderio.api.misc.Vector2i;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public interface EnderScreen {

    default Screen getScreen() {
        return (Screen) this;
    }

    static void renderIcon(GuiGraphics guiGraphics, Vector2i pos, Icon icon) {
        if (!icon.shouldRender()) {
            return;
        }

        RenderSystem.enableBlend();
        guiGraphics.blit(icon.getTextureLocation(), pos.x(), pos.y(), icon.getRenderSize().x(), icon.getRenderSize().y(), icon.getTexturePosition().x(),
            icon.getTexturePosition().y(), icon.getIconSize().x(), icon.getIconSize().y(), icon.getTextureSize().x(), icon.getTextureSize().y());
        RenderSystem.disableBlend();
    }

    default void renderSimpleArea(GuiGraphics guiGraphics, Vector2i pos, Vector2i pos2) {
        guiGraphics.fill(pos.x(), pos.y(), pos2.x(), pos2.y(), 0xFF8B8B8B);
        guiGraphics.fill(pos.x(), pos.y(), pos2.x() - 1, pos2.y() - 1, 0xFF373737);
        guiGraphics.fill(pos.x() + 1, pos.y() + 1, pos2.x(), pos2.y(), 0xFFFFFFFF);
        guiGraphics.fill(pos.x() + 1, pos.y() + 1, pos2.x() - 1, pos2.y() - 1, 0xFF8B8B8B);
    }

    default void renderIconBackground(GuiGraphics guiGraphics, Vector2i pos, Icon icon) {
        renderSimpleArea(guiGraphics, pos, pos.add(icon.getRenderSize()));
    }

}
