package com.enderio.enderio.content.machines.obelisks.aversion;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AversionObeliskMenu extends ObeliskMenu<AversionObeliskBlockEntity> {

    public AversionObeliskMenu(int containerId, Inventory inventory, AversionObeliskBlockEntity blockEntity) {
        super(EIOMenus.AVERSION_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public AversionObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.AVERSION_OBELISK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.AVERSION_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), AversionObeliskBlockEntity.FILTER, 81, 31));

        addPlayerInventorySlots(8, 84);
    }
}
