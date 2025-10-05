package com.enderio.enderio.content.machines.obelisks.aversion;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.MachineBlockEntities;
import com.enderio.enderio.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AversionObeliskMenu extends ObeliskMenu<AversionObeliskBlockEntity> {

    public AversionObeliskMenu(int containerId, Inventory inventory, AversionObeliskBlockEntity blockEntity) {
        super(MachineMenus.AVERSION_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public AversionObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.AVERSION_OBELISK.get(), containerId, playerInventory, buf,
                MachineBlockEntities.AVERSION_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), AversionObeliskBlockEntity.FILTER, 81, 31));

        addPlayerInventorySlots(8, 84);
    }
}
