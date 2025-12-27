package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskMenu;
import com.enderio.enderio.content.machines.wireless_charger.WirelessChargerMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class WirelessChargerScreen extends MachineScreen<WirelessChargerMenu> {

    private static final Identifier BG_TEXTURE = EnderIO.rl("textures/gui/screen/wireless_charger.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public WirelessChargerScreen(WirelessChargerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);

        int rangeOffset = 50;
        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + rangeOffset,
            MachinesLang.HIDE_RANGE, MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignored) -> handleButtonPress(AversionObeliskMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 2 * 16,
                topPos + rangeOffset + 1, (b) -> handleButtonPress(AversionObeliskMenu.INCREASE_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 2 * 16,
                topPos + rangeOffset + 9, (b) -> handleButtonPress(AversionObeliskMenu.DECREASE_BUTTON_ID)));

        addRenderableWidget(
                new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + rangeOffset + 16, menu::getMachineStates));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

        int rangeOffset = 50;
        guiGraphics.drawString(font, getMenu().getBlockEntity().getRange() + "",
                leftPos + imageWidth - 8 - 16 - font.width(getMenu().getBlockEntity().getRange() + "") - 10,
                topPos + rangeOffset + 5, 0, false);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int rangeOffset = 50;
        guiGraphics.drawString(font, MachinesLang.RANGE, imageWidth - 9 - font.width(MachinesLang.RANGE), rangeOffset - 10,
                4210752, false);
        guiGraphics.drawString(font, MachinesLang.MAX_RANGE, imageWidth / 2 - font.width(MachinesLang.MAX_RANGE) / 2, 20, 0,
                false);
        String maxRange = getMenu().getMaxRange() + "";
        guiGraphics.drawString(font, maxRange, imageWidth / 2 - font.width(maxRange) / 2, 20 + font.lineHeight + 3, 0,
                false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }
}
