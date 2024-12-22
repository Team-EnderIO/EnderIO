package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.widget.EIOCommonWidgets;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.FluidStackStaticWidget;
import com.enderio.machines.common.blocks.vacuum.xp.XPVacuumMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumScreen extends MachineScreen<XPVacuumMenu> {

    private static final ResourceLocation XP_VACUUM_BG = EnderIOBase.loc("textures/gui/screen/xp_vacuum.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    private static final ResourceLocation PLUS = EnderIOBase.loc("buttons/plus_small");
    private static final ResourceLocation MINUS = EnderIOBase.loc("buttons/minus_small");
    private static final WidgetSprites PLUS_SPRITES = new WidgetSprites(PLUS, PLUS);
    private static final WidgetSprites MINUS_SPRITES = new WidgetSprites(MINUS, MINUS);

    public XPVacuumScreen(XPVacuumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(EIOCommonWidgets.createRange(leftPos + imageWidth - 6 - 16, topPos + 34, EIOLang.HIDE_RANGE,
                EIOLang.SHOW_RANGE, menu::isRangeVisible,
                (ignored) -> handleButtonPress(XPVacuumMenu.VISIBILITY_BUTTON_ID)));

        addRenderableWidget(new ImageButton(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 34, 8, 8, PLUS_SPRITES,
                (b) -> handleButtonPress(XPVacuumMenu.INCREASE_BUTTON_ID)));
        addRenderableWidget(new ImageButton(leftPos + imageWidth - 6 - 8 - 2 - 16, topPos + 42, 8, 8, MINUS_SPRITES,
                (b) -> handleButtonPress(XPVacuumMenu.DECREASE_BUTTON_ID)));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(XP_VACUUM_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, EIOLang.RANGE, this.imageWidth - 6 - this.font.width(EIOLang.RANGE), 24, 4210752,
                false);
        guiGraphics.drawString(font, menu.getRange() + "", leftPos + imageWidth - 6 - 16 - 2 - 8 - 10, topPos + 38, 0,
                false);
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
    }

}
