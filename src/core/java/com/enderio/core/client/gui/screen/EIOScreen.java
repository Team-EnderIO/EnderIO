package com.enderio.core.client.gui.screen;

import com.enderio.api.misc.Vector2i;
import com.enderio.core.common.menu.SyncedMenu;
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

import java.util.*;

public abstract class EIOScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements IEnderScreen {

    private final boolean renderLabels;
    private final List<EditBox> editBoxList = new ArrayList<>();

    private final List<LateTooltipData> tooltips = new ArrayList<>();

    protected EIOScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        this(pMenu, pPlayerInventory, pTitle, false);
    }

    protected EIOScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, boolean renderLabels) {
        super(pMenu, pPlayerInventory, pTitle);
        this.renderLabels = renderLabels;
        this.imageWidth = getBackgroundImageSize().x();
        this.imageHeight = getBackgroundImageSize().y();
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        Map<String, String> oldEditBoxValues = new HashMap<>();
        for (EditBox editBox : editBoxList) {
            oldEditBoxValues.put(editBox.getMessage().getString(), editBox.getValue());
        }
        editBoxList.clear();
        super.resize(pMinecraft, pWidth, pHeight);
        for (EditBox editBox : editBoxList) {
            editBox.setValue(oldEditBoxValues.getOrDefault(editBox.getMessage().getString(), ""));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        if (menu instanceof SyncedMenu menu && menu.getBlockEntity() == null)
            return;
        renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
        for (LateTooltipData tooltip : tooltips) {
            tooltip.getGuiGraphics().renderTooltip(this.font, tooltip.getText(), Optional.empty(), tooltip.getMouseX(), tooltip.getMouseY());
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        tooltips.clear();
        guiGraphics.blit(getBackgroundImage(), getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == 256) { //ESC has priority
            Minecraft.getInstance().player.closeContainer();
        }
        for (EditBox editBox : editBoxList) {
            if (editBox.keyPressed(pKeyCode, pScanCode, pModifiers) || editBox.canConsumeInput()) {
                return true;
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (GuiEventListener widget : children()) {
            if (widget instanceof AbstractWidget abstractWidget && abstractWidget.isActive() && widget instanceof IFullScreenListener fullScreenListener) {
                fullScreenListener.onGlobalClick(pMouseX, pMouseY);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (getFocused() instanceof AbstractWidget abstractWidget && abstractWidget.isActive()) {
            return abstractWidget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        } return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        if (renderLabels) {
            super.renderLabels(guiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (EditBox editBox : editBoxList) {
            editBox.tick();
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

    @Override
    public void addTooltip(LateTooltipData data) {
        tooltips.add(data);
    }
}
