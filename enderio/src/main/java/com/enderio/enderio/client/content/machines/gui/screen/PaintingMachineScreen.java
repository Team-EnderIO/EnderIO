package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.NewProgressWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.painting.PaintingMachineMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PaintingMachineScreen extends MachineScreen<PaintingMachineMenu> {

    private static final Identifier PAINTING_MACHINE_BG = EnderIO.id("textures/gui/screen/painting_machine.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final Identifier PROGRESS_SPRITE = EnderIO.id("screen/painting_machine/progress");

    public PaintingMachineScreen(PaintingMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);
        this.inventoryLabelY = HEIGHT - 106;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(NewProgressWidget.leftRight(leftPos + 88, topPos + 35, 24, 16, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, PAINTING_MACHINE_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

}
