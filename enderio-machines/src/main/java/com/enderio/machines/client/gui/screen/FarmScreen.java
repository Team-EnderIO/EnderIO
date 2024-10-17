package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FluidStackStaticWidget;
import com.enderio.machines.client.gui.widget.NewCapacitorEnergyWidget;
import com.enderio.machines.common.menu.FarmMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FarmScreen extends MachineScreen<FarmMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIOBase.loc("textures/gui/farm_station.png");

    public FarmScreen(FarmMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIOBase.loc("textures/gui/icons/range_buttons.png");

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new NewCapacitorEnergyWidget(leftPos + 7, topPos + 27, menu::getEnergyStorage, menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - 16 - 2, menu::getRedstoneControl,
            menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 114, 162, 87);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - (16 + 2) * 2, overlay);

        addRenderableWidget(EIOCommonWidgets.createRange(
            leftPos + imageWidth - 6 - 16,
            topPos + 34,
            EIOLang.HIDE_RANGE,
            EIOLang.SHOW_RANGE,
            menu::isRangeVisible,
            menu::setRangeVisible));

        addRenderableWidget(new FluidStackStaticWidget(leftPos + 20, topPos + 22, 16, 16, menu::getFluidTank));

        addRenderableOnly(new ActivityWidget(leftPos + 153, topPos + 89, menu::getMachineStates, true));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
