package com.enderio.enderio.common.content.machines.vacuum.chest;

import com.enderio.enderio.common.foundation.menu.MachineSlot;
import com.enderio.enderio.common.content.machines.vacuum.VacuumMachineBlockEntity;
import com.enderio.enderio.common.content.machines.vacuum.VacuumMenu;
import com.enderio.enderio.common.init.MachineBlockEntities;
import com.enderio.enderio.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestMenu extends VacuumMenu<VacuumChestBlockEntity> {

    public VacuumChestMenu(int containerId, Inventory inventory, VacuumChestBlockEntity blockEntity) {
        super(MachineMenus.VACUUM_CHEST.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public VacuumChestMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.VACUUM_CHEST.get(), containerId, playerInventory, buf,
                MachineBlockEntities.VACUUM_CHEST.get());
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
