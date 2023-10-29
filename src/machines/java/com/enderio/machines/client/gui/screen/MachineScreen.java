package com.enderio.machines.client.gui.screen;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.common.menu.MachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class MachineScreen<T extends MachineMenu> extends EIOScreen<T> {
    protected MachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected MachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, boolean renderLabels) {
        super(pMenu, pPlayerInventory, pTitle, renderLabels);
    }

}
