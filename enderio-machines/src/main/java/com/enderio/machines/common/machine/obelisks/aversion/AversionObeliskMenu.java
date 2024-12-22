package com.enderio.machines.common.machine.obelisks.aversion;

import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.machine.base.menu.MachineSlot;
import com.enderio.machines.common.machine.obelisks.ObeliskMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AversionObeliskMenu extends ObeliskMenu<AversionObeliskBlockEntity> {

    public AversionObeliskMenu(int containerId, Inventory inventory, AversionObeliskBlockEntity blockEntity) {
        super(MachineMenus.AVERSION_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public AversionObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.AVERSION_OBELISK.get(), MachineBlockEntities.AVERSION_OBELISK.get(), containerId, playerInventory, buf);
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), AversionObeliskBlockEntity.FILTER, 40, 60));

        addPlayerInventorySlots(8, 84);
    }
}
