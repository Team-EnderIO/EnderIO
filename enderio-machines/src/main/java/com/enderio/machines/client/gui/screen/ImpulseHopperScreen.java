package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.common.blocks.impulse_hopper.ImpulseHopperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ImpulseHopperScreen extends MachineScreen<ImpulseHopperMenu> {
    private static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/screen/impulse_hopper.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public ImpulseHopperScreen(ImpulseHopperMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new CapacitorEnergyWidget(15 + leftPos, 9 + topPos, 9, 47, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        guiGraphics.blit(BG_TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight);

        // for all ghost slots
        for (int i = 0; i < 6; i++) {
            if (getMenu().getBlockEntity().ghostSlotHasItem(i)) {
                if (getMenu().getBlockEntity().canPass(i)) {
                    guiGraphics.blit(BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 9, 18, 9);
                } else {
                    guiGraphics.blit(BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 26, 200, 0, 18, 9);
                }
                if (getMenu().getBlockEntity().canHoldAndMerge(i)) {
                    guiGraphics.blit(BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 9, 18, 9);
                } else {
                    guiGraphics.blit(BG_TEXTURE, getGuiLeft() + 43 + (18 * i), getGuiTop() + 53, 200, 0, 18, 9);
                }
            }
        }
    }
}
