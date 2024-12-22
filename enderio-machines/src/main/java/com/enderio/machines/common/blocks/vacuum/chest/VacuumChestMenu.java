package com.enderio.machines.common.blocks.vacuum.chest;

import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.vacuum.VacuumMachineBlockEntity;
import com.enderio.machines.common.blocks.vacuum.VacuumMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestMenu extends VacuumMenu<VacuumChestBlockEntity> {

    public VacuumChestMenu(int containerId, Inventory inventory, VacuumChestBlockEntity blockEntity) {
        super(MachineMenus.VACUUM_CHEST.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public VacuumChestMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.VACUUM_CHEST.get(), MachineBlockEntities.VACUUM_CHEST.get(), containerId, playerInventory,
                buf);
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
