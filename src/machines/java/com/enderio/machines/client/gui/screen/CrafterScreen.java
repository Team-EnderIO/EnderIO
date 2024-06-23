package com.enderio.machines.client.gui.screen;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.client.gui.widget.RedstoneControlIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.machines.client.gui.widget.ActivityWidget;
import com.enderio.machines.client.gui.widget.CapacitorEnergyWidget;
import com.enderio.machines.client.gui.widget.ioconfig.IOConfigButton;
import com.enderio.machines.common.menu.CrafterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrafterScreen extends MachineScreen<CrafterMenu> {

    private static final ResourceLocation BG_TEXTURE = EnderIO.loc("textures/gui/crafter.png");

    public CrafterScreen(CrafterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new CapacitorEnergyWidget(this, getMenu().getBlockEntity()::getEnergyStorage, getMenu().getBlockEntity()::isCapacitorInstalled, 10 + leftPos, 14 + topPos, 9, 42));

        addRenderableWidget(new RedstoneControlIconWidget(leftPos + imageWidth - 6 - 16, topPos + 6, () -> menu.getBlockEntity().getRedstoneControl(),
            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));

        addRenderableWidget(new IOConfigButton<>(this, leftPos + imageWidth - 6 - 16, topPos + 24, 16, 16, menu, this::addRenderableWidget, font));
        addRenderableWidget(new ActivityWidget(this, menu.getBlockEntity()::getMachineStates, leftPos + imageWidth - 6 - 16, topPos + 16 * 4));
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return BG_TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(220, 166);
    }
}

