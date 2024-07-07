package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.AlloySmelterModeWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ProgressWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloySmelterScreen extends MachineScreen<AlloySmelterMenu> {

    public static final ResourceLocation BG_TEXTURE_AUTO = EnderIO.loc("textures/gui/alloy_smelter_auto.png");
    private static final ResourceLocation BG_TEXTURE_ALLOY = EnderIO.loc("textures/gui/alloy_smelter_alloy.png");
    private static final ResourceLocation BG_TEXTURE_FURNACE = EnderIO.loc("textures/gui/alloy_smelter_furnace.png");

    public AlloySmelterScreen(AlloySmelterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getCraftingProgress(), getGuiLeft() + 56, getGuiTop() + 36, 14, 14, 176, 0));
        addRenderableOnly(new ProgressWidget.BottomUp(this, () -> menu.getBlockEntity().getCraftingProgress(), getGuiLeft() + 104, getGuiTop() + 36, 14, 14, 176, 0));
        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, menu.getBlockEntity()::isCapacitorInstalled, 16 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new RedstoneControlIconWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));

        addRenderableWidget(new AlloySmelterModeWidget(leftPos + imageWidth - 6 - 16, topPos + 6 + 18 * 2, () -> menu.getBlockEntity().getMode(), mode -> menu.getBlockEntity().setMode(mode), MachineLang.ALLOY_SMELTER_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        AlloySmelterBlockEntity be = getMenu().getBlockEntity();
        return switch (be.getMode()) {
            case ALL -> BG_TEXTURE_AUTO;
            case ALLOYS -> BG_TEXTURE_ALLOY;
            case FURNACE -> BG_TEXTURE_FURNACE;
        };
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(176, 166);
    }
}
