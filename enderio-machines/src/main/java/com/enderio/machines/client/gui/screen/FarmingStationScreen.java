package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.common.blocks.farming_station.FarmingStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FarmingStationScreen extends MachineScreen<FarmingStationMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/screen/farm_station.png");
    private static final ResourceLocation RANGE_BUTTON_TEXTURE = EnderIO.loc("textures/gui/icons/range_buttons.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 169;

    public FarmingStationScreen(FarmingStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(leftPos + 16, topPos + 14, 9, 45, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 86, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 16, topPos + 6 + 16 + 2, overlay);

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 16, topPos + 6 + (16 + 2) * 2,
                EIOLang.HIDE_RANGE, EIOLang.SHOW_RANGE, menu::isRangeVisible,
                (ignore) -> handleButtonPress(FarmingStationMenu.VISIBILITY_BUTTON_ID)));

        addRenderableOnly(new ActivityWidget(leftPos + imageWidth - 16, topPos + 62, menu::getMachineStates, false));

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
