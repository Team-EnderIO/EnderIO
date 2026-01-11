package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.foundation.widgets.EIOCommonWidgets;
import com.enderio.enderio.client.foundation.widgets.FluidStackStaticWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.vacuum.xp.XPVacuumMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumScreen extends MachineScreen<XPVacuumMenu> {

    private static final ResourceLocation XP_VACUUM_BG = EnderIO.rl("textures/gui/screen/xp_vacuum.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public XPVacuumScreen(XPVacuumMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        shouldRenderLabels = true;
        this.inventoryLabelY = this.imageHeight - 106;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new FluidStackStaticWidget(leftPos + 27, topPos + 22, 32, 32, menu::getFluidTank));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 34, MachinesLang.HIDE_RANGE,
            MachinesLang.SHOW_RANGE, menu::isRangeVisible,
            (ignored) -> handleButtonPress(XPVacuumMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(EIOCommonWidgets.createRangeIncrease(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 34,
                (b) -> handleButtonPress(XPVacuumMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(EIOCommonWidgets.createRangeDecrease(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 42,
                (b) -> handleButtonPress(XPVacuumMenu.DECREASE_BUTTON_ID)));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(XP_VACUUM_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, MachinesLang.RANGE, this.imageWidth - 6 - this.font.width(MachinesLang.RANGE), 24, 4210752,
                false);
        guiGraphics.drawString(font, menu.getRange() + "", leftPos + imageWidth - 6 - 16 - 2 - 8 - 10, topPos + 38, 0,
                false);
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

}
