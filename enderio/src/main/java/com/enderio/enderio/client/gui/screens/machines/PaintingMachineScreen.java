package com.enderio.enderio.client.gui.screens.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.gui.screens.machines.base.MachineScreen;
import com.enderio.enderio.client.gui.widgets.ActivityWidget;
import com.enderio.enderio.client.gui.widgets.CapacitorEnergyWidget;
import com.enderio.enderio.client.gui.widgets.NewProgressWidget;
import com.enderio.enderio.client.gui.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.common.foundation.lang.EIOLang;
import com.enderio.enderio.common.content.machines.painting.PaintingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PaintingMachineScreen extends MachineScreen<PaintingMachineMenu> {

    private static final ResourceLocation PAINTING_MACHINE_BG = EnderIO.rl("textures/gui/screen/painting_machine.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation PROGRESS_SPRITE = EnderIO.rl("screen/painting_machine/progress");

    public PaintingMachineScreen(PaintingMachineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        this.inventoryLabelY = this.imageHeight - 106;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(NewProgressWidget.leftRight(leftPos + 88, topPos + 35, 24, 16, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(PAINTING_MACHINE_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

}
