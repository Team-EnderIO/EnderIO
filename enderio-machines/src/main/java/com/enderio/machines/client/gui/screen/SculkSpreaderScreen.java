package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.base.client.gui.widget.RedstoneControlPickerWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.common.blocks.sculkspreader.SculkSpreaderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SculkSpreaderScreen extends MachineScreen<SculkSpreaderMenu> {

    public static final ResourceLocation VAT_BG = EnderIO.loc("textures/gui/screen/vat.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public SculkSpreaderScreen(SculkSpreaderMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackWidget(132 + leftPos, 12 + topPos, 15, 47, menu::getTank));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(VAT_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
