package com.enderio.core.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(forRemoval = true, since = "7.0")
public abstract class EIOScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
        implements EnderScreen {

    private final boolean renderLabels;
    private final List<EditBox> editBoxList = new ArrayList<>();

    protected EIOScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        this(menu, playerInventory, title, imageWidth, imageHeight, false);
    }

    protected EIOScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight, boolean renderLabels) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
        this.renderLabels = renderLabels;
    }

    @Override
    public void resize(int width, int height) {
        Map<String, String> oldEditBoxValues = new HashMap<>();
        for (EditBox editBox : editBoxList) {
            oldEditBoxValues.put(editBox.getMessage().getString(), editBox.getValue());
        }
        editBoxList.clear();

        super.resize(width, height);
        for (EditBox editBox : editBoxList) {
            editBox.setValue(oldEditBoxValues.getOrDefault(editBox.getMessage().getString(), ""));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        /*
         * if (menu instanceof SyncedMenu<?> syncedMenu && syncedMenu.getBlockEntity()
         * == null) { return; }
         */

        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // TODO: 1.21.4: Do not assume 256x256 texture?
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, getBackgroundImage(), getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256) { // ESC has priority
            Minecraft.getInstance().player.closeContainer();
        }

        for (EditBox editBox : editBoxList) {
            if (editBox.keyPressed(event) || editBox.canConsumeInput()) {
                return true;
            }
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (getFocused() instanceof AbstractWidget abstractWidget && abstractWidget.isActive()) {
            return abstractWidget.mouseDragged(event, dragX, dragY);
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        if (renderLabels) {
            super.extractLabels(graphics, xm, ym);
        }
    }

    public abstract Identifier getBackgroundImage();

    @Override
    protected <U extends GuiEventListener & NarratableEntry> U addWidget(U guiEventListener) {
        if (guiEventListener instanceof EditBox editBox) {
            editBoxList.add(editBox);
        }
        return super.addWidget(guiEventListener);
    }

    @Override
    protected void removeWidget(GuiEventListener guiEventListener) {
        super.removeWidget(guiEventListener);
        if (guiEventListener instanceof EditBox editBox) {
            editBoxList.remove(editBox);
        }
    }
}
