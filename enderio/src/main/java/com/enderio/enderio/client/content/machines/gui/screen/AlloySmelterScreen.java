package com.enderio.enderio.client.content.machines.gui.screen;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.client.content.machines.gui.screen.base.MachineScreen;
import com.enderio.enderio.client.content.machines.gui.widget.ActivityWidget;
import com.enderio.enderio.client.content.machines.gui.widget.AlloySmelterModeWidget;
import com.enderio.enderio.client.content.machines.gui.widget.NewCapacitorEnergyWidget;
import com.enderio.enderio.client.foundation.widgets.NewProgressWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMenu;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.NotImplementedException;

public class AlloySmelterScreen extends MachineScreen<AlloySmelterMenu> {

    public static final ResourceLocation BG_TEXTURE_AUTO = EnderIO.rl("textures/gui/screen/alloy_smelter_auto.png");
    private static final ResourceLocation BG_TEXTURE_ALLOY = EnderIO.rl("textures/gui/screen/alloy_smelter_alloy.png");
    private static final ResourceLocation BG_TEXTURE_FURNACE = EnderIOAPI
            .rl("textures/gui/screen/alloy_smelter_furnace.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 208;

    private static final ResourceLocation PROGRESS_SPRITE = EnderIO.rl("screen/alloy_smelter/lit_progress");

    public AlloySmelterScreen(AlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth = WIDTH;
        imageHeight = HEIGHT;
        shouldRenderLabels = true;

        titleLabelY = 6 + 2;
        inventoryLabelY = 115;
    }

    @Override
    protected void init() {
        super.init();
        centerAlignTitleLabelX();

        addRenderableOnly(NewProgressWidget.bottomUp(leftPos + 56, topPos + 56, 14, 14, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));
        addRenderableOnly(NewProgressWidget.bottomUp(leftPos + 104, topPos + 56, 14, 14, PROGRESS_SPRITE,
                menu::getCraftingProgress, true));

        addRenderableOnly(new ActivityWidget(leftPos + 153, topPos + 89, menu::getMachineStates, true));

        addRenderableOnly(new NewCapacitorEnergyWidget(leftPos + 7, topPos + 27, menu::getEnergyStorage,
                menu::isCapacitorInstalled));

        addRenderableWidget(new AlloySmelterModeWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55, menu::getMode,
                menu::setMode, MachinesLang.ALLOY_SMELTER_MODE));

        addRenderableWidget(new RedstoneControlPickerWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - (16 + 2),
                menu::getRedstoneControl, menu::setRedstoneControl, EIOCommonLang.REDSTONE_MODE));

        var overlay = addIOConfigOverlay(1, leftPos + 7, topPos + 114, 162, 87);
        addIOConfigButton(leftPos + imageWidth - 6 - 16, topPos + 6 + 55 - (16 + 2) * 2, overlay);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        switch (menu.getMode()) {
        case ALL -> pGuiGraphics.blit(BG_TEXTURE_AUTO, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        case ALLOYS -> pGuiGraphics.blit(BG_TEXTURE_ALLOY, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        case FURNACE -> pGuiGraphics.blit(BG_TEXTURE_FURNACE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        default -> throw new NotImplementedException();
        }
    }
}
