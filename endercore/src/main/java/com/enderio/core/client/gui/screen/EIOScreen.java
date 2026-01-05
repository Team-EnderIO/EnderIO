package com.enderio.core.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated(forRemoval = true, since = "7.0")
public abstract class EIOScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
        implements EnderScreen {

    private final boolean renderLabels;
    private final List<EditBox> editBoxList = new ArrayList<>();

    protected EIOScreen(T menu, Inventory playerInventory, Component title) {
        this(menu, playerInventory, title, false);
    }

    protected EIOScreen(T menu, Inventory playerInventory, Component title, boolean renderLabels) {
        super(menu, playerInventory, title);
        this.renderLabels = renderLabels;
        this.imageWidth = getBackgroundImageSize().x();
        this.imageHeight = getBackgroundImageSize().y();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        Map<String, String> oldEditBoxValues = new HashMap<>();
        for (EditBox editBox : editBoxList) {
            oldEditBoxValues.put(editBox.getMessage().getString(), editBox.getValue());
        }
        editBoxList.clear();

        super.resize(minecraft, width, height);
        for (EditBox editBox : editBoxList) {
            editBox.setValue(oldEditBoxValues.getOrDefault(editBox.getMessage().getString(), ""));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        /*
         * if (menu instanceof SyncedMenu<?> syncedMenu && syncedMenu.getBlockEntity()
         * == null) { return; }
         */

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(getBackgroundImage(), getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC has priority
            Minecraft.getInstance().player.closeContainer();
        }

        for (EditBox editBox : editBoxList) {
            if (editBox.keyPressed(keyCode, scanCode, modifiers) || editBox.canConsumeInput()) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (getFocused() instanceof AbstractWidget abstractWidget && abstractWidget.isActive()) {
            return abstractWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (renderLabels) {
            super.renderLabels(guiGraphics, mouseX, mouseY);
        }
    }

    public abstract ResourceLocation getBackgroundImage();

    protected abstract Vector2i getBackgroundImageSize();

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
