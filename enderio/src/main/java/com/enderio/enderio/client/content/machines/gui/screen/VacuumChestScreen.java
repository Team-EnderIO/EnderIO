package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.vacuum.chest.VacuumChestMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestScreen extends MachineScreen<VacuumChestMenu> {

    private static final ResourceLocation VACUUM_CHEST_BG = EnderIO.rl("textures/gui/screen/vacuum_chest.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 206;

    public VacuumChestScreen(VacuumChestMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        shouldRenderLabels = true;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 8 - 16, topPos + 105,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 8 - 16 * 2 - 2, topPos + 105,
            MachinesLang.HIDE_RANGE, MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignored) -> handleButtonPress(VacuumChestMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 8 - 8, topPos + 86,
                (b) -> handleButtonPress(VacuumChestMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 8 - 8, topPos + 94,
                (b) -> handleButtonPress(VacuumChestMenu.DECREASE_BUTTON_ID)));

        addRenderableWidget(
                new ActivityWidget(leftPos + imageWidth - 8 - 16 * 3 - 4, topPos + 105, menu::getMachineStates));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(VACUUM_CHEST_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, FiltersLang.GUI_FILTER, 8, 74, 4210752, false);
        guiGraphics.drawString(font, MachinesLang.RANGE, imageWidth - 8 - font.width(MachinesLang.RANGE), 74, 4210752, false);
        guiGraphics.drawString(font, menu.getRange() + "", leftPos + imageWidth - 8 - 8 - 10, topPos + 90, 0, false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }
}
