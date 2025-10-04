package com.enderio.enderio.common.content.machines.obelisks.relocator;

import com.enderio.enderio.common.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.common.foundation.menu.MachineSlot;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class RelocatorObeliskMenu extends ObeliskMenu<RelocatorObeliskBlockEntity> {

    public RelocatorObeliskMenu(int containerId, Inventory inventory, RelocatorObeliskBlockEntity blockEntity) {
        super(MachineMenus.RELOCATOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public RelocatorObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.RELOCATOR_OBELISK.get(), containerId, playerInventory, buf,
                MachineBlockEntities.RELOCATOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), RelocatorObeliskBlockEntity.FILTER, 81, 31));
        addPlayerInventorySlots(8, 84);
    }
}
