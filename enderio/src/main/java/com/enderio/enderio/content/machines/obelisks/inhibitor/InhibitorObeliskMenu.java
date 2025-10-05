package com.enderio.enderio.content.machines.obelisks.inhibitor;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.init.EIOMenus;
import com.enderio.enderio.init.MachineBlockEntities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class InhibitorObeliskMenu extends ObeliskMenu<InhibitorObeliskBlockEntity> {

    public InhibitorObeliskMenu(int pContainerId, Inventory inventory, InhibitorObeliskBlockEntity blockEntity) {
        super(EIOMenus.INHIBITOR_OBELISK.get(), pContainerId, inventory, blockEntity);
        addSlots();
    }

    public InhibitorObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.INHIBITOR_OBELISK.get(), containerId, playerInventory, buf,
                MachineBlockEntities.INHIBITOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
    }
}
