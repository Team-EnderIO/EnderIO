package com.enderio.enderio.common.content.travel.travel_anchor;

import com.enderio.enderio.common.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.common.foundation.menu.MachineMenu;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class TravelAnchorMenu extends MachineMenu<TravelAnchorBlockEntity> {
    public TravelAnchorMenu(int containerId, Inventory inventory, TravelAnchorBlockEntity blockEntity) {
        super(MachineMenus.TRAVEL_ANCHOR.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public TravelAnchorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.TRAVEL_ANCHOR.get(), containerId, playerInventory, buf,
                MachineBlockEntities.TRAVEL_ANCHOR.get(), MachineBlockEntities.PAINTED_TRAVEL_ANCHOR.get());
        addSlots();
    }

    private void addSlots() {
        addSlot(new GhostMachineSlot(getMachineInventory(), TravelAnchorBlockEntity.GHOST, 125, 10));
        addPlayerInventorySlots(8, 103);
    }

    public String getName() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getName();
    }

    public void setName(String name) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setName(name);
    }

    public boolean isVisible() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().isVisible();
    }

    public void setVisible(boolean visible) {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        getBlockEntity().setIsVisible(visible);
    }
}
