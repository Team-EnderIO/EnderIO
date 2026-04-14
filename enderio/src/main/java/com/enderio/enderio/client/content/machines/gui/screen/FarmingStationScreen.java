package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.farming_station.FarmingStationMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class FarmingStationScreen extends MachineScreen<FarmingStationMenu> {
    public static final Identifier BG_TEXTURE = EnderIO.id("textures/gui/screen/farm_station.png");
    private static final Identifier RANGE_BUTTON_TEXTURE = EnderIO.id("textures/gui/icons/range_buttons.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 169;

    public FarmingStationScreen(FarmingStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new CapacitorEnergyWidget(leftPos + 16, topPos + 14, 9, 45, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 86, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 16, topPos + 6 + 16 + 2, overlay);

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 16, topPos + 6 + (16 + 2) * 2,
            MachinesLang.HIDE_RANGE, MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignore) -> handleButtonPress(FarmingStationMenu.VISIBILITY_BUTTON_ID)));

        addRenderableOnly(new ActivityWidget(leftPos + imageWidth - 16, topPos + 62, menu::getMachineStates, false));

    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
