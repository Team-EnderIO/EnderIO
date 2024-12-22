package com.enderio.machines.common.blocks.obelisks.relocator;

import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.obelisks.ObeliskMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class RelocatorObeliskMenu extends ObeliskMenu<RelocatorObeliskBlockEntity> {

    public RelocatorObeliskMenu(int containerId, Inventory inventory, RelocatorObeliskBlockEntity blockEntity) {
        super(MachineMenus.RELOCATOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public RelocatorObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.RELOCATOR_OBELISK.get(), MachineBlockEntities.RELOCATOR_OBELISK.get(), containerId,
                playerInventory, buf);
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), RelocatorObeliskBlockEntity.FILTER, 40, 60));

        addPlayerInventorySlots(8, 84);
    }
}
