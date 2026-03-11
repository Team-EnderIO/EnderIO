package com.enderio.enderio.content.machines.vacuum.chest;

import com.enderio.enderio.content.machines.vacuum.VacuumMachineBlockEntity;
import com.enderio.enderio.content.machines.vacuum.VacuumMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestMenu extends VacuumMenu<VacuumChestBlockEntity> {

    public VacuumChestMenu(int containerId, Inventory inventory, VacuumChestBlockEntity blockEntity) {
        super(EIOMenus.VACUUM_CHEST.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public VacuumChestMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(EIOMenus.VACUUM_CHEST.get(), containerId, playerInventory, buf,
            EIOBlockEntities.VACUUM_CHEST.get());
        addSlots();
    }

    private void addSlots() {
        this.addSlot(new MachineSlot(getMachineInventory(), VacuumMachineBlockEntity.FILTER, 8, 86));

        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new MachineSlot(getMachineInventory(), k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        addPlayerInventorySlots(8, 124);
    }
}
