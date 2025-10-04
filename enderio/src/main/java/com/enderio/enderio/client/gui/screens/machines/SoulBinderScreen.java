package com.enderio.enderio.client.gui.screens.machines;

import com.enderio.EnderIO;
import com.enderio.enderio.client.gui.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.client.gui.screens.machines.base.MachineScreen;
import com.enderio.enderio.client.gui.widgets.ActivityWidget;
import com.enderio.enderio.client.gui.widgets.CapacitorEnergyWidget;
import com.enderio.enderio.client.gui.widgets.ExperienceCraftingWidget;
import com.enderio.enderio.client.gui.widgets.ProgressWidget;
import com.enderio.enderio.machines.common.blocks.soul_binder.SoulBinderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulBinderScreen extends MachineScreen<SoulBinderMenu> {

    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/soul_binder.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public SoulBinderScreen(SoulBinderMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget.LeftRight(BG_TEXTURE, menu::getCraftingProgress, getGuiLeft() + 80,
                getGuiTop() + 34, 24, 17, 176, 14));

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableOnly(new ExperienceCraftingWidget(56 + leftPos, 68 + topPos, 65, 5, menu::getFluidTank,
                menu::getExperience));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
