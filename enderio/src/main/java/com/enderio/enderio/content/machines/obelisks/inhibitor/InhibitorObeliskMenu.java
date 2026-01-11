package com.enderio.enderio.content.machines.obelisks.inhibitor;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class InhibitorObeliskMenu extends ObeliskMenu<InhibitorObeliskBlockEntity> {

    public InhibitorObeliskMenu(int containerId, Inventory inventory, InhibitorObeliskBlockEntity blockEntity) {
        super(EIOMenus.INHIBITOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public InhibitorObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.INHIBITOR_OBELISK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.INHIBITOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addPlayerInventorySlots(8, 84);
    }
}
