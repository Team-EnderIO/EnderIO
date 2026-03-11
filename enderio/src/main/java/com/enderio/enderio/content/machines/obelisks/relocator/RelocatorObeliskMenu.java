package com.enderio.enderio.content.machines.obelisks.relocator;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class RelocatorObeliskMenu extends ObeliskMenu<RelocatorObeliskBlockEntity> {

    public RelocatorObeliskMenu(int containerId, Inventory inventory, RelocatorObeliskBlockEntity blockEntity) {
        super(EIOMenus.RELOCATOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public RelocatorObeliskMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(EIOMenus.RELOCATOR_OBELISK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.RELOCATOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), RelocatorObeliskBlockEntity.FILTER, 81, 31));
        addPlayerInventorySlots(8, 84);
    }
}
