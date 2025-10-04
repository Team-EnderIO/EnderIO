package com.enderio.enderio.client.gui.screens.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.gui.screens.machines.base.MachineScreen;
import com.enderio.enderio.client.gui.widgets.ActivityWidget;
import com.enderio.enderio.client.gui.widgets.CapacitorEnergyWidget;
import com.enderio.enderio.client.gui.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.machines.common.blocks.crafter.CrafterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen extends MachineScreen<CrafterMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/crafter.png");
    private static final int WIDTH = 220;
    private static final int HEIGHT = 166;

    public CrafterScreen(CrafterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new CapacitorEnergyWidget(10 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 6, topPos + 83, 208, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
