package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.foundation.widgets.FluidStackWidget;
import com.enderio.enderio.client.foundation.widgets.ProgressWidget;
import com.enderio.enderio.content.machines.obelisks.weather.WeatherObeliskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class WeatherObeliskScreen extends MachineScreen<WeatherObeliskMenu> {

    public static final Identifier WEATHER_BG = EnderIO.rl("textures/gui/screen/weather_obelisk.png");
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
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WEATHER_BG, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
