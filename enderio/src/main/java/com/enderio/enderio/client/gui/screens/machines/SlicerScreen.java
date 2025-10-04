package com.enderio.enderio.client.gui.screens.machines;

import com.enderio.EnderIO;
import com.enderio.enderio.client.gui.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.client.gui.screens.machines.base.MachineScreen;
import com.enderio.enderio.client.gui.widgets.ActivityWidget;
import com.enderio.enderio.client.gui.widgets.NewCapacitorEnergyWidget;
import com.enderio.enderio.client.gui.widgets.NewProgressWidget;
import com.enderio.enderio.machines.common.blocks.slicer.SlicerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SlicerScreen extends MachineScreen<SlicerMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/slice_and_splice.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private static final ResourceLocation PROGRESS_SPRITE = EnderIO.rl("screen/slice_and_splice/progress");

    public SlicerScreen(SlicerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        shouldRenderLabels = true;

        titleLabelY = 6 + 2;
        inventoryLabelY = 115;
    }

    @Override
    protected void init() {
        super.init();
        centerAlignTitleLabelX();

        addRenderableOnly(NewProgressWidget.leftRight(leftPos + 98, topPos + 61, 24, 16, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));

        addRenderableOnly(new ActivityWidget(leftPos + 153, topPos + 89, menu::getMachineStates, true));

        addRenderableOnly(new NewCapacitorEnergyWidget(leftPos + 7, topPos + 27, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 125, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - 16 - 2, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

}
