package com.enderio.enderio.common.content.machines.obelisks.attractor;

import com.enderio.enderio.common.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.common.foundation.menu.MachineSlot;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AttractorObeliskMenu extends ObeliskMenu<AttractorObeliskBlockEntity> {

    public AttractorObeliskMenu(int containerId, Inventory inventory, AttractorObeliskBlockEntity blockEntity) {
        super(MachineMenus.ATTRACTOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public AttractorObeliskMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.ATTRACTOR_OBELISK.get(), containerId, playerInventory, buf,
                MachineBlockEntities.ATTRACTOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), AttractorObeliskBlockEntity.FILTER, 81, 31));

        addPlayerInventorySlots(8, 84);
    }

}
