package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.CapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.ProgressWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.stirling_generator.StirlingGeneratorMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

public class StirlingGeneratorScreen extends MachineScreen<StirlingGeneratorMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");
    public static final ResourceLocation BG_TEXTURE = EnderIO.rl("textures/gui/screen/stirling_generator.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;

    public StirlingGeneratorScreen(StirlingGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableOnly(new ProgressWidget.BottomUp(BG_TEXTURE, menu::getBurnProgress, getGuiLeft() + 81,
                getGuiTop() + 53, 14, 14, 176, 0));

        addRenderableOnly(new CapacitorEnergyWidget(16 + leftPos, 14 + topPos, 9, 42, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6,
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(leftPos + imageWidth - 6 - 16, topPos + 16 * 4, menu::getMachineStates));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 83, 162, 76);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 24, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);
        float genRate = menu.getBlockEntity().getGenerationRate();
        float efficiency = menu.getBlockEntity().getFuelEfficiency();
        MutableComponent gen = TooltipUtil.withArgs(MachinesLang.GENERATING, FORMAT.format(genRate));
        MutableComponent eff = TooltipUtil.withArgs(MachinesLang.FUEL_EFFICIENCY, (int) efficiency);
        guiGraphics.drawString(font, gen, imageWidth / 2 - font.width(gen.getString()) / 2, 9, 0, false);
        if (menu.getBlockEntity().isCapacitorInstalled()) {
            guiGraphics.drawString(font, eff, imageWidth / 2 - font.width(eff.getString()) / 2, 9 + font.lineHeight + 2,
                    0, false);
        }
    }
}
