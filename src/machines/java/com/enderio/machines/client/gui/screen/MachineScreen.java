package com.enderio.machines.client.gui.screen;

import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.common.menu.GhostMachineSlot;
import com.enderio.machines.common.menu.MachineMenu;
import com.enderio.machines.common.menu.PreviewMachineSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class MachineScreen<T extends MachineMenu<?>> extends EIOScreen<T> {
    public static final int SLOT_COLOR = -2130706433;
    protected MachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    protected MachineScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, boolean renderLabels) {
        super(pMenu, pPlayerInventory, pTitle, renderLabels);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        if (slot instanceof GhostMachineSlot || slot instanceof PreviewMachineSlot) {
            if (slot.hasItem()) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 300F);
                guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, SLOT_COLOR);
                guiGraphics.pose().popPose();
            }
        }
    }
}
