package com.enderio.enderio.client.gui.screens.machines;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.gui.screens.machines.base.MachineScreen;
import com.enderio.enderio.client.gui.widgets.ActivityWidget;
import com.enderio.enderio.client.gui.widgets.FluidStackWidget;
import com.enderio.enderio.client.gui.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.common.lang.EIOLang;
import com.enderio.enderio.machines.common.blocks.fluid_tank.FluidTankMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankScreen extends MachineScreen<FluidTankMenu> {
    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/tank.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public FluidTankScreen(FluidTankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new FluidStackWidget(80 + leftPos, 21 + topPos, 16, 47, menu::getFluidTank));
        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOLang.REDSTONE_MODE));
        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
