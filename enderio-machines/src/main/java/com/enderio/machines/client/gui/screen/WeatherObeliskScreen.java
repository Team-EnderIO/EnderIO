package com.enderio.machines.client.gui.screen;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.client.gui.screen.base.MachineScreen;
import com.enderio.machines.client.gui.widget.FluidStackWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.common.blocks.obelisks.weather.WeatherObeliskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WeatherObeliskScreen extends MachineScreen<WeatherObeliskMenu> {

    public static final ResourceLocation WEATHER_BG = EnderIO.loc("textures/gui/screen/weather_obelisk.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public WeatherObeliskScreen(WeatherObeliskMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new FluidStackWidget(22 + leftPos, 11 + topPos, 16, 63, menu::getFluidTank));

        addRenderableOnly(new ProgressWidget.BottomUp(WEATHER_BG, menu::getCraftingProgress, 81 + leftPos, 28 + topPos,
                12, 32, 176, 0));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(WEATHER_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
