package com.enderio.enderio.content.machines.obelisks.attractor;

import com.enderio.enderio.content.machines.obelisks.ObeliskMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class AttractorObeliskMenu extends ObeliskMenu<AttractorObeliskBlockEntity> {

    public AttractorObeliskMenu(int containerId, Inventory inventory, AttractorObeliskBlockEntity blockEntity) {
        super(EIOMenus.ATTRACTOR_OBELISK.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public AttractorObeliskMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(EIOMenus.ATTRACTOR_OBELISK.get(), containerId, playerInventory, buf,
            EIOBlockEntities.ATTRACTOR_OBELISK.get());
        addSlots();
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);
        addSlot(new MachineSlot(getMachineInventory(), AttractorObeliskBlockEntity.FILTER, 81, 31));

        addPlayerInventorySlots(8, 84);
    }

}
