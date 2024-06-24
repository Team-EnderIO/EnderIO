package com.enderio.core.client.gui.screen;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class EnderContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private final List<BaseScreenOverlay> overlays = new ArrayList<>();

    public EnderContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (menu instanceof BaseBlockEntityMenu<?> baseBlockEntityMenu && baseBlockEntityMenu.getBlockEntity() == null) {
            return;
        }

        // TODO: Render back-to-front?
        for (var overlay : overlays) {
            if (overlay.isActive()) {
                overlay.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            }
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    public <U extends BaseScreenOverlay> U addOverlay(U overlay) {
        overlays.add(overlay);
        return overlay;
    }

    @Override
    protected void clearWidgets() {
        overlays.clear();
        super.clearWidgets();
    }

    // TODO: Pass events through to overlays.

    // TODO: Expose our own keyboard events that do pre checks first, then overlays, then custom screen logic then super.

    // If you implement custom keyPressed behaviour, always check this first.
    protected boolean preKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return false;
    }
}
